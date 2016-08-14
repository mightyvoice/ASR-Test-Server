package com.example.lj.asrttstest;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;

import com.example.lj.asrttstest.info.AllContactInfo;
import com.example.lj.asrttstest.info.AppInfo;
import com.example.lj.asrttstest.info.ContactInfo;
import com.example.lj.asrttstest.upload.ContactsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final Button cloudRecognizerButton = (Button) findViewById(R.id.cloudRecognizerButton);
        cloudRecognizerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent localIntent = new Intent(MainActivity.this, CloudASRActivity.class);
                MainActivity.this.startActivity(localIntent);
            }
        });
    }

}
