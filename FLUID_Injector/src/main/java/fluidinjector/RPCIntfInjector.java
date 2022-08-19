package fluidinjector;

import soot.*;
import soot.jimple.*;
import soot.util.Chain;
import soot.util.EmptyChain;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RPCIntfInjector extends BodyTransformer {
	boolean isInsert = true;
	boolean isAnalize = true;
	// final static int MAINACTIVITY_INDEX2 = 1918;
	static int MAINACTIVITY_INDEX = 1391;
	final static String TMP_DIR_PATH = "/data/local/tmp/";
	final static String FLUID_LIB_PATH = "/data/local/tmp/fluidlib.apk";
	final static String FLUID_MAIN_CLASS = "com.hmsl.fluidlib.FLUIDMain";
	final static String FLUID_PACKAGE_NAME = "com.hmsl.fluidmanager";
	final static String FLUID_SERVICE_NAME = "com.hmsl.fluidmanager.FLUIDManagerService";
	static String MAINACTIVITY_CLASS_NAME;
	static String MAIN_PACKAGE_NAME;
	public RPCIntfInjector(String namePackage) {
		
		super();
		MAIN_PACKAGE_NAME = namePackage;
		namePackage = namePackage+".MainActivity";
		MAINACTIVITY_CLASS_NAME = namePackage;
	}

	@Override
	protected void internalTransform(Body b, String s, Map<String, String> map) {
		JimpleBody body = (JimpleBody) b;

		if (AndroidUtil.isAndroidMethod(b.getMethod()))
			return;
		if (isAnalize) {
			isAnalize = false;
			printClasses(body);

		}

		if (isInsert) {
			isInsert = false;
			Object[] arr = Scene.v().getApplicationClasses().toArray();
			for (int i = 0; i < arr.length; i++) {
				if (arr[i].toString().equals(MAINACTIVITY_CLASS_NAME)) {
					MAINACTIVITY_INDEX = i;
				}
			}
			SootClass a = (SootClass) arr[MAINACTIVITY_INDEX];// MainActivity

//			SootClass aa = Scene.v().getSootClass("dalvik.system.DexClassLoader");
//			SootField testfld = new SootField("dex", aa.getType());
//			a.addField(testfld);

			InstrumentUtil.addField(a, "dex", RefType.v("dalvik.system.DexClassLoader"),
					Modifier.PUBLIC | Modifier.STATIC);
			InstrumentUtil.addField(a, "objFluidInterface", RefType.v("java.lang.Object"),
					Modifier.PUBLIC | Modifier.STATIC);

		}
		if (b.getMethod().getName().equals("onCreate")) {
			System.out.println("==== before ====");
			System.out.println(b);
			injectCode((JimpleBody) b);
			// injectfield((JimpleBody) b);
			// injectClassLoader((JimpleBody) b);
			System.out.println("==== after ====");
			System.out.println(b);
		}
		if (b.getMethod().getName().equals("onLongClick")) {
			System.out.println("==== before ====");
			System.out.println(b);
			injectTriggerCode((JimpleBody) b);
			// injectClassLoader((JimpleBody) b);
			System.out.println("==== after ====");
			System.out.println(b);
		} else if (b.getMethod().getName().equals("onClick")) {
			System.out.println("==== before ====");
			System.out.println(b);
			System.out.println("onClick");
//			printLocals((JimpleBody)b);
			injectUpdateCode((JimpleBody) b);

			System.out.println("==== after ====");
			System.out.println(b);
		}
	}

	void printClasses(JimpleBody body) {
		Object[] arr = Scene.v().getApplicationClasses().toArray();
		for (int i = 0; i < arr.length; i++) {
			System.out.println("Class [" + i + "] : " + arr[i].toString());
		}
	}

	void printLocals(JimpleBody body) {
		Object[] arr = body.getLocals().toArray();
		for (int i = 0; i < arr.length; i++) {
			System.out.println("Local [" + i + "] : " + arr[i].toString());
		}
	}

	void injectfield(JimpleBody body) {
		UnitPatchingChain units = body.getUnits();
		Object[] test = units.toArray();
		for (int i = 0; i < test.length; i++) {
			System.out.println("unit : [" + i + "] : " + test[i].toString());
		}
	}

	void injectUpdateCode(JimpleBody body) {
		UnitPatchingChain units = body.getUnits();
		// List<Unit> generated = new ArrayList<>();
		Local thisVar = body.getThisLocal();

		Local exceptionVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Exception"));
		Local viewVar = InstrumentUtil.generateNewLocal(body, RefType.v("android.view.View"));
		Local methodVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.reflect.Method"));
		Local clazzVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Class"));
		Local dexLoaderVar = InstrumentUtil.generateNewLocal(body, RefType.v("dalvik.system.DexClassLoader"));
		Local classVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Class"));
		Local classLoaderVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.ClassLoader"));
		Local classStringVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Class"));
		Local classViewVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Class"));
		Local widgetnameVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.String"));
		Local eviewVar = InstrumentUtil.generateNewLocal(body, RefType.v("android.view.View"));
		Local signatureVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.String"));
		Local classArrayVar = InstrumentUtil.generateNewLocal(body, ArrayType.v(RefType.v("java.lang.Class"), 1));
		Local objectArrayVar = InstrumentUtil.generateNewLocal(body, ArrayType.v(RefType.v("java.lang.Object"), 1));
		Local paramIntVar = InstrumentUtil.generateNewLocal(body, IntType.v());
		Local paramIntegerVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Integer"));
		Local objectFluidInterfaceVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Object"));

		// get parameter
		// generated.add(Jimple.v().newAssignStmt(viewVar,body.getParameterLocal(0)));
		System.out.println("onClick");
		printLocals(body);

		// edit here

		Object[] unitarray = units.toArray();
		for (int i = 0; i < unitarray.length; i++) {
			if (unitarray[i].toString().contains("setText")) {
				List<Unit> generated = new ArrayList<>();
				generated.add(Jimple.v().newAssignStmt(viewVar, body.getParameterLocal(0)));
				Object[] arrayClasses = Scene.v().getApplicationClasses().toArray();
				SootClass classMainActivity = (SootClass) arrayClasses[MAINACTIVITY_INDEX];
				SootField fieldDex = classMainActivity.getFieldByName("dex");
				generated.add(Jimple.v().newAssignStmt(dexLoaderVar, Jimple.v().newStaticFieldRef(fieldDex.makeRef())));
				SootField fieldFluidInterface = classMainActivity.getFieldByName("objFluidInterface");
				generated.add(Jimple.v().newAssignStmt(objectFluidInterfaceVar,
						Jimple.v().newStaticFieldRef(fieldFluidInterface.makeRef())));

				generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.ClassLoader",
						"java.lang.Class loadClass(java.lang.String)", dexLoaderVar, classVar,
						StringConstant.v(FLUID_MAIN_CLASS)));
				Unit tryBegin = generated.get(generated.size() - 1);

				// create class array for getDeclaredMethod
				SootClass cls = Scene.v().getSootClass("java.lang.Class");
				generated.add(Jimple.v().newAssignStmt(classArrayVar,
						Jimple.v().newNewArrayExpr(cls.getType(), IntConstant.v(2))));

				// insert class to class array
				generated.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(classArrayVar, IntConstant.v(0)),
						ClassConstant.v("Ljava/lang/String;")));
				generated.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(classArrayVar, IntConstant.v(1)),
						ClassConstant.v("Landroid/view/View;")));

				// get runupdate
				generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Class",
						"java.lang.reflect.Method getDeclaredMethod(java.lang.String,java.lang.Class[])", classVar,
						methodVar, StringConstant.v("runUpdate"), classArrayVar));

				// make object
				SootClass cls2 = Scene.v().getSootClass("java.lang.Object");

				// get view
				Object[] locals = body.getLocals().toArray();
				String sig = unitarray[i].toString();
				System.out.println("sig : " + sig);

				// get base for ui_update method unit
				String sigfortok = new String(sig);
				String[] toks = sigfortok.split(" ");
