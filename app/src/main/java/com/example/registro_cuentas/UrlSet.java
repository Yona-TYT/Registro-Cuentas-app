package com.example.registro_cuentas;

import android.content.Context;

import androidx.fragment.app.FragmentActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UrlSet {

    private static Context mContext;
    private static FragmentActivity mActivity;

    static String mUrl = "https://pydolarve.org/api/v1/dollar?page=bcv";

    public UrlSet(Context applicationContext, FragmentActivity mActivity) {
        this.mContext = mContext;
        this.mActivity = mActivity;
    }

    public static void urlRun() throws IOException {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(mUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String myResponse = response.body().string();

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //JSONObject json = new JSONObject(myResponse);
                            JSONObject json = new JSONObject(myResponse);
                            Iterator<String> mKeysA = json.keys();
                            for (; mKeysA.hasNext(); ) {
                                String mObjA = mKeysA.next();
                                JSONObject newJson = json.getJSONObject(mObjA);
                                Iterator<String> mKeysB = newJson.keys();

                                for (; mKeysB.hasNext(); ) {
                                    String mObjB = mKeysB.next();
                                    if (mObjB.equals("usd")) {

                                        Basic.msg("--- " + newJson.getJSONObject(mObjB).get("price"));
                                    }
                                }
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });
    }
}
