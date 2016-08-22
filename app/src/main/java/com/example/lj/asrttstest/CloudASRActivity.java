package com.example.lj.asrttstest;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;

import android.widget.TextView;
import android.widget.Toast;

import com.example.lj.asrttstest.info.Global;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class CloudASRActivity extends AppCompatActivity
{
    private String server_url = "http://192.168.1.87:5000/groundTruth?id=";

    private TextView resultTextView;

    private MediaRecorder googleRecorder;
//    private File googleAudioFile;
    private File googleAudioDir;
    private boolean sdCardExist;
    private String tmpAudioName = "xxx";

    private String currentIP;
    private final int serverPort = 13458;
    private ServerSocket serverSocket;
    private ArrayList<Socket> clientSocketList = new ArrayList<Socket>();
    Handler clientMessgageHandler;
    boolean getGoogleAsrResult = false;
    boolean getNuanceAsrResult = false;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cloud_asr);

        // UI initialization
        resultTextView = (TextView)findViewById(R.id.cloudResultEditText);
        final Button nextSentenceButton = (Button) findViewById(R.id.doNextSentenceButton);
        final Button startAsrButton = (Button) findViewById(R.id.sendCommandButton);
        startAsrButton.setEnabled(false);

        currentIP = getWIFILocalIpAdress(getApplicationContext());

        clientMessgageHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                //get current sentence for ASR
                if(msg.what == 1){
                    updateTextView();
                    startAsrButton.setEnabled(true);
//                    nextSentenceButton.setEnabled(false);
                }

                //two ASR finished
                if(msg.what == 2){
                    updateTextView();
                    startAsrButton.setEnabled(true);
                    nextSentenceButton.setEnabled(true);
                }

            }
        };

        Global.currentSentenceID--;
        nextSentenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.currentSentenceID++;
                new Thread(new GetCurrentSentenceThread()).start();
            }
        });

        startAsrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAsrButton.setEnabled(false);
                nextSentenceButton.setEnabled(false);
                initGoogleRecoder();
                Boolean first = true;
                for(Socket s: clientSocketList){
                    String msg;
                    if(first){
                        msg = "Nuance\n";
                        first = false;
                    }
                    else{
                        msg = "Google\n";
                        first = true;
                    }
                    getNuanceAsrResult = false;
                    getGoogleAsrResult = false;
                    new Thread(new ServerSendThread(s, msg)).start();
                    new Thread(new WaitAsrFinishThread()).start();
                }
            }
        });

        new Thread(new ServerThread()).start();
    }

    private String[] getAsrResult(String input){
        JSONObject cur;
        String[] ans = new String[2];
        ans[0] = ans[1] = "";
        try {
            cur = new JSONObject(input);
            ans[0] = cur.optString("type");
            ans[1] = cur.optString("result");
        }catch (JSONException e){
            e.printStackTrace();
            return ans;
        }
        return ans;
    }

    private void updateTextView(){
        resultTextView.setText("Phone IP: " + currentIP + "\n" +
                               "Sentence ID: " + String.valueOf(Global.currentSentenceID) +"\n"+
                               "Sentence for ASR: \n"+ Global.currentSentenceForASR);
    }

    private String getWIFILocalIpAdress(Context mContext) {
        WifiManager wifiManager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = formatIpAddress(ipAddress);
        return ip;
    }

    private String formatIpAddress(int ipAdress) {

        return (ipAdress & 0xFF ) + "." +
                ((ipAdress >> 8 ) & 0xFF) + "." +
                ((ipAdress >> 16 ) & 0xFF) + "." +
                ( ipAdress >> 24 & 0xFF) ;
    }


    private void initGoogleRecoder(){
        sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            googleAudioDir = Environment.getExternalStorageDirectory();
        }
        try {
            tmpAudioName = Global.currentUserID+"_"+String.valueOf(Global.currentSentenceID);
            Log.d("sss", "File name: " + tmpAudioName);
            Global.currentAudioFile = File.createTempFile(tmpAudioName, ".amr", googleAudioDir);
            Log.d("sss", "File path: " + Global.currentAudioFile.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        googleRecorder = new MediaRecorder();
        googleRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        googleRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
        googleRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        googleRecorder.setOutputFile(Global.currentAudioFile.getAbsolutePath());
        try {
            googleRecorder.prepare();
            googleRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopGoogleRecording(){
        googleRecorder.stop();
        googleRecorder.release();
    }

    private void playAudioFile(File f)
    {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        String type = getFileType(f);
        intent.setDataAndType(Uri.fromFile(f), type);
        startActivity(intent);
    }

    private String getFileType(File f)
    {
        String end = f.getName().substring(
                f.getName().lastIndexOf(".") + 1, f.getName().length())
                .toLowerCase();
        String type = "";
        if (end.equals("mp3") || end.equals("aac") || end.equals("aac")
                || end.equals("amr") || end.equals("mpeg")
                || end.equals("mp4"))
        {
            type = "audio";
        } else if (end.equals("jpg") || end.equals("gif")
                || end.equals("png") || end.equals("jpeg"))
        {
            type = "image";
        } else
        {
            type = "*";
        }
        type += "/*";
        return type;
    }

    private class ServerThread implements Runnable{
        @Override
        public void run() {
            try{
                serverSocket = new ServerSocket(serverPort);
            }catch (Exception e){
                e.printStackTrace();
            }
            clientSocketList.clear();
            while(!serverSocket.isClosed()){
                try {
                    Log.d("sss", "waiting for client");
                    Socket clientSocket = serverSocket.accept();
                    clientSocketList.add(clientSocket);
                    Log.d("sss", "Connect to client: "+clientSocket.getInetAddress().toString());
                    new Thread(new ServerReceiveThread(clientSocket)).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class ServerReceiveThread implements Runnable{

        private Socket clientSocket;
        private BufferedReader input;
        private String messageFromClient;

        public ServerReceiveThread(Socket clientSocket){
            this.clientSocket = clientSocket;
            try {
                input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    messageFromClient = input.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("sss", "Msg from client: " + messageFromClient);
                new Thread(new ProcessMessageFromClientThread(messageFromClient)).start();
            }
        }
    }

    private class ServerSendThread implements Runnable{

        private Socket clientSocket;
        private BufferedWriter severWriter;
        private String msgToSend;

        public ServerSendThread(Socket clientSocket, String msg){
            this.clientSocket = clientSocket;
            this.msgToSend = msg;
            try {
                this.severWriter = new BufferedWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                this.severWriter.write(msgToSend);
                this.severWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String parseCurrentSentence(String input){
        String result = "";
        try{
            JSONObject cur = new JSONObject(input);
            cur = cur.optJSONObject("result");
            result = cur.optString("ground_truth");
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    private class GetCurrentSentenceThread implements Runnable{
        @Override
        public void run() {
            HttpURLConnection conn = null;
            try {
                URL mURL = new URL(Global.Get_Cur_Sentence_URL+String.valueOf(Global.currentSentenceID));
                conn = (HttpURLConnection) mURL.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    InputStream is = conn.getInputStream();
                    String tmp = getStringFromInputStream(is);
                    Log.d("sentence: ", tmp);
                    Global.currentSentenceForASR = parseCurrentSentence(tmp);
                    Message msg = new Message();
                    msg.what = 1;
                    clientMessgageHandler.sendMessage(msg);
                    Log.d("sss", Global.currentSentenceForASR);
                } else {
                    throw new NetworkErrorException("response status is "+responseCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }
    }

    private static String getStringFromInputStream(InputStream is)
            throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        is.close();
        String state = os.toString();
        os.close();
        return state;
    }

    private class ProcessMessageFromClientThread implements Runnable{

        private String client_msg;

        public ProcessMessageFromClientThread(String _msg){
            client_msg = _msg;
        }

        @Override
        public void run() {
            String[] results = getAsrResult(client_msg);
            Log.d("sss", "From client: "+results[0]+" "+results[1]);
            if(results[0].equals("end")){
                stopGoogleRecording();
            }
            else if(results[0].equals("google")){
                Global.googleAsrResult = results[1];
                getGoogleAsrResult = true;
            }
            else{
                Global.nuanceAsrResult = results[1];
                getNuanceAsrResult = true;
            }
        }
    }

    private class WaitAsrFinishThread implements Runnable{
        @Override
        public void run() {
            while(true){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(getGoogleAsrResult && getNuanceAsrResult){
                    getGoogleAsrResult = false;
                    getNuanceAsrResult = false;
                    Global.uploadSuccessCount = 0;
                    Intent localIntent = new Intent(CloudASRActivity.this, ConfirmAndUploadActivity.class);
                    CloudASRActivity.this.startActivity(localIntent);
                    Message msg = new Message();
                    msg.what = 2;
                    clientMessgageHandler.sendMessage(msg);
                }
            }
        }
    }

}