package com.code4a.wificonnectlib;

import android.content.Context;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.code4a.wificonnectlib.wifi.WiFi;
import com.code4a.wificonnectlib.wifi.WiFiConnect;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by code4a on 2017/5/16.
 */

public final class WiFiHelper {

    //    private Context mContext;
    private WifiManager mWifiManager;
    private static WiFiHelper INSTANCE;

    public static WiFiHelper getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (WiFiHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new WiFiHelper(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 构造方法
     *
     * @param context 上下文对象
     */
    private WiFiHelper(Context context) {
        mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    /**
     * 开始扫描WiFi
     */
    public void startScan() {
        //如果WIFI没有打开，则打开WIFI
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
        mWifiManager.startScan();
    }

    public void disconnectWifi() {
        mWifiManager.disconnect();
    }

    /**
     * 获取扫描结果
     *
     * @return 扫描结果
     */
    public List<ScanResult> getScanResults() {
        return mWifiManager.getScanResults();
    }

    /**
     * 获取当前连接的wifi信息
     *
     * @return
     */
    public String getCurrentWifiSSID() {
        return mWifiManager.getConnectionInfo().getSSID().replace("\"", "");
    }

    /**
     * 连接到指定SSID名称的WiFi热点上
     *
     * @param ssid                wifi名称
     * @param password            wifi密码
     * @param wiFiConnectListener 连接监听
     */
    public void connectWiFi(Context context, String ssid, String password, WiFiConnectListener wiFiConnectListener) {
        ScanResult scanResult = getTargetScanResultBySSID(ssid);
        if (scanResult == null && wiFiConnectListener != null) {
            wiFiConnectListener.connectStart();
            wiFiConnectListener.connectResult(ssid, false);
            wiFiConnectListener.connectEnd();
        } else {
            connectWiFi(context, scanResult, password, wiFiConnectListener);
        }
    }


    /**
     * 连接到指定ScanResult的WiFi热点上
     *
     * @param mScanResult         wifi的扫描结果
     * @param password            wifi密码
     * @param wiFiConnectListener 连接监听
     */
    public void connectWiFi(Context context, ScanResult mScanResult, String password, WiFiConnectListener wiFiConnectListener) {
        if (wiFiConnectListener != null) {
            wiFiConnectListener.connectStart();
        }
        boolean connectResult;
        final WifiConfiguration config = WiFiConnect.getWifiConfiguration(mWifiManager, mScanResult);
        if (config == null) {
            connectResult = WiFiConnect.newNetworkToConnect(context, mWifiManager, mScanResult, password);
            if (wiFiConnectListener != null) {
                wiFiConnectListener.connectResult(mScanResult.SSID, connectResult);
            }
        } else {
//            final boolean isCurrentNetwork_ConfigurationStatus = config.status == WifiConfiguration.Status.CURRENT;
            final WifiInfo info = mWifiManager.getConnectionInfo();
            final boolean isCurrentNetwork_WifiInfo = info != null
                    && TextUtils.equals(info.getSSID(), mScanResult.SSID)
                    && TextUtils.equals(info.getBSSID(), mScanResult.BSSID);
//            isCurrentNetwork_ConfigurationStatus ||
            if (isCurrentNetwork_WifiInfo) {
                if (wiFiConnectListener != null) {
                    wiFiConnectListener.connectResult(mScanResult.SSID, true);
                }
            } else {
                boolean forgetResult = WiFiConnect.forgetCurrentNetwork(mWifiManager, mScanResult);
                Log.i("TAG", "forgetResult:" + forgetResult);
                if (forgetResult) {
                    connectResult = WiFiConnect.newNetworkToConnect(context, mWifiManager, mScanResult, password);
                } else {
                    connectResult = WiFiConnect.configNetworkToConnect(context, mWifiManager, mScanResult);
                }

                if (wiFiConnectListener != null) {
                    wiFiConnectListener.connectResult(mScanResult.SSID, connectResult);
                }
            }
        }
        if (wiFiConnectListener != null) {
            wiFiConnectListener.connectEnd();
        }
    }

    /**
     * 根据SSID名称 获取到ScanResult
     *
     * @param ssid wifi名称
     * @return 扫描结果
     */
    public ScanResult getTargetScanResultBySSID(String ssid) {
        ScanResult sr = null;
        List<ScanResult> wifiList = getScanResults();
        if (wifiList == null || TextUtils.isEmpty(ssid)) {
            return null;
        }
        for (int i = 0; i < wifiList.size(); i++) {
            if (ssid.equals(wifiList.get(i).SSID)) {
                sr = wifiList.get(i);
                break;
            }
        }
        return sr;
    }

    /**
     * 排除ssid的其他wifi信息
     *
     * @param ssid
     * @return
     */
    public List<ScanResult> getScanResultsOutTarget(String ssid) {
        List<ScanResult> results = new ArrayList<>();
        List<ScanResult> wifiList = getScanResults();
        if (wifiList == null || TextUtils.isEmpty(ssid)) {
            return null;
        }
        for (int i = 0; i < wifiList.size(); i++) {
            if (!ssid.equals(wifiList.get(i).SSID) && !TextUtils.isEmpty(wifiList.get(i).SSID)) {
                results.add(wifiList.get(i));
            }
        }
        return results;
    }

    /**
     * 根据SSID名称 获取到ScanResults
     *
     * @param ssid wifi名称
     * @return 扫描结果
     */
    public List<ScanResult> getTargetScanResults(String ssid) {
        List<ScanResult> results = new ArrayList<>();
        List<ScanResult> wifiList = getScanResults();
        if (wifiList == null || TextUtils.isEmpty(ssid)) {
            return null;
        }
        for (int i = 0; i < wifiList.size(); i++) {
            if (ssid.equals(wifiList.get(i).SSID)) {
                results.add(wifiList.get(i));
            }
        }
        return results;
    }

    /**
     * wifi连接监听
     */
    public interface WiFiConnectListener {
        void connectStart();

        void connectResult(String ssid, boolean isSuccess);

        void connectEnd();
    }
}
