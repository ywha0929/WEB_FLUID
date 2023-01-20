package org.example;

import soot.SootClass;

public class ComparableSootClass extends soot.SootClass{
    SootClass sootClass;
    public ComparableSootClass(String name) {
        super(name);
    }
    @Override
    public boolean equals(Object obj)
    {
        if( this.sootClass.toString().equals( ((SootClass)obj).toString()))
            return true;
        else
            return false;
    }
}
