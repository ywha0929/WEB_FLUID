package fluidinjector;

import soot.*;
import soot.jimple.*;
import soot.jimple.internal.JEqExpr;
import soot.tagkit.Tag;
import soot.util.Chain;
import soot.util.EmptyChain;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.io.File;
import java.util.StringTokenizer;

public class RPCIntfInjector extends BodyTransformer {
	boolean isInsert = true;
	boolean isAnalize = false;
	boolean isFirstDone = false;
	static AtomicInteger threadNum = new AtomicInteger();
	// final static int MAINACTIVITY_INDEX2 = 1918;
	static int MAINACTIVITY_INDEX = 0;
	static String MAIN_ACTIVITY_CLASS = null;
	final static String TMP_DIR_PATH = "/data/local/tmp/";
	final static String FLUID_LIB_PATH = "/data/local/tmp/fluidlib.apk";
	final static String FLUID_MAIN_CLASS = "com.hmsl.fluidlib.FLUIDMain";
	final static String FLUID_PACKAGE_NAME = "com.hmsl.fluidmanager";
	final static String FLUID_SERVICE_NAME = "com.hmsl.fluidmanager.FLUIDManagerService";
	static String MAINACTIVITY_CLASS_NAME;
	static String MAIN_PACKAGE_NAME;
	static List<SootClass> injectedClasses = new ArrayList<>();
	static List<String> listUIUpdateSignature = new ArrayList();
	static List<String> listUIUpdateTargetSignature = new ArrayList();
	static String StaticAnalysisFileName = System.getProperty("user.home")+ "/WEB_FLUID/FLUID_Injector/"+"StaticAnalysisResult/test.result";
	static String apkPath;
	static String staticAnalysisPath;
	public void loadStaticAnalysisResult()
	{
		try {
			Scanner scanner = new Scanner(new File(staticAnalysisPath));

			while (scanner.hasNextLine()) {
				String buffer = scanner.nextLine();
//				System.out.println(buffer);
				StringTokenizer stk = new StringTokenizer(buffer,"<>");
				listUIUpdateSignature.add(stk.nextToken());
				stk.nextToken();
				listUIUpdateTargetSignature.add(stk.nextToken());
			}

			scanner.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("listUIUpdateSignature");
		for(String string : listUIUpdateSignature)
		{
			System.out.println(string);
		}
		for(String string : listUIUpdateTargetSignature)
		{
			System.out.println(string);
		}
	}

	public RPCIntfInjector(String apkPath) {

		super();
		this.apkPath = apkPath;
		this.staticAnalysisPath = apkPath+".result";
//		this.staticAnalysisPath = StaticAnalysisFileName;
		loadStaticAnalysisResult();
		System.out.println("starting first pass");
		System.err.println("starting first pass");
		findLeafActivities(Scene.v().getApplicationClasses().toArray());
		performFirstPass();
		System.out.println("finishing first pass");
		System.err.println("finishing first pass");
//		MAIN_PACKAGE_NAME = namePackage;
//		String classname = namePackage+".MainActivity";
//		MAINACTIVITY_CLASS_NAME = classname;
	}

	@Override
	protected void internalTransform(Body b, String s, Map<String, String> map) {

//		int threadNum = this.threadNum.getAndIncrement();
//		if(threadNum ==0)
//		{
//			System.out.println("Thread ID start: "+Thread.currentThread().getId());
//			System.out.println("starting first pass");
//			System.err.println("starting first pass");
//			findLeafActivities(Scene.v().getApplicationClasses().toArray());
//			performFirstPass();
//
//			System.out.println("finishing first pass");
//			System.err.println("finishing first pass");
//			System.out.println("Thread ID end : "+Thread.currentThread().getId());
//			isFirstDone = true;
//		}
//		else
//		{
//			System.out.println("Thread ID created : "+Thread.currentThread().getId());
//			while(!isFirstDone)
//			{
//				System.out.println("Thread ID : "+Thread.currentThread().getId() + "waiting");
//			}
//			System.out.println("Thread ID start : "+Thread.currentThread().getId());
////			performSecondPassbySignature(b,s,map,threadNum,1);
//			if(!b.getMethod().toString().contains("init") && !b.getMethod().toString().contains("onCreate"))
//			{
				performSecondPassbyBaseClass(b,s,map,0);

////				performSecondPassbySignature(b,s,map,threadNum,1);
//			}
//
//			System.out.println("Thread ID end : "+Thread.currentThread().getId());
//
//		}

		System.out.println("just print\n");

//		InjectOnActivity(b,s,map);
//		SecondPass(b,s,map);

	}


	void InjectOnActivity(Body b, String s, Map<String,String> map) //insert method, fields
	{
		JimpleBody body = (JimpleBody) b;

		if (AndroidUtil.isAndroidMethod(b.getMethod()))
			return;
		//System.out.println(body);
		if (isAnalize) {
			isAnalize = false;
//			printClasses(body);
		}

		if (isInsert) {
			isInsert = false;

			Object[] arr = Scene.v().getApplicationClasses().toArray();
			SootClass hi = (SootClass)arr[0];

			boolean found = false;
			int index = 0;
			for (int i = 0; i < arr.length; i++) {
				if (arr[i].toString().equals(MAINACTIVITY_CLASS_NAME)) {
					found = true;
					index = i;
				}
			}
			if(found == true)
			{
				System.err.println("found MainActivity");
				MAINACTIVITY_INDEX = index;
				System.err.println("super class name"+((SootClass)arr[index]).getSuperclass().toString());

			}
			else
			{
				System.err.println("unable to find MainActivity");
			}
			SootClass classMainActivity = (SootClass) arr[MAINACTIVITY_INDEX];// MainActivity
			System.err.println("mainactivity : "+classMainActivity.toString());
//			Object[] fields = classMainActivity.getFields().toArray();
//			for(int i = 0; i<fields.length;i++)
//			{
//				System.err.println("mainactivity field : ["+i+"] - "+fields[i].toString());
//			}
//			SootClass aa = Scene.v().getSootClass("dalvik.system.DexClassLoader");
//			SootField testfld = new SootField("dex", aa.getType());
//			a.addField(testfld);

			InstrumentUtil.addField(classMainActivity, "dex", RefType.v("dalvik.system.DexClassLoader"),
					Modifier.PUBLIC | Modifier.STATIC);
			InstrumentUtil.addField(classMainActivity, "objFluidInterface", RefType.v("java.lang.Object"),
					Modifier.PUBLIC | Modifier.STATIC);
//			dispatchTouchEvent(Soo);

		}
	}
	void EditMethods(Body b, String s, Map<String,String> map) //edit methods
	{
		while(isFirstDone == false);
		if (b.getMethod().getName().equals("onCreate")) {
			System.out.println("==== before ====");
			System.out.println(b);
			//injectOnCreate((JimpleBody) b);
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
		}
		if (b.getMethod().getName().equals("onClick")) {
			System.out.println("==== before ====");
			System.out.println(b);
			System.out.println("onClick");
//			printLocals((JimpleBody)b);
			injectUpdateCodebySignature((JimpleBody) b);

			System.out.println("==== after ====");
			System.out.println(b);
		}
		if(b.getMethod().getSignature().contains("dispatchTouchEvent") && b.getMethod().getSignature().contains("Activity"))
		{
			System.out.println("==== before ====");
			System.out.println(b);
			System.err.println("dispatchTouchEvent");
//			printLocals((JimpleBody)b);
//			injectiDispatchTouchEvent((JimpleBody) b);
		}
	}
	void findLeafActivities(Object[] arrClasses)
	{
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
				if(injectedClasses.contains(superClass)) //if ancestor in list -> switch
				{
					System.err.println("switching Class "+superClass.toString()+" -> "+thisClass.toString());
					System.out.println("switching Class "+superClass.toString()+" -> "+thisClass.toString());
					injectedClasses.remove(superClass);
					injectedClasses.add(thisClass);
					break;
				}
				if(superClass.toString().equals("androidx.appcompat.app.AppCompatActivity")|| superClass.toString().equals("android.app.Activity"))
				{
					System.err.println("found activity class : "+thisClass.toString());
					System.out.println("found activity class : "+thisClass.toString());
					if(!injectedClasses.contains(thisClass))
					{
						injectedClasses.add(thisClass);
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
		for(int i = 0; i< injectedClasses.size(); i++)
		{
			if(injectedClasses.get(i).toString().contains("androidx"))
			{
				injectedClasses.remove(injectedClasses.get(i));
			}
		}
		for(SootClass thisClass : injectedClasses)
		{
			if(thisClass.toString().contains("MainActivity"))
			{
				MAIN_ACTIVITY_CLASS = thisClass.toString();
			}
		}
		for(SootClass thisClass : injectedClasses)
		{
			System.out.println(thisClass.toString());
		}
	}
	void performFirstPass() //inject fields and edit onCreate
	{
		if (isAnalize) {
			isAnalize = false;
			printClasses();
		}
		SootClass classMainActivity = null;
		for(SootClass thatClass : injectedClasses)
		{
			if(thatClass.toString().contains("MainActivity")) {

				classMainActivity = thatClass;
				injectedClasses.remove(classMainActivity);
				injectedClasses.add(0,classMainActivity);
				break;
			}

		}
		System.out.println("class to be injected : ");
		for(SootClass thatClass : injectedClasses)
		{
			System.out.println(thatClass.toString());

		}
		for(int i = 0; i< injectedClasses.size(); i++)
		{

			SootClass thisClass = (SootClass) injectedClasses.get(i);
			System.out.println("performFirstPass : injecting "+thisClass.toString());
			//inject fields
			InstrumentUtil.addField(thisClass, "dex", RefType.v("dalvik.system.DexClassLoader"),
					Modifier.PUBLIC | Modifier.STATIC);
			InstrumentUtil.addField(thisClass, "objFluidInterface", RefType.v("java.lang.Object"),
					Modifier.PUBLIC | Modifier.STATIC);


			SootMethod onCreate = thisClass.getMethodByNameUnsafe("onCreate");
			//System.err.println("onCreate "+onCreate.toString());

			if(onCreate == null)
			{
				System.err.println("this activity class("+thisClass.toString()+ ") has no onCreate method");
				System.out.println("this activity class("+thisClass.toString()+ ") has no onCreate method");
				if(thisClass.toString().contains("MainActivity"))
					addOnCreateTypeA(thisClass);
				else
					addOnCreateTypeB(thisClass,classMainActivity);
			}
			else
			{
				System.err.println("this activity class("+thisClass.toString()+ ") has onCreate method");
				System.out.println("this activity class("+thisClass.toString()+ ") has onCreate method");
				Body onCreateBody = onCreate.getActiveBody();
				if(thisClass.toString().contains("MainActivity")) {
					System.out.println("this is MainActivity");
					injectOnCreateTypeA((JimpleBody) onCreateBody, thisClass);
				}
				else {
					System.out.println("this is not MainActivity");
					injectOnCreateTypeB((JimpleBody) onCreateBody,thisClass, classMainActivity);
				}
			}

//			SootMethod onResume = thisClass.getMethodByNameUnsafe("onResume");
//			if(onResume == null)
//			{
//				System.err.println("this activity class("+thisClass.toString()+ ") has no onResume method");
//				System.out.println("this activity class("+thisClass.toString()+ ") has no onResume method");
//				addOnResume(thisClass);
//			}
//			else
//			{
//				System.err.println("this activity class("+thisClass.toString()+ ") has onResume method");
//				System.out.println("this activity class("+thisClass.toString()+ ") has onResume method");
//			}



			SootMethod dispatchTouchEvent = thisClass.getMethodByNameUnsafe("dispatchTouchEvent");
			if(dispatchTouchEvent == null)
			{
				System.err.println("this activity class("+thisClass.toString()+ ") has no dispatchTouchEvent method");
				System.out.println("this activity class("+thisClass.toString()+ ") has no dispatchTouchEvent method");
				if(thisClass.toString().contains("MainActivity"))
					addDispatchTouchEventTypeA(thisClass);
				else
					addDispatchTouchEventTypeB(thisClass,classMainActivity);
			}
			else
			{
				System.err.println("this activity class("+thisClass.toString()+ ") has dispatchTouchEvent method");
				System.out.println("this activity class("+thisClass.toString()+ ") has dispatchTouchEvent method");
				Body dispatchTouchEventBody = dispatchTouchEvent.getActiveBody();
				if(thisClass.toString().contains("MainActivity"))
					editDispatchTouchEventTypeA((JimpleBody)dispatchTouchEventBody,thisClass);
				else
					editDispatchTouchEventTypeB((JimpleBody) dispatchTouchEventBody, thisClass, classMainActivity);
			}



		}


//		Object[] classes = Scene.v().getApplicationClasses().toArray();
//		for(int i = 0; i< classes.length;i++)
//		{
//			SootClass thisClass = (SootClass)classes[i];
////			System.err.println("["+i+"]th class : "+thisClass.toString());
////			System.out.println("["+i+"]th class : "+thisClass.toString());
//			SootClass superClass = thisClass.getSuperclass();
//			while(true)
//			{
//
//				if(superClass.toString().equals("androidx.appcompat.app.AppCompatActivity")|| superClass.toString().equals("android.app.Activity"))
//				{
//					System.err.println("found activity class : "+thisClass.toString());
//					System.out.println("found activity class : "+thisClass.toString());
//					if(!injectedClasses.contains(thisClass))
//					{
//						injectedClasses.add(thisClass);
//
//					}
//					else
//					{
//						System.out.println("thisClass is already injected");
//						break;
//
//					}
//
//					InstrumentUtil.addField(thisClass, "dex", RefType.v("dalvik.system.DexClassLoader"),
//							Modifier.PUBLIC | Modifier.STATIC);
//					InstrumentUtil.addField(thisClass, "objFluidInterface", RefType.v("java.lang.Object"),
//							Modifier.PUBLIC | Modifier.STATIC);
////					Object[] mtdList = thisClass.getMethods().toArray();
////					System.err.println("mtdList length"+mtdList.length);
////					for(int j = 0; j<mtdList.length; j++)
////					{
////						System.err.println(mtdList[j].toString());
////					}
////
//					SootMethod onCreate = thisClass.getMethodByNameUnsafe("onCreate");
//					//System.err.println("onCreate "+onCreate.toString());
//
//					if(onCreate == null)
//					{
//						System.err.println("this activity class has no onCreate method");
//						System.out.println("this activity class has no onCreate method");
//						addOnCreate(thisClass);
//					}
//					else
//					{
//						Body onCreateBody = onCreate.getActiveBody();
//						injectOnCreate((JimpleBody)onCreateBody,thisClass);
//					}
//					SootMethod dispatchTouchEvent = thisClass.getMethodByNameUnsafe("dispatchTouchEvent");
//					if(dispatchTouchEvent == null)
//					{
//						System.err.println("this activity class has no dispatchTouchEvent method");
//						System.out.println("this activity class has no dispatchTouchEvent method");
//						addDispatchTouchEvent(thisClass);
//					}
//					else
//					{
//						Body dispatchTouchEventBody = dispatchTouchEvent.getActiveBody();
//						editDispatchTouchEvent((JimpleBody)dispatchTouchEventBody,thisClass);
//					}
//
//					break;
//				}
//				else
//				{
//					if(superClass.hasSuperclass())
//					{
//						superClass = superClass.getSuperclass();
//					}
//					else
//					{
//						break;
//					}
//				}
//			}
//		}
		System.out.println("First Pass Done");
		isFirstDone = true;
	}
	void performSecondPassbySignature(Body b, String s, Map<String, String> map,int threadNum,int onlyInjectedClass)
	{
		//System.out.println("performSecondPass start : "+threadNum);
		if(onlyInjectedClass == 1)
		{
			if(injectedClasses.contains(b.getMethod().getDeclaringClass()))
			{
				System.out.println("injecting Update code to "+b.getMethod().getDeclaringClass().toString()+" : "+b.getMethod().toString());
				System.err.println("injecting Update code to "+b.getMethod().getDeclaringClass().toString()+" : "+b.getMethod().toString());
				injectUpdateCodebySignature((JimpleBody)b);
			}
			else
			{
				System.out.println("injecting Update code to "+b.getMethod().getDeclaringClass().toString()+" : "+b.getMethod().toString());
				System.err.println("injecting Update code to "+b.getMethod().getDeclaringClass().toString()+" : "+b.getMethod().toString());
			}
		}
		else
		{
			System.out.println("injecting Update code to "+b.getMethod().getDeclaringClass().toString()+" : "+b.getMethod().toString());
			System.err.println("injecting Update code to "+b.getMethod().getDeclaringClass().toString()+" : "+b.getMethod().toString());
			injectUpdateCodebySignature((JimpleBody)b);
			System.out.println("injecting Update code to "+b.getMethod().getDeclaringClass().toString()+" : "+b.getMethod().toString()+"...done");
			System.err.println("injecting Update code to "+b.getMethod().getDeclaringClass().toString()+" : "+b.getMethod().toString()+"...done");
		}
	}
	void performSecondPassbyBaseClass(Body b, String s, Map<String, String> map,int onlyInjectedClass)
	{
		SootClass declaringClass = b.getMethod().getDeclaringClass();
		if(declaringClass.isLibraryClass()|| declaringClass.toString().contains("androidx"))
		{
			System.out.println("this is Library Class : "+declaringClass.toString());
			System.err.println("this is Library Class : "+declaringClass.toString());
			System.out.println("this end of performSecondPassbyBaseClass : ");
			System.err.println("this end of performSecondPassbyBaseClass : ");
			return;
		}

		if(onlyInjectedClass == 1) {
			if(injectedClasses.contains(declaringClass))
			{
				injectUpdateCodebyBaseClass((JimpleBody)b);
			}
			else if(declaringClass.hasOuterClass() && injectedClasses.contains(declaringClass.getOuterClass())){
				injectUpdateCodebyBaseClass((JimpleBody)b);
			}
			else {

			}
		}
		else {
			injectUpdateCodebyBaseClass((JimpleBody)b);
		}
		System.out.println("this end of performSecondPassbyBaseClass : ");
		System.err.println("this end of performSecondPassbyBaseClass : ");
	}


	void printClasses() {
		Object[] arr = Scene.v().getApplicationClasses().toArray();
		for (int i = 0; i < arr.length; i++) {
			System.out.println("Class [" + i + "] : " + arr[i].toString());
		}
		System.out.println("printClasses done");
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

	void injectiDispatchTouchEvent(JimpleBody body){
		UnitPatchingChain units = body.getUnits();
		List<Unit> generated = new ArrayList<>();


		body.validate();
	}


	boolean isView(SootClass cls)
	{
		SootClass classView = Scene.v().getSootClass("android.view.View");
		if(cls == classView)
		{
			return true;
		}
		while(cls.hasSuperclass())
		{
			if(cls == classView)
			{
				return true;
			}
			cls = cls.getSuperclass();
		}
		return false;
	}

	void injectUpdateCodebyBaseClass(JimpleBody body) {
		UnitPatchingChain units = body.getUnits();
		Boolean hasUpdateCode = false;
		// List<Unit> generated = new ArrayList<>();

//		Local thisVar = body.getThisLocal();
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
//		System.out.println("onClick");
//		printLocals(body);

		// edit here

		Object[] unitarray = units.toArray();
		for (int i = 0; i < unitarray.length; i++) {

			String targetUnitString = unitarray[i].toString();
			Unit targetUnit = (Unit)unitarray[i];

			//find base class
			if(targetUnitString.contains("virtualinvoke") && !targetUnitString.contains("goto"))
			{
				String sig = unitarray[i].toString();
				System.out.println(body.getMethod() + "- sig : " + sig);

				// get base for ui_update method unit
				Object[] locals = body.getLocals().toArray();
				String sigfortok = new String(sig);
				String[] toks = sigfortok.split(" ");
				char[] chararr;
				if(sigfortok.contains("="))
				{
					chararr = toks[3].toCharArray();
				}
				else
				{
					chararr = toks[1].toCharArray();
				}

//				for(int k = 0; k<toks.length;k++)
//				{
//					System.out.println(toks[k]);
//				}

				String local = "";
				for (int k = 0; k < chararr.length; k++) {
					if (chararr[k] != '.') {
						local = local + chararr[k];
					} else
						break;
				}
				Local Base = null;
				System.out.println(body.getMethod() + "- local : " + local);
				for (int j = 0; j < locals.length; j++) {
					if (locals[j].toString().equals(local))
						Base = (Local)locals[j];

				}
				if(!local.contains("$"))
				{
					System.out.println("this is primitive");
					System.err.println("this is primitive");
					continue;
				}

				Type baseType = Base.getType();
				String baseClassString = baseType.toString();
				SootClass baseSootClass = Scene.v().getSootClassUnsafe(baseClassString);
				if(baseSootClass == null)
				{
					System.out.println("this is primitive");
					System.err.println("this is primitive");
					continue;
				}

				if(isView(baseSootClass))
				{
					System.out.println("this is View's Child class");
					System.err.println("this is View's Child class");
					continue;
				}
				if (!isView(baseSootClass))  {

					System.out.println(body.getMethod() + "- found base : "+targetUnitString+"\n"+body.getMethod().toString()+"\n");
					System.err.println(body.getMethod() + "- found base : "+targetUnitString+"\n"+body.getMethod().toString()+"\n");

					StringTokenizer stk = new StringTokenizer(targetUnitString,"<>");
					int indexSignature = 0;
					String token = null;
					if(stk.countTokens() >= 2)
					{
						stk.nextToken();
						token = stk.nextToken();

					}
					if(listUIUpdateSignature.contains(token))
					{

					}
					else
					{
						System.out.println(token+" not in UIUpdate Signature List");
						System.err.println(token+" not in UIUpdate Signature List");
						continue;

					}

					hasUpdateCode = true;
					List<Unit> generated = new ArrayList<>();
					List<Unit> generated_catch = new ArrayList<>();
					generated.addAll(InstrumentUtil.generateLogStmts(body,"UI update signature : ",StringConstant.v(targetUnitString)));
					//generated.add(Jimple.v().newAssignStmt(viewVar, body.getParameterLocal(0)));

					SootClass thisActivity = Scene.v().getSootClass(MAIN_ACTIVITY_CLASS);
					SootField fieldDex = thisActivity.getFieldByNameUnsafe("dex");
					if(fieldDex == null)
					{
						thisActivity = body.getMethod().getDeclaringClass().getOuterClass();
						fieldDex = thisActivity.getFieldByNameUnsafe("dex");
						if(fieldDex == null)
						{
							System.err.println("this class has no dex");
							System.out.println("this class has no dex");
							return;
						}

					}
					generated.add(Jimple.v().newAssignStmt(dexLoaderVar, Jimple.v().newStaticFieldRef(fieldDex.makeRef())));
					thisActivity = Scene.v().getSootClass(MAIN_ACTIVITY_CLASS);
					SootField fieldFluidInterface = thisActivity.getFieldByNameUnsafe("objFluidInterface");
					if(fieldFluidInterface == null)
					{
						thisActivity = body.getMethod().getDeclaringClass().getOuterClass();
						fieldFluidInterface = thisActivity.getFieldByNameUnsafe("objFluidInterface");
						if(fieldFluidInterface == null) {
							System.err.println("this class has no objFluidInterface");
							System.out.println("this class has no objFluidInterface");
							return;
						}
					}



					generated.add(Jimple.v().newAssignStmt(objectFluidInterfaceVar,
							Jimple.v().newStaticFieldRef(fieldFluidInterface.makeRef())));

//					generated.addAll(InstrumentUtil.generateLogStmts(body,"Activity",body.getThisLocal()));
//					generated.addAll(InstrumentUtil.generateLogStmts(body,"objFLUIDInterface",objectFluidInterfaceVar));
//					//generated.addAll(InstrumentUtil.generateLogStmts(body,"classLoader",classLoaderVar));
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
					generated.add(Jimple.v().newAssignStmt(viewVar, Base));

					// generated.add(Jimple.v().newAssignStmt(viewVar, (Local)locals[0]));
//					generated.add(Jimple.v().newAssignStmt(signatureVar, StringConstant.v(sig)));
					System.out.println(body.getMethod()+"  token : " + token);
					for(int k = 0; k< listUIUpdateSignature.size(); k++)
					{
						String signature = listUIUpdateSignature.get(k);
						System.out.println(body.getMethod()+"      signature : "+signature);
						if(signature.equals(token))
						{
							System.out.println(body.getMethod()+"     "+token + "\n"+ signature+"\ntrue");
							indexSignature = k;
							generated.add(Jimple.v().newAssignStmt(signatureVar,StringConstant.v(listUIUpdateTargetSignature.get(indexSignature))));

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
						}
					}

//					if(unitarray[i+1].toString().contains("return"))
//					generated.add(Jimple.v().newGotoStmt((Unit)unitarray[i+1]));
//					Unit tryEnd = generated.get(generated.size() - 1);

//					CaughtExceptionRef exceptionRef = soot.jimple.Jimple.v().newCaughtExceptionRef();

//					Unit catchBegin = Jimple.v().newIdentityStmt(exceptionVar, exceptionRef);

//					generated_catch.add(catchBegin);
//					generated.add(catchBegin);

					//return at catch
//					SootClass exceptionClass = Scene.v().getSootClass("java.lang.Exception");

//					generated_catch.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Throwable",
//							"void printStackTrace()", exceptionVar, null));
//					generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Throwable",
//							"void printStackTrace()", exceptionVar, null));
//					generated.add(Jimple.v().newReturnVoidStmt());

					//Unit catchEnd = Jimple.v().newReturnVoidStmt();
					//generated.add(catchEnd);
//					generated.add(Jimple.v().newBreakpointStmt());

//					Unit returnStmt = (Unit) ((Unit)unitarray[i+1]).clone();
//					units.insertBefore(returnStmt, (Unit) unitarray[i+1]);
					units.insertAfter(generated, (Unit) unitarray[i]);
//					units.insertBefore(generated_catch, units.getLast());
//					Trap trap = soot.jimple.Jimple.v().newTrap(exceptionClass, tryBegin, tryEnd, catchBegin);
//					units.insertBefore(generated, units.getSuccOf((Unit)unitarray[i]));

//					body.getTraps().add(trap);
//					System.out.println("trap : "+trap.toString());



				}
			}


		}

		//wrap entire code with try catch
		if(hasUpdateCode == true)
		{
			Unit tryBegin = units.getFirst();
			Unit tryEnd = units.getLast();

			CaughtExceptionRef exceptionRef = soot.jimple.Jimple.v().newCaughtExceptionRef();

			Unit catchBegin = Jimple.v().newIdentityStmt(exceptionVar, exceptionRef);

//					generated_catch.add(catchBegin);
			units.add(catchBegin);

			//return at catch
			SootClass exceptionClass = Scene.v().getSootClass("java.lang.Exception");

//					generated_catch.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Throwable",
//							"void printStackTrace()", exceptionVar, null));
			units.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Throwable",
					"void printStackTrace()", exceptionVar, null));

			Type returnType = body.getMethod().getReturnType();
			SootClass returnTypeClass = Scene.v().getSootClass(returnType.toString());
			SootClass returnTypeSuperClass = returnTypeClass;
			Boolean isReturnTypeRef = false;
			//search if return type is child class of Object
			while(returnTypeSuperClass.hasSuperclass())
			{
				returnTypeSuperClass = returnTypeSuperClass.getSuperclass();
				if(returnTypeSuperClass.toString().contains("Object"))
				{
					isReturnTypeRef = true;
				}

			}
			if(returnType.equals(VoidType.v())) //void type
			{
				units.addLast(Jimple.v().newReturnVoidStmt());
			}
			else if(returnTypeClass.toString().contains("String"))
			{
				units.addLast(Jimple.v().newReturnStmt(StringConstant.v("")));
			}
			else if(isReturnTypeRef) //other ref type
			{
				units.addLast(Jimple.v().newReturnStmt(NullConstant.v()));
			}
			else
			{
				units.addLast(Jimple.v().newReturnStmt(IntConstant.v(0)));
			}


			Trap trap = soot.jimple.Jimple.v().newTrap(exceptionClass, tryBegin, tryEnd, catchBegin);
//					units.insertBefore(generated, units.getSuccOf((Unit)unitarray[i]));

			body.getTraps().add(trap);
		}




		System.out.println("Edited code : "+body.getMethod().toString());
		System.out.println(body.getMethod()+" \n"+body.toString());
		body.validate();

	}



	void injectUpdateCodebySignature(JimpleBody body) {
		UnitPatchingChain units = body.getUnits();
		// List<Unit> generated = new ArrayList<>();

//		Local thisVar = body.getThisLocal();
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
			String targetUnit = unitarray[i].toString();
			if (!targetUnit.contains("goto") &&(targetUnit.contains("setText") || targetUnit.contains("setImage")))  {
				System.out.println("found update code : "+targetUnit+"\n"+body.getMethod().toString()+"\n");
				System.err.println("found update code : "+targetUnit+"\n"+body.getMethod().toString()+"\n");
				List<Unit> generated = new ArrayList<>();

				//generated.add(Jimple.v().newAssignStmt(viewVar, body.getParameterLocal(0)));
				Object[] arrayClasses = Scene.v().getApplicationClasses().toArray();
				SootClass thisClass = body.getMethod().getDeclaringClass();
				SootField fieldDex = thisClass.getFieldByNameUnsafe("dex");
				if(fieldDex == null)
				{
					System.err.println("this class has no dex");
					System.out.println("this class has no dex");
					return;
				}
				generated.add(Jimple.v().newAssignStmt(dexLoaderVar, Jimple.v().newStaticFieldRef(fieldDex.makeRef())));
				SootField fieldFluidInterface = thisClass.getFieldByNameUnsafe("objFluidInterface");
				if(fieldFluidInterface == null)
				{
					System.err.println("this class has no objFluidInterface");
					System.out.println("this class has no objFluidInterface");
					return;
				}
				generated.add(Jimple.v().newAssignStmt(objectFluidInterfaceVar,
						Jimple.v().newStaticFieldRef(fieldFluidInterface.makeRef())));

				generated.addAll(InstrumentUtil.generateLogStmts(body,"Activity",body.getThisLocal()));
				generated.addAll(InstrumentUtil.generateLogStmts(body,"dexclassLoader",dexLoaderVar));


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
//				generated.add(Jimple.v().newGotoStmt(units.getLast()));

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
	void addOnResume(SootClass classActivity)
	{

		List<Type> parameterType = new ArrayList<Type>();
		parameterType.add(VoidType.v());

		Type returnType = VoidType.v();


		SootMethod onResume = new SootMethod("onResume", null, returnType, Modifier.PROTECTED );
		JimpleBody newBody = Jimple.v().newBody(onResume);
		UnitPatchingChain units = newBody.getUnits();
		//Object[] fields = classMainActivity.getFields().toArray();
//		System.err.println("mainactivity : "+classMainActivity.toString());
//		for(int i = 0; i<fields.length;i++)
//		{
//			System.err.println("mainactivity field : ["+i+"] - "+fields[i].toString());
//		}
//		dispatchTouchEvent.setDeclared(true);
//		dispatchTouchEvent.setDeclaringClass(classMainActivity);
		classActivity.addMethod(onResume);
		//newBody.getDefBoxes().add( Jimple.v().newIdentityRefBox(Jimple.v().newThisRef(RefType.v(MAINACTIVITY_CLASS_NAME))) );
		Local thisVar = InstrumentUtil.generateNewLocal(newBody, RefType.v(classActivity.toString()));
		Local paramVar = InstrumentUtil.generateNewLocal(newBody, VoidType.v());
//		Local retVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Boolean"));
		Local retVar = InstrumentUtil.generateNewLocal(newBody, IntType.v());

		units.add( Jimple.v().newIdentityStmt(thisVar, Jimple.v().newThisRef(RefType.v(classActivity.toString()))));
		units.add ( Jimple.v().newIdentityStmt( thisVar, Jimple.v().newParameterRef(null,0) ) );
		//no parameter
		//		units.add( Jimple.v().newIdentityStmt(paramVar, Jimple.v().newParameterRef(VoidType.v(), 0) ));


		//newBody.getDefBoxes().add(Jimple.v().newArgBox((new ThisRef(RefType.v(MAINACTIVITY_CLASS_NAME)))));
		//units.add(Jimple.v().newAssignStmt(thisVar, newBody.getThisLocal()));
		Local thisClassVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Class"));
		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.Object", "java.lang.Class getClass()", thisVar, thisClassVar));
		units.addAll(InstrumentUtil.generateLogStmts(newBody, "onResume of : ", thisClassVar));
		units.addAll(InstrumentUtil.generateLogStmts(newBody,"Method Invocation check"+newBody.getMethod().toString()));





		Local exceptionVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Exception"));
		Local viewVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("android.view.View"));
		Local methodVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.reflect.Method"));
		Local clazzVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Class"));
		Local dexLoaderVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("dalvik.system.DexClassLoader"));
		Local classVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Class"));
		Local classLoaderVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.ClassLoader"));
		Local classStringVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Class"));
		Local classViewVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Class"));
		Local widgetnameVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.String"));
		Local eviewVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("android.view.View"));
		Local classArrayVar = InstrumentUtil.generateNewLocal(newBody, ArrayType.v(RefType.v("java.lang.Class"), 1));
		Local objectArrayVar = InstrumentUtil.generateNewLocal(newBody, ArrayType.v(RefType.v("java.lang.Object"), 1));
		Local objectFluidInterfaceVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Object"));
		Local isPass = InstrumentUtil.generateNewLocal(newBody, IntType.v());
		Local isPassObject = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Object"));
		Local isPassString = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.String"));
		Local isPassInteger = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Integer"));
		Local classAppCompatActivity = InstrumentUtil.generateNewLocal(newBody, Scene.v().getSootClass("androidx.appcompat.app.AppCompatActivity").getType());
		Local clazz = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Class"));
		Local classLoaderVar2 = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.ClassLoader"));
		Local classArrayVar2 = InstrumentUtil.generateNewLocal(newBody, ArrayType.v(RefType.v("java.lang.Class"), 1));
		Local objectArrayVar2 = InstrumentUtil.generateNewLocal(newBody, ArrayType.v(RefType.v("java.lang.Object"), 1));
		Local methodVar2 = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.reflect.Method"));
		Local retObjVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Object"));

		//System.out.println("onLongClick : ");
		SootClass clsClass = Scene.v().getSootClass("java.lang.Class");
		SootClass clsObject = Scene.v().getSootClass("java.lang.Object");
		// printLocals(body);

		// get parameter
		//generated.add(Jimple.v().newAssignStmt(viewVar, newBody.getParameterLocal(0)));

		// get dexloader from field

		SootField fieldDex = classActivity.getFieldByName("dex");
		units.add(Jimple.v().newAssignStmt(dexLoaderVar, Jimple.v().newStaticFieldRef(fieldDex.makeRef())));


		units.addAll(InstrumentUtil.generateLogStmts(newBody,"dex before : ", dexLoaderVar));


		SootField fieldFluidInterface = classActivity.getFieldByName("objFluidInterface");
		units.add(Jimple.v().newAssignStmt(objectFluidInterfaceVar,
				Jimple.v().newStaticFieldRef(fieldFluidInterface.makeRef())));

		units.addAll(InstrumentUtil.generateLogStmts(newBody,"dex before : ", dexLoaderVar));

		units.addAll(InstrumentUtil.generateSpecialInvokeStmt(newBody, "android.app.Activity", "void onResume()", newBody.getThisLocal(), null));

		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.ClassLoader",
				"java.lang.Class loadClass(java.lang.String)", dexLoaderVar, classVar,
				StringConstant.v(FLUID_MAIN_CLASS)));



