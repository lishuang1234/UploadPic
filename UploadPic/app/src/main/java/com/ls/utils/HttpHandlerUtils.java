package com.ls.utils;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;

import java.io.File;
import java.util.List;

public class HttpHandlerUtils {


    public interface HttpStateListener {

        void fail(String loginState);

        void success(String refreshState);


    }

    private HttpStateListener httpStateListener;

    public void upLoad(String url, List<File> files) {

        RequestParams params = new RequestParams();
        if (files.size() == 0) {
            if (httpStateListener != null) {
                httpStateListener.fail("failure");
            }
            return;
        }
        for (int i = 0; i < files.size(); i++) {
            params.addBodyParameter("file:   " + files.get(i).getAbsolutePath(), files.get(i));
            LogUtils.e("   file   " + i);
        }
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST,
                url,
                params,
                new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        System.out.println("上传onStart");
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        LogUtils.e("上传onLoading" + current + "/" + total);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        LogUtils.e("上传onSuccess   " + responseInfo.result);
                        if (httpStateListener != null) {
                            httpStateListener.success("success");
                        }

                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        LogUtils.e("上传onFailure" + msg);
                        if (httpStateListener != null) {
                            httpStateListener.fail("failure");
                        }
                    }
                });
    }

    public void httpGet(String url) {
        RequestParams params = new RequestParams();
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.GET,
                url,
                new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        LogUtils.e("onStart()   ");
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        LogUtils.e("onLoading()   ");
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        LogUtils.e("onSuccess()   " + responseInfo.result);
                        if (httpStateListener != null) {
                            httpStateListener.success(responseInfo.result);
                        }

                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        LogUtils.e("onFailure()   " + msg);
                        if (httpStateListener != null) {
                            httpStateListener.fail("failure");
                        }
                    }
                });
    }


    public HttpStateListener getHttpStateListener() {
        return httpStateListener;
    }

    public void setHttpStateListener(HttpStateListener httpStateListener) {
        this.httpStateListener = httpStateListener;
    }

}