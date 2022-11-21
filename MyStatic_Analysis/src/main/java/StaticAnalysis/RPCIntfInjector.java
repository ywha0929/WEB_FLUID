package StaticAnalysis;

import soot.*;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RPCIntfInjector extends BodyTransformer {
	boolean isInsert = true;
	boolean isAnalize = false;
	boolean isFirstDone = false;
	static AtomicInteger threadNum = new AtomicInteger();
	// final static int MAINACTIVITY_INDEX2 = 1918;
	static int MAINACTIVITY_INDEX = 1391;
	final static String TMP_DIR_PATH = "/data/local/tmp/";
	final static String FLUID_LIB_PATH = "/data/local/tmp/fluidlib.apk";
	final static String FLUID_MAIN_CLASS = "com.hmsl.fluidlib.FLUIDMain";
	final static String FLUID_PACKAGE_NAME = "com.hmsl.fluidmanager";
	final static String FLUID_SERVICE_NAME = "com.hmsl.fluidmanager.FLUIDManagerService";
	static String MAINACTIVITY_CLASS_NAME;
	static String MAIN_PACKAGE_NAME;
	static List<SootClass> ActivityClasses = new ArrayList<>();

	public RPCIntfInjector() {

		super();
//		MAIN_PACKAGE_NAME = namePackage;
//		String classname = namePackage+".MainActivity";
//		MAINACTIVITY_CLASS_NAME = classname;
	}

	@Override
	protected void internalTransform(Body b, String s, Map<String, String> map) {

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
				if(ActivityClasses.contains(superClass)) //if ancestor in list -> switch
				{
					System.err.println("switching Class "+superClass.toString()+" -> "+thisClass.toString());
//					System.out.println("switching Class "+superClass.toString()+" -> "+thisClass.toString());
					ActivityClasses.remove(superClass);
					ActivityClasses.add(thisClass);
					break;
				}
				if(superClass.toString().equals("androidx.appcompat.app.AppCompatActivity")|| superClass.toString().equals("android.app.Activity"))
				{
					System.err.println("found activity class : "+thisClass.toString());
					System.out.println("found activity class : "+thisClass.toString());
					if(!ActivityClasses.contains(thisClass))
					{
						ActivityClasses.add(thisClass);
						break;

					}
					else
					{
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
	}

}

