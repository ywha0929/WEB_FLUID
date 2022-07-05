// IRemoteServiceCallback.aidl
package com.hmsl.fluidmanager;

// Declare any non-default types here with import statements

oneway interface IRemoteServiceCallback {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void Docheck(int value);
}