// IReverseConnection.aidl
package com.hmsl.fluidlib;

// Declare any non-default types here with import statements

interface IReverseConnection {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void doCheck(String msg);
    void reverseMotionEvent(in Bundle bundle);
    void reverseKeyboardEvent(in Bundle bundle);
}