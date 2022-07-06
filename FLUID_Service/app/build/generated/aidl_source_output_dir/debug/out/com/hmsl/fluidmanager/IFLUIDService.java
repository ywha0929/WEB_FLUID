/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package com.hmsl.fluidmanager;
// Declare any non-default types here with import statements

public interface IFLUIDService extends android.os.IInterface
{
  /** Default implementation for IFLUIDService. */
  public static class Default implements com.hmsl.fluidmanager.IFLUIDService
  {
    @Override public void test(android.os.Bundle bundle) throws android.os.RemoteException
    {
    }
    @Override public void update(android.os.Bundle bundle) throws android.os.RemoteException
    {
    }
    @Override public void reverseConnect(android.os.Bundle bundle) throws android.os.RemoteException
    {
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements com.hmsl.fluidmanager.IFLUIDService
  {
    private static final java.lang.String DESCRIPTOR = "com.hmsl.fluidmanager.IFLUIDService";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an com.hmsl.fluidmanager.IFLUIDService interface,
     * generating a proxy if needed.
     */
    public static com.hmsl.fluidmanager.IFLUIDService asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof com.hmsl.fluidmanager.IFLUIDService))) {
        return ((com.hmsl.fluidmanager.IFLUIDService)iin);
      }
      return new com.hmsl.fluidmanager.IFLUIDService.Stub.Proxy(obj);
    }
    @Override public android.os.IBinder asBinder()
    {
      return this;
    }
    @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
    {
      java.lang.String descriptor = DESCRIPTOR;
      switch (code)
      {
        case INTERFACE_TRANSACTION:
        {
          reply.writeString(descriptor);
          return true;
        }
        case TRANSACTION_test:
        {
          data.enforceInterface(descriptor);
          android.os.Bundle _arg0;
          if ((0!=data.readInt())) {
            _arg0 = android.os.Bundle.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          this.test(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_update:
        {
          data.enforceInterface(descriptor);
          android.os.Bundle _arg0;
          if ((0!=data.readInt())) {
            _arg0 = android.os.Bundle.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          this.update(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_reverseConnect:
        {
          data.enforceInterface(descriptor);
          android.os.Bundle _arg0;
          if ((0!=data.readInt())) {
            _arg0 = android.os.Bundle.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          this.reverseConnect(_arg0);
          reply.writeNoException();
          return true;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements com.hmsl.fluidmanager.IFLUIDService
    {
      private android.os.IBinder mRemote;
      Proxy(android.os.IBinder remote)
      {
        mRemote = remote;
      }
      @Override public android.os.IBinder asBinder()
      {
        return mRemote;
      }
      public java.lang.String getInterfaceDescriptor()
      {
        return DESCRIPTOR;
      }
      @Override public void test(android.os.Bundle bundle) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((bundle!=null)) {
            _data.writeInt(1);
            bundle.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_test, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().test(bundle);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void update(android.os.Bundle bundle) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((bundle!=null)) {
            _data.writeInt(1);
            bundle.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_update, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().update(bundle);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void reverseConnect(android.os.Bundle bundle) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((bundle!=null)) {
            _data.writeInt(1);
            bundle.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_reverseConnect, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().reverseConnect(bundle);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      public static com.hmsl.fluidmanager.IFLUIDService sDefaultImpl;
    }
    static final int TRANSACTION_test = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_update = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_reverseConnect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
    public static boolean setDefaultImpl(com.hmsl.fluidmanager.IFLUIDService impl) {
      // Only one user of this interface can use this function
      // at a time. This is a heuristic to detect if two different
      // users in the same process use this function.
      if (Stub.Proxy.sDefaultImpl != null) {
        throw new IllegalStateException("setDefaultImpl() called twice");
      }
      if (impl != null) {
        Stub.Proxy.sDefaultImpl = impl;
        return true;
      }
      return false;
    }
    public static com.hmsl.fluidmanager.IFLUIDService getDefaultImpl() {
      return Stub.Proxy.sDefaultImpl;
    }
  }
  public void test(android.os.Bundle bundle) throws android.os.RemoteException;
  public void update(android.os.Bundle bundle) throws android.os.RemoteException;
  public void reverseConnect(android.os.Bundle bundle) throws android.os.RemoteException;
}
