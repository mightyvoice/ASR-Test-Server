package com.example.lj.asrttstest;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.example.lj.asrttstest.info.AllContactInfo;
import com.example.lj.asrttstest.info.AppInfo;
import com.example.lj.asrttstest.info.ContactInfo;
import com.example.lj.asrttstest.info.Global;
import com.example.lj.asrttstest.upload.ContactsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

public class MainActivity extends Activity {

    private static String server_url = "https://www.baidu.com";

    private Spinner nameSpinner;
    private ArrayAdapter nameSpinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Global.allUserNameList.add("CHINA");
        Global.allUserNameList.add("JAPAN");
        Global.allUserNameList.add("USA");
        Global.allUserNameList.add("Germany");
        Global.allUserNameList.add("France");

        nameSpinner = (Spinner) findViewById(R.id.userNameSpinner);
        nameSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Global.allUserNameList);
        nameSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nameSpinner.setAdapter(nameSpinnerAdapter);
        nameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Global.currentUserName = Global.allUserNameList.get(position);
                Toast.makeText(getApplicationContext(), "Chosen: "+Global.currentUserName, Toast.LENGTH_LONG);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        getAllUserNamesFromServer();

        final Button confirmButton = (Button)findViewById(R.id.userNameConfirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent localIntent = new Intent(MainActivity.this, InputIdActivity.class);
                MainActivity.this.startActivity(localIntent);
            }
        });

//        final Button cloudRecognizerButton = (Button) findViewById(R.id.cloudRecognizerButton);
//        cloudRecognizerButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent localIntent = new Intent(MainActivity.this, CloudASRActivity.class);
//                MainActivity.this.startActivity(localIntent);
//            }
//        });
    }

    private void getAllUserNamesFromServer(){
        new Thread(new getUserNameThread()).start();
    }

    private class getUserNameThread implements Runnable{
        @Override
        public void run() {
            HttpURLConnection conn = null;
            try {
                URL mURL = new URL(server_url);
                conn = (HttpURLConnection) mURL.openConnection();

                conn.setRequestMethod("GET");
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(10000);

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {

                    InputStream is = conn.getInputStream();
                    String response = getStringFromInputStream(is);
                    Log.d("sss", response);
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
}
