// IFLUIDService.aidl
package com.hmsl.fluidmanager;
import com.hmsl.fluidmanager.IRemoteServiceCallback;
// Declare any non-default types here with import statements

interface IFLUIDService {
    void test(in Bundle bundle);
    void update(in Bundle bundle);
    boolean registerCallback(IRemoteServiceCallback callback);
    boolean unregisterCallback(IRemoteServiceCallback callback);
}