package com.benbaba.dadpat.host.drum;

import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import androidx.annotation.DrawableRes;
import com.bhx.common.utils.LogUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.json.JSONObject;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Enumeration;

@SuppressWarnings("checkresult")
public class SocketManager {
    //
    private static final int SEND_PORT = 10025;
    private static final int RECEIVE_PORT = 10026;
    private static SocketManager INSTANCE;
    //接收的缓冲数据
    private OnSocketReceiveCallBack mCallBack;
    private DatagramSocket mSocket;
    private Disposable mSocketDisposable;
    private Gson mGson;
    private byte[] mReceiveData = new byte[2048];

    private SocketManager() {
        mGson = new Gson();
    }

    public static SocketManager getInstance() {
        if (INSTANCE == null) {
            synchronized (SocketManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SocketManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 开启接收Udp得线程
     */
    public void startReceiveUdpMsg(OnSocketReceiveCallBack callBack) {
        mCallBack = callBack;
        mSocketDisposable = Observable.create((ObservableOnSubscribe<String>) e -> {
            try {
                if (mSocket == null || mSocket.isClosed()) {
                    mSocket = new DatagramSocket(new InetSocketAddress(RECEIVE_PORT));
                    mSocket.setBroadcast(true);
                    mSocket.setReuseAddress(true);
                }
                DatagramPacket receivePacket = new DatagramPacket(mReceiveData, mReceiveData.length);
                while (!e.isDisposed() && !Thread.interrupted()) {
                    mSocket.receive(receivePacket);
                    byte[] data = Arrays.copyOf(mReceiveData, receivePacket.getLength());
                    e.onNext(new String(data));
                }
            } catch (SocketException so) {
                so.printStackTrace();
            } finally {
                release();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::parseMsg);
    }

    /**
     * 解析数据
     *
     * @param msg
     */
    private void parseMsg(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        LogUtils.i("接收到消息：" + msg);
        if (mCallBack != null) {
            mCallBack.receiveMsg(mGson.fromJson(msg, DrumBean.class));
        }
    }




    /**
     * 释放资源
     */
    public void release() {
        if (mSocketDisposable != null) {
            mSocketDisposable.dispose();
            mSocketDisposable = null;
        }
        if (mSocket != null && !mSocket.isClosed()) {
            mSocket.close();
            mSocket = null;
        }
    }

    /**
     * Socket得回调
     */
    public interface OnSocketReceiveCallBack {
        void receiveMsg(DrumBean bean);
    }

    public interface OnSocketSendCallBack {
        void sendMsgSuccess();

        void sendMsgError();
    }

    /**
     * 获取本机IP地址
     *
     * @return
     */
    private static String getLocalIPAddress() {
        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return hostIp;
    }


    /**
     * 发送Udp消息
     */
    public void sendReceiveUdpMsg(String msg, OnSocketSendCallBack callBack) {
        Observable.create((ObservableOnSubscribe<Boolean>) e -> {
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);
            socket.setReuseAddress(true);
            socket.setSoTimeout(2000);
            byte[] sendData = msg.getBytes("utf-8");
            DatagramPacket packet = new DatagramPacket(sendData, sendData.length,
                    InetAddress.getByName("255.255.255.255"), SEND_PORT);
            socket.send(packet);
            socket.close();
            Log.i("TAG", "sendReceiveUdpMsg:" + msg);
            e.onNext(true);
            e.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(aBoolean -> callBack.sendMsgSuccess(),
                        throwable -> callBack.sendMsgError());

    }


    /**
     * 发送消息并接收消息
     *
     * @param sendMsg
     * @param serverIp
     * @param serverPort
     * @param receivePort
     * @return Pair 得key是接收得IP地址 value是接收得消息
     * @throws SocketException
     * @throws UnknownHostException
     */
    public Pair<String, String> sendMsgForReceive(String sendMsg, String serverIp, int serverPort, int receivePort)
            throws SocketException, UnknownHostException {
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            throw new RuntimeException("请在子线程运行");
        }
        Log.i("TAG","sendMsg:"+sendMsg);
        DatagramSocket socket = new DatagramSocket(new InetSocketAddress(receivePort));
        socket.setReuseAddress(true); // 允许多个DatagramSocket 绑定到同一个端口号
        socket.setBroadcast(true);
        socket.setSoTimeout(500);
        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, 0, receiveData.length);
        byte[] data = sendMsg.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(data, 0, data.length);
        sendPacket.setAddress(InetAddress.getByName(serverIp));
        sendPacket.setPort(serverPort);
        try {
            socket.send(sendPacket);
            socket.receive(receivePacket);
            String receiveMsg = new String(receiveData, 0, receivePacket.getLength());
            String receiveAddress = receivePacket.getAddress().toString().substring(1);
            if (!TextUtils.isEmpty(receiveMsg) && !sendMsg.equals(receiveMsg)) {
                return new Pair<>(receiveAddress, receiveMsg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
        return null;
    }


}
