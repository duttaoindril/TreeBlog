package com.drillin.oindrildutta.treeblog;

import android.os.AsyncTask;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class OkHttpHandler extends AsyncTask<String, Void, byte[]> {
    OkHttpClient client = new OkHttpClient();
    @Override
    protected byte[] doInBackground(String... params) {
        Request.Builder builder = new Request.Builder();
        builder.url(params[0]);
        Request request = builder.build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().bytes();
        } catch (Exception ignored) {}
        return null;
    }
}