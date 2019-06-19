package com.benbaba.dadpat.common.bluetooth.config;

/**
 * 扫描配置类
 */
public class SearchConfig {
    private String deviceName;// 需要扫描得设备名称

    private String macAddress;// 需要扫描得mac地址

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
}
