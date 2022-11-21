import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.HashSet;
import java.util.HashMap;
import java.io.PrintWriter;
import java.io.IOException;

import soot.MethodOrMethodContext;
import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.SootField;
import soot.SootFieldRef;
import soot.Type;
import soot.ArrayType;
import soot.PrimType;
import soot.jimple.Stmt;
import soot.jimple.FieldRef;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Targets;
import soot.jimple.toolkits.callgraph.Edge;
import soot.jimple.JimpleBody;
import soot.tagkit.LineNumberTag;

import soot.*;
import soot.util.*;
import soot.jimple.*;
import soot.jimple.internal.*;

public class DuiCHATool
{	
	public static String appName;
	public static String appJar;
	public static void main(String[] args) {
		appName = args[0];
		appJar = args[1];
		args = new String[0];
		List<String> argsList = new ArrayList<String>(Arrays.asList(args));
		argsList.addAll(Arrays.asList(new String[]{
				"-cp",
				"./build/"
				+ ":" + appJar
				+ ":./libs/framework.jar"
				+ ":./libs/core-oj.jar"
				+ ":./libs/core-libart.jar"
				+ ":./libs/conscrypt.jar"
				+ ":./libs/okhttp.jar"
				+ ":./libs/bouncycastle.jar"
				+ ":./libs/ext.jar"
				+ ":./libs/legacy-test.jar"
				+ ":./libs/org.apache.http.legacy.jar"
				+ ":./libs/annotations.jar"
				+ ":./libs/animal-sniffer-annotations-1.5.jar"
				+ ":./libs/jsr305.jar",
				"-android-jars",
				"./libs/framework.jar",
				"-w",
				"-main-class",
				"FakeMainClass",//main-class
				"FakeMainClass",//argument classes
		}));


		PackManager.v().getPack("wjtp").add(new Transform("wjtp.myTrans", new SceneTransformer() {
			@Override
			protected void internalTransform(String phaseName, Map options) {
				// Key: class name, Valeu: a set of fields' name
				HashMap<String, HashSet<String>> result = new HashMap<String, HashSet<String>>();
				HashSet<String> visited = new HashSet<String>();

				CHATransformer.v().transform();

				CallGraph cg = Scene.v().getCallGraph();

				SootMethod root = Scene.v().getMainClass().getMethodByName("fakeRendering");
				Iterator<MethodOrMethodContext> methods = new Targets(cg.edgesOutOf(root));
				while (methods.hasNext()) {
					SootMethod src = (SootMethod)methods.next();
					if (skipMethod(src))
						continue;

					Queue<SootMethod> queue = new LinkedList<SootMethod>();
					visited.add(src.getSignature());
					queue.add(src);

					while(!queue.isEmpty()) {
						src = queue.poll();
						if (src.isNative())
							continue;

						System.out.println(src);
						HashSet<Value> localForField = new HashSet<Value>();
						JimpleBody body = (JimpleBody)src.retrieveActiveBody();
						Local thisLocal = null;
						try {
							thisLocal = body.getThisLocal();
							localForField.add(thisLocal);
						} catch (Exception e) {
							//System.out.println(body);
						}

						List<Local> params = null;
						try {
							params = body.getParameterLocals();
							for (Local param : params)
								localForField.add(param);
						} catch (Exception e) {}

						// Collect member fields of thisLocal (@this)
						Iterator iter = body.getUnits().iterator();
						while(iter.hasNext()) {
							Stmt stmt = (Stmt)iter.next();
							
							// Logging for debugging
							showStmtForDebug(src, stmt, "setState");
							
							// In Jimple, Local is used to access a member field.
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
										if (!skipField(field)) {
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

						// Iterate the call graph
						Iterator<Edge> edges = cg.edgesOutOf(src);
						while (edges.hasNext()) {
							Edge edge = edges.next();
							SootMethod tgt = (SootMethod)edge.getTgt();
							
							//if (tgt.getName().equals("isBoring")) {
							//	System.out.println("		" + tgt);
							//	System.out.println("		" + edge.srcStmt());
							//	System.out.println("		" + edge.srcStmt().getInvokeExpr());
							//}

							// Visit methods of member fields or thisLocal
							InvokeExpr ie = null;
							try {
								ie = edge.srcStmt().getInvokeExpr();
								if (ie instanceof AbstractInstanceInvokeExpr) {
									Value base = ((AbstractInstanceInvokeExpr)ie).getBase();
									if (!localForField.contains(base))
										continue;
								}
								else if (ie instanceof AbstractStaticInvokeExpr) {
									boolean includeField = false;
									List<Value> args = ie.getArgs();
									for (Value arg : args) {
										if (localForField.contains(arg)) {
											includeField = true;
											break;
										}
									}
									if (!includeField)
										continue;
								}
							} catch(Exception e) { continue; }

							if (!skipMethod(tgt) && !visited.contains(tgt.getSignature())) {
								visited.add(tgt.getSignature());
								queue.add(tgt);
								System.out.println("		" + tgt);
							}
						}
					}
				}

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

				try {
				PrintWriter output = new PrintWriter("./results/" + appName);
				for (String clazz : result.keySet()) {
					HashSet<String> fields = result.get(clazz);
					if (fields.isEmpty())
						continue;
					output.println("<" + clazz + ">");
					for (String field : fields)
						output.println(field);
					if (clazz.equals("java.lang.ref.Reference"))
					    output.println("referent");
					output.println("</" + clazz + ">");
				}
				output.close();
				} catch (IOException e) { 
					System.out.println("Result Write failed");
				}
			}

			public boolean skipField(SootField field) {
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

			public boolean skipMethod(SootMethod method) {
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
			
			public void showStmtForDebug(SootMethod method, Stmt stmt, String methodName) {
				if (method.getName().equals(methodName)) {
					System.out.println(stmt);
					if (stmt instanceof JAssignStmt) {
						Value left = ((JAssignStmt)stmt).getLeftOp();
						Value right = ((JAssignStmt)stmt).getRightOp();
						System.out.println(left.getClass() + "		" + right.getClass());
					}
				}
			}
		}));

		args = argsList.toArray(new String[0]);

		soot.Main.main(args);
	}
}
