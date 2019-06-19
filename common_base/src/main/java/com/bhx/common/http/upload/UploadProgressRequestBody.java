package com.bhx.common.http.upload;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * 上传进度监听得RequestBody
 */
public class UploadProgressRequestBody extends RequestBody {

    private RequestBody requestBody;
    private UploadParam params;
    private UploadProgressObserver<ResponseBody> progressObserver;

    public UploadProgressRequestBody(UploadParam params, UploadProgressObserver<ResponseBody> observer) {
        this.params = params;
        this.requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), params.getFile());
        this.progressObserver = observer;

    }

    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public long contentLength() {
        try {
            if (requestBody.contentLength() > 0) {
                return requestBody.contentLength();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return params.getFile().length();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        CountingSink countingSink = new CountingSink(sink);
        BufferedSink bufferedSink = Okio.buffer(countingSink);
        //写入
        requestBody.writeTo(bufferedSink);
        //刷新
        bufferedSink.flush();
    }

    /**
     * 计算下载得大小
     */
    private final class CountingSink extends ForwardingSink {
        private long bytesWritten = 0;

        CountingSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            bytesWritten += byteCount;
            if (progressObserver != null) {
                progressObserver.onProgressChange(bytesWritten, contentLength(), params);
            }
        }
    }
}
