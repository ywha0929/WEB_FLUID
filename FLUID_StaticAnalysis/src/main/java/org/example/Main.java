package org.example;


import scala.util.control.TailCalls;
import soot.G;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.options.Options;

import java.io.File;
import java.util.*;

public class Main {
    private final static String USER_HOME = System.getProperty("user.home");
    private static String androidJar = USER_HOME + "/Library/Android/sdk/platforms";
    static String androidAPKPath = System.getProperty("user.dir") + File.separator + "apks";
    //    static String apkPath = androidDemoPath + File.separator + "/st_demo.apk";
//    static String apkPath = androidDemoPath + File.separator + "/com.simplemobiletools.notes.apk";
//   static String apkPath = androidAPKPath + File.separator + "com.simplemobiletools.calculator_5.8.2-51_minAPI21(nodpi)_apkmirror.com.apk";
// static String apkPath = androidAPKPath + File.separator + "FalseNegativeTestApp.apk";
    static String apkPath = androidAPKPath + File.separator + "StaticAnalysisTestApp.apk";
//    static String apkPath = androidAPKPath + File.separator + "calculator.apk";
    static String outputPath = USER_HOME + File.separator + "output";
    static List<SootMethod> listTargetMethod = new ArrayList<>();
    static List<SootClass> listTargetClass = new ArrayList<>();
    static Map<SootMethod,SootMethod> foundUIUpdateMethods = new HashMap<>();
    static List<SootMethod> listCallSources = new ArrayList<>();
    static List<SootMethod> listCallTargets = new ArrayList<>();
//    static List<CallEdge> listCallEdges = new ArrayList<>();


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

    private static void fillListTargetClass() //find custom UI classes
    {
        System.out.println("preparing listTargetClass ... ");
        Object[] classApplication = Scene.v().getApplicationClasses().toArray();
        SootClass classView = Scene.v().getSootClass("android.view.View");
//        List<SootClass> listVistedClass = new ArrayList<>();
        for(int i= 0; i< classApplication.length; i++)
        {
            SootClass thisClass = (SootClass) classApplication[i];
            if( thisClass.getPackageName().contains("androidx") ||
                thisClass.getPackageName().contains("android.widget") ||
                thisClass.getPackageName().contains("com.google.android"))
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

    public static void main(String[] args){

        if(System.getenv().containsKey("ANDROID_HOME"))
            androidJar = System.getenv("ANDROID_HOME")+ File.separator+"platforms";

        setupSoot(androidJar,apkPath,outputPath);

        fillListTargetMethod();
        fillListTargetClass();
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

        for(SootClass thisClass : listTargetClass)
        {
            List<SootMethod> listMethods = thisClass.getMethods();
            for(SootMethod thisMethod : listMethods)
            {
                List<CallEdge> listCallEdges = getAllReachableMethodsToList(thisMethod);
                for(int i = 0; i< listCallEdges.size(); i++)
                {
                    System.out.println("thisMethod : " + thisMethod);
                    printAllEdges(listCallEdges);
                    System.out.println("---------------------------------------------------------------------\n");
                    if(listTargetMethod.contains( listCallEdges.get(i).targetMethod ) == true)
                    {
                        System.out.println( thisMethod + " to " + listCallEdges.get(i).targetMethod );
                    }
                }
            }
        }
        List<CallEdge> listCallEdges = getAllReachableMethodsToList(app.getDummyMainMethod());
        printAllEdges(listCallEdges);

        System.out.println("---------------------------------------------------------------------\n");

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
            System.out.println(listCallEdges.get(i).toString());
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
