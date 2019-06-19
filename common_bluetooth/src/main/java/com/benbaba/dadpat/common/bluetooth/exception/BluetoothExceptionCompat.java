package com.benbaba.dadpat.common.bluetooth.exception;

/**
 * 蓝牙异常得组装类
 */
public class BluetoothExceptionCompat {

    public static final int CODE_A2DP_CONNECT_ERROR = 501;
    public static final int CODE_CONNECT_SOCKET_ERROR = 502;
    public static final int CODE_SOCKET_ERROR = 503;
    public static final int CODE_BLUETOOTH_NOT_OPEN = 504;
    public static final int CODE_SEARCH_ERROR = 505;
    public static final int CODE_CANCEL_BOND = 506;


    public static BaseBluetoothException getA2dpException() {
        return new BaseBluetoothException(CODE_A2DP_CONNECT_ERROR, "连接蓝牙设备A2DP失败");
    }

    public static BaseBluetoothException getConnectSocketException() {
        return new BaseBluetoothException(CODE_CONNECT_SOCKET_ERROR, "连接蓝牙设备失败");
    }

    public static BaseBluetoothException getBluetoothSocketException() {
        return new BaseBluetoothException(CODE_SOCKET_ERROR, "蓝牙断开连接");
    }

    public static BaseBluetoothException getBluetoothNotOpenException() {
        return new BaseBluetoothException(CODE_BLUETOOTH_NOT_OPEN, "蓝牙未打开");
    }
    public static BaseBluetoothException getBluetoothSearchException() {
        return new BaseBluetoothException(CODE_SEARCH_ERROR, "蓝牙搜索异常");
    }

    public static BaseBluetoothException getCancelBondException() {
        return new BaseBluetoothException(CODE_CANCEL_BOND, "取消绑定");
    }
}
