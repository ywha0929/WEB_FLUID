package org.example;

import soot.SootMethod;

public class CallEdge {
    private SootMethod srcMethod;
	private SootMethod tgtMethod;
    public CallEdge(SootMethod srcMethod, SootMethod targetMethod)
    {
        this.srcMethod = srcMethod;
        this.tgtMethod = targetMethod;
    }

    @Override
    public String toString()
    {
        if(srcMethod == null)
            return "null to " + tgtMethod.toString();
        return srcMethod.toString() +" to " + tgtMethod.toString();
    }

    @Override
    public boolean equals(Object obj) {
        CallEdge edge = (CallEdge) obj;
        if(this.getSrcMethodString().equals(edge.getSrcMethodString()) && this.getTgtMethodString().equals(edge.getTgtMethodString()))
            return true;
        else
            return false;
    }
    public String getSrcMethodString()
    {
        if(srcMethod == null)
            return "null";
        return srcMethod.toString();
    }
    public String getTgtMethodString()
    {
        return tgtMethod.toString();
    }
    public SootMethod getSrcMethod() {
        return srcMethod;
    }

    public SootMethod getTgtMethod() {
        return tgtMethod;
    }
}
