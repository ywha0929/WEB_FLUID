// IFLUIDService.aidl
package com.hmsl.fluidmanager;
// Declare any non-default types here with import statements

interface IFLUIDService {
    void distribute(in Bundle bundle);
    void update(in Bundle bundle);
    void reverseConnect(in Bundle bundle);
}