		Unit tryBegin = units.getLast();
		SootClass cls = Scene.v().getSootClass("java.lang.Class");
		units.add(
				Jimple.v().newAssignStmt(classArrayVar, Jimple.v().newNewArrayExpr(cls.getType(), IntConstant.v(1))));
		// put class to class array
		// generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body,
		// "java.lang.Object", "java.lang.Class getClass()", widgetnameVar,
		// classStringVar));
		units.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(classArrayVar, IntConstant.v(0)),
				ClassConstant.v("Landroid/content/Context;")));
		// generated.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(classArrayVar,
		// IntConstant.v(1)), ClassConstant.v("Landroid/view/View;")));
		// get getInstance
		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.Class",
				"java.lang.reflect.Method getDeclaredMethod(java.lang.String,java.lang.Class[])", classVar, methodVar,
				StringConstant.v("getInstance"), classArrayVar));
		// create object array for invoke
		SootClass cls2 = Scene.v().getSootClass("java.lang.Object");
		units.add(
				Jimple.v().newAssignStmt(objectArrayVar, Jimple.v().newNewArrayExpr(cls2.getType(), IntConstant.v(1))));
		units.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(objectArrayVar, IntConstant.v(0)), thisVar));
		// invoke getInstance
		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.reflect.Method",
				"java.lang.Object invoke(java.lang.Object,java.lang.Object[])", methodVar, objectFluidInterfaceVar,
				NullConstant.v(), objectArrayVar));
		units.add(Jimple.v().newAssignStmt(Jimple.v().newStaticFieldRef(fieldFluidInterface.makeRef()),
				objectFluidInterfaceVar));
		// create Class array for getDeclaredMethod
		// SootClass cls = Scene.v().getSootClass("java.lang.Class");
		units.add(
				Jimple.v().newAssignStmt(classArrayVar, Jimple.v().newNewArrayExpr(cls.getType(), IntConstant.v(1))));
		// put class to class array
		// generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body,
		// "java.lang.Object", "java.lang.Class getClass()", widgetnameVar,
		// classStringVar));
		units.add(
				Jimple.v().newAssignStmt(Jimple.v().newArrayRef(classArrayVar, IntConstant.v(0)), NullConstant.v()));
		// generated.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(classArrayVar,
		// IntConstant.v(1)), ClassConstant.v("Landroid/view/View;")));
		// get getInstance
		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.Class",
				"java.lang.reflect.Method getDeclaredMethod(java.lang.String,java.lang.Class[])", classVar, methodVar,
				StringConstant.v("runBind"), NullConstant.v()));
		// create object array for invoke
		// SootClass cls2 = Scene.v().getSootClass("java.lang.Object");
		units.add(
				Jimple.v().newAssignStmt(objectArrayVar, Jimple.v().newNewArrayExpr(cls2.getType(), IntConstant.v(1))));
		units.add(
				Jimple.v().newAssignStmt(Jimple.v().newArrayRef(objectArrayVar, IntConstant.v(0)), NullConstant.v()));
		// invoke getInstance
		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.reflect.Method",
				"java.lang.Object invoke(java.lang.Object,java.lang.Object[])", methodVar, null,
				objectFluidInterfaceVar, NullConstant.v()));
		units.add(Jimple.v().newAssignStmt(objectFluidInterfaceVar,Jimple.v().newStaticFieldRef(fieldFluidInterface.makeRef())));
		// insert new code
		units.add(Jimple.v().newReturnVoidStmt());
		Unit tryEnd = units.getLast();
		// insert try-catch statement
		CaughtExceptionRef exceptionRef = soot.jimple.Jimple.v().newCaughtExceptionRef();
		Unit catchBegin = Jimple.v().newIdentityStmt(exceptionVar, exceptionRef);
		units.add(catchBegin);
		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.Throwable", "void printStackTrace()",
				exceptionVar, null));
		units.add(Jimple.v().newReturnVoidStmt());
		SootClass exceptionClass = Scene.v().getSootClass("java.lang.Exception");
		Trap trap = soot.jimple.Jimple.v().newTrap(exceptionClass, tryBegin, tryEnd, catchBegin);
		newBody.getTraps().add(trap);

		// validate the instrumented code

		newBody.validate();
		onResume.setActiveBody(newBody);
		System.out.println("add onResume done");
		System.err.println("add onResume done");
	}

	void addOnCreateTypeB(SootClass classActivity, SootClass classMainActivity)
	{
		List<Type> parameterType = new ArrayList<Type>();
		parameterType.add(Scene.v().getSootClass("android.os.Bundle").getType());

		Type returnType = VoidType.v();


		SootMethod onCreate = new SootMethod("onCreate", parameterType, returnType, Modifier.PROTECTED );
		JimpleBody newBody = Jimple.v().newBody(onCreate);
		UnitPatchingChain units = newBody.getUnits();
		//Object[] fields = classMainActivity.getFields().toArray();
//		System.err.println("mainactivity : "+classMainActivity.toString());
//		for(int i = 0; i<fields.length;i++)
//		{
//			System.err.println("mainactivity field : ["+i+"] - "+fields[i].toString());
//		}
//		dispatchTouchEvent.setDeclared(true);
//		dispatchTouchEvent.setDeclaringClass(classMainActivity);
		classActivity.addMethod(onCreate);
		//newBody.getDefBoxes().add( Jimple.v().newIdentityRefBox(Jimple.v().newThisRef(RefType.v(MAINACTIVITY_CLASS_NAME))) );
		Local thisVar = InstrumentUtil.generateNewLocal(newBody, RefType.v(classActivity.toString()));
		Local paramVar = InstrumentUtil.generateNewLocal(newBody, Scene.v().getSootClass("android.os.Bundle").getType());
//		Local retVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Boolean"));
		Local retVar = InstrumentUtil.generateNewLocal(newBody, IntType.v());

		units.add( Jimple.v().newIdentityStmt(thisVar, Jimple.v().newThisRef(RefType.v(classActivity.toString()))));
		units.add( Jimple.v().newIdentityStmt(paramVar, Jimple.v().newParameterRef(Scene.v().getSootClass("android.os.Bundle").getType(), 0) ));
		//newBody.getDefBoxes().add(Jimple.v().newArgBox((new ThisRef(RefType.v(MAINACTIVITY_CLASS_NAME)))));
		//units.add(Jimple.v().newAssignStmt(thisVar, newBody.getThisLocal()));
		Local thisClassVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Class"));
		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.Object", "java.lang.Class getClass()", thisVar, thisClassVar));
		units.addAll(InstrumentUtil.generateLogStmts(newBody, "onCreate of : ", thisClassVar));
		units.addAll(InstrumentUtil.generateLogStmts(newBody,"Method Invocation check"+newBody.getMethod().toString()));




		Local exceptionVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Exception"));
		Local viewVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("android.view.View"));
		Local methodVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.reflect.Method"));
		Local clazzVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Class"));
		Local dexLoaderVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("dalvik.system.DexClassLoader"));
		Local classVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Class"));
		Local classLoaderVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.ClassLoader"));
		Local classStringVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Class"));
		Local classViewVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Class"));
		Local widgetnameVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.String"));
		Local eviewVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("android.view.View"));
		Local classArrayVar = InstrumentUtil.generateNewLocal(newBody, ArrayType.v(RefType.v("java.lang.Class"), 1));
		Local objectArrayVar = InstrumentUtil.generateNewLocal(newBody, ArrayType.v(RefType.v("java.lang.Object"), 1));
		Local objectFluidInterfaceVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Object"));
		Local isPass = InstrumentUtil.generateNewLocal(newBody, IntType.v());
		Local isPassObject = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Object"));
		Local isPassString = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.String"));
		Local isPassInteger = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Integer"));
		Local classAppCompatActivity = InstrumentUtil.generateNewLocal(newBody, Scene.v().getSootClass("androidx.appcompat.app.AppCompatActivity").getType());
		Local clazz = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Class"));
		Local classLoaderVar2 = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.ClassLoader"));
		Local classArrayVar2 = InstrumentUtil.generateNewLocal(newBody, ArrayType.v(RefType.v("java.lang.Class"), 1));
		Local objectArrayVar2 = InstrumentUtil.generateNewLocal(newBody, ArrayType.v(RefType.v("java.lang.Object"), 1));
		Local methodVar2 = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.reflect.Method"));
		Local retObjVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Object"));

		//System.out.println("onLongClick : ");
		SootClass clsClass = Scene.v().getSootClass("java.lang.Class");
		SootClass clsObject = Scene.v().getSootClass("java.lang.Object");
		// printLocals(body);

		// get parameter
		//generated.add(Jimple.v().newAssignStmt(viewVar, newBody.getParameterLocal(0)));

		// get dexloader from field

		SootField fieldDex = classMainActivity.getFieldByName("dex");
		units.add(Jimple.v().newAssignStmt(dexLoaderVar, Jimple.v().newStaticFieldRef(fieldDex.makeRef())));

		SootField fieldFluidInterface = classMainActivity.getFieldByName("objFluidInterface");
		units.add(Jimple.v().newAssignStmt(objectFluidInterfaceVar,
				Jimple.v().newStaticFieldRef(fieldFluidInterface.makeRef())));

		units.addAll(InstrumentUtil.generateSpecialInvokeStmt(newBody, "android.app.Activity", "void onCreate(android.os.Bundle)", newBody.getThisLocal(), null,newBody.getParameterLocal(0)));

		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.ClassLoader",
				"java.lang.Class loadClass(java.lang.String)", dexLoaderVar, classVar,
				StringConstant.v(FLUID_MAIN_CLASS)));



		Unit tryBegin = units.getLast();
		SootClass cls = Scene.v().getSootClass("java.lang.Class");
		units.add(
				Jimple.v().newAssignStmt(classArrayVar, Jimple.v().newNewArrayExpr(cls.getType(), IntConstant.v(1))));
		// put class to class array
		// generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body,
		// "java.lang.Object", "java.lang.Class getClass()", widgetnameVar,
		// classStringVar));
		units.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(classArrayVar, IntConstant.v(0)),
				ClassConstant.v("Landroid/content/Context;")));
		// generated.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(classArrayVar,
		// IntConstant.v(1)), ClassConstant.v("Landroid/view/View;")));
		// get getInstance
		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.Class",
				"java.lang.reflect.Method getDeclaredMethod(java.lang.String,java.lang.Class[])", classVar, methodVar,
				StringConstant.v("getInstance"), classArrayVar));
		// create object array for invoke
		SootClass cls2 = Scene.v().getSootClass("java.lang.Object");
		units.add(
				Jimple.v().newAssignStmt(objectArrayVar, Jimple.v().newNewArrayExpr(cls2.getType(), IntConstant.v(1))));
		units.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(objectArrayVar, IntConstant.v(0)), thisVar));
		// invoke getInstance
		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.reflect.Method",
				"java.lang.Object invoke(java.lang.Object,java.lang.Object[])", methodVar, objectFluidInterfaceVar,
				NullConstant.v(), objectArrayVar));
		units.add(Jimple.v().newAssignStmt(Jimple.v().newStaticFieldRef(fieldFluidInterface.makeRef()),
				objectFluidInterfaceVar));
		// create Class array for getDeclaredMethod
		// SootClass cls = Scene.v().getSootClass("java.lang.Class");
		units.add(
				Jimple.v().newAssignStmt(classArrayVar, Jimple.v().newNewArrayExpr(cls.getType(), IntConstant.v(1))));
		// put class to class array
		// generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body,
		// "java.lang.Object", "java.lang.Class getClass()", widgetnameVar,
		// classStringVar));
		units.add(
				Jimple.v().newAssignStmt(Jimple.v().newArrayRef(classArrayVar, IntConstant.v(0)), NullConstant.v()));
		// generated.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(classArrayVar,
		// IntConstant.v(1)), ClassConstant.v("Landroid/view/View;")));
		// get getInstance
		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.Class",
				"java.lang.reflect.Method getDeclaredMethod(java.lang.String,java.lang.Class[])", classVar, methodVar,
				StringConstant.v("runBind"), NullConstant.v()));
		// create object array for invoke
		// SootClass cls2 = Scene.v().getSootClass("java.lang.Object");
		units.add(
				Jimple.v().newAssignStmt(objectArrayVar, Jimple.v().newNewArrayExpr(cls2.getType(), IntConstant.v(1))));
		units.add(
				Jimple.v().newAssignStmt(Jimple.v().newArrayRef(objectArrayVar, IntConstant.v(0)), NullConstant.v()));
		// invoke getInstance
		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.reflect.Method",
				"java.lang.Object invoke(java.lang.Object,java.lang.Object[])", methodVar, null,
				objectFluidInterfaceVar, NullConstant.v()));
		units.add(Jimple.v().newAssignStmt(objectFluidInterfaceVar,Jimple.v().newStaticFieldRef(fieldFluidInterface.makeRef())));
		// insert new code
		units.add(Jimple.v().newReturnVoidStmt());
		Unit tryEnd = units.getLast();
		// insert try-catch statement
		CaughtExceptionRef exceptionRef = soot.jimple.Jimple.v().newCaughtExceptionRef();
		Unit catchBegin = Jimple.v().newIdentityStmt(exceptionVar, exceptionRef);
		units.add(catchBegin);
		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.Throwable", "void printStackTrace()",
				exceptionVar, null));
		units.add(Jimple.v().newReturnVoidStmt());
		SootClass exceptionClass = Scene.v().getSootClass("java.lang.Exception");
		Trap trap = soot.jimple.Jimple.v().newTrap(exceptionClass, tryBegin, tryEnd, catchBegin);
		newBody.getTraps().add(trap);

		// validate the instrumented code

		newBody.validate();
		onCreate.setActiveBody(newBody);
		System.out.println("add onCreate done");
		System.err.println("add onCreate done");
	}

	void addOnCreateTypeA(SootClass classActivity)
	{
		List<Type> parameterType = new ArrayList<Type>();
		parameterType.add(Scene.v().getSootClass("android.os.Bundle").getType());

		Type returnType = VoidType.v();


		SootMethod onCreate = new SootMethod("onCreate", parameterType, returnType, Modifier.PROTECTED );
		JimpleBody newBody = Jimple.v().newBody(onCreate);
		UnitPatchingChain units = newBody.getUnits();
		//Object[] fields = classMainActivity.getFields().toArray();
//		System.err.println("mainactivity : "+classMainActivity.toString());
//		for(int i = 0; i<fields.length;i++)
//		{
//			System.err.println("mainactivity field : ["+i+"] - "+fields[i].toString());
//		}
//		dispatchTouchEvent.setDeclared(true);
//		dispatchTouchEvent.setDeclaringClass(classMainActivity);
		classActivity.addMethod(onCreate);
		//newBody.getDefBoxes().add( Jimple.v().newIdentityRefBox(Jimple.v().newThisRef(RefType.v(MAINACTIVITY_CLASS_NAME))) );
		Local thisVar = InstrumentUtil.generateNewLocal(newBody, RefType.v(classActivity.toString()));
		Local paramVar = InstrumentUtil.generateNewLocal(newBody, Scene.v().getSootClass("android.os.Bundle").getType());
//		Local retVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Boolean"));
		Local retVar = InstrumentUtil.generateNewLocal(newBody, IntType.v());

		units.add( Jimple.v().newIdentityStmt(thisVar, Jimple.v().newThisRef(RefType.v(classActivity.toString()))));
		units.add( Jimple.v().newIdentityStmt(paramVar, Jimple.v().newParameterRef(Scene.v().getSootClass("android.os.Bundle").getType(), 0) ));
		//newBody.getDefBoxes().add(Jimple.v().newArgBox((new ThisRef(RefType.v(MAINACTIVITY_CLASS_NAME)))));
		//units.add(Jimple.v().newAssignStmt(thisVar, newBody.getThisLocal()));
		Local thisClassVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Class"));
		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.Object", "java.lang.Class getClass()", thisVar, thisClassVar));
		units.addAll(InstrumentUtil.generateLogStmts(newBody, "onCreate of : ", thisClassVar));
		units.addAll(InstrumentUtil.generateLogStmts(newBody,"Method Invocation check"+newBody.getMethod().toString()));




		Local exceptionVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Exception"));
		Local viewVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("android.view.View"));
		Local methodVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.reflect.Method"));
		Local clazzVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Class"));
		Local dexLoaderVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("dalvik.system.DexClassLoader"));
		Local classVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Class"));
		Local classLoaderVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.ClassLoader"));
		Local classStringVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Class"));
		Local classViewVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Class"));
		Local widgetnameVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.String"));
		Local eviewVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("android.view.View"));
		Local classArrayVar = InstrumentUtil.generateNewLocal(newBody, ArrayType.v(RefType.v("java.lang.Class"), 1));
		Local objectArrayVar = InstrumentUtil.generateNewLocal(newBody, ArrayType.v(RefType.v("java.lang.Object"), 1));
		Local objectFluidInterfaceVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Object"));
		Local isPass = InstrumentUtil.generateNewLocal(newBody, IntType.v());
		Local isPassObject = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Object"));
		Local isPassString = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.String"));
		Local isPassInteger = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Integer"));
		Local classAppCompatActivity = InstrumentUtil.generateNewLocal(newBody, Scene.v().getSootClass("androidx.appcompat.app.AppCompatActivity").getType());
		Local clazz = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Class"));
		Local classLoaderVar2 = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.ClassLoader"));
		Local classArrayVar2 = InstrumentUtil.generateNewLocal(newBody, ArrayType.v(RefType.v("java.lang.Class"), 1));
		Local objectArrayVar2 = InstrumentUtil.generateNewLocal(newBody, ArrayType.v(RefType.v("java.lang.Object"), 1));
		Local methodVar2 = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.reflect.Method"));
		Local retObjVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Object"));

		//System.out.println("onLongClick : ");
		SootClass clsClass = Scene.v().getSootClass("java.lang.Class");
		SootClass clsObject = Scene.v().getSootClass("java.lang.Object");
		// printLocals(body);

		// get parameter
		//generated.add(Jimple.v().newAssignStmt(viewVar, newBody.getParameterLocal(0)));

		// get dexloader from field

		SootField fieldDex = classActivity.getFieldByName("dex");
		units.add(Jimple.v().newAssignStmt(dexLoaderVar, Jimple.v().newStaticFieldRef(fieldDex.makeRef())));
		SootField fieldFluidInterface = classActivity.getFieldByName("objFluidInterface");
		units.add(Jimple.v().newAssignStmt(objectFluidInterfaceVar,
				Jimple.v().newStaticFieldRef(fieldFluidInterface.makeRef())));

		// create DexClassLoader instance
		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.Object",
				"java.lang.Class getClass()", thisVar, classVar));
		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.Class",
				"java.lang.ClassLoader getClassLoader()", classVar, classLoaderVar));
		units.addAll(InstrumentUtil.generateNewInstance(newBody, "dalvik.system.DexClassLoader",
				"void <init>(java.lang.String,java.lang.String,java.lang.String,java.lang.ClassLoader)", dexLoaderVar,
				StringConstant.v(FLUID_LIB_PATH), StringConstant.v(TMP_DIR_PATH), NullConstant.v(), classLoaderVar));

		units.addAll(InstrumentUtil.generateSpecialInvokeStmt(newBody, "android.app.Activity", "void onCreate(android.os.Bundle)", newBody.getThisLocal(), null,newBody.getParameterLocal(0)));

		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.ClassLoader",
				"java.lang.Class loadClass(java.lang.String)", dexLoaderVar, classVar,
				StringConstant.v(FLUID_MAIN_CLASS)));



		Unit tryBegin = units.getLast();
		SootClass cls = Scene.v().getSootClass("java.lang.Class");
		units.add(
				Jimple.v().newAssignStmt(classArrayVar, Jimple.v().newNewArrayExpr(cls.getType(), IntConstant.v(1))));
		// put class to class array
		// generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body,
		// "java.lang.Object", "java.lang.Class getClass()", widgetnameVar,
		// classStringVar));
		units.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(classArrayVar, IntConstant.v(0)),
				ClassConstant.v("Landroid/content/Context;")));
		// generated.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(classArrayVar,
		// IntConstant.v(1)), ClassConstant.v("Landroid/view/View;")));
		// get getInstance
		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.Class",
				"java.lang.reflect.Method getDeclaredMethod(java.lang.String,java.lang.Class[])", classVar, methodVar,
				StringConstant.v("getInstance"), classArrayVar));
		// create object array for invoke
		SootClass cls2 = Scene.v().getSootClass("java.lang.Object");
		units.add(
				Jimple.v().newAssignStmt(objectArrayVar, Jimple.v().newNewArrayExpr(cls2.getType(), IntConstant.v(1))));
		units.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(objectArrayVar, IntConstant.v(0)), thisVar));
		// invoke getInstance
		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.reflect.Method",
				"java.lang.Object invoke(java.lang.Object,java.lang.Object[])", methodVar, objectFluidInterfaceVar,
				NullConstant.v(), objectArrayVar));
		units.add(Jimple.v().newAssignStmt(Jimple.v().newStaticFieldRef(fieldFluidInterface.makeRef()),
				objectFluidInterfaceVar));
		// create Class array for getDeclaredMethod
		// SootClass cls = Scene.v().getSootClass("java.lang.Class");
		units.add(
				Jimple.v().newAssignStmt(classArrayVar, Jimple.v().newNewArrayExpr(cls.getType(), IntConstant.v(1))));
		// put class to class array
		// generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body,
		// "java.lang.Object", "java.lang.Class getClass()", widgetnameVar,
		// classStringVar));
		units.add(
				Jimple.v().newAssignStmt(Jimple.v().newArrayRef(classArrayVar, IntConstant.v(0)), NullConstant.v()));
		// generated.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(classArrayVar,
		// IntConstant.v(1)), ClassConstant.v("Landroid/view/View;")));
		// get getInstance
		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.Class",
				"java.lang.reflect.Method getDeclaredMethod(java.lang.String,java.lang.Class[])", classVar, methodVar,
				StringConstant.v("runBind"), NullConstant.v()));
		// create object array for invoke
		// SootClass cls2 = Scene.v().getSootClass("java.lang.Object");
		units.add(
				Jimple.v().newAssignStmt(objectArrayVar, Jimple.v().newNewArrayExpr(cls2.getType(), IntConstant.v(1))));
		units.add(
				Jimple.v().newAssignStmt(Jimple.v().newArrayRef(objectArrayVar, IntConstant.v(0)), NullConstant.v()));
		// invoke getInstance
		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.reflect.Method",
				"java.lang.Object invoke(java.lang.Object,java.lang.Object[])", methodVar, null,
				objectFluidInterfaceVar, NullConstant.v()));
		 units.add(Jimple.v().newAssignStmt(objectFluidInterfaceVar,Jimple.v().newStaticFieldRef(fieldFluidInterface.makeRef())));
		// insert new code
		units.add(Jimple.v().newReturnVoidStmt());
		Unit tryEnd = units.getLast();
		// insert try-catch statement
		CaughtExceptionRef exceptionRef = soot.jimple.Jimple.v().newCaughtExceptionRef();
		Unit catchBegin = Jimple.v().newIdentityStmt(exceptionVar, exceptionRef);
		units.add(catchBegin);
		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.Throwable", "void printStackTrace()",
				exceptionVar, null));
		units.add(Jimple.v().newReturnVoidStmt());
		SootClass exceptionClass = Scene.v().getSootClass("java.lang.Exception");
		Trap trap = soot.jimple.Jimple.v().newTrap(exceptionClass, tryBegin, tryEnd, catchBegin);
		newBody.getTraps().add(trap);

		// validate the instrumented code

		newBody.validate();
		onCreate.setActiveBody(newBody);
		System.out.println("add onCreate done");
		System.err.println("add onCreate done");
	}

	void injectOnCreateTypeB(JimpleBody body,SootClass classActivity, SootClass classMainActivity) {
		UnitPatchingChain units = body.getUnits();
		List<Unit> generated = new ArrayList<>();
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
//		Local IDVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Integer"));
		Local IDVar = InstrumentUtil.generateNewLocal(body, IntType.v());
		//Object[] arrayClasses = Scene.v().getApplicationClasses().toArray();

		SootField fieldDex = classMainActivity.getFieldByName("dex");
		generated.add(Jimple.v().newAssignStmt(dexLoaderVar,Jimple.v().newStaticFieldRef(fieldDex.makeRef())));
		SootField fieldFluidInterface = classMainActivity.getFieldByName("objFluidInterface");
		generated.add(Jimple.v().newAssignStmt(objectFluidInterfaceVar,Jimple.v().newStaticFieldRef(fieldFluidInterface.makeRef())));


//		for(int i = 0; i<arrayClasses.length; i++)
//		{
//			if(arrayClasses[i].toString().equals(MAIN_PACKAGE_NAME+".R$id"))
//			{
//				System.err.println("found id");
//				SootField fieldID = ((SootClass)arrayClasses[i]).getFieldByName("rootlayout");
//				generated.add(Jimple.v().newAssignStmt(IDVar, Jimple.v().newStaticFieldRef(fieldID.makeRef())));
//			}
//		}
//
//		//setonTouchListener
//		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "androidx.appcompat.app.AppCompatActivity",
//				"android.view.View findViewById(int)", thisVar, viewVar, IDVar));
//		//TODO
//		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "android.view.View",
//				"void setOnTouchListener(android.view.View$OnTouchListener)", viewVar, null,
//				Jimple.v().newNewExpr(RefType.v(inject_onTouchClass()))
//				));

		//TODO: override dispatchTouchEvent

		generated.addAll(InstrumentUtil.generateLogStmts(body,"Method Invocation check"+body.getMethod().toString()));


		// copy dexLoaderVar to this.dex field
//		generated.add(Jimple.v().newAssignStmt(Jimple.v().newStaticFieldRef(fieldDex.makeRef()),dexLoaderVar));
		// generated.add(Jimple.v().newAssignStmt(Jimple.v().newStaticFieldRef(fieldFluidInterface.makeRef()),objectFluidInterfaceVar));
		// create FLUID instance
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.ClassLoader",
				"java.lang.Class loadClass(java.lang.String)", dexLoaderVar, classVar,
				StringConstant.v(FLUID_MAIN_CLASS)));
		Unit tryBegin = generated.get(generated.size() - 1);
		generated.addAll(InstrumentUtil.generateLogStmts(body,"after loadClass"));
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
//		generated.add(Jimple.v().newGotoStmt(units.getLast()));
		generated.add(Jimple.v().newAssignStmt(objectFluidInterfaceVar,Jimple.v().newStaticFieldRef(fieldFluidInterface.makeRef())));
		// insert new code
		units.insertAfter(generated, units.getSuccOf( units.getFirst() ));
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
		System.out.println("edit onCreate done");
		System.err.println("edit onCreate done");
	}

	void injectOnCreateTypeA(JimpleBody body,SootClass classActivity) {
		System.out.println("injectOnCreateTypeA Start");
		UnitPatchingChain units = body.getUnits();
		List<Unit> generated = new ArrayList<>();
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
//		Local IDVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Integer"));
		Local IDVar = InstrumentUtil.generateNewLocal(body, IntType.v());
//		Object[] arrayClasses = Scene.v().getApplicationClasses().toArray();
		System.out.println("injectOnCreateTypeA: field referencing start");
		SootField fieldDex = classActivity.getFieldByName("dex");
		generated.add(Jimple.v().newAssignStmt(dexLoaderVar,Jimple.v().newStaticFieldRef(fieldDex.makeRef())));
		SootField fieldFluidInterface = classActivity.getFieldByName("objFluidInterface");
		System.out.println("injectOnCreateTypeA: field referencing done");

//		for(int i = 0; i<arrayClasses.length; i++)
//		{
//			if(arrayClasses[i].toString().equals(MAIN_PACKAGE_NAME+".R$id"))
//			{
//				System.err.println("found id");
//				SootField fieldID = ((SootClass)arrayClasses[i]).getFieldByName("rootlayout");
//				generated.add(Jimple.v().newAssignStmt(IDVar, Jimple.v().newStaticFieldRef(fieldID.makeRef())));
//			}
//		}
//
//		//setonTouchListener
//		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "androidx.appcompat.app.AppCompatActivity",
//				"android.view.View findViewById(int)", thisVar, viewVar, IDVar));
//		//TODO
//		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "android.view.View",
//				"void setOnTouchListener(android.view.View$OnTouchListener)", viewVar, null,
//				Jimple.v().newNewExpr(RefType.v(inject_onTouchClass()))
//				));

		//TODO: override dispatchTouchEvent

		generated.addAll(InstrumentUtil.generateLogStmts(body,"Method Invocation check"+body.getMethod().toString()));

		// create DexClassLoader instance
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Object",
				"java.lang.Class getClass()", thisVar, classVar));
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Class",
				"java.lang.ClassLoader getClassLoader()", classVar, classLoaderVar));
		generated.addAll(InstrumentUtil.generateNewInstance(body, "dalvik.system.DexClassLoader",
				"void <init>(java.lang.String,java.lang.String,java.lang.String,java.lang.ClassLoader)", dexLoaderVar,
				StringConstant.v(FLUID_LIB_PATH), StringConstant.v(TMP_DIR_PATH), NullConstant.v(), classLoaderVar));
		// copy dexLoaderVar to this.dex field
		generated.add(Jimple.v().newAssignStmt(Jimple.v().newStaticFieldRef(fieldDex.makeRef()),dexLoaderVar));
		// generated.add(Jimple.v().newAssignStmt(Jimple.v().newStaticFieldRef(fieldFluidInterface.makeRef()),objectFluidInterfaceVar));
		// create FLUID instance
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.ClassLoader",
				"java.lang.Class loadClass(java.lang.String)", dexLoaderVar, classVar,
				StringConstant.v(FLUID_MAIN_CLASS)));
		Unit tryBegin = generated.get(generated.size() - 1);
		generated.addAll(InstrumentUtil.generateLogStmts(body,"after loadClass"));
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
//		generated.add(Jimple.v().newGotoStmt(units.getLast()));
		 generated.add(Jimple.v().newAssignStmt(objectFluidInterfaceVar,Jimple.v().newStaticFieldRef(fieldFluidInterface.makeRef())));
		// insert new code
		units.insertAfter(generated, units.getSuccOf( units.getFirst() ));
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
		System.out.println("injectOnCreateTypeA validate");
		body.validate();
		System.out.println("edit onCreate done");
		System.err.println("edit onCreate done");
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
	void editDispatchTouchEventTypeA(JimpleBody body, SootClass classActivity)
	{
		UnitPatchingChain units = body.getUnits();
		List<Unit> generated = new ArrayList<>();
		Local retVar = InstrumentUtil.generateNewLocal(body, IntType.v());
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
		Local isPass = InstrumentUtil.generateNewLocal(body, IntType.v());
		Local isPassObject = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Object"));
		Local isPassString = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.String"));
		Local isPassInteger = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Integer"));
		Local classAppCompatActivity = InstrumentUtil.generateNewLocal(body, Scene.v().getSootClass("androidx.appcompat.app.AppCompatActivity").getType());
		Local clazz = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Class"));
		Local classLoaderVar2 = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.ClassLoader"));
		Local classArrayVar2 = InstrumentUtil.generateNewLocal(body, ArrayType.v(RefType.v("java.lang.Class"), 1));
		Local objectArrayVar2 = InstrumentUtil.generateNewLocal(body, ArrayType.v(RefType.v("java.lang.Object"), 1));
		Local methodVar2 = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.reflect.Method"));
		Local retObjVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Object"));

		//System.out.println("onLongClick : ");
		SootClass clsClass = Scene.v().getSootClass("java.lang.Class");
		SootClass clsObject = Scene.v().getSootClass("java.lang.Object");
		// printLocals(body);

		// get parameter
		//generated.add(Jimple.v().newAssignStmt(viewVar, newBody.getParameterLocal(0)));

		// get dexloader from field

		SootField fieldDex = classActivity.getFieldByName("dex");
		generated.add(Jimple.v().newAssignStmt(dexLoaderVar, Jimple.v().newStaticFieldRef(fieldDex.makeRef())));
		SootField fieldFluidInterface = classActivity.getFieldByName("objFluidInterface");
		generated.add(Jimple.v().newAssignStmt(objectFluidInterfaceVar,
				Jimple.v().newStaticFieldRef(fieldFluidInterface.makeRef())));


		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.ClassLoader",
				"java.lang.Class loadClass(java.lang.String)", dexLoaderVar, classVar,
				StringConstant.v(FLUID_MAIN_CLASS)));
		Unit tryBegin = units.getLast();

		//reflect super.dispatchTouchEvent
