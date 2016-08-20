package com.example.lj.asrttstest;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.lj.asrttstest.R;
import com.example.lj.asrttstest.info.Global;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ConfirmAndUploadActivity extends AppCompatActivity {

    private String uploadResultJSON = "";
    private Handler messageHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_and_upload);

        LinearLayout layout=(LinearLayout)findViewById(R.id.cancelOrUploadLayout);
        Button cancelButton = (Button) findViewById(R.id.cancelAsrResultButton);
        Button uploadButton = (Button) findViewById(R.id.uploadsAsrResultButton);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),
                        "Click outside area of the window to exit",
                        Toast.LENGTH_LONG);
            }
        });

        messageHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1){
                    new Thread(new UploadAudioFileThread(Global.HOST)).start();
                }
                if(msg.what == 2){
                    finish();
                }
            }
        };

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUploadResult();
                new Thread(new UploadAsrResultThread(Global.Upload_ASR_Result_URL)).start();
            }
        });

        resizePopUpWindow();
    }

    public void getUploadResult(){
        JSONObject tmp = new JSONObject();
        try {
            tmp.put("user_id", Global.currentUserID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            tmp.put("ground_truth_id", String.valueOf(Global.currentSentenceID));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            tmp.put("asr_google", Global.googleAsrResult);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            tmp.put("asr_nuance", Global.nuanceAsrResult);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        uploadResultJSON = tmp.toString();
        try {
            Log.d("sss", "uploaded result: "+tmp.toString(4));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private byte[] getAudioByteArray(){
        InputStream is = null;
        try {
            is = new FileInputStream(Global.currentAudioFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int bytesRead;
        try {
            while ((bytesRead = is.read(b)) != -1) {
                bos.write(b, 0, bytesRead);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return bos.toByteArray();
    }

    private void resizePopUpWindow(){
        DisplayMetrics ds = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(ds);

        int width = ds.widthPixels;
        int height = ds.heightPixels;

        getWindow().setLayout(
                (int) (width * 0.8),
                (int) (height * 0.5));
    }

    private class UploadAsrResultThread implements Runnable{

        String cur_url;
        public UploadAsrResultThread(String _url){
            cur_url = _url;
        }

        @Override
        public void run() {
            HttpURLConnection conn = null;
            try {
                URL mURL = new URL(cur_url);
                conn = (HttpURLConnection) mURL.openConnection();
                conn.setRequestMethod("POST");
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(10000);
                conn.setDoOutput(true);

                OutputStream out = conn.getOutputStream();
                out.write(uploadResultJSON.getBytes());
                int responseCode = conn.getResponseCode();
                Log.d("sss", "upload ASR result response: "+conn.getResponseMessage());
                if (responseCode == 200) {
                    InputStream is = conn.getInputStream();
                } else {
                    throw new NetworkErrorException("response status is " + responseCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();// 关闭连接
                }
                Message msg = new Message();
                msg.what = 1;
                messageHandler.sendMessage(msg);
            }
        }
    }

    private class UploadAudioFileThread implements Runnable{

        String cur_url;
        public UploadAudioFileThread(String _url){
            cur_url = _url;
        }

        @Override
        public void run() {
            try {
                    HttpUrl httpUrl = new HttpUrl.Builder()
                            .scheme("http")
                            .host(cur_url)
                            .port(5000)
                            .addPathSegment("uploadFile")//adds "/pathSegment" at the end of hostname
                            .addQueryParameter("userId", Global.currentUserID)
                            .addQueryParameter("groundTruthId", Global.currentSentenceID.toString()) //add query parameters to the URL
//                            .addEncodedQueryParameter("encodedName", "encodedValue")//add encoded query parameters to the URL
                            .build();
                    OkHttpClient client = new OkHttpClient();
                    MediaType MEDIA_TYPE = MediaType.parse("audio/amr");
                    MultipartBody body = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("action", "upload")
                            .addFormDataPart("format", "json")
                            .addFormDataPart("filename", Global.currentAudioFile.getName()) //e.g. title.png --> imageFormat = png
                            .addFormDataPart("fileData", Global.currentAudioFile.getName(), RequestBody.create(MEDIA_TYPE, Global.currentAudioFile))
                            .addFormDataPart("name", "fileData")
                            .build();
                    String response = new ApiCall().POST(client, httpUrl, body);
                    Log.d("sss", "upload file response: "+response);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Message msg = new Message();
                msg.what = 2;
                messageHandler.sendMessage(msg);
            }
        }
    }
    private class ApiCall {

        //GET network request
        public String GET(OkHttpClient client, HttpUrl url) throws IOException {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }

        //POST network request
        public String POST(OkHttpClient client, HttpUrl url, RequestBody body) throws IOException {
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }
    }

}
