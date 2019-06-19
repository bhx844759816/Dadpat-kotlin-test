package com.benbaba.dadpat.common.bluetooth.conn;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import com.benbaba.dadpat.common.bluetooth.SmartBle;
import com.benbaba.dadpat.common.bluetooth.exception.BluetoothExceptionCompat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * 连接A2dp(蓝牙音箱)蓝牙设备
 */
@SuppressLint("MissingPermission")
public class BluetoothConnect extends BaseConnectDevice {
    private static final int STATE_NONE = 0;       // we're doing nothing
    private static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    private static final int STATE_CONNECTED = 3;  // now startReceiveThread to a remote device
    private static final String TAG = BluetoothConnect.class.getSimpleName();
    private static final String UUID = "00001101-0000-1000-8000-00805F9B34FB";
    private ConnectedReceiveMsgThread mConnectReceiveMsgThread;
    private ConnectBluetoothThread mConnectThread;
    private int mState;


    public BluetoothConnect(BluetoothDevice device) {
        super(device);
    }

    @Override
    void realConnect() {
        initBluetoothSocket();
    }

    /**
     * 初始化蓝牙Socket
     */
    private void initBluetoothSocket() {
        //关闭上次连接重新开始连接
        if (mConnectThread != null) {
            mConnectThread.close();
            mConnectThread = null;
        }
        if (mConnectReceiveMsgThread != null) {
            mConnectReceiveMsgThread.close();
            mConnectReceiveMsgThread = null;
        }
        mConnectThread = new ConnectBluetoothThread();
        mConnectThread.start();
    }


    /**
     * 发送消息
     *
     * @param msg
     */
    public boolean sendMsg(String msg) {
        if (mState == STATE_CONNECTED && mConnectReceiveMsgThread != null) {
            byte[] sendData = msg.getBytes();
            Log.i(TAG, "BluetoothConnect sendMsg：" + msg);
            return mConnectReceiveMsgThread.write(sendData);
        }
        return false;
    }

    @Override
    public void release() {

    }

    @Override
    public void cancelBond() {
        mState = STATE_NONE;
    }

    @Override
    public void bluetoothClose() {
        mState = STATE_NONE;
    }

    @Override
    public void disConnect() {
        mState = STATE_NONE;
        if (mConnectThread != null) {
            mConnectThread.close();
            mConnectThread = null;
        }
        if (mConnectReceiveMsgThread != null) {
            mConnectReceiveMsgThread.close();
            mConnectReceiveMsgThread = null;
        }
    }


    /**
     * 是否和蓝牙连接
     *
     * @return
     */
    public boolean isConnect() {
        return mState == STATE_CONNECTED;
    }

    /**
     * 连接蓝牙的线程
     */
    private class ConnectBluetoothThread extends Thread {
        BluetoothSocket mSocket;

        ConnectBluetoothThread() {
            BluetoothSocket tmp = null;
            try {
                tmp = mDevice.createRfcommSocketToServiceRecord(java.util.UUID.fromString(UUID));
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    tmp = (BluetoothSocket) mDevice.getClass().getMethod("createRfcommSocket",
                            new Class[]{int.class}).invoke(mDevice, 1);
                } catch (Exception e2) {

                }
            }
            mSocket = tmp;
            mState = STATE_CONNECTING;
        }

        @Override
        public void run() {
            Log.i(TAG, "开始连接");
            SmartBle.getInstance().stopSearch();
            if (mSocket == null) {
                if (mCallBack != null) {
                    mCallBack.connectError(BluetoothExceptionCompat.getConnectSocketException());
                }
                return;
            }
            try {
                mSocket.connect();
                if (mCallBack != null) {
                    mCallBack.connectSuccess(mDevice);
                }
                //开启连接
                startReceiveThread(mSocket);
            } catch (IOException e) {
                e.printStackTrace();
                close();
                if (mCallBack != null) {
                    mCallBack.connectError(BluetoothExceptionCompat.getConnectSocketException());
                }
            }
            Log.i(TAG, "ConnectBluetoothThread end");
            super.run();
        }

        void close() {
            if (mSocket != null) {
                try {
                    mSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    /**
     * 开启接收的线程
     *
     * @param socket
     */
    private void startReceiveThread(BluetoothSocket socket) {
        if (mConnectReceiveMsgThread != null) {
            mConnectReceiveMsgThread.close();
            mConnectReceiveMsgThread = null;
        }
        mConnectReceiveMsgThread = new ConnectedReceiveMsgThread(socket);
        mConnectReceiveMsgThread.start();
    }

    /**
     * 接收消息的线程
     */
    private class ConnectedReceiveMsgThread extends Thread {
        private final BluetoothSocket mSocket;
        private final DataOutputStream mOutputStream;
        private final DataInputStream mInputStream;

        ConnectedReceiveMsgThread(BluetoothSocket socket) {
            mSocket = socket;
            DataInputStream tmpIn = null;
            DataOutputStream tmpOut = null;
            try {
                tmpOut = new DataOutputStream(mSocket.getOutputStream());
                tmpIn = new DataInputStream(mSocket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            mOutputStream = tmpOut;
            mInputStream = tmpIn;
            mState = STATE_CONNECTED;
        }

        @Override
        public void run() {
            byte[] receiveData = new byte[1024];
            while (mState == STATE_CONNECTED) {
                try {
                    int size = mInputStream.read(receiveData);
                    if (size > 0 && mCallBack != null) {
                        byte[] data = new byte[size];
                        System.arraycopy(receiveData, 0, data, 0, data.length);
                        if (mCallBack != null)
                            mCallBack.receiveMsg(data);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    disConnect();
                }
            }
            Log.i(TAG,"ConnectedReceiveMsgThread end");
            if (mCallBack != null) {
                mCallBack.disConnect();
            }
            super.run();

        }

        //写入数据
        boolean write(byte[] buffer) {
            try {
                mOutputStream.write(buffer);
                mOutputStream.flush();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        void close() {
            if (mSocket != null) {
                try {
                    mSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