//				for(int k = 0; k<toks.length;k++)
//				{
//					System.out.println(toks[k]);
//				}
				char[] chararr = toks[1].toCharArray();
				String local = "";
				for (int k = 0; k < chararr.length; k++) {
					if (chararr[k] != '.') {
						local = local + chararr[k];
					} else
						break;
				}
				System.out.println("local : " + local);
				for (int j = 0; j < locals.length; j++) {
					if (locals[j].toString().equals(local))

						generated.add(Jimple.v().newAssignStmt(viewVar, (Local) locals[j]));

				}
				// generated.add(Jimple.v().newAssignStmt(viewVar, (Local)locals[0]));
				generated.add(Jimple.v().newAssignStmt(signatureVar, StringConstant.v(sig)));

				generated.add(Jimple.v().newAssignStmt(objectArrayVar,
						Jimple.v().newNewArrayExpr(cls2.getType(), IntConstant.v(2))));
				generated.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(objectArrayVar, IntConstant.v(0)),
						signatureVar));
				generated.add(
						Jimple.v().newAssignStmt(Jimple.v().newArrayRef(objectArrayVar, IntConstant.v(1)), viewVar));

				// invoke runupdate
				generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.reflect.Method",
						"java.lang.Object invoke(java.lang.Object,java.lang.Object[])", methodVar, null,
						objectFluidInterfaceVar, objectArrayVar));
				generated.add(Jimple.v().newReturnVoidStmt());
				Unit tryEnd = generated.get(generated.size() - 1);
				CaughtExceptionRef exceptionRef = soot.jimple.Jimple.v().newCaughtExceptionRef();
				Unit catchBegin = Jimple.v().newIdentityStmt(exceptionVar, exceptionRef);
				generated.add(catchBegin);
				generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Throwable",
						"void printStackTrace()", exceptionVar, null));
				
				SootClass exceptionClass = Scene.v().getSootClass("java.lang.Exception");
				Trap trap = soot.jimple.Jimple.v().newTrap(exceptionClass, tryBegin, tryEnd, catchBegin);
				units.insertBefore(generated, units.getSuccOf((Unit)unitarray[i]));
				body.getTraps().add(trap);
				
				
			}

		}
		// get ui_update method unit

		// get dexclassloader from field

//		Object[] unitarray = units.toArray();
//		for(int i = 0; i<unitarray.length;i++)
//		{
//			if(unitArray[i].toString().contains("setText"))
//			{
//				List<Unit> tobeInserted = new ArrayList<Unit>();
//				for(Unit u :generated)
//				{
//					tobeInserted.add((Unit) u.clone());
//				}
//				units.insertBefore(tobeInserted,units.getSuccOf((Unit)unitArray[i]));
//			}
//		}
//		units.insertBefore(generated, units.getLast());
		body.validate();

