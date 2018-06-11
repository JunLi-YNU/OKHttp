package com.example.junli.okhttp;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    OkHttpClient okHttpClient = new OkHttpClient();
    private TextView mTvResult;
    private String mBaseUrl = "http://192.168.149.1:8080/imooc_okhttp/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvResult = findViewById(R.id.id_tv_result);

    }

    public void doPost(View view) throws IOException {
        //1.拿到okhttpClient的对象
        FormBody requestBodyBuilder = new FormBody.Builder()
                .add("username","JunLi1")
                .add("password","12345")
                .build();

        //2.构造Request
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(mBaseUrl + "login").post(requestBodyBuilder).build();

        executeRequest(request);
    }
    public void doGet(View view) throws IOException {
        //1.拿到okHttpClient对象。全局的执行者。
//        OkHttpClient okHttpClient = new OkHttpClient();

        //2.构造request 设计模式的构建者模式
        //yi
        Request.Builder builder = new Request.Builder();
        final Request request = builder
                .get()
                .url(mBaseUrl+"login?username=JunLi&password=1234").build();

        //3.将Request封装为call
        executeRequest(request);
    }

    public void doPostString(View view) throws IOException {
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain;chaset=utf-8"),
                "{username:JunLi2,password:123}");

        Request.Builder builder = new Request.Builder();
        Request request = builder.url(mBaseUrl + "postString").post(requestBody).build();

        executeRequest(request);
    }
    public void doPostFile(View view) throws IOException {

        File file = new File(Environment.getExternalStorageDirectory(),"/sdcard/Download/山.jpg");
        if(!file.exists()){
            L.e(file.getAbsolutePath()+"no exists");
            return;
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-st"),
                file);

        Request.Builder builder = new Request.Builder();
        Request request = builder.url(mBaseUrl + "postFile").post(requestBody).build();

        executeRequest(request);
    }

    private void executeRequest(Request request) throws IOException {
        Call call = okHttpClient.newCall(request);

        //4.执行call
//        call.execute();
        //同步调用一般不用会阻塞线程。一般用异步。
        call.enqueue(new Callback() {
            @Override
            //发生了一些错误执行的。
            public void onFailure(Call call, IOException e) {
                L.e("onFailure"+e.getMessage());
                e.printStackTrace();
            }

            //此方法非UI线程，无法更新UI。它放在子线程中执行目的是为了能够对大文件进行UI操作。
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                L.e("onResponse:");
                final String res = response.body().string();
                L.e(res);
//                InputStream is = request.body().byteStream();
//                mTvResult.setText(res);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTvResult.setText(res);
                    }
                });
            }
        });
    }
}
