package com.bhx.common.http.upload;

import java.io.File;

/**
 * 上传得参数
 */
public class UploadParam {
    //标识是文本参数
    public static final int TYPE_TEXT = 0x01;
    //标识是文件参数
    public static final int TYPE_FILE = 0x02;


    private String fileKey; // 文件得key 一般是表单得key
    private File file; //需要上传得文件

    private String paramsKey; // 参数得key
    private String paramsValue;//参数得Value

    private int type; // 标识是什么类型得参数

    public UploadParam(String fileKey, File file) {
        this.type = TYPE_FILE;
        this.fileKey = fileKey;
        this.file = file;
    }

    public UploadParam(String paramsKey, String paramsValue) {
        this.paramsKey = paramsKey;
        this.paramsValue = paramsValue;
        this.type = TYPE_TEXT;
    }


    public String getFileKey() {
        return fileKey;
    }

    public File getFile() {
        return file;
    }

    public String getParamsKey() {
        return paramsKey;
    }

    public String getParamsValue() {
        return paramsValue;
    }

    public int getType() {
        return type;
    }
}