//		Unit tryEnd = units.getLast(); //return
//
//		// insert try-catch statement
//		CaughtExceptionRef exceptionRef = soot.jimple.Jimple.v().newCaughtExceptionRef();
//		Unit catchBegin = Jimple.v().newIdentityStmt(exceptionVar, exceptionRef);
//		units.add(catchBegin);
//		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Throwable", 
//			"void printStackTrace()", exceptionVar, null));
//		
//		units.add(Jimple.v().newReturnVoidStmt());
//		SootClass exceptionClass = Scene.v().getSootClass("java.lang.Exception");
//		Trap trap = soot.jimple.Jimple.v().newTrap(exceptionClass, tryBegin, tryEnd, catchBegin);
//		body.getTraps().add(trap);
//		body.validate();
	}

	void injectUpdateCode_U(JimpleBody body) {
		UnitPatchingChain units = body.getUnits();
		List<Unit> generated = new ArrayList<>();
		Local thisVar = body.getThisLocal();

		Local exceptionVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Exception"));
		Local viewVar = InstrumentUtil.generateNewLocal(body, RefType.v("android.view.View"));
		Local methodVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.reflect.Method"));
		Local clazzVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Class"));
		Local dexLoaderVar = InstrumentUtil.generateNewLocal(body, RefType.v("dalvik.system.DexClassLoader"));
		Local classVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Class"));
		Local classLoaderVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.ClassLoader"));
		Local classStringVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Class"));
		Local classViewVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Class"));
		Local widgetnameVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.String"));
		Local eviewVar = InstrumentUtil.generateNewLocal(body, RefType.v("android.view.View"));
		Local signatureVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.String"));
		Local classArrayVar = InstrumentUtil.generateNewLocal(body, ArrayType.v(RefType.v("java.lang.Class"), 1));
		Local objectArrayVar = InstrumentUtil.generateNewLocal(body, ArrayType.v(RefType.v("java.lang.Object"), 1));
		Local paramIntVar = InstrumentUtil.generateNewLocal(body, IntType.v());
		Local paramIntegerVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Integer"));
		Object[] localarr = body.getLocals().toArray();
		Local paramViewVar = InstrumentUtil.generateNewLocal(body, RefType.v("android.view.View"));
		// get parameter
		generated.add(Jimple.v().newAssignStmt(viewVar, body.getParameterLocal(0)));

		// get dexclassloader from field
		Object[] arr = Scene.v().getApplicationClasses().toArray();
		SootClass a = (SootClass) arr[MAINACTIVITY_INDEX];
		SootField ar = a.getFieldByName("dex");
		Jimple.v().newStaticFieldRef(ar.makeRef());
		generated.add(Jimple.v().newAssignStmt(dexLoaderVar, Jimple.v().newStaticFieldRef(ar.makeRef())));

		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.ClassLoader",
				"java.lang.Class loadClass(java.lang.String)", dexLoaderVar, classVar,
				StringConstant.v(FLUID_MAIN_CLASS)));
		Unit tryBegin = generated.get(generated.size() - 1);

		// create class array for getDeclaredMethod
		SootClass cls = Scene.v().getSootClass("java.lang.Class");
		generated.add(
				Jimple.v().newAssignStmt(classArrayVar, Jimple.v().newNewArrayExpr(cls.getType(), IntConstant.v(1))));

		// insert class to class array
		generated.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(classArrayVar, IntConstant.v(0)),
				ClassConstant.v("Ljava/lang/Object;")));

		// get runupdate
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Class",
				"java.lang.reflect.Method getDeclaredMethod(java.lang.String,java.lang.Class[])", classVar, methodVar,
				StringConstant.v("runUpdate"), classArrayVar));

		// make object
		SootClass cls2 = Scene.v().getSootClass("java.lang.Object");
		generated.add(
				Jimple.v().newAssignStmt(objectArrayVar, Jimple.v().newNewArrayExpr(cls2.getType(), IntConstant.v(1))));
		// get view
		viewVar = body.getLocals().getFirst();
		// get ui_update method unit

		// get paramVar
		Object[] Locals = body.getLocals().toArray();
		generated.add(Jimple.v().newAssignStmt(paramIntVar, (Local) Locals[1]));

		generated.addAll(InstrumentUtil.generateStaticInvokeStmt(body, "java.lang.Integer",
				"java.lang.Integer valueOf(int)", paramIntegerVar, paramIntVar));

		generated.add(
				Jimple.v().newAssignStmt(Jimple.v().newArrayRef(objectArrayVar, IntConstant.v(0)), paramIntegerVar));

		// invoke runupdate
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.reflect.Method",
				"java.lang.Object invoke(java.lang.Object,java.lang.Object[])", methodVar, null, NullConstant.v(),
				objectArrayVar));

		units.insertBefore(generated, units.getLast());
		Unit tryEnd = units.getLast(); // return

		// insert try-catch statement
		CaughtExceptionRef exceptionRef = soot.jimple.Jimple.v().newCaughtExceptionRef();
		Unit catchBegin = Jimple.v().newIdentityStmt(exceptionVar, exceptionRef);
		units.add(catchBegin);
		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Throwable", "void printStackTrace()",
				exceptionVar, null));

		units.add(Jimple.v().newReturnVoidStmt());
		SootClass exceptionClass = Scene.v().getSootClass("java.lang.Exception");
		Trap trap = soot.jimple.Jimple.v().newTrap(exceptionClass, tryBegin, tryEnd, catchBegin);
		body.getTraps().add(trap);
		body.validate();
	}

	void injectTriggerCode(JimpleBody body) {

		UnitPatchingChain units = body.getUnits();
		List<Unit> generated = new ArrayList<>();
		Local thisVar = body.getThisLocal();
		Local exceptionVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Exception"));
		Local viewVar = InstrumentUtil.generateNewLocal(body, RefType.v("android.view.View"));
		Local methodVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.reflect.Method"));
		Local clazzVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Class"));
		Local dexLoaderVar = InstrumentUtil.generateNewLocal(body, RefType.v("dalvik.system.DexClassLoader"));
		Local classVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Class"));
		Local classLoaderVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.ClassLoader"));
		Local classStringVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Class"));
		Local classViewVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Class"));
		Local widgetnameVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.String"));
		Local eviewVar = InstrumentUtil.generateNewLocal(body, RefType.v("android.view.View"));
		Local classArrayVar = InstrumentUtil.generateNewLocal(body, ArrayType.v(RefType.v("java.lang.Class"), 1));
		Local objectArrayVar = InstrumentUtil.generateNewLocal(body, ArrayType.v(RefType.v("java.lang.Object"), 1));
		Local objectFluidInterfaceVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Object"));
		System.out.println("onLongClick : ");
		// printLocals(body);

		// get parameter
		generated.add(Jimple.v().newAssignStmt(viewVar, body.getParameterLocal(0)));

		// get dexloader from field
		Object[] arrayClasses = Scene.v().getApplicationClasses().toArray();
		SootClass classMainActivity = (SootClass) arrayClasses[MAINACTIVITY_INDEX];
		SootField fieldDex = classMainActivity.getFieldByName("dex");
		generated.add(Jimple.v().newAssignStmt(dexLoaderVar, Jimple.v().newStaticFieldRef(fieldDex.makeRef())));
		SootField fieldFluidInterface = classMainActivity.getFieldByName("objFluidInterface");
		generated.add(Jimple.v().newAssignStmt(objectFluidInterfaceVar,
				Jimple.v().newStaticFieldRef(fieldFluidInterface.makeRef())));

		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.ClassLoader",
				"java.lang.Class loadClass(java.lang.String)", dexLoaderVar, classVar,
				StringConstant.v(FLUID_MAIN_CLASS)));
		Unit tryBegin = generated.get(generated.size() - 1);

		// get widget type
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Object",
				"java.lang.Class getClass()", viewVar, classViewVar));

		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Object",
				"java.lang.String toString()", classViewVar, widgetnameVar));

		// create Class array for getDeclaredMethod
		SootClass cls = Scene.v().getSootClass("java.lang.Class");
		generated.add(
				Jimple.v().newAssignStmt(classArrayVar, Jimple.v().newNewArrayExpr(cls.getType(), IntConstant.v(2))));

		// put class to class array
		// generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body,
		// "java.lang.Object", "java.lang.Class getClass()", widgetnameVar,
		// classStringVar));
		generated.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(classArrayVar, IntConstant.v(0)),
				ClassConstant.v("Ljava/lang/String;")));
		generated.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(classArrayVar, IntConstant.v(1)),
				ClassConstant.v("Landroid/view/View;")));

		// get runtest
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Class",
				"java.lang.reflect.Method getDeclaredMethod(java.lang.String,java.lang.Class[])", classVar, methodVar,
				StringConstant.v("runDistribute"), classArrayVar));

		// create object array for invoke
		SootClass cls2 = Scene.v().getSootClass("java.lang.Object");
		generated.add(
				Jimple.v().newAssignStmt(objectArrayVar, Jimple.v().newNewArrayExpr(cls2.getType(), IntConstant.v(2))));
		generated
				.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(objectArrayVar, IntConstant.v(0)), widgetnameVar));
		generated.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(objectArrayVar, IntConstant.v(1)), viewVar));
		// invoke runtest
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.reflect.Method",
				"java.lang.Object invoke(java.lang.Object,java.lang.Object[])", methodVar, null,
				objectFluidInterfaceVar, objectArrayVar));

		units.insertBefore(generated, units.getLast());
		Unit tryEnd = units.getLast(); // return

		// insert try-catch statement
		CaughtExceptionRef exceptionRef = soot.jimple.Jimple.v().newCaughtExceptionRef();
		Unit catchBegin = Jimple.v().newIdentityStmt(exceptionVar, exceptionRef);
		units.add(catchBegin);
		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Throwable", "void printStackTrace()",
				exceptionVar, null));

		units.add(Jimple.v().newReturnStmt(IntConstant.v(1)));
		SootClass exceptionClass = Scene.v().getSootClass("java.lang.Exception");
		Trap trap = soot.jimple.Jimple.v().newTrap(exceptionClass, tryBegin, tryEnd, catchBegin);
		body.getTraps().add(trap);
		body.validate();

	}

	void injectTriggerCode_U(JimpleBody body) {

		UnitPatchingChain units = body.getUnits();
		List<Unit> generated = new ArrayList<>();
		Local thisVar = body.getThisLocal();
		Local exceptionVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Exception"));
		Local viewVar = InstrumentUtil.generateNewLocal(body, RefType.v("android.view.View"));
		Local methodVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.reflect.Method"));
		Local clazzVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Class"));
		Local dexLoaderVar = InstrumentUtil.generateNewLocal(body, RefType.v("dalvik.system.DexClassLoader"));
		Local classVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Class"));
		Local classLoaderVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.ClassLoader"));
		Local classStringVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Class"));
		Local classViewVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Class"));
		Local widgetnameVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.String"));
		Local eviewVar = InstrumentUtil.generateNewLocal(body, RefType.v("android.view.View"));
		Local classArrayVar = InstrumentUtil.generateNewLocal(body, ArrayType.v(RefType.v("java.lang.Class"), 1));
		Local objectArrayVar = InstrumentUtil.generateNewLocal(body, ArrayType.v(RefType.v("java.lang.Object"), 1));
		Local paramBitmapVar = InstrumentUtil.generateNewLocal(body, ArrayType.v(RefType.v("java.lang.String"), 1));
		Local indexVar = InstrumentUtil.generateNewLocal(body, IntType.v());
		Local paramIndexVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Integer"));

		// get parameter
		generated.add(Jimple.v().newAssignStmt(viewVar, body.getParameterLocal(0)));

		// get dexloader from field
		Object[] arr = Scene.v().getApplicationClasses().toArray();
		SootClass a = (SootClass) arr[MAINACTIVITY_INDEX];
		SootField ar = a.getFieldByName("dex");
		SootField url = a.getFieldByName("sampleArr");
		SootField index = a.getFieldByName("index");
//		generated.add(Jimple.v().newAssignStmt(indexVar, IntConstant.v(0)));
		generated.add(Jimple.v().newAssignStmt(indexVar, Jimple.v().newStaticFieldRef(index.makeRef())));
		generated.addAll(InstrumentUtil.generateStaticInvokeStmt(body, "java.lang.Integer",
				"java.lang.Integer valueOf(int)", paramIndexVar, indexVar));

		generated.add(Jimple.v().newAssignStmt(dexLoaderVar, Jimple.v().newStaticFieldRef(ar.makeRef())));

		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.ClassLoader",
				"java.lang.Class loadClass(java.lang.String)", dexLoaderVar, classVar,
				StringConstant.v(FLUID_MAIN_CLASS)));
		Unit tryBegin = generated.get(generated.size() - 1);

		// create Class array for getDeclaredMethod
		SootClass cls = Scene.v().getSootClass("java.lang.Class");
		generated.add(
				Jimple.v().newAssignStmt(classArrayVar, Jimple.v().newNewArrayExpr(cls.getType(), IntConstant.v(2))));

		// put class to class array

		generated.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(classArrayVar, IntConstant.v(0)),
				ClassConstant.v("[Landroid/graphics/Bitmap;")));
		generated.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(classArrayVar, IntConstant.v(1)),
				ClassConstant.v("Ljava/lang/Object;")));
		// get runtest
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Class",
				"java.lang.reflect.Method getDeclaredMethod(java.lang.String,java.lang.Class[])", classVar, methodVar,
				StringConstant.v("runtest"), classArrayVar));

		// create object array for invoke
		SootClass cls2 = Scene.v().getSootClass("java.lang.Object");
		generated.add(
				Jimple.v().newAssignStmt(objectArrayVar, Jimple.v().newNewArrayExpr(cls2.getType(), IntConstant.v(2))));

		generated.add(Jimple.v().newAssignStmt(paramBitmapVar, Jimple.v().newStaticFieldRef(url.makeRef())));
		generated.add(
				Jimple.v().newAssignStmt(Jimple.v().newArrayRef(objectArrayVar, IntConstant.v(0)), paramBitmapVar));
		generated
				.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(objectArrayVar, IntConstant.v(1)), paramIndexVar));
		// invoke runtest
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.reflect.Method",
				"java.lang.Object invoke(java.lang.Object,java.lang.Object[])", methodVar, null, NullConstant.v(),
				objectArrayVar));

		units.insertBefore(generated, units.getLast());
		Unit tryEnd = units.getLast(); // return

		// insert try-catch statement
		CaughtExceptionRef exceptionRef = soot.jimple.Jimple.v().newCaughtExceptionRef();
		Unit catchBegin = Jimple.v().newIdentityStmt(exceptionVar, exceptionRef);
		units.add(catchBegin);
		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Throwable", "void printStackTrace()",
				exceptionVar, null));

		units.add(Jimple.v().newReturnStmt(IntConstant.v(1)));
		SootClass exceptionClass = Scene.v().getSootClass("java.lang.Exception");
		Trap trap = soot.jimple.Jimple.v().newTrap(exceptionClass, tryBegin, tryEnd, catchBegin);
		body.getTraps().add(trap);
		body.validate();

	}

	void injectCode(JimpleBody body) {
		UnitPatchingChain units = body.getUnits();
		List<Unit> generated = new ArrayList<>();
		InvokeStmt invkStmt = null;
		InvokeStmt stmt = null;
		// local variables
		Local thisVar = body.getThisLocal();
		Local dexLoaderVar = InstrumentUtil.generateNewLocal(body, RefType.v("dalvik.system.DexClassLoader"));
		Local classVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Class"));
		Local classLoaderVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.ClassLoader"));
		Local objVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Object"));
		Local fieldVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.reflect.Field"));
		Local serviceConnVar = InstrumentUtil.generateNewLocal(body, RefType.v("android.content.ServiceConnection"));
		Local intentVar = InstrumentUtil.generateNewLocal(body, RefType.v("android.content.Intent"));
		Local exceptionVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Exception"));
		Local classArrayVar = InstrumentUtil.generateNewLocal(body, ArrayType.v(RefType.v("java.lang.Class"), 1));
		Local objectArrayVar = InstrumentUtil.generateNewLocal(body, ArrayType.v(RefType.v("java.lang.Object"), 1));
		Local objectFluidInterfaceVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Object"));
		Local methodVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.reflect.Method"));
		Local viewVar = InstrumentUtil.generateNewLocal(body, RefType.v("android.view.View"));
		Local IDVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Integer"));
		Object[] arrayClasses = Scene.v().getApplicationClasses().toArray();
		SootClass classMainActivity = (SootClass) arrayClasses[MAINACTIVITY_INDEX];
		SootField fieldDex = classMainActivity.getFieldByName("dex");
		generated.add(Jimple.v().newAssignStmt(Jimple.v().newStaticFieldRef(fieldDex.makeRef()), dexLoaderVar));
		SootField fieldFluidInterface = classMainActivity.getFieldByName("objFluidInterface");
		for(int i = 0; i<arrayClasses.length; i++)
		{
			if(arrayClasses[i].toString().equals(MAIN_PACKAGE_NAME+"R$id"))
			{
				SootField fieldID = ((SootClass)arrayClasses[i]).getFieldByName("rootlayout");
				generated.add(Jimple.v().newAssignStmt(Jimple.v().newStaticFieldRef(fieldID.makeRef()), IDVar));
			}
		}
		

		//setonTouchListener
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "androidx.appcompat.app.AppCompatActivity", 
				"android.view.View findViewById(int)", thisVar, viewVar, IDVar));
		//TODO
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "android.view.View", 
				"void setOnTouchListener(android.view.View$OnTouchListener)", viewVar, null, 
				Jimple.v().newNewExpr(RefType.v(inject_onTouchClass()))
				));
		
		
		// create DexClassLoader instance
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Object",
				"java.lang.Class getClass()", thisVar, classVar));
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Class",
				"java.lang.ClassLoader getClassLoader()", classVar, classLoaderVar));
		generated.addAll(InstrumentUtil.generateNewInstance(body, "dalvik.system.DexClassLoader",
				"void <init>(java.lang.String,java.lang.String,java.lang.String,java.lang.ClassLoader)", dexLoaderVar,
				StringConstant.v(FLUID_LIB_PATH), StringConstant.v(TMP_DIR_PATH), NullConstant.v(), classLoaderVar));
		// copy dexLoaderVar to this.dex field
		
		// generated.add(Jimple.v().newAssignStmt(Jimple.v().newStaticFieldRef(fieldFluidInterface.makeRef()),objectFluidInterfaceVar));

		// create FLUID instance
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.ClassLoader",
				"java.lang.Class loadClass(java.lang.String)", dexLoaderVar, classVar,
				StringConstant.v(FLUID_MAIN_CLASS)));
		Unit tryBegin = generated.get(generated.size() - 1);

		// create Class array for getDeclaredMethod
		SootClass cls = Scene.v().getSootClass("java.lang.Class");
		generated.add(
				Jimple.v().newAssignStmt(classArrayVar, Jimple.v().newNewArrayExpr(cls.getType(), IntConstant.v(1))));

		// put class to class array
		// generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body,
		// "java.lang.Object", "java.lang.Class getClass()", widgetnameVar,
		// classStringVar));
		generated.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(classArrayVar, IntConstant.v(0)),
				ClassConstant.v("Landroid/content/Context;")));
		// generated.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(classArrayVar,
		// IntConstant.v(1)), ClassConstant.v("Landroid/view/View;")));

		// get getInstance
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Class",
				"java.lang.reflect.Method getDeclaredMethod(java.lang.String,java.lang.Class[])", classVar, methodVar,
				StringConstant.v("getInstance"), classArrayVar));

		// create object array for invoke
		SootClass cls2 = Scene.v().getSootClass("java.lang.Object");
		generated.add(
				Jimple.v().newAssignStmt(objectArrayVar, Jimple.v().newNewArrayExpr(cls2.getType(), IntConstant.v(1))));
		generated.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(objectArrayVar, IntConstant.v(0)), thisVar));
		// invoke getInstance
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.reflect.Method",
				"java.lang.Object invoke(java.lang.Object,java.lang.Object[])", methodVar, objectFluidInterfaceVar,
				NullConstant.v(), objectArrayVar));

		generated.add(Jimple.v().newAssignStmt(Jimple.v().newStaticFieldRef(fieldFluidInterface.makeRef()),
				objectFluidInterfaceVar));

		// create Class array for getDeclaredMethod
		// SootClass cls = Scene.v().getSootClass("java.lang.Class");
		generated.add(
				Jimple.v().newAssignStmt(classArrayVar, Jimple.v().newNewArrayExpr(cls.getType(), IntConstant.v(1))));

		// put class to class array
		// generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body,
		// "java.lang.Object", "java.lang.Class getClass()", widgetnameVar,
		// classStringVar));
		generated.add(
				Jimple.v().newAssignStmt(Jimple.v().newArrayRef(classArrayVar, IntConstant.v(0)), NullConstant.v()));
		// generated.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(classArrayVar,
		// IntConstant.v(1)), ClassConstant.v("Landroid/view/View;")));

		// get getInstance
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Class",
				"java.lang.reflect.Method getDeclaredMethod(java.lang.String,java.lang.Class[])", classVar, methodVar,
				StringConstant.v("runBind"), NullConstant.v()));

		// create object array for invoke
		// SootClass cls2 = Scene.v().getSootClass("java.lang.Object");
		generated.add(
				Jimple.v().newAssignStmt(objectArrayVar, Jimple.v().newNewArrayExpr(cls2.getType(), IntConstant.v(1))));
		generated.add(
				Jimple.v().newAssignStmt(Jimple.v().newArrayRef(objectArrayVar, IntConstant.v(0)), NullConstant.v()));
		// invoke getInstance
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.reflect.Method",
				"java.lang.Object invoke(java.lang.Object,java.lang.Object[])", methodVar, null,
				objectFluidInterfaceVar, NullConstant.v()));

		// generated.add(Jimple.v().newAssignStmt(objectFluidInterfaceVar,Jimple.v().newStaticFieldRef(fieldFluidInterface.makeRef())));

		// insert new code
		units.insertBefore(generated, units.getLast());
		Unit tryEnd = units.getLast();
		// insert try-catch statement
		CaughtExceptionRef exceptionRef = soot.jimple.Jimple.v().newCaughtExceptionRef();
		Unit catchBegin = Jimple.v().newIdentityStmt(exceptionVar, exceptionRef);
		units.add(catchBegin);
		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Throwable", "void printStackTrace()",
				exceptionVar, null));
		units.add(Jimple.v().newReturnVoidStmt());
		SootClass exceptionClass = Scene.v().getSootClass("java.lang.Exception");
		Trap trap = soot.jimple.Jimple.v().newTrap(exceptionClass, tryBegin, tryEnd, catchBegin);
		body.getTraps().add(trap);
		// validate the instrumented code
		body.validate();
	}

	void injectCode_t(JimpleBody body) {
		UnitPatchingChain units = body.getUnits();
		List<Unit> generated = new ArrayList<>();
		InvokeStmt invkStmt = null;
		InvokeStmt stmt = null;
		// local variables
		Local thisVar = body.getThisLocal();
		Local dexLoaderVar = InstrumentUtil.generateNewLocal(body, RefType.v("dalvik.system.DexClassLoader"));
		Local classVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Class"));
		Local classLoaderVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.ClassLoader"));
		Local objVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Object"));
		Local fieldVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.reflect.Field"));
		Local serviceConnVar = InstrumentUtil.generateNewLocal(body, RefType.v("android.content.ServiceConnection"));
		Local intentVar = InstrumentUtil.generateNewLocal(body, RefType.v("android.content.Intent"));
		Local exceptionVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Exception"));

		// create DexClassLoader instance
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Object",
				"java.lang.Class getClass()", thisVar, classVar));
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Class",
				"java.lang.ClassLoader getClassLoader()", classVar, classLoaderVar));
		generated.addAll(InstrumentUtil.generateNewInstance(body, "dalvik.system.DexClassLoader",
				"void <init>(java.lang.String,java.lang.String,java.lang.String,java.lang.ClassLoader)", dexLoaderVar,
				StringConstant.v(FLUID_LIB_PATH), StringConstant.v(TMP_DIR_PATH), NullConstant.v(), classLoaderVar));
		// copy dexLoaderVar to this.dex field
		Object[] arr = Scene.v().getApplicationClasses().toArray();
		SootClass a = (SootClass) arr[MAINACTIVITY_INDEX];
		SootField ar = a.getFieldByName("dex");
		generated.add(Jimple.v().newAssignStmt(Jimple.v().newStaticFieldRef(ar.makeRef()), dexLoaderVar));

		// create FLUID instance
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.ClassLoader",
				"java.lang.Class loadClass(java.lang.String)", dexLoaderVar, classVar,
				StringConstant.v(FLUID_MAIN_CLASS)));
		Unit tryBegin = generated.get(generated.size() - 1);
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Class",
				"java.lang.Object newInstance()", classVar, objVar));
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Class",
				"java.lang.reflect.Field getDeclaredField(java.lang.String)", classVar, fieldVar,
				StringConstant.v("mServiceConnection")));
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.reflect.Field",
				"java.lang.Object get(java.lang.Object)", fieldVar, serviceConnVar, objVar));
		// invoke bindService
		generated
				.addAll(InstrumentUtil.generateNewInstance(body, "android.content.Intent", "void <init>()", intentVar));
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "android.content.Intent",
				"android.content.Intent setClassName(java.lang.String,java.lang.String)", intentVar, null,
				StringConstant.v(FLUID_PACKAGE_NAME), StringConstant.v(FLUID_SERVICE_NAME)));
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "android.content.Context",
				"boolean bindService(android.content.Intent,android.content.ServiceConnection,int)", thisVar, null,
				intentVar, serviceConnVar, IntConstant.v(1)));
		// insert new code
		units.insertBefore(generated, units.getLast());
		Unit tryEnd = units.getLast();
		// insert try-catch statement
		CaughtExceptionRef exceptionRef = soot.jimple.Jimple.v().newCaughtExceptionRef();
		Unit catchBegin = Jimple.v().newIdentityStmt(exceptionVar, exceptionRef);
		units.add(catchBegin);
		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Throwable", "void printStackTrace()",
				exceptionVar, null));
		units.add(Jimple.v().newReturnVoidStmt());
		SootClass exceptionClass = Scene.v().getSootClass("java.lang.Exception");
		Trap trap = soot.jimple.Jimple.v().newTrap(exceptionClass, tryBegin, tryEnd, catchBegin);
		body.getTraps().add(trap);
		// validate the instrumented code
		body.validate();
	}
	SootClass inject_onTouchClass() {
		
		
		String ListenerClassSignature = MAINACTIVITY_CLASS_NAME+"onTouchListener";
		SootClass onTouchListener = new SootClass(ListenerClassSignature,Modifier.PUBLIC);
		onTouchListener.setSuperclass(Scene.v().getSootClass("java.lang.object"));
		onTouchListener.setApplicationClass();
		List<Type> parameterType = new ArrayList<Type>();
		parameterType.add(Scene.v().getSootClass("android.view.View").getType());
		parameterType.add(Scene.v().getSootClass("android.view.MotionEvent").getType());
		Type returnType = Scene.v().getSootClass("java.lang.Boolean").getType();
		SootMethod onTouch = new SootMethod("onTouch",parameterType,returnType, Modifier.PUBLIC);
		onTouchListener.addMethod(onTouch);
		JimpleBody body = Jimple.v().newBody();
		body.setMethod(onTouch);
		UnitPatchingChain units = body.getUnits();
		
		Local exceptionVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Exception"));
		Local viewVar = InstrumentUtil.generateNewLocal(body, RefType.v("android.view.View"));
		Local methodVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.reflect.Method"));
		Local dexLoaderVar = InstrumentUtil.generateNewLocal(body, RefType.v("dalvik.system.DexClassLoader"));
		Local classVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Class"));
		Local classViewVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Class"));
		Local widgetnameVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.String"));
		Local classArrayVar = InstrumentUtil.generateNewLocal(body, ArrayType.v(RefType.v("java.lang.Class"), 1));
		Local objectArrayVar = InstrumentUtil.generateNewLocal(body, ArrayType.v(RefType.v("java.lang.Object"), 1));
		Local objectFluidInterfaceVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Object"));
		Local param0Var = InstrumentUtil.generateNewLocal(body, RefType.v("android.view.View"));
		Local param1Var = InstrumentUtil.generateNewLocal(body, RefType.v("android.view.MotionEvent"));
		List<Unit> generated = new ArrayList<>();
		
		Object[] arrayClasses = Scene.v().getApplicationClasses().toArray();
		SootClass classMainActivity = (SootClass) arrayClasses[MAINACTIVITY_INDEX];
		SootField fieldDex = classMainActivity.getFieldByName("dex");
		
		units.add(Jimple.v().newAssignStmt(dexLoaderVar, Jimple.v().newStaticFieldRef(fieldDex.makeRef())));
		SootField fieldFluidInterface = classMainActivity.getFieldByName("objFluidInterface");
		generated.add(Jimple.v().newAssignStmt(objectFluidInterfaceVar,
				Jimple.v().newStaticFieldRef(fieldFluidInterface.makeRef())));

		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.ClassLoader",
				"java.lang.Class loadClass(java.lang.String)", dexLoaderVar, classVar,
				StringConstant.v(FLUID_MAIN_CLASS)));
		Unit tryBegin = generated.get(generated.size() - 1);

		// get widget type
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Object",
				"java.lang.Class getClass()", viewVar, classViewVar));

		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Object",
				"java.lang.String toString()", classViewVar, widgetnameVar));

		// create Class array for getDeclaredMethod
		SootClass cls = Scene.v().getSootClass("java.lang.Class");
		generated.add(
				Jimple.v().newAssignStmt(classArrayVar, Jimple.v().newNewArrayExpr(cls.getType(), IntConstant.v(2))));

		// put class to class array
		// generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body,
		// "java.lang.Object", "java.lang.Class getClass()", widgetnameVar,
		// classStringVar));
		generated.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(classArrayVar, IntConstant.v(0)),
				ClassConstant.v("Landroid/view/View;")));
		generated.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(classArrayVar, IntConstant.v(1)),
				ClassConstant.v("Landroid/view/MotionEvent;")));

		// get runtest
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Class",
				"java.lang.reflect.Method getDeclaredMethod(java.lang.String,java.lang.Class[])", classVar, methodVar,
				StringConstant.v("runThreeFinger"), classArrayVar));

		// create object array for invoke
		SootClass cls2 = Scene.v().getSootClass("java.lang.Object");
		generated.add(
				Jimple.v().newAssignStmt(objectArrayVar, Jimple.v().newNewArrayExpr(cls2.getType(), IntConstant.v(2))));
		units.insertAfter(generated, units.getFirst());
		Object[] ar = body.getParameterLocals().toArray();
		for(int i = 0; i<ar.length; i++)
			System.err.println("check: "+ar[i].toString());
		generated.add(Jimple.v().newAssignStmt(param0Var, body.getParameterLocal(0)));
		generated.add(Jimple.v().newAssignStmt(param1Var, body.getParameterLocal(1)));
		generated.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(objectArrayVar, IntConstant.v(0)), param0Var));
		generated.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(objectArrayVar, IntConstant.v(1)), param1Var));
		// invoke runtest
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.reflect.Method",
				"java.lang.Object invoke(java.lang.Object,java.lang.Object[])", methodVar, null,
				objectFluidInterfaceVar, objectArrayVar));

		units.insertAfter(generated, units.getLast());
		Unit tryEnd = units.getLast(); // return

		// insert try-catch statement
		CaughtExceptionRef exceptionRef = soot.jimple.Jimple.v().newCaughtExceptionRef();
		Unit catchBegin = Jimple.v().newIdentityStmt(exceptionVar, exceptionRef);
		units.add(catchBegin);
		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Throwable", "void printStackTrace()",
				exceptionVar, null));

		units.add(Jimple.v().newReturnStmt(IntConstant.v(0)));
		SootClass exceptionClass = Scene.v().getSootClass("java.lang.Exception");
		Trap trap = soot.jimple.Jimple.v().newTrap(exceptionClass, tryBegin, tryEnd, catchBegin);
		body.getTraps().add(trap);
		body.validate();
		
		return onTouchListener;
	}

}
