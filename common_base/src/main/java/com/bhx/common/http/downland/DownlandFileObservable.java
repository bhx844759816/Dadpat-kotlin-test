package com.bhx.common.http.downland;

import android.util.Pair;


import java.io.File;
import java.io.IOException;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * 文件存储 以及订阅下载进度
 */
public class DownlandFileObservable implements ObservableOnSubscribe<Object> {
    /**
     * 需要下载得文件信息
     */
    private FileInfo mFileInfo;
    private long mContentSize; //下载文件总得大小
    private long mDownLandSize;// 当前下载得大小
    private ObservableEmitter<Object> mObservableEmitter;
    private ResponseBody mResonseBody;
    private Source mSource;
    private Source mProgressSource;
    private BufferedSink mSink;
    private String mFileNameTmp;

    public DownlandFileObservable(ResponseBody responseBody, FileInfo fileInfo) throws IOException {
        this.mResonseBody = responseBody;
        this.mFileInfo = fileInfo;
        this.mFileNameTmp = fileInfo.getFileName() + ".tmp";
        init(responseBody);
    }

    @Override
    public void subscribe(ObservableEmitter<Object> e) throws Exception {
        mObservableEmitter = e;
        mSink.writeAll(Okio.buffer(mProgressSource));
        mSink.close();
        File file = new File(mFileInfo.getFilePath() + File.separator + mFileNameTmp);
        File destFile = new File(mFileInfo.getFilePath() + File.separator + mFileInfo.getFileName());
        if (destFile.exists()) {
            destFile.delete();
        }
        boolean result = file.renameTo(new File(mFileInfo.getFilePath() + File.separator + mFileInfo.getFileName()));
        if (result) {
            mObservableEmitter.onNext(mFileInfo);
        }
        mObservableEmitter.onComplete();
    }

    private void init(ResponseBody responseBody) throws IOException {
        mContentSize = responseBody.contentLength();
        mSource = responseBody.source();
        mProgressSource = getProgressSource(mSource);
        File dir = new File(mFileInfo.getFilePath());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(mFileInfo.getFilePath() + File.separator + mFileNameTmp);
        if (!file.exists()) {
            file.delete();
        }
        mSink = Okio.buffer(Okio.sink(file));
    }

    private void onRead(long read) {
        mDownLandSize += (read == -1 ? 0 : read);
        if (mObservableEmitter == null) {
            return;
        }
        if (mDownLandSize > mContentSize) {
            mDownLandSize = mContentSize;
        }
        mObservableEmitter.onNext(Pair.create(mDownLandSize, mContentSize));
    }

    private ForwardingSource getProgressSource(Source source) {
        return new ForwardingSource(source) {
            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long read = super.read(sink, byteCount);
                onRead(read);
                return read;
            }
        };
    }


}
