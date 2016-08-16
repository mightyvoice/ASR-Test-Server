package com.example.lj.asrttstest;

import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import android.widget.TextView;
import android.widget.Toast;


public class CloudASRActivity extends AppCompatActivity
{
    private TextView resultTextView;

    private MediaRecorder googleRecorder;
    private File googleAudioFile;
    private File googleAudioDir;
    private boolean sdCardExist;
    private String tmpAudioName = "xxx";
    private String msgFromClient = "";

    private final int serverPort = 13458;
    private ServerSocket serverSocket;
    private ArrayList<Socket> clientSocketList = new ArrayList<Socket>();
    Handler clientMessgageHandler;
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
        final Button startRecordingButton = (Button) findViewById(R.id.startAudioRecordButton);
        final Button stopRecordingButton = (Button) findViewById(R.id.stopAudioRecordButton);
        final Button sendCommandButton = (Button) findViewById(R.id.sendCommandButton);

        startRecordingButton.setEnabled(false);
        stopRecordingButton.setEnabled(false);

        getWIFILocalIpAdress(getApplicationContext());

        clientMessgageHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 1){
                    stopGoogleRecording();
//                    playFile(googleAudioFile);
                }
            }
        };

        new Thread(new ServerThread()).start();

        sendCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    new Thread(new ServerSendThread(s, msg)).start();
                }
            }
        });

        startRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initGoogleRecoder();
                startRecordingButton.setEnabled(false);
            }
        });

        stopRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopGoogleRecording();
                if (googleAudioFile != null && googleAudioFile.exists()) {
                    playFile(googleAudioFile);
                }
                else{
                    Log.d("sss", "No audio file");
                }
                startRecordingButton.setEnabled(true);
            }
        });

    }

    private void getWIFILocalIpAdress(Context mContext) {
        WifiManager wifiManager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = formatIpAddress(ipAddress);
        resultTextView.setText("IP: "+ip);
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
            googleAudioFile = File.createTempFile(tmpAudioName+String.valueOf(System.currentTimeMillis()), ".amr",
                    googleAudioDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        googleRecorder = new MediaRecorder();
        googleRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        googleRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
        googleRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        googleRecorder.setOutputFile(googleAudioFile.getAbsolutePath());
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

    private void playFile(File f)
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
                    msgFromClient = input.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("sss", "Msg from client: "+msgFromClient);
                if(msgFromClient.equals("End")){
                    Message msg = new Message();
                    msg.what = 1;
                    clientMessgageHandler.sendMessage(msg);
                }
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
}