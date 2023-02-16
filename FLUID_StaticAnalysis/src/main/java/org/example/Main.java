package org.example;


import com.sun.mail.iap.ByteArray;
import scala.concurrent.Lock;
import scala.util.control.TailCalls;
import soot.*;
import soot.jimple.FieldRef;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.internal.JArrayRef;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JInstanceFieldRef;
import soot.jimple.internal.JimpleLocal;
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
    static AtomicInteger threadStartNum = new AtomicInteger(0);
    static AtomicInteger threadEndNum = new AtomicInteger(0);
    static String outputFilePath = "/home/ywha/WEB_FLUID/FLUID_StaticAnalysis/output/StaticAnalysisResult.log";
    static List<byte[]> outputBuffer = new ArrayList<>();
    static HashMap<String, HashSet<String>> result = new HashMap<String, HashSet<String>>();
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
                thisMethod.toString().equals("<android.widget.TextView: void setText(int,android.widget.TextView$BufferType)>") ||
                thisMethod.toString().equals("<android.widget.TextView: void setTextColor(int)>")
                )
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

    private static void fillFieldMap()
    {

        for(SootMethod thisMethod : listTargetMethod)
        {
            List<SootMethod> queue = new ArrayList<>();
            queue.add(thisMethod);
//            for(SootClass thisClass : Scene.v().getApplicationClasses())
//            {
//                for(SootMethod thatMethod : thisClass.getMethods())
//                {
//                    if(thatMethod.toString().equals(thisMethod.toString()))
//                    {
//                        queue.add(thatMethod);
//                        List<CallEdge> callGraph = getAllReachableMethodsToList( Scene.v().getSootClass(thisMethod.getDeclaringClass().toString()).getMethod(thisMethod.getSubSignature()) );
//                        printAllEdges(callGraph,1);
//                        for(CallEdge edge : callGraph)
//                        {
//                            queue.add(edge.getTgtMethod());
//                        }
//                    }
//                }
//            }



            for(int i = 0; i< queue.size(); i++)
            {
                SootMethod nextMethod = queue.get(i);
                HashSet<Value> localForField = new HashSet<Value>();
                Body body = thisMethod.retrieveActiveBody();

                System.out.println(body.toString());


                Local thisLocal = null;
                try {
                    thisLocal = body.getThisLocal();
                    localForField.add(thisLocal);
                } catch (Exception e) {
                    System.out.println(body);
                }

                List<Local> params = null;
                try {
                    params = body.getParameterLocals();
                    for (Local param : params)
                        localForField.add(param);
                } catch (Exception e) {}
                Iterator iter = thisMethod.getActiveBody().getUnits().iterator();

                while(iter.hasNext())
                {
//                System.out.println("inside loop");
                    Stmt stmt = (Stmt) iter.next();
                    showStmtForDebug(thisMethod, stmt, "setState");

                    if (stmt instanceof JAssignStmt) {

                        JAssignStmt aStmt = (JAssignStmt)stmt;
                        boolean shouldPeek = false;

                        if (aStmt.containsFieldRef()) {

                            // Direct assignment case.
                            // We should peek the member field that is
                            // 1) a member filed of thisLocal, 2) a static field,
                            // or 3) a member field of thisLocal's member field (direct access)
                            FieldRef fieldRef = aStmt.getFieldRef();
                            if ((fieldRef instanceof JInstanceFieldRef
                                    && ((JInstanceFieldRef)fieldRef).getBase() == thisLocal)) {
                                shouldPeek = true;
                            }
                            else if (fieldRef instanceof StaticFieldRef) {
                                shouldPeek = true;
                            }
                            else if (fieldRef instanceof JInstanceFieldRef) {
                                // In method src, base has been already assigned to Local
                                Value base = ((JInstanceFieldRef)fieldRef).getBase();
                                if (localForField.contains(base))
                                    shouldPeek = true;
                            }

                            if (shouldPeek) {

                                SootField field = fieldRef.getField();

                                System.out.println("	" + field.getSignature());
                                Value left = aStmt.getLeftOp();
                                assert left instanceof JimpleLocal;
                                localForField.add(left);

                                // Store result
                                String clazzName = field.getDeclaringClass().getName();
                                HashSet<String> fieldSet = result.get(clazzName);
                                fieldSet = result.get(clazzName);
                                if (fieldSet == null) {
                                    fieldSet = new HashSet<String>();
                                    result.put(clazzName, fieldSet);
                                }
                                fieldSet.add(field.getName());

                            }
                        }
                        else {
                            // Transitive assignment case
                            Value right = aStmt.getRightOp();
                            if (right instanceof JArrayRef) {
                                // Transitively access to an element of th array-typed member field
                                Value base = ((JArrayRef)right).getBase();
                                if (localForField.contains(base))
                                    shouldPeek = true;
                            }
                            else if (right instanceof JimpleLocal && localForField.contains(right))
                                shouldPeek = true;

                            if (shouldPeek) {
                                Value left = aStmt.getLeftOp();
                                assert left instanceof JimpleLocal;
                                localForField.add(left);
                            }
                        }
                    }

                }
            }

        }
    }
    public static void showStmtForDebug(SootMethod method, Stmt stmt, String methodName) {
        if (method.getName().equals(methodName)) {
            System.out.println(stmt);
            if (stmt instanceof JAssignStmt) {
                Value left = ((JAssignStmt)stmt).getLeftOp();
                Value right = ((JAssignStmt)stmt).getRightOp();
                System.out.println(left.getClass() + "		" + right.getClass());
            }
        }
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
            if(thisClass.getPackageName().contains("androidx") ||
//                    thisClass.getPackageName().contains("android.widget")
//
                thisClass.getPackageName().contains("com.google.android")
                )
            {
                continue;
            }

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
//        for(SootClass thisClass : Scene.v().getApplicationClasses())
//        {
//            if(thisClass.toString().contains("MainActivity"))
//                listTargetClass.add(thisClass);
//        }
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

    public static boolean skipField(SootField field) {
        Type type = field.getType();
        if (type instanceof PrimType)
            return true;
        if (type instanceof ArrayType
                && ((ArrayType)type).getElementType() instanceof PrimType)
            return true;
        if (type instanceof ArrayType
                && ((ArrayType)type).getElementType().toString().equals("java.lang.String"))
            return true;
        if (field.getDeclaringClass().getName().equals("android.view.ViewGroup")
                && field.getName().equals("mChildren"))
            return true;
        if (type.toString().equals("java.lang.String")
                || type.toString().equals("java.lang.Byte")
                || type.toString().equals("java.lang.Short")
                || type.toString().equals("java.lang.Integer")
                || type.toString().equals("java.lang.Long")
                || type.toString().equals("java.lang.Float")
                || type.toString().equals("java.lang.Double")
                || type.toString().equals("java.lang.Character")
                || type.toString().equals("java.lang.Boolean")
                || type.toString().equals("android.fluid.FLUIDManager")
                || type.toString().equals("android.view.RenderNode")
                || type.toString().equals("android.view.View$AttachInfo")
                || type.toString().equals("android.view.View")
                || type.toString().equals("android.view.ViewGroup")
                || type.toString().equals("android.view.ViewParent")
                || type.toString().equals("android.os.IBinder")
                || type.toString().equals("android.os.Binder")
                || type.toString().equals("android.os.BinderProxy")
                || type.toString().equals("android.app.ActivityThread")
                || type.toString().equals("android.app.Activity")
                || type.toString().equals("android.fluid.kryo.Kryo")
                || type.toString().equals("android.content.Context")
                || type.toString().equals("android.app.ContextImpl")
        )
            return true;
        return false;
    }

    static void printFieldMap()
    {
        System.out.println("============RESULT============");
        System.out.println();
        for (String clazz : result.keySet()) {
            HashSet<String> fields = result.get(clazz);
            System.out.println("[" + clazz + "]");
            for (String field : fields)
                System.out.println("	" + field);
            System.out.println();
            System.out.println();
        }
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


        fillListTargetClass();

//        fillListTargetActivityClass();
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
//        for(SootClass thisClass : Scene.v().getApplicationClasses())
//        {
//            if(thisClass.toString().equals("com.simplemobiletools.commons.views.MyEditText"))
//                for(SootMethod thisMethod : thisClass.getMethods())
//                {
//                    List<CallEdge> subGraph = getAllReachableMethodsToList(thisMethod);
//                    printAllEdges(subGraph,1);
//                }
//        }
//
//        fillFieldMap();
//        printFieldMap();

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
                if(thisMethod.getName().contains("init")
                    || thisMethod.getName().contains("clinit")
                )
                    continue;

                Thread workerThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        lock.acquire();
                        threadStartNum.incrementAndGet();
                        System.out.println(threadStartNum.get()+" start");
                        lock.release();
                        List<CallEdge> subList = getAllReachableMethodsToList(thisMethod);

//                        lock.acquire();

                        output.add("\n" + thisMethod.toString());
                        System.err.println(thisMethod.toString());
//                        printAllEdges(subList);
//                        System.out.println("\nUI update?");

                        output.add("UI update?");

                        for (CallEdge thisEdge : subList) {
                            for (int k = 0; k < listTargetMethod.size(); k++) {
                                if (listTargetMethod.get(k).toString().equals(thisEdge.getTgtMethodString())) {
                                    output.add(thisMethod + " to " + thisEdge.getTgtMethod() + " true");
                                    String output = thisMethod + " to " + thisEdge.getTgtMethod() + "\n";
                                    byte[] outputs = output.getBytes(StandardCharsets.UTF_8);
                                    if( listBytesContains(outputBuffer,outputs) )
                                        continue;
                                    outputBuffer.add( outputs );
                                    System.out.println(output);
//                                    System.out.println(thisEdge.getTgtMethod() + " true");
                                }
                            }

                        }


                        lock.acquire();
//                        System.out.println("thread + "+ threadEndNum.get());
                        threadEndNum.getAndIncrement();
//                        lock.release();
                        System.out.println("Thread done"+ threadEndNum.get());
                        lock.release();
                        return;
                    }
                });
//                if (i == listTargetMethod.size() - 1 && j == listMethod.size() - 1)
//                    System.out.println("last Thread : " + thisMethod);
                threads.add(workerThread);
                workerThread.start();


            }
            output.add("------------------------------------------------------");
			System.err.println("------------------------------------------------------");
        }
//        lock.acquire();
        System.out.println("main Thread waiting : "+ threadEndNum.get());
        lock.acquire();
        while(threadEndNum.get() != threadStartNum.get())
        {
            lock.release();
//            System.err.println("thread not ending"+ (threadEndNum.get()-threadStartNum.get()));
            for(int i = 0; i< 100000;i++);
            lock.acquire();
        }
        lock.release();
        for(int i = 0; i< 10000;i++);
        System.out.println("thread Execution complete");
//        lock.acquire();
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream= new FileOutputStream(outputFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
//        for(String thisOutput : output)
//        {
//            System.out.println(thisOutput);
//        }
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

    public static void printAllEdges(List<CallEdge> listCallEdges,int flag) {
        for(int i = 0; i< listCallEdges.size(); i++)
        {
            if((flag & 1) == 1)
            {
                System.out.println(listCallEdges.get(i).toString());
            }
            if((flag &  2) == 2)
            {
                output.add(listCallEdges.get(i).toString());
            }

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
                    continue;
//                boolean isDone= false;
//                for(CallEdge thisEdge : listCallEdges)
//                {
//                    System.out.println(callEdge.toString() + "\n" + thisEdge.toString() + "\n" + thisEdge.equals(callEdge) + "\n");
//                    if(thisEdge.equals(callEdge))
//                        isDone = true;
//
//                }
//                if(isDone)
//                {
//                    continue;
//                }
                listCallEdges.add(callEdge);
//                System.out.println("getAllReachableMethodsToList working... for "+initialMethod.getName() + " -> " + childMethod);
//                listCallSources.add(method);
//                listCallTargets.add(childMethod);
//                listCallEdges.add(new CallEdge(method,childMethod));
                if( skipMethod(childMethod) )
                    continue;
                else
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
    public static boolean skipMethod(SootMethod method) {
        SootClass clazz = method.getDeclaringClass();
        String methodName = method.getName();
        if (clazz.getName().equals("java.lang.Object")
                || clazz.getName().equals("java.lang.Class")
                || clazz.getName().startsWith("android.view.LayoutInflater")
                || clazz.getName().startsWith("android.view.ViewRootImpl")
                || clazz.getName().startsWith("android.view.Choreographer")
                || clazz.getName().startsWith("android.app.ActivityThread")
                || clazz.getName().startsWith("com.android.internal.policy")
                || clazz.getName().equals("android.app.Activity")
                || clazz.getName().equals("android.app.ContextImpl")
                || clazz.getName().equals("android.content.Context")
                || clazz.getName().equals("android.content.res.Resources")
                || clazz.getName().equals("android.os.IBinder")
                || clazz.getName().equals("android.os.Binder")
                || clazz.getName().equals("android.os.BinderProxy")
                || clazz.getName().equals("java.lang.StringBuilder")
                || clazz.getName().equals("java.lang.Byte")
                || clazz.getName().equals("java.lang.Short")
                || clazz.getName().equals("java.lang.Integer")
                || clazz.getName().equals("java.lang.Long")
                || clazz.getName().equals("java.lang.Float")
                || clazz.getName().equals("java.lang.Double")
                || clazz.getName().equals("java.lang.Character")
                || clazz.getName().equals("java.lang.Boolean")
                || clazz.getName().equals("java.lang.String")
                || (clazz.getPackageName().startsWith("com.android") && !clazz.getPackageName().startsWith("com.android.internal"))
                || clazz.getPackageName().startsWith("sun")
                || clazz.getPackageName().startsWith("android.fluid")
                || clazz.getPackageName().startsWith("org.")
                || clazz.getPackageName().startsWith("android.net")
                || clazz.getPackageName().startsWith("android.media.session")
                || clazz.getPackageName().startsWith("android.telecom")
                || clazz.getPackageName().startsWith("sun.nio.ch")
                || clazz.getPackageName().startsWith("jdk.net")
                || clazz.getPackageName().startsWith("android.icu")
                || clazz.getPackageName().startsWith("java.net")
                || clazz.getPackageName().startsWith("java.nio")
                || clazz.getPackageName().startsWith("java.time")
        )
            return true;
        if (methodName.equals("<init>")
                || methodName.equals("<clinit>")
                || methodName.equals("addView")
                || methodName.equals("addViewInLayout")
                || methodName.equals("addViewInner")
                || methodName.equals("removeView")
                || methodName.equals("removeAllViews")
                || methodName.equals("removeDetachedView")
                || methodName.equals("removeAllViewsInLayout")
                || methodName.equals("removeViewInternal")
                || methodName.equals("finishAnimatingView")
                || methodName.equals("run")
                || methodName.equals("post")
                || methodName.equals("postDelayed")
                || methodName.equals("toString")
                || methodName.equals("equals")
                || methodName.equals("clone")
        )
            return true;

        return false;
    }
    public static boolean listBytesContains(List<byte[]> list, byte[] target)
    {
        for (byte[] b : list)
            if (Arrays.equals(b, target)) return true;
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
