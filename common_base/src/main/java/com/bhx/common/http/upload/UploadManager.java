package com.bhx.common.http.upload;

import android.text.TextUtils;

import com.bhx.common.http.RetrofitManager;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

/**
 * 上传文件得单例
 */
public class UploadManager {

    public static void uploadFile(String url, List<UploadParam> params, UploadProgressObserver<ResponseBody> observer) {
        ApiService apiService = RetrofitManager.getInstance().createApiService(ApiService.class);
        List<MultipartBody.Part> paramList = new ArrayList<>();
        for (UploadParam param : params)
            switch (param.getType()) {
                case UploadParam.TYPE_TEXT:
                    paramList.add(MultipartBody.Part.createFormData(param.getParamsKey(), param.getParamsValue()));
                    break;
                case UploadParam.TYPE_FILE:
                    if (param.getFile() != null && !TextUtils.isEmpty(param.getFileKey())) {
                        UploadProgressRequestBody uploadRequestBody = new UploadProgressRequestBody(param, observer);
                        MultipartBody.Part part = MultipartBody.Part.createFormData(param.getFileKey(), param.getFile().getName(), uploadRequestBody);
                        paramList.add(part);
                    }
                    break;
            }
        apiService.uploadFile(url, paramList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public static void uploadFile(String url,  List<UploadParam> params, Observer<ResponseBody> observer) {
        ApiService apiService = RetrofitManager.getInstance().createApiService(ApiService.class);
        List<MultipartBody.Part> paramList = new ArrayList<>();
        for (UploadParam param : params) {
            switch (param.getType()) {
                case UploadParam.TYPE_TEXT:
                    paramList.add(MultipartBody.Part.createFormData(param.getParamsKey(), param.getParamsValue()));
                    break;
                case UploadParam.TYPE_FILE:
                    if (param.getFile() != null && !TextUtils.isEmpty(param.getFileKey())) {
                        RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"), param.getFile());
                        MultipartBody.Part part = MultipartBody.Part.createFormData(param.getFileKey(), param.getFile().getName(), body);
                        paramList.add(part);
                    }
                    break;
            }
        }
        apiService.uploadFile(url, paramList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public static void uploadFile(String url, UploadParam params, UploadProgressObserver<ResponseBody> observer) {
        uploadFile(url, Collections.singletonList(params), observer);
    }

    public static void uploadFile(String url, UploadParam params, Observer<ResponseBody> observer) {
        uploadFile(url, Collections.singletonList(params), observer);
    }

    interface ApiService {
        @Multipart
        @POST
        Observable<ResponseBody> uploadFile(@Url String url, @Part List<MultipartBody.Part> params);
    }
}
