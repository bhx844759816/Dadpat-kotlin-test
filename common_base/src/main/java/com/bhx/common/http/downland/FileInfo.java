package com.bhx.common.http.downland;

/**
 * 需要下载得文件信息
 */
public class FileInfo {

    private String url;
    private String filePath; // 文件存储路径
    private String fileName; // 文件名称

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