//		units.add(Jimple.v().newAssignStmt(clazz, ClassConstant.v("Landroid/app/Activity;")));
//
//		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "android.content.ContextWrapper",
//				"java.lang.ClassLoader getClassLoader()", newBody.getThisLocal(), classLoaderVar2));
//		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.ClassLoader",
//				"java.lang.Class loadClass(java.lang.String)", classLoaderVar2, clazz,
//				StringConstant.v("Landroidx/appcompat/app/AppCompatActivity")));

//		units.add(
//				Jimple.v().newAssignStmt(classArrayVar2, Jimple.v().newNewArrayExpr(clsClass.getType(), IntConstant.v(1))));
//		units.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(classArrayVar2, IntConstant.v(0)),
//				ClassConstant.v("Landroid/view/MotionEvent;")));
//		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.Class",
//				"java.lang.reflect.Method getDeclaredMethod(java.lang.String,java.lang.Class[])",
//				clazz, methodVar2,
//				StringConstant.v("dispatchTouchEvent"), classArrayVar2));
//
//		units.add(
//				Jimple.v().newAssignStmt(objectArrayVar2, Jimple.v().newNewArrayExpr(clsObject.getType(), IntConstant.v(1))));
//		units.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(objectArrayVar2, IntConstant.v(0)), newBody.getParameterLocal(0)));
//		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.reflect.Method",
//				"java.lang.Object invoke(java.lang.Object,java.lang.Object[])", methodVar2, retObjVar,
//				newBody.getThisLocal(), objectArrayVar2));
//		units.add(Jimple.v().newAssignStmt(retVar, Jimple.v().newCastExpr(retObjVar, Scene.v().getSootClass("java.lang.Boolean").getType()) ));
//


		// create Class array for getDeclaredMethod
//		SootClass cls = Scene.v().getSootClass("java.lang.Class");
		generated.add(
				Jimple.v().newAssignStmt(classArrayVar, Jimple.v().newNewArrayExpr(clsClass.getType(), IntConstant.v(1))));

		// put class to class array
		// generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body,
		// "java.lang.Object", "java.lang.Class getClass()", widgetnameVar,
		// classStringVar));
		generated.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(classArrayVar, IntConstant.v(0)),
				ClassConstant.v("Landroid/view/MotionEvent;")));

		// get runtest
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Class",
				"java.lang.reflect.Method getDeclaredMethod(java.lang.String,java.lang.Class[])", classVar, methodVar,
				StringConstant.v("runTouchCheck"), classArrayVar));

		// create object array for invoke
//		SootClass cls2 = Scene.v().getSootClass("java.lang.Object");
		generated.add(
				Jimple.v().newAssignStmt(objectArrayVar, Jimple.v().newNewArrayExpr(clsObject.getType(), IntConstant.v(1))));
		generated.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(objectArrayVar, IntConstant.v(0)), body.getParameterLocal(0)));
		// invoke runtest
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.reflect.Method",
				"java.lang.Object invoke(java.lang.Object,java.lang.Object[])", methodVar, isPassObject,
				objectFluidInterfaceVar, objectArrayVar));
		generated.add(Jimple.v().newAssignStmt(isPassInteger, Jimple.v().newCastExpr(isPassObject, RefType.v("java.lang.Integer"))));
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Integer", "java.lang.String toString()", isPassInteger, isPassString));
		generated.addAll(InstrumentUtil.generateStaticInvokeStmt(body, "java.lang.Integer", "int parseInt(java.lang.String)", isPass, isPassString));
		generated.addAll(InstrumentUtil.generateLogStmts(body, "isPass Value : ", isPass));
		//call super.dispatchTouchEvent
		//Unit target1 = InstrumentUtil.generateLogStmts(newBody, "override dispatch touch event"));
		soot.jimple.internal.JEqExpr condition_true = new JEqExpr(isPass,IntConstant.v(1));
		List<Unit> supercall_Unit =  InstrumentUtil.generateSpecialInvokeStmt(body, "android.app.Activity",
				"boolean dispatchTouchEvent(android.view.MotionEvent)", body.getThisLocal(), retVar, body.getParameterLocal(0));
		supercall_Unit.add(Jimple.v().newReturnStmt(retVar));
		Unit supercall = supercall_Unit.get(0);


		//supercall_UnitBox.setUnit();
		//Value condition = (Value) Jimple.v().newConditionExprBox(isPass);
		generated.add(Jimple.v().newIfStmt(condition_true, supercall));
		soot.jimple.internal.JEqExpr condition_false = new JEqExpr(isPass,IntConstant.v(0));
		Unit just_return = Jimple.v().newReturnStmt(IntConstant.v(0));

		generated.add(Jimple.v().newIfStmt(condition_false, just_return));

		generated.addAll(supercall_Unit);



		units.add(just_return);
		//ValueBox condition = Jimple.v().newConditionExprBox(isPass);



		//units.add(Jimple.v().newReturnStmt(IntConstant.v(0)));


		Object[] unitArr = units.toArray();
