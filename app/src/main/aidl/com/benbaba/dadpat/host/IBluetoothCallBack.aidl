// IBluetoothCallBack.aidl
package com.benbaba.dadpat.host;

// Declare any non-default types here with import statements

interface IBluetoothCallBack {
   void receiveMsg( String msg);
   void connectCallBack(boolean result);
   void disConnectCallBack();
}
