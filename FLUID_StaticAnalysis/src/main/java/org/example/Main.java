package org.example;


import scala.concurrent.Lock;
import scala.util.control.TailCalls;
import soot.*;
import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.options.Options;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    private final static String USER_HOME = System.getProperty("user.home");
    private static String androidJar = USER_HOME + "/Library/Android/sdk/platforms";
    static String androidAPKPath = System.getProperty("user.dir") + File.separator + "apks";
    //    static String apkPath = androidDemoPath + File.separator + "/st_demo.apk";
//    static String apkPath = androidAPKPath + File.separator + "/com.simplemobiletools.notes.apk";
//   static String apkPath = androidAPKPath + File.separator + "com.simplemobiletools.calculator_5.8.2-51_minAPI21(nodpi)_apkmirror.com.apk";
// static String apkPath = androidAPKPath + File.separator + "FalseNegativeTestApp.apk";
//    static String apkPath = androidAPKPath + File.separator + "StaticAnalysisTestApp.apk";
    static String apkPath = androidAPKPath + File.separator + "calculator.apk";

    static String outputPath = USER_HOME + File.separator + "output";
    static File outputFile ;
    static List<SootMethod> listTargetMethod = new ArrayList<>();
    static List<SootClass> listTargetClass = new ArrayList<>();
    static Map<SootMethod,SootMethod> foundUIUpdateMethods = new HashMap<>();
    static List<SootMethod> listCallSources = new ArrayList<>();
    static List<SootMethod> listCallTargets = new ArrayList<>();
//    static List<CallEdge> listCallEdges = new ArrayList<>();
    static AtomicBoolean lockStdout = new AtomicBoolean();
    static List<String> output = new ArrayList<>();
    static Lock lock = new Lock();
    static AtomicInteger threadNum = new AtomicInteger(0);
    static String outputFilePath = "/home/ywha/WEB_FLUID/FLUID_StaticAnalysis/output/StaticAnalysisResult.log";
    static List<byte[]> outputBuffer = new ArrayList<>();
    private static void fillListTargetMethod()
    {
        System.out.println("preparing listTargetMethod ... ");
        Object[] arrayApplicationClasses = Scene.v().getApplicationClasses().toArray();
        SootClass classTextView = Scene.v().getSootClass("android.widget.TextView");
        List<SootMethod> listTextViewMethods = classTextView.getMethods();
        for(int i = 0; i< listTextViewMethods.size(); i++)
        {
            SootMethod thisMethod = listTextViewMethods.get(i);
            if( thisMethod.toString().equals("<android.widget.TextView: void setText(java.lang.CharSequence)>") ||
                thisMethod.toString().equals("<android.widget.TextView: void setText(int)>") ||
                thisMethod.toString().equals("<android.widget.TextView: void setText(java.lang.CharSequence,android.widget.TextView$BufferType)>") ||
                thisMethod.toString().equals("<android.widget.TextView: void setText(char[],int,int)>") ||
                thisMethod.toString().equals("<android.widget.TextView: void setText(int,android.widget.TextView$BufferType)>") )
            {
                System.out.println("adding method to listTargetMethod : "+thisMethod.toString());
                listTargetMethod.add(thisMethod);
            }
        }

        SootClass classImageView = Scene.v().getSootClass("android.widget.ImageView");
        Object[] methodsImageView = classImageView.getMethods().toArray();
        for(int i = 0; i< methodsImageView.length; i++)
        {
            SootMethod thisMethod = (SootMethod) methodsImageView[i];
            if( thisMethod.toString().equals("<android.widget.ImageView: void setImageResource(int)>") ||
                thisMethod.toString().equals("<android.widget.ImageView: void setImageURI(android.net.Uri)>") ||
                thisMethod.toString().equals("<android.widget.ImageView: void setImageDrawable(android.graphics.drawable.Drawable)>") ||
                thisMethod.toString().equals("<android.widget.ImageView: void setImageIcon(android.graphics.drawable.Icon)>") ||
                thisMethod.toString().equals("<android.widget.ImageView: void setImageBitmap(android.graphics.Bitmap)>") ||
                thisMethod.toString().equals("<android.widget.ImageView: void setImageMatrix(android.graphics.Matrix)>") )
            {
                System.out.println("adding method to listTargetMethod : "+thisMethod.toString());
                listTargetMethod.add(thisMethod);
            }
        }
        System.out.println("preparing listTargetMethod ... done");
        System.out.println("---------------------------------------------------------------------\n");
    }

    private static void fillListTargetActivityClass()
    {
        System.out.println("preparing listTargetClass ... ");
        Object[] arrClasses = Scene.v().getApplicationClasses().toArray();
        List<SootClass> listNodes = new ArrayList<>();
        for(int i = 0; i< arrClasses.length; i++)
        {
            SootClass thisClass = (SootClass)arrClasses[i];
            if(listNodes.contains(thisClass))
            {
                continue;
            }
            listNodes.add(thisClass);
//			System.err.println("["+i+"]th class : "+thisClass.toString());
//			System.out.println("["+i+"]th class : "+thisClass.toString());
            SootClass superClass = thisClass.getSuperclass();

            while(true)
            {
                listNodes.add(superClass);
                if(listTargetClass.contains(superClass)) //if ancestor in list -> switch
                {
                    System.err.println("switching Class "+superClass.toString()+" -> "+thisClass.toString());
                    System.out.println("switching Class "+superClass.toString()+" -> "+thisClass.toString());
                    listTargetClass.remove(superClass);
                    listTargetClass.add(thisClass);
                    break;
                }
                if(superClass.toString().equals("androidx.appcompat.app.AppCompatActivity")|| superClass.toString().equals("android.app.Activity"))
                {
                    System.err.println("found activity class : "+thisClass.toString());
                    System.out.println("found activity class : "+thisClass.toString());
                    if(!listTargetClass.contains(thisClass))
                    {
                        listTargetClass.add(thisClass);
                        break;

                    }
                    else
                    {
                        System.out.println("thisClass is already injected");
                        break;

                    }

                }
                else
                {
                    if(superClass.hasSuperclass())
                    {
                        superClass = superClass.getSuperclass();
                    }
                    else
                    {
                        break;
                    }
                }
            }


        }
        for(int i = 0; i< listTargetClass.size(); i++)
        {
            if(listTargetClass.get(i).toString().contains("androidx"))
            {
                listTargetClass.remove(listTargetClass.get(i));
            }
        }

        //add inner classes
        for(int i = 0; i< arrClasses.length; i++)
        {
            SootClass thisClass = (SootClass) arrClasses[i];
            if(thisClass.hasOuterClass() && listTargetClass.contains(thisClass.getOuterClass()))
            {
                System.err.println("found inner class : "+thisClass.toString());
                System.out.println("found inner class : "+thisClass.toString());
                listTargetClass.add(thisClass);
            }
        }
        System.out.println("preparing listTargetClass ... done");
        System.out.println("---------------------------------------------------------------------\n");
    }

    private static void fillListTargetClass() //find custom UI classes
    {
        System.out.println("preparing listTargetClass ... ");
        Object[] classApplication = Scene.v().getApplicationClasses().toArray();

        SootClass classView = Scene.v().getSootClass("android.view.View");
//        List<SootClass> listVistedClass = new ArrayList<>();
        for(int i= 0; i< classApplication.length; i++)
        {
            SootClass thisClass = (SootClass) classApplication[i];


            //exclude android native UIs
//            if( thisClass.getPackageName().contains("androidx") ||
//                thisClass.getPackageName().contains("android.widget") ||
//                thisClass.getPackageName().contains("com.google.android"))
//            {
//                continue;
//            }

//            listVistedClass.add(thisClass);
            SootClass superClass;
            if(thisClass.hasSuperclass())
            {
                superClass = thisClass.getSuperclass();
            }
            else
            {
                continue;
            }
            while(superClass != null)
            {
                if(superClass.equals(classView))
                {
                    System.out.println("adding class to to listTargetClass : " + thisClass.getName());
                    listTargetClass.add(thisClass);
                    break;
                }
                else
                {
                    if(superClass.hasSuperclass())
                    {
                        superClass = superClass.getSuperclass();
                    }
                    else
                    {
                        superClass = null;
                    }
                }

            }
        }

        System.out.println("preparing listTargetClass ... done");
        System.out.println("---------------------------------------------------------------------\n");
    }

    public static void setupSoot(String androidJar, String apkPath, String outputPath) {
        G.reset();
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_whole_program(true);
        Options.v().set_prepend_classpath(true);
        Options.v().set_validate(true);
        Options.v().set_src_prec(Options.src_prec_apk);
        Options.v().set_output_format(Options.output_format_dex);
        Options.v().set_android_jars(androidJar);
        Options.v().set_process_dir(Collections.singletonList(apkPath));
        Options.v().set_include_all(true);
        Options.v().set_process_multiple_dex(true);
        Options.v().set_output_dir(outputPath);


        //new options
//        Options.v().set_oaat(true);
        Scene.v().addBasicClass("java.lang.Class",SootClass.HIERARCHY);
        Scene.v().addBasicClass("java.io.PrintStream",SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.lang.System",SootClass.SIGNATURES);
        Scene.v().addBasicClass("dalvik.system.DexClassLoader",SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.lang.Class[]",SootClass.HIERARCHY);
        Scene.v().addBasicClass("java.lang.String[]",SootClass.HIERARCHY);
        Scene.v().addBasicClass("android.graphics.Bitmap[]",SootClass.HIERARCHY);
//        Scene.v().addBasicClass("com.simplemobiletools.notes.MainActivity",SootClass.HIERARCHY);

        Scene.v().loadNecessaryClasses();
    }

    public static void main(String[] args) throws InterruptedException, IOException {

        if(System.getenv().containsKey("ANDROID_HOME"))
            androidJar = System.getenv("ANDROID_HOME")+ File.separator+"platforms";


        if(args.length != 1)
        {
            System.out.println("Usage : gradlew run [apk file]");
        }
        apkPath = args[0];
//        System.out.println(apkPath);

        setupSoot(androidJar,apkPath,outputPath);

        fillListTargetMethod();
        outputFilePath = apkPath+".result";
        outputFile = new File(outputFilePath);


//        fillListTargetClass();
        fillListTargetActivityClass();
        lockStdout.set(false);
        InfoflowConfiguration.CallgraphAlgorithm cgAlgorithm = InfoflowConfiguration.CallgraphAlgorithm.CHA;


        // Parse arguments
//        InfoflowConfiguration.CallgraphAlgorithm cgAlgorithm = InfoflowConfiguration.CallgraphAlgorithm.SPARK;
//        if (args.length > 0 && args[0].equals("CHA"))
//            cgAlgorithm = InfoflowConfiguration.CallgraphAlgorithm.CHA;
        boolean drawGraph = false;
        if (args.length > 1 && args[1].equals("draw"))
            drawGraph = true;
        // Setup FlowDroid
        final InfoflowAndroidConfiguration config = AndroidUtil.getFlowDroidConfig(apkPath, androidJar, cgAlgorithm);
        config.setFlowSensitiveAliasing(false);
        config.setCodeEliminationMode(InfoflowConfiguration.CodeEliminationMode.NoCodeElimination);
        SetupApplication app = new SetupApplication(config);
        // Create the Callgraph without executing taint analysis
        app.constructCallgraph();
//
		System.out.println("CallGraph constructed \n-------------------------");


        for(SootClass sootClass : listTargetClass) {
            System.out.println(sootClass.toString());
            SootClass thatClass = Scene.v().getSootClass(sootClass.toString());
            for (SootMethod sootMethod : thatClass.getMethods()) {
                System.out.println(sootMethod);
            }
        }
//        List<CallEdge> listCallEdges = getAllReachableMethodsToList(app.getDummyMainMethod());
//        System.out.println("listCallEdges constructed");
////
//        SootClass sootClass1 = Scene.v().getSootClass("com.example.staticanalysistestapp.UIUpdate");
//        Object[] sootClasses = Scene.v().getApplicationClasses().toArray();
//        SootClass sootClass2 = null;
//        for(Object thisObject : sootClasses)
//        {
//            SootClass thisClass = (SootClass) thisObject;
//            if(thisClass.toString().contains("com.example.staticanalysistestapp.UIUpdate")) {
//                sootClass2 = thisClass;
//                break;
//            }
//        }
		System.out.println("-------------------------");
        List<Thread> threads = new ArrayList<>();
        for(int i = 0; i< listTargetClass.size(); i++)
        {
            System.err.println(listTargetClass.get(i).toString());
            output.add(listTargetClass.get(i).toString());
            SootClass thisClass = Scene.v().getSootClass( listTargetClass.get(i).toString());
//            System.out.println("class : "+thisClass.toString());
//            System.out.println("class1 : "+sootClass1.toString());
//            System.out.println("class2 : " + sootClass2.toString());
//            System.out.println("class equal? " + sootClass1.equals(thisClass));
//            System.out.println("class equal? " + sootClass2.equals(thisClass));
            List<SootMethod> listMethod = thisClass.getMethods();
//            thisClass2 = thisClass;
////            List<SootMethod> listMethods = thisClass.getMethods();
            for(int j = 0; j< listMethod.size(); j++)
            {
                SootMethod thisMethod = listMethod.get(j);
                List<CallEdge> subList = getAllReachableMethodsToList(thisMethod);

//                        lock.acquire();
                threadNum.incrementAndGet();
                output.add("\n" + thisMethod.toString());
                System.err.println(thisMethod.toString());
                printAllEdges(subList);
//                        System.out.println("\nUI update?");

                output.add("UI update?");

                for (CallEdge thisEdge : subList) {
                    for (int k = 0; k < listTargetMethod.size(); k++) {
                        if (listTargetMethod.get(k).toString().equals(thisEdge.getTgtMethodString())) {
                            output.add(thisMethod + " to " + thisEdge.getTgtMethod() + "true");
                            String output = thisMethod + " to " + thisEdge.getTgtMethod() + "\n";
                            outputBuffer.add(output.getBytes(StandardCharsets.UTF_8));
                            System.out.println(output);
//                                    System.out.println(thisEdge.getTgtMethod() + " true");
                        }
                    }

                }



                threadNum.decrementAndGet();
//                        lock.release();
                System.out.println("Thread done");

//                Thread workerThread = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        List<CallEdge> subList = getAllReachableMethodsToList(thisMethod);
//
////                        lock.acquire();
//                        threadNum.incrementAndGet();
//                        output.add("\n" + thisMethod.toString());
//                        System.err.println(thisMethod.toString());
//                        printAllEdges(subList);
////                        System.out.println("\nUI update?");
//
//                        output.add("UI update?");
//
//                        for (CallEdge thisEdge : subList) {
//                            for (int k = 0; k < listTargetMethod.size(); k++) {
//                                if (listTargetMethod.get(k).toString().equals(thisEdge.getTgtMethodString())) {
//                                    output.add(thisMethod + " to " + thisEdge.getTgtMethod() + "true");
//                                    String output = thisMethod + " to " + thisEdge.getTgtMethod() + "\n";
//                                    outputBuffer.add(output.getBytes(StandardCharsets.UTF_8));
//                                    System.out.println(output);
////                                    System.out.println(thisEdge.getTgtMethod() + " true");
//                                }
//                            }
//
//                        }
//
//
//
//                        threadNum.decrementAndGet();
////                        lock.release();
//                        System.out.println("Thread done");
//                        return;
//                    }
//                });
////                if (i == listTargetMethod.size() - 1 && j == listMethod.size() - 1)
////                    System.out.println("last Thread : " + thisMethod);
//                threads.add(workerThread);
//                workerThread.start();


            }
            output.add("------------------------------------------------------");
			System.err.println("------------------------------------------------------");
        }
//        lock.acquire();
        while(threadNum.get() != 0)
        {
            System.err.println("thread not ending"+ threadNum.get());
            for(int i = 0; i< 10000;i++);
        }
        for(int i = 0; i< 10000;i++);
        System.out.println("thread Execution complete");
//        lock.acquire();
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream= new FileOutputStream(outputFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        for(String thisOutput : output)
        {
            System.out.println(thisOutput);
        }
        for(byte[] buffer : outputBuffer)
        {
            fileOutputStream.write(buffer);
        }
        for(SootMethod targetMethod : listTargetMethod)
        {
            String string = targetMethod.toString() + " to " + targetMethod.toString()+"\n";
            fileOutputStream.write(string.getBytes(StandardCharsets.UTF_8));
        }
        fileOutputStream.flush();
        fileOutputStream.close();
        System.out.println("StaticAnalysis Complete!!");
//        lock.release();
//        System.exit(0);
//        System.out.println("class1 : "+sootClass1.toString());
//        System.out.println("class2 : " + sootClass2.toString());
//        System.out.println("class equal? " + sootClass1.equals(sootClass2));
//        System.out.println("-------------------------");
//
//        SootMethod sootMethod1 = sootClass1.getMethodByName("uiUpdate");
//        SootMethod sootMethod2 = sootClass2.getMethods().get(1);
//        System.out.println("method1 : "+sootMethod1.toString());
//        System.out.println("method2 : " + sootMethod2.toString());
//        System.out.println("method equals? : " +sootMethod1.equals(sootMethod2));
//
//        System.out.println("class 1 : " + thisClass1);
//        System.out.println("class 2 : " + thisClass2);
//        System.out.println("equal? : " + thisClass1.equals(thisClass2));
//        System.out.println("method 1 : " + method1);
//
//        System.out.println("method 2 : " + method2);
//
//
//        System.out.println("equal? : " + method1.equals(method2));
//
////        List<CallEdge> listCallEdges = getAllReachableMethodsToList(app.getDummyMainMethod());
////        printAllEdges(listCallEdges);
//
//        System.out.println("---------------------------------------------------------------------\n");
//
//        for(SootClass sootClass : Scene.v().getApplicationClasses())
//        {
////            System.out.println(sootClass);
//            if(sootClass.toString().equals("com.example.staticanalysistestapp.UIUpdate"))
//            {
////                System.out.println("change");
//                thisClass1 = sootClass;
//                break;
//            }
//        }
//        System.out.println("---------------------------------------------------------------------\n");
//        thisClass2 = Scene.v().getSootClass("com.example.staticanalysistestapp.UIUpdate");
//        System.out.println("class 1 : " + thisClass1);
//        System.out.println("class 2 : " + thisClass2);
//        System.out.println("equal? : " + thisClass1.equals(thisClass2));

    }

    // A Breadth-First Search algorithm to get all reachable methods from initialMethod in the callgraph
    // The output is a map from reachable methods to their parents
    public static Map<SootMethod, SootMethod> getAllReachableMethods(SootMethod initialMethod){
        CallGraph callgraph = Scene.v().getCallGraph();
        List<SootMethod> queue = new ArrayList<>();
        queue.add(initialMethod);
        Map<SootMethod, SootMethod> parentMap = new HashMap<>();
        parentMap.put(initialMethod, null);
        for(int i=0; i< queue.size(); i++){
            SootMethod method = queue.get(i);
            for (Iterator<Edge> it = callgraph.edgesOutOf(method); it.hasNext(); ) {
                Edge edge = it.next();
                SootMethod childMethod = edge.tgt();
                if(parentMap.containsKey(childMethod))
                    continue;
                parentMap.put(childMethod, method);
                queue.add(childMethod);
            }
        }
        return parentMap;
    }

    public static void printAllEdges(List<CallEdge> listCallEdges) {
        for(int i = 0; i< listCallEdges.size(); i++)
        {
//            System.out.println(listCallEdges.get(i).toString());
            output.add(listCallEdges.get(i).toString());
        }
    }
    public static List<CallEdge> getAllReachableMethodsToList(SootMethod initialMethod){
        CallGraph callgraph = Scene.v().getCallGraph();
        List<SootMethod> queue = new ArrayList<>();
        queue.add(initialMethod);
        List<CallEdge> listCallEdges = new ArrayList<>();
//        listCallSources.add(null);
//        listCallTargets.add(initialMethod);
        listCallEdges.add(new CallEdge(null,initialMethod));
//        System.out.println("NULL to " +initialMethod);
//        listCallEdges.add(new CallEdge(null,initialMethod));
        for(int i=0; i< queue.size(); i++){
            SootMethod method = queue.get(i);
            for (Iterator<Edge> it = callgraph.edgesOutOf(method); it.hasNext(); ) {
                Edge edge = it.next();
                SootMethod childMethod = edge.tgt();
                CallEdge callEdge = new CallEdge(method,childMethod);
                if(listCallEdges.contains(callEdge))
                {
                    continue;
                }
                listCallEdges.add(callEdge);
//                System.out.println(method +" to "+childMethod);
//                listCallSources.add(method);
//                listCallTargets.add(childMethod);
//                listCallEdges.add(new CallEdge(method,childMethod));

                queue.add(childMethod);
            }
        }
        return listCallEdges;
    }
    public static List<CallEdge> getReachableMethodsSubList(List<CallEdge> callEdges,SootMethod initialMethod)
    {
        List<SootMethod> queue = new ArrayList<>();
        queue.add(initialMethod);
        List<CallEdge> subList = new ArrayList<>();
        subList.add(new CallEdge(null,initialMethod));
        for(int i = 0; i< queue.size();i++)
        {
            SootMethod thisMethod = queue.get(i);
            for(int j = 0; i < callEdges.size(); j++)
            {
                if(callEdges.get(i).getSrcMethodString().equals(thisMethod))
                {	

					CallEdge thisEdge = new CallEdge(thisMethod,callEdges.get(i).getTgtMethod());
					if(subList.contains(thisEdge))
					{
						continue;
					}
                    subList.add(thisEdge);
                    queue.add(callEdges.get(i).getTgtMethod());
                }
            }
        }
        return subList;
    }

    public static Boolean getPossiblePath(Map<SootMethod,SootMethod> reachableMap,SootMethod from, SootMethod to)
    {
        return false;
    }

//    public static String getPossiblePath(Map<SootMethod, SootMethod> reachableParentMap, SootMethod it) {
//        String possiblePath = null;
//        while(it != null){
//            String itName = it.getDeclaringClass().getShortName()+"."+it.getName();
//            if(possiblePath == null)
//                possiblePath = itName;
//            else
//                possiblePath = itName + " -> " + possiblePath;
//            it = reachableParentMap.get(it);
//        } return possiblePath;
//    }

}