//		for(int i = 0; i<unitArr.length; i++)
//		{
//			System.err.println("unit ["+i+"] : "+unitArr[i].toString());
//		}
		//generated.add(Jimple.v().newGotoStmt(units.getLast()));
		units.insertBefore(generated, units.getLast());
		Unit tryEnd = units.getLast();

		// insert try-catch statement
		CaughtExceptionRef exceptionRef = soot.jimple.Jimple.v().newCaughtExceptionRef();
		Unit catchBegin = Jimple.v().newIdentityStmt(exceptionVar, exceptionRef);
		units.add(catchBegin);
		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Throwable", "void printStackTrace()",
				exceptionVar, null));


		SootClass exceptionClass = Scene.v().getSootClass("java.lang.Exception");
		Trap trap = soot.jimple.Jimple.v().newTrap(exceptionClass, tryBegin, tryEnd, catchBegin);


		units.add(Jimple.v().newReturnStmt(IntConstant.v(0)));

		body.getTraps().add(trap);



		//override super dispatchTouchEvent


//		generated2.add(Jimple.v().newGotoStmt(units.getLast()));
//		units.insertBefore(generated2,(Unit)unitArr2[unitArr2.length-5]);
//		Object[] unitArr3 = units.toArray();
//		for(int i = 0; i<unitArr3.length; i++)
//		{
//			System.err.println("unit3 ["+i+"] : "+unitArr3[i].toString());
//		}
		body.validate();

		System.out.println("edit DispatchTouchEvent done");
		System.err.println("edit DispatchTouchEvent done");
	}

	void editDispatchTouchEventTypeB(JimpleBody body, SootClass classActivity, SootClass classMainActivity)
	{
		UnitPatchingChain units = body.getUnits();
		List<Unit> generated = new ArrayList<>();
		Local retVar = InstrumentUtil.generateNewLocal(body, IntType.v());
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
		Local isPass = InstrumentUtil.generateNewLocal(body, IntType.v());
		Local isPassObject = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Object"));
		Local isPassString = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.String"));
		Local isPassInteger = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Integer"));
		Local classAppCompatActivity = InstrumentUtil.generateNewLocal(body, Scene.v().getSootClass("androidx.appcompat.app.AppCompatActivity").getType());
		Local clazz = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Class"));
		Local classLoaderVar2 = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.ClassLoader"));
		Local classArrayVar2 = InstrumentUtil.generateNewLocal(body, ArrayType.v(RefType.v("java.lang.Class"), 1));
		Local objectArrayVar2 = InstrumentUtil.generateNewLocal(body, ArrayType.v(RefType.v("java.lang.Object"), 1));
		Local methodVar2 = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.reflect.Method"));
		Local retObjVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Object"));

		//System.out.println("onLongClick : ");
		SootClass clsClass = Scene.v().getSootClass("java.lang.Class");
		SootClass clsObject = Scene.v().getSootClass("java.lang.Object");
		// printLocals(body);

		// get parameter
		//generated.add(Jimple.v().newAssignStmt(viewVar, newBody.getParameterLocal(0)));

		// get dexloader from field

		SootField fieldDex = classMainActivity.getFieldByName("dex");
		generated.add(Jimple.v().newAssignStmt(dexLoaderVar, Jimple.v().newStaticFieldRef(fieldDex.makeRef())));
		SootField fieldFluidInterface = classMainActivity.getFieldByName("objFluidInterface");
		generated.add(Jimple.v().newAssignStmt(objectFluidInterfaceVar,
				Jimple.v().newStaticFieldRef(fieldFluidInterface.makeRef())));


		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.ClassLoader",
				"java.lang.Class loadClass(java.lang.String)", dexLoaderVar, classVar,
				StringConstant.v(FLUID_MAIN_CLASS)));
		Unit tryBegin = units.getLast();

		//reflect super.dispatchTouchEvent
