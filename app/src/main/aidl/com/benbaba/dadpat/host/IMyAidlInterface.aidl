// IMyAidlInterface.aidl
package com.benbaba.dadpat.host;
import com.benbaba.dadpat.host.IBluetoothCallBack;
interface IMyAidlInterface {

   //连接蓝牙
   void connectBluetooth();
   //注册接收回调
   void registerCallBack(IBluetoothCallBack callback);
   //取消注册接收回调
   void unRegisterCallBack(IBluetoothCallBack callback);
   //发送消息到玩具鼓
   boolean  sendMsg( in String msg);
   //蓝牙是否连接
   boolean isConnect();
   //设置蓝牙音量
   void saveBluetoothVolume(int volume);


}
