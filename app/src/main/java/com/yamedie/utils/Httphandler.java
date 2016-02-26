package com.yamedie.utils;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

/**
 * Created by Li Dachang on 16/1/26.
 * ..-..---.-.--..---.-...-..-....-.
 */
public class Httphandler {
    /**
     * 上传图片并获取URl地址
     *
     * @param params          请求队列
     * @param url             url地址
     * @param responseHandler 回调
     */
    public static void postImg(RequestParams params, String url, JsonHttpResponseHandler responseHandler) {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            MySSLSocketFactory socketFactory = new MySSLSocketFactory(trustStore);
            socketFactory.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(30 * 1000);
            client.setSSLSocketFactory(socketFactory);
            client.post(url, params, responseHandler);
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException
                | NoSuchAlgorithmException | IOException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据图片URL检查人脸
     *
     * @param context         上下文
     * @param params          请求队列
     * @param url             地址
     * @param responseHandler 回调
     */
    public static void checkFace(final Context context, RequestParams params, String url, JsonHttpResponseHandler responseHandler) {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            MySSLSocketFactory socketFactory = new MySSLSocketFactory(trustStore);
            socketFactory.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(30 * 1000);
            client.setSSLSocketFactory(socketFactory);
            client.get(url, params, responseHandler);
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | KeyManagementException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static void getFaceSet(final Context context, RequestParams params, String url, JsonHttpResponseHandler jsonHttpResponseHandler) {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            MySSLSocketFactory socketFactory = new MySSLSocketFactory(trustStore);
            socketFactory.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(30 * 1000);
            client.setSSLSocketFactory(socketFactory);
            client.get(url, params, jsonHttpResponseHandler);
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | KeyManagementException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * 普通异步get请求
     *
     * @param params                  请求队列
     * @param url                     url
     * @param jsonHttpResponseHandler 回调
     */
    public static void normalAsyncGet(final Context context, RequestParams params, String url, JsonHttpResponseHandler jsonHttpResponseHandler) {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            MySSLSocketFactory socketFactory = new MySSLSocketFactory(trustStore);
            socketFactory.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(30 * 1000);
            client.setSSLSocketFactory(socketFactory);
            client.get(url, params, jsonHttpResponseHandler);
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | KeyManagementException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * 普通同步请求
     *
     * @param params                  请求队列
     * @param url                     url
     * @param jsonHttpResponseHandler 回调
     */
    public static void normalSyncGet(final Context context, RequestParams params, String url, JsonHttpResponseHandler jsonHttpResponseHandler) {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            MySSLSocketFactory socketFactory = new MySSLSocketFactory(trustStore);
            socketFactory.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            SyncHttpClient client = new SyncHttpClient();
            client.setTimeout(30 * 1000);
            client.setSSLSocketFactory(socketFactory);
            client.get(url, params, jsonHttpResponseHandler);
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | KeyManagementException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