//		units.add(Jimple.v().newAssignStmt(clazz, ClassConstant.v("Landroid/app/Activity;")));
//
//		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "android.content.ContextWrapper",
//				"java.lang.ClassLoader getClassLoader()", newBody.getThisLocal(), classLoaderVar2));
//		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.ClassLoader",
//				"java.lang.Class loadClass(java.lang.String)", classLoaderVar2, clazz,
//				StringConstant.v("Landroidx/appcompat/app/AppCompatActivity")));

//		units.add(
//				Jimple.v().newAssignStmt(classArrayVar2, Jimple.v().newNewArrayExpr(clsClass.getType(), IntConstant.v(1))));
//		units.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(classArrayVar2, IntConstant.v(0)),
//				ClassConstant.v("Landroid/view/MotionEvent;")));
//		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.Class",
//				"java.lang.reflect.Method getDeclaredMethod(java.lang.String,java.lang.Class[])",
//				clazz, methodVar2,
//				StringConstant.v("dispatchTouchEvent"), classArrayVar2));
//
//		units.add(
//				Jimple.v().newAssignStmt(objectArrayVar2, Jimple.v().newNewArrayExpr(clsObject.getType(), IntConstant.v(1))));
//		units.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(objectArrayVar2, IntConstant.v(0)), newBody.getParameterLocal(0)));
//		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.reflect.Method",
//				"java.lang.Object invoke(java.lang.Object,java.lang.Object[])", methodVar2, retObjVar,
//				newBody.getThisLocal(), objectArrayVar2));
//		units.add(Jimple.v().newAssignStmt(retVar, Jimple.v().newCastExpr(retObjVar, Scene.v().getSootClass("java.lang.Boolean").getType()) ));
//


		// create Class array for getDeclaredMethod
//		SootClass cls = Scene.v().getSootClass("java.lang.Class");
		generated.add(
				Jimple.v().newAssignStmt(classArrayVar, Jimple.v().newNewArrayExpr(clsClass.getType(), IntConstant.v(1))));

		// put class to class array
		// generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body,
		// "java.lang.Object", "java.lang.Class getClass()", widgetnameVar,
		// classStringVar));
		generated.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(classArrayVar, IntConstant.v(0)),
				ClassConstant.v("Landroid/view/MotionEvent;")));

		// get runtest
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Class",
				"java.lang.reflect.Method getDeclaredMethod(java.lang.String,java.lang.Class[])", classVar, methodVar,
				StringConstant.v("runTouchCheck"), classArrayVar));

		// create object array for invoke
//		SootClass cls2 = Scene.v().getSootClass("java.lang.Object");
		generated.add(
				Jimple.v().newAssignStmt(objectArrayVar, Jimple.v().newNewArrayExpr(clsObject.getType(), IntConstant.v(1))));
		generated.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(objectArrayVar, IntConstant.v(0)), body.getParameterLocal(0)));
		// invoke runtest
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.reflect.Method",
				"java.lang.Object invoke(java.lang.Object,java.lang.Object[])", methodVar, isPassObject,
				objectFluidInterfaceVar, objectArrayVar));
		generated.add(Jimple.v().newAssignStmt(isPassInteger, Jimple.v().newCastExpr(isPassObject, RefType.v("java.lang.Integer"))));
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Integer", "java.lang.String toString()", isPassInteger, isPassString));
		generated.addAll(InstrumentUtil.generateStaticInvokeStmt(body, "java.lang.Integer", "int parseInt(java.lang.String)", isPass, isPassString));
		generated.addAll(InstrumentUtil.generateLogStmts(body, "isPass Value : ", isPass));
		//call super.dispatchTouchEvent
		//Unit target1 = InstrumentUtil.generateLogStmts(newBody, "override dispatch touch event"));
		soot.jimple.internal.JEqExpr condition_true = new JEqExpr(isPass,IntConstant.v(1));
		List<Unit> supercall_Unit =  InstrumentUtil.generateSpecialInvokeStmt(body, "android.app.Activity",
				"boolean dispatchTouchEvent(android.view.MotionEvent)", body.getThisLocal(), retVar, body.getParameterLocal(0));
		supercall_Unit.add(Jimple.v().newReturnStmt(retVar));
		Unit supercall = supercall_Unit.get(0);


		//supercall_UnitBox.setUnit();
		//Value condition = (Value) Jimple.v().newConditionExprBox(isPass);
		generated.add(Jimple.v().newIfStmt(condition_true, supercall));
		soot.jimple.internal.JEqExpr condition_false = new JEqExpr(isPass,IntConstant.v(0));
		Unit just_return = Jimple.v().newReturnStmt(IntConstant.v(0));

		generated.add(Jimple.v().newIfStmt(condition_false, just_return));

		generated.addAll(supercall_Unit);



		units.add(just_return);
		//ValueBox condition = Jimple.v().newConditionExprBox(isPass);



		//units.add(Jimple.v().newReturnStmt(IntConstant.v(0)));


		Object[] unitArr = units.toArray();
//		for(int i = 0; i<unitArr.length; i++)
//		{
//			System.err.println("unit ["+i+"] : "+unitArr[i].toString());
//		}
		//generated.add(Jimple.v().newGotoStmt(units.getLast()));
		units.insertBefore(generated, units.getLast());
		Unit tryEnd = units.getLast();

		// insert try-catch statement
		CaughtExceptionRef exceptionRef = soot.jimple.Jimple.v().newCaughtExceptionRef();
		Unit catchBegin = Jimple.v().newIdentityStmt(exceptionVar, exceptionRef);
		units.add(catchBegin);
		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Throwable", "void printStackTrace()",
				exceptionVar, null));


		SootClass exceptionClass = Scene.v().getSootClass("java.lang.Exception");
		Trap trap = soot.jimple.Jimple.v().newTrap(exceptionClass, tryBegin, tryEnd, catchBegin);


		units.add(Jimple.v().newReturnStmt(IntConstant.v(0)));

		body.getTraps().add(trap);



		//override super dispatchTouchEvent


//		generated2.add(Jimple.v().newGotoStmt(units.getLast()));
//		units.insertBefore(generated2,(Unit)unitArr2[unitArr2.length-5]);
//		Object[] unitArr3 = units.toArray();
//		for(int i = 0; i<unitArr3.length; i++)
//		{
//			System.err.println("unit3 ["+i+"] : "+unitArr3[i].toString());
//		}
		body.validate();

		System.out.println("edit DispatchTouchEvent done");
		System.err.println("edit DispatchTouchEvent done");
	}


	void addDispatchTouchEventTypeB(SootClass classActivity, SootClass classMainActivity) {

//		SootClass classMainActivity = (SootClass) arrayClasses[MAINACTIVITY_INDEX];
		List<Type> parameterType = new ArrayList<Type>();
		parameterType.add(Scene.v().getSootClass("android.view.MotionEvent").getType());

		Type returnType = BooleanType.v();


		SootMethod dispatchTouchEvent = new SootMethod("dispatchTouchEvent", parameterType, returnType, Modifier.PUBLIC );
		JimpleBody newBody = Jimple.v().newBody(dispatchTouchEvent);
		UnitPatchingChain units = newBody.getUnits();
		//Object[] fields = classMainActivity.getFields().toArray();
//		System.err.println("mainactivity : "+classMainActivity.toString());
//		for(int i = 0; i<fields.length;i++)
//		{
//			System.err.println("mainactivity field : ["+i+"] - "+fields[i].toString());
//		}
//		dispatchTouchEvent.setDeclared(true);
//		dispatchTouchEvent.setDeclaringClass(classMainActivity);
		classActivity.addMethod(dispatchTouchEvent);
		//newBody.getDefBoxes().add( Jimple.v().newIdentityRefBox(Jimple.v().newThisRef(RefType.v(MAINACTIVITY_CLASS_NAME))) );
		Local thisVar = InstrumentUtil.generateNewLocal(newBody, RefType.v(classActivity.toString()));
		Local paramVar = InstrumentUtil.generateNewLocal(newBody, Scene.v().getSootClass("android.view.MotionEvent").getType());
//		Local retVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Boolean"));
		Local retVar = InstrumentUtil.generateNewLocal(newBody, IntType.v());

		units.add( Jimple.v().newIdentityStmt(thisVar, Jimple.v().newThisRef(RefType.v(classActivity.toString()))));
		units.add( Jimple.v().newIdentityStmt(paramVar, Jimple.v().newParameterRef(Scene.v().getSootClass("android.view.MotionEvent").getType(), 0) ));
		//newBody.getDefBoxes().add(Jimple.v().newArgBox((new ThisRef(RefType.v(MAINACTIVITY_CLASS_NAME)))));
		//units.add(Jimple.v().newAssignStmt(thisVar, newBody.getThisLocal()));
		Local thisClassVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Class"));
		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.Object", "java.lang.Class getClass()", thisVar, thisClassVar));
		units.addAll(InstrumentUtil.generateLogStmts(newBody, "dispatchTouchEvent of : ", thisClassVar));


		Local exceptionVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Exception"));
		Local viewVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("android.view.View"));
		Local methodVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.reflect.Method"));
		Local clazzVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Class"));
		Local dexLoaderVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("dalvik.system.DexClassLoader"));
		Local classVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Class"));
		Local classLoaderVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.ClassLoader"));
		Local classStringVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Class"));
		Local classViewVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Class"));
		Local widgetnameVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.String"));
		Local eviewVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("android.view.View"));
		Local classArrayVar = InstrumentUtil.generateNewLocal(newBody, ArrayType.v(RefType.v("java.lang.Class"), 1));
		Local objectArrayVar = InstrumentUtil.generateNewLocal(newBody, ArrayType.v(RefType.v("java.lang.Object"), 1));
		Local objectFluidInterfaceVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Object"));
		Local isPass = InstrumentUtil.generateNewLocal(newBody, IntType.v());
		Local isPassObject = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Object"));
		Local isPassString = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.String"));
		Local isPassInteger = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Integer"));
		Local classAppCompatActivity = InstrumentUtil.generateNewLocal(newBody, Scene.v().getSootClass("androidx.appcompat.app.AppCompatActivity").getType());
		Local clazz = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Class"));
		Local classLoaderVar2 = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.ClassLoader"));
		Local classArrayVar2 = InstrumentUtil.generateNewLocal(newBody, ArrayType.v(RefType.v("java.lang.Class"), 1));
		Local objectArrayVar2 = InstrumentUtil.generateNewLocal(newBody, ArrayType.v(RefType.v("java.lang.Object"), 1));
		Local methodVar2 = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.reflect.Method"));
		Local retObjVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Object"));

		//System.out.println("onLongClick : ");
		SootClass clsClass = Scene.v().getSootClass("java.lang.Class");
		SootClass clsObject = Scene.v().getSootClass("java.lang.Object");
		// printLocals(body);

		// get parameter
		//generated.add(Jimple.v().newAssignStmt(viewVar, newBody.getParameterLocal(0)));

		// get dexloader from field

		SootField fieldDex = classMainActivity.getFieldByName("dex");
		units.add(Jimple.v().newAssignStmt(dexLoaderVar, Jimple.v().newStaticFieldRef(fieldDex.makeRef())));
		SootField fieldFluidInterface = classMainActivity.getFieldByName("objFluidInterface");
		units.add(Jimple.v().newAssignStmt(objectFluidInterfaceVar,
				Jimple.v().newStaticFieldRef(fieldFluidInterface.makeRef())));


		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.ClassLoader",
				"java.lang.Class loadClass(java.lang.String)", dexLoaderVar, classVar,
				StringConstant.v(FLUID_MAIN_CLASS)));
		Unit tryBegin = units.getLast();

		//reflect super.dispatchTouchEvent
//		units.add(Jimple.v().newAssignStmt(clazz, ClassConstant.v("Landroid/app/Activity;")));
//
//		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "android.content.ContextWrapper",
//				"java.lang.ClassLoader getClassLoader()", newBody.getThisLocal(), classLoaderVar2));
//		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.ClassLoader",
//				"java.lang.Class loadClass(java.lang.String)", classLoaderVar2, clazz,
//				StringConstant.v("Landroidx/appcompat/app/AppCompatActivity")));

//		units.add(
//				Jimple.v().newAssignStmt(classArrayVar2, Jimple.v().newNewArrayExpr(clsClass.getType(), IntConstant.v(1))));
//		units.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(classArrayVar2, IntConstant.v(0)),
//				ClassConstant.v("Landroid/view/MotionEvent;")));
//		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.Class",
//				"java.lang.reflect.Method getDeclaredMethod(java.lang.String,java.lang.Class[])",
//				clazz, methodVar2,
//				StringConstant.v("dispatchTouchEvent"), classArrayVar2));
//
//		units.add(
//				Jimple.v().newAssignStmt(objectArrayVar2, Jimple.v().newNewArrayExpr(clsObject.getType(), IntConstant.v(1))));
//		units.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(objectArrayVar2, IntConstant.v(0)), newBody.getParameterLocal(0)));
//		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.reflect.Method",
//				"java.lang.Object invoke(java.lang.Object,java.lang.Object[])", methodVar2, retObjVar,
//				newBody.getThisLocal(), objectArrayVar2));
//		units.add(Jimple.v().newAssignStmt(retVar, Jimple.v().newCastExpr(retObjVar, Scene.v().getSootClass("java.lang.Boolean").getType()) ));
//


		// create Class array for getDeclaredMethod
//		SootClass cls = Scene.v().getSootClass("java.lang.Class");
		units.add(
				Jimple.v().newAssignStmt(classArrayVar, Jimple.v().newNewArrayExpr(clsClass.getType(), IntConstant.v(1))));

		// put class to class array
		// generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body,
		// "java.lang.Object", "java.lang.Class getClass()", widgetnameVar,
		// classStringVar));
		units.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(classArrayVar, IntConstant.v(0)),
				ClassConstant.v("Landroid/view/MotionEvent;")));

		// get runtest
		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.Class",
				"java.lang.reflect.Method getDeclaredMethod(java.lang.String,java.lang.Class[])", classVar, methodVar,
				StringConstant.v("runTouchCheck"), classArrayVar));

		// create object array for invoke
//		SootClass cls2 = Scene.v().getSootClass("java.lang.Object");
		units.add(
				Jimple.v().newAssignStmt(objectArrayVar, Jimple.v().newNewArrayExpr(clsObject.getType(), IntConstant.v(1))));
		units.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(objectArrayVar, IntConstant.v(0)), newBody.getParameterLocal(0)));
		// invoke runtest
		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.reflect.Method",
				"java.lang.Object invoke(java.lang.Object,java.lang.Object[])", methodVar, isPassObject,
				objectFluidInterfaceVar, objectArrayVar));
		units.add(Jimple.v().newAssignStmt(isPassInteger, Jimple.v().newCastExpr(isPassObject, RefType.v("java.lang.Integer"))));
		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.Integer", "java.lang.String toString()", isPassInteger, isPassString));
		units.addAll(InstrumentUtil.generateStaticInvokeStmt(newBody, "java.lang.Integer", "int parseInt(java.lang.String)", isPass, isPassString));
		units.addAll(InstrumentUtil.generateLogStmts(newBody, "isPass Value : ", isPass));
		//call super.dispatchTouchEvent
		//Unit target1 = InstrumentUtil.generateLogStmts(newBody, "override dispatch touch event"));
		soot.jimple.internal.JEqExpr condition_true = new JEqExpr(isPass,IntConstant.v(1));
		List<Unit> supercall_Unit =  InstrumentUtil.generateSpecialInvokeStmt(newBody, "android.app.Activity",
				"boolean dispatchTouchEvent(android.view.MotionEvent)", newBody.getThisLocal(), retVar, newBody.getParameterLocal(0));
		supercall_Unit.add(Jimple.v().newReturnStmt(retVar));
		Unit supercall = supercall_Unit.get(0);


		//supercall_UnitBox.setUnit();
		//Value condition = (Value) Jimple.v().newConditionExprBox(isPass);
		units.add(Jimple.v().newIfStmt(condition_true, supercall));
		soot.jimple.internal.JEqExpr condition_false = new JEqExpr(isPass,IntConstant.v(0));
		Unit just_return = Jimple.v().newReturnStmt(IntConstant.v(0));

		units.add(Jimple.v().newIfStmt(condition_false, just_return));

		units.addAll(supercall_Unit);



		units.add(just_return);
		//ValueBox condition = Jimple.v().newConditionExprBox(isPass);



		//units.add(Jimple.v().newReturnStmt(IntConstant.v(0)));


		Object[] unitArr = units.toArray();
//		for(int i = 0; i<unitArr.length; i++)
//		{
//			System.err.println("unit ["+i+"] : "+unitArr[i].toString());
//		}
		//generated.add(Jimple.v().newGotoStmt(units.getLast()));

		Unit tryEnd = units.getLast();

		// insert try-catch statement
		CaughtExceptionRef exceptionRef = soot.jimple.Jimple.v().newCaughtExceptionRef();
		Unit catchBegin = Jimple.v().newIdentityStmt(exceptionVar, exceptionRef);
		units.add(catchBegin);
		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.Throwable", "void printStackTrace()",
				exceptionVar, null));


		SootClass exceptionClass = Scene.v().getSootClass("java.lang.Exception");
		Trap trap = soot.jimple.Jimple.v().newTrap(exceptionClass, tryBegin, tryEnd, catchBegin);


		units.add(Jimple.v().newReturnStmt(IntConstant.v(0)));

		newBody.getTraps().add(trap);



		//override super dispatchTouchEvent


//		generated2.add(Jimple.v().newGotoStmt(units.getLast()));
//		units.insertBefore(generated2,(Unit)unitArr2[unitArr2.length-5]);
//		Object[] unitArr3 = units.toArray();
//		for(int i = 0; i<unitArr3.length; i++)
//		{
//			System.err.println("unit3 ["+i+"] : "+unitArr3[i].toString());
//		}
		newBody.validate();
		dispatchTouchEvent.setActiveBody(newBody);

		System.out.println("add DispatchTouchEvent done");
		System.err.println("add DispatchTouchEvent done");



	}

	void addDispatchTouchEventTypeA(SootClass classActivity) {

//		SootClass classMainActivity = (SootClass) arrayClasses[MAINACTIVITY_INDEX];
		List<Type> parameterType = new ArrayList<Type>();
		parameterType.add(Scene.v().getSootClass("android.view.MotionEvent").getType());

		Type returnType = BooleanType.v();


		SootMethod dispatchTouchEvent = new SootMethod("dispatchTouchEvent", parameterType, returnType, Modifier.PUBLIC );
		JimpleBody newBody = Jimple.v().newBody(dispatchTouchEvent);
		UnitPatchingChain units = newBody.getUnits();
		//Object[] fields = classMainActivity.getFields().toArray();
//		System.err.println("mainactivity : "+classMainActivity.toString());
//		for(int i = 0; i<fields.length;i++)
//		{
//			System.err.println("mainactivity field : ["+i+"] - "+fields[i].toString());
//		}
//		dispatchTouchEvent.setDeclared(true);
//		dispatchTouchEvent.setDeclaringClass(classMainActivity);
		classActivity.addMethod(dispatchTouchEvent);
		//newBody.getDefBoxes().add( Jimple.v().newIdentityRefBox(Jimple.v().newThisRef(RefType.v(MAINACTIVITY_CLASS_NAME))) );
		Local thisVar = InstrumentUtil.generateNewLocal(newBody, RefType.v(classActivity.toString()));
		Local paramVar = InstrumentUtil.generateNewLocal(newBody, Scene.v().getSootClass("android.view.MotionEvent").getType());
//		Local retVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Boolean"));
		Local retVar = InstrumentUtil.generateNewLocal(newBody, IntType.v());

		units.add( Jimple.v().newIdentityStmt(thisVar, Jimple.v().newThisRef(RefType.v(classActivity.toString()))));
		units.add( Jimple.v().newIdentityStmt(paramVar, Jimple.v().newParameterRef(Scene.v().getSootClass("android.view.MotionEvent").getType(), 0) ));
		//newBody.getDefBoxes().add(Jimple.v().newArgBox((new ThisRef(RefType.v(MAINACTIVITY_CLASS_NAME)))));
		//units.add(Jimple.v().newAssignStmt(thisVar, newBody.getThisLocal()));
		Local thisClassVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Class"));
		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.Object", "java.lang.Class getClass()", thisVar, thisClassVar));
		units.addAll(InstrumentUtil.generateLogStmts(newBody, "dispatchTouchEvent of : ", thisClassVar));


		Local exceptionVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Exception"));
		Local viewVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("android.view.View"));
		Local methodVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.reflect.Method"));
		Local clazzVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Class"));
		Local dexLoaderVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("dalvik.system.DexClassLoader"));
		Local classVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Class"));
		Local classLoaderVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.ClassLoader"));
		Local classStringVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Class"));
		Local classViewVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Class"));
		Local widgetnameVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.String"));
		Local eviewVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("android.view.View"));
		Local classArrayVar = InstrumentUtil.generateNewLocal(newBody, ArrayType.v(RefType.v("java.lang.Class"), 1));
		Local objectArrayVar = InstrumentUtil.generateNewLocal(newBody, ArrayType.v(RefType.v("java.lang.Object"), 1));
		Local objectFluidInterfaceVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Object"));
		Local isPass = InstrumentUtil.generateNewLocal(newBody, IntType.v());
		Local isPassObject = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Object"));
		Local isPassString = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.String"));
		Local isPassInteger = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Integer"));
		Local classAppCompatActivity = InstrumentUtil.generateNewLocal(newBody, Scene.v().getSootClass("androidx.appcompat.app.AppCompatActivity").getType());
		Local clazz = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Class"));
		Local classLoaderVar2 = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.ClassLoader"));
		Local classArrayVar2 = InstrumentUtil.generateNewLocal(newBody, ArrayType.v(RefType.v("java.lang.Class"), 1));
		Local objectArrayVar2 = InstrumentUtil.generateNewLocal(newBody, ArrayType.v(RefType.v("java.lang.Object"), 1));
		Local methodVar2 = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.reflect.Method"));
		Local retObjVar = InstrumentUtil.generateNewLocal(newBody, RefType.v("java.lang.Object"));

		//System.out.println("onLongClick : ");
		SootClass clsClass = Scene.v().getSootClass("java.lang.Class");
		SootClass clsObject = Scene.v().getSootClass("java.lang.Object");
		// printLocals(body);

		// get parameter
		//generated.add(Jimple.v().newAssignStmt(viewVar, newBody.getParameterLocal(0)));

		// get dexloader from field

		SootField fieldDex = classActivity.getFieldByName("dex");
		units.add(Jimple.v().newAssignStmt(dexLoaderVar, Jimple.v().newStaticFieldRef(fieldDex.makeRef())));
		SootField fieldFluidInterface = classActivity.getFieldByName("objFluidInterface");
		units.add(Jimple.v().newAssignStmt(objectFluidInterfaceVar,
				Jimple.v().newStaticFieldRef(fieldFluidInterface.makeRef())));


		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.ClassLoader",
				"java.lang.Class loadClass(java.lang.String)", dexLoaderVar, classVar,
				StringConstant.v(FLUID_MAIN_CLASS)));
		Unit tryBegin = units.getLast();

		//reflect super.dispatchTouchEvent
//		units.add(Jimple.v().newAssignStmt(clazz, ClassConstant.v("Landroid/app/Activity;")));
//
//		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "android.content.ContextWrapper",
//				"java.lang.ClassLoader getClassLoader()", newBody.getThisLocal(), classLoaderVar2));
//		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.ClassLoader",
//				"java.lang.Class loadClass(java.lang.String)", classLoaderVar2, clazz,
//				StringConstant.v("Landroidx/appcompat/app/AppCompatActivity")));

//		units.add(
//				Jimple.v().newAssignStmt(classArrayVar2, Jimple.v().newNewArrayExpr(clsClass.getType(), IntConstant.v(1))));
//		units.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(classArrayVar2, IntConstant.v(0)),
//				ClassConstant.v("Landroid/view/MotionEvent;")));
//		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.Class",
//				"java.lang.reflect.Method getDeclaredMethod(java.lang.String,java.lang.Class[])",
//				clazz, methodVar2,
//				StringConstant.v("dispatchTouchEvent"), classArrayVar2));
//
//		units.add(
//				Jimple.v().newAssignStmt(objectArrayVar2, Jimple.v().newNewArrayExpr(clsObject.getType(), IntConstant.v(1))));
//		units.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(objectArrayVar2, IntConstant.v(0)), newBody.getParameterLocal(0)));
//		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.reflect.Method",
//				"java.lang.Object invoke(java.lang.Object,java.lang.Object[])", methodVar2, retObjVar,
//				newBody.getThisLocal(), objectArrayVar2));
//		units.add(Jimple.v().newAssignStmt(retVar, Jimple.v().newCastExpr(retObjVar, Scene.v().getSootClass("java.lang.Boolean").getType()) ));
//


		// create Class array for getDeclaredMethod
//		SootClass cls = Scene.v().getSootClass("java.lang.Class");
		units.add(
				Jimple.v().newAssignStmt(classArrayVar, Jimple.v().newNewArrayExpr(clsClass.getType(), IntConstant.v(1))));

		// put class to class array
		// generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body,
		// "java.lang.Object", "java.lang.Class getClass()", widgetnameVar,
		// classStringVar));
		units.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(classArrayVar, IntConstant.v(0)),
				ClassConstant.v("Landroid/view/MotionEvent;")));

		// get runtest
		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.Class",
				"java.lang.reflect.Method getDeclaredMethod(java.lang.String,java.lang.Class[])", classVar, methodVar,
				StringConstant.v("runTouchCheck"), classArrayVar));

		// create object array for invoke
//		SootClass cls2 = Scene.v().getSootClass("java.lang.Object");
		units.add(
				Jimple.v().newAssignStmt(objectArrayVar, Jimple.v().newNewArrayExpr(clsObject.getType(), IntConstant.v(1))));
		units.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(objectArrayVar, IntConstant.v(0)), newBody.getParameterLocal(0)));
		// invoke runtest
		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.reflect.Method",
				"java.lang.Object invoke(java.lang.Object,java.lang.Object[])", methodVar, isPassObject,
				objectFluidInterfaceVar, objectArrayVar));
		units.add(Jimple.v().newAssignStmt(isPassInteger, Jimple.v().newCastExpr(isPassObject, RefType.v("java.lang.Integer"))));
		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.Integer", "java.lang.String toString()", isPassInteger, isPassString));
		units.addAll(InstrumentUtil.generateStaticInvokeStmt(newBody, "java.lang.Integer", "int parseInt(java.lang.String)", isPass, isPassString));
		units.addAll(InstrumentUtil.generateLogStmts(newBody, "isPass Value : ", isPass));
		//call super.dispatchTouchEvent
		//Unit target1 = InstrumentUtil.generateLogStmts(newBody, "override dispatch touch event"));
		soot.jimple.internal.JEqExpr condition_true = new JEqExpr(isPass,IntConstant.v(1));
		List<Unit> supercall_Unit =  InstrumentUtil.generateSpecialInvokeStmt(newBody, "android.app.Activity",
				"boolean dispatchTouchEvent(android.view.MotionEvent)", newBody.getThisLocal(), retVar, newBody.getParameterLocal(0));
		supercall_Unit.add(Jimple.v().newReturnStmt(retVar));
		Unit supercall = supercall_Unit.get(0);


		//supercall_UnitBox.setUnit();
		//Value condition = (Value) Jimple.v().newConditionExprBox(isPass);
		units.add(Jimple.v().newIfStmt(condition_true, supercall));
		soot.jimple.internal.JEqExpr condition_false = new JEqExpr(isPass,IntConstant.v(0));
		Unit just_return = Jimple.v().newReturnStmt(IntConstant.v(0));

		units.add(Jimple.v().newIfStmt(condition_false, just_return));

		units.addAll(supercall_Unit);



		units.add(just_return);
		//ValueBox condition = Jimple.v().newConditionExprBox(isPass);



		//units.add(Jimple.v().newReturnStmt(IntConstant.v(0)));


		Object[] unitArr = units.toArray();
//		for(int i = 0; i<unitArr.length; i++)
//		{
//			System.err.println("unit ["+i+"] : "+unitArr[i].toString());
//		}
		//generated.add(Jimple.v().newGotoStmt(units.getLast()));

		Unit tryEnd = units.getLast();

		// insert try-catch statement
		CaughtExceptionRef exceptionRef = soot.jimple.Jimple.v().newCaughtExceptionRef();
		Unit catchBegin = Jimple.v().newIdentityStmt(exceptionVar, exceptionRef);
		units.add(catchBegin);
		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(newBody, "java.lang.Throwable", "void printStackTrace()",
				exceptionVar, null));


		SootClass exceptionClass = Scene.v().getSootClass("java.lang.Exception");
		Trap trap = soot.jimple.Jimple.v().newTrap(exceptionClass, tryBegin, tryEnd, catchBegin);


		units.add(Jimple.v().newReturnStmt(IntConstant.v(0)));

		newBody.getTraps().add(trap);



		//override super dispatchTouchEvent


//		generated2.add(Jimple.v().newGotoStmt(units.getLast()));
//		units.insertBefore(generated2,(Unit)unitArr2[unitArr2.length-5]);
//		Object[] unitArr3 = units.toArray();
//		for(int i = 0; i<unitArr3.length; i++)
//		{
//			System.err.println("unit3 ["+i+"] : "+unitArr3[i].toString());
//		}
		newBody.validate();
		dispatchTouchEvent.setActiveBody(newBody);

		System.out.println("add DispatchTouchEvent done");
		System.err.println("add DispatchTouchEvent done");



	}


//	SootClass inject_onTouchClass() {
//
//
//		String ListenerClassSignature = MAINACTIVITY_CLASS_NAME+".onTouchListener";
//		SootClass onTouchListener = new SootClass(ListenerClassSignature,Modifier.PUBLIC);
//		onTouchListener.setSuperclass(Scene.v().getSootClass("java.lang.object"));
//		onTouchListener.setApplicationClass();
//		List<Type> parameterType = new ArrayList<Type>();
//		parameterType.add(Scene.v().getSootClass("android.view.View").getType());
//		parameterType.add(Scene.v().getSootClass("android.view.MotionEvent").getType());
//		Type returnType = Scene.v().getSootClass("java.lang.Boolean").getType();
//		SootMethod onTouch = new SootMethod("onTouch",parameterType,returnType, Modifier.PUBLIC);
//		onTouchListener.addMethod(onTouch);
//		JimpleBody body = Jimple.v().newBody();
//		body.setMethod(onTouch);
//		UnitPatchingChain units = body.getUnits();
//
//		Local exceptionVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Exception"));
//		Local viewVar = InstrumentUtil.generateNewLocal(body, RefType.v("android.view.View"));
//		Local methodVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.reflect.Method"));
//		Local dexLoaderVar = InstrumentUtil.generateNewLocal(body, RefType.v("dalvik.system.DexClassLoader"));
//		Local classVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Class"));
//		Local classViewVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Class"));
//		Local widgetnameVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.String"));
//		Local classArrayVar = InstrumentUtil.generateNewLocal(body, ArrayType.v(RefType.v("java.lang.Class"), 1));
//		Local objectArrayVar = InstrumentUtil.generateNewLocal(body, ArrayType.v(RefType.v("java.lang.Object"), 1));
//		Local objectFluidInterfaceVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Object"));
//		Local param0Var = InstrumentUtil.generateNewLocal(body, RefType.v("android.view.View"));
//		Local param1Var = InstrumentUtil.generateNewLocal(body, RefType.v("android.view.MotionEvent"));
////		List<Unit> generated = new ArrayList<>();
////
////		Object[] arrayClasses = Scene.v().getApplicationClasses().toArray();
////		SootClass classMainActivity = (SootClass) arrayClasses[MAINACTIVITY_INDEX];
////		SootField fieldDex = classMainActivity.getFieldByName("dex");
////
////		units.add(Jimple.v().newAssignStmt(dexLoaderVar, Jimple.v().newStaticFieldRef(fieldDex.makeRef())));
////		SootField fieldFluidInterface = classMainActivity.getFieldByName("objFluidInterface");
////		units.add(Jimple.v().newAssignStmt(objectFluidInterfaceVar,
////				Jimple.v().newStaticFieldRef(fieldFluidInterface.makeRef())));
////		Unit tryBegin = units.getLast();
////		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.ClassLoader",
////				"java.lang.Class loadClass(java.lang.String)", dexLoaderVar, classVar,
////				StringConstant.v(FLUID_MAIN_CLASS)));
////
////
////		// get widget type
////		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Object",
////				"java.lang.Class getClass()", viewVar, classViewVar));
////
////		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Object",
////				"java.lang.String toString()", classViewVar, widgetnameVar));
////
////		// create Class array for getDeclaredMethod
////		SootClass cls = Scene.v().getSootClass("java.lang.Class");
////		units.add(
////				Jimple.v().newAssignStmt(classArrayVar, Jimple.v().newNewArrayExpr(cls.getType(), IntConstant.v(2))));
////
////		// put class to class array
////		// generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body,
////		// "java.lang.Object", "java.lang.Class getClass()", widgetnameVar,
////		// classStringVar));
////		units.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(classArrayVar, IntConstant.v(0)),
////				ClassConstant.v("Landroid/view/View;")));
////		units.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(classArrayVar, IntConstant.v(1)),
////				ClassConstant.v("Landroid/view/MotionEvent;")));
////
////		// get runtest
////		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Class",
////				"java.lang.reflect.Method getDeclaredMethod(java.lang.String,java.lang.Class[])", classVar, methodVar,
////				StringConstant.v("runThreeFinger"), classArrayVar));
////
////		// create object array for invoke
////		SootClass cls2 = Scene.v().getSootClass("java.lang.Object");
////		units.add(
////				Jimple.v().newAssignStmt(objectArrayVar, Jimple.v().newNewArrayExpr(cls2.getType(), IntConstant.v(2))));
//////		units.insertAfter(generated, units.getFirst());
////		Object[] ar = body.getParameterLocals().toArray();
////		for(int i = 0; i<ar.length; i++)
////			System.err.println("check: "+ar[i].toString());
////		units.add(Jimple.v().newAssignStmt(param0Var, body.getParameterLocal(0)));
////		units.add(Jimple.v().newAssignStmt(param1Var, body.getParameterLocal(1)));
////		units.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(objectArrayVar, IntConstant.v(0)), param0Var));
////		units.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(objectArrayVar, IntConstant.v(1)), param1Var));
////		// invoke runtest
////		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.reflect.Method",
////				"java.lang.Object invoke(java.lang.Object,java.lang.Object[])", methodVar, null,
////				objectFluidInterfaceVar, objectArrayVar));
////
//////		units.insertAfter(generated, units.getLast());
////		Unit tryEnd = units.getLast(); // return
////
////		// insert try-catch statement
////		CaughtExceptionRef exceptionRef = soot.jimple.Jimple.v().newCaughtExceptionRef();
////		Unit catchBegin = Jimple.v().newIdentityStmt(exceptionVar, exceptionRef);
////		units.add(catchBegin);
////		units.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Throwable", "void printStackTrace()",
////				exceptionVar, null));
////
////		units.add(Jimple.v().newReturnStmt(IntConstant.v(0)));
////		SootClass exceptionClass = Scene.v().getSootClass("java.lang.Exception");
////		Trap trap = soot.jimple.Jimple.v().newTrap(exceptionClass, tryBegin, tryEnd, catchBegin);
////		body.getTraps().add(trap);
////		body.validate();
//
//		return onTouchListener;
//	}

}