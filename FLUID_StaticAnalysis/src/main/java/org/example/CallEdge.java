package org.example;

import soot.SootMethod;

public class CallEdge {
    SootMethod srcMethod;
    SootMethod targetMethod;
    public CallEdge(SootMethod srcMethod, SootMethod targetMethod)
    {
        this.srcMethod = srcMethod;
        this.targetMethod = targetMethod;
    }
    @Override
    public String toString()
    {
        if(srcMethod == null)
            return "null to " + targetMethod.toString();
        return srcMethod.toString() +" to " + targetMethod.toString();
    }

    @Override
    public boolean equals(Object obj) {
        CallEdge edge = (CallEdge) obj;
        if(this.srcMethod.equals(edge.srcMethod) && this.targetMethod.equals(edge.targetMethod))
            return true;
        else
            return false;
    }
}
