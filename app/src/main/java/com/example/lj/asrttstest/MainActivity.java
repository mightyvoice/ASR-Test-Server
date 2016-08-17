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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

public class MainActivity extends Activity {

    private Spinner nameSpinner;
    private ArrayAdapter nameSpinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameSpinner = (Spinner) findViewById(R.id.userNameSpinner);
        nameSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Global.allUserNameList);
        nameSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nameSpinner.setAdapter(nameSpinnerAdapter);
        nameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Global.currentUserName = Global.allUserNameList.get(position);
                Global.currentUserID = Global.allUserNameToIdTable.get(Global.currentUserName);
                Toast.makeText(getApplicationContext(), "Chosen: "+Global.currentUserName, Toast.LENGTH_LONG);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        new Thread(new InitServerSocketThread()).start();

        new Thread(new getUserNameThread()).start();

        final Button confirmButton = (Button)findViewById(R.id.userNameConfirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent localIntent = new Intent(MainActivity.this, InputIdActivity.class);
                MainActivity.this.startActivity(localIntent);
            }
        });

    }


    private void parseAllUserNames(String input){
        Global.allUserNameList.clear();
        Global.allUserNameToIdTable.clear();
        Global.allUserNameToIdTable.clear();
        try {
            JSONObject cur = new JSONObject(input);
//            Log.d("sss", cur.toString(4));
            JSONArray items = cur.optJSONArray("result");
            for(int i = 0; i < items.length(); i++){
                JSONObject tmp = (JSONObject) items.get(i);
                String userName = tmp.optString("name");
                String userID = tmp.optString("_id");
                Global.allUserNameList.add(userName);
                Global.allUserNameToIdTable.put(userName, userID);
                Global.allUserIdToNameTable.put(userID, userName);
            }
            nameSpinnerAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
//            Log.d("sss", input);
            e.printStackTrace();
        }
    }

    private class getUserNameThread implements Runnable{
        @Override
        public void run() {
            HttpURLConnection conn = null;
            try {
                URL mURL = new URL(Global.Get_All_User_Names_URL);
                conn = (HttpURLConnection) mURL.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(10000);
                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    InputStream is = conn.getInputStream();
                    String response = getStringFromInputStream(is);
                    parseAllUserNames(response);
//                    Log.d("sss", response);
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

    private class InitServerSocketThread implements Runnable{
        @Override
        public void run() {
            try {
                Global.serverSocket = new ServerSocket(Global.SERVER_PHONE_PORT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
