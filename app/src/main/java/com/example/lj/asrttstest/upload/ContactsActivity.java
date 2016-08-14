package com.example.lj.asrttstest.upload;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import com.example.lj.asrttstest.R;
import com.example.lj.asrttstest.info.AllContactInfo;
import com.example.lj.asrttstest.info.ContactInfo;
import com.nuance.dragon.toolkit.data.Data;

import 	android.content.ContentResolver;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

public class ContactsActivity extends AppCompatActivity {
    /**
     * Called when the activity is first created.
     */
    private final static String TAG = "ContactsActivity";
    private DataUploaderCloudActivity dataUploader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_asr);

        getAllContactList();
        try {
            getAllContactJsonArrayAndObject();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        final TextView resultEditText = (TextView)findViewById(R.id.cloudResultEditText);
        try {
            resultEditText.setText(AllContactInfo.allContactJsonObject.toString(4));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        dataUploader = new DataUploaderCloudActivity(this);
        dataUploader.TclUploadData(AllContactInfo.allContactJsonObject, null, null);
        Log.d("haha", dataUploader.resultStatus);
        if(dataUploader.resultStatus.equalsIgnoreCase("success")) {
            Toast.makeText(this, "Upload Success!", Toast.LENGTH_SHORT).show();
            this.finish();
        }
    }

    private void getAllContactList(){
        AllContactInfo.allContactList = new ArrayList<ContactInfo>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                ContactInfo contact = new ContactInfo();

                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));
                String[] nameList = null;
                if(name != null && !name.equals("")){
                    nameList = name.split(" ");
                    contact.setFirstName(nameList[0]);
                    if(nameList.length > 1) contact.setLastName(nameList[1]);
                }
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNum = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        String phoneType = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.TYPE));
                        phoneType = contact.phoneTypeTable.get(phoneType);
                        contact.phoneNumberTable.put(phoneType, phoneNum);
                    }
                    pCur.close();
                }
                AllContactInfo.allContactList.add(contact);
            }
        }
    }

    private void getAllContactJsonArrayAndObject() throws JSONException {
        int curID = -1;
        AllContactInfo.allContactJsonArray = new JSONArray();
        AllContactInfo.allPhoneIDtoPhoneNum = new Hashtable<String, String>();
        AllContactInfo.allContactJsonObject = new JSONObject();
        for (ContactInfo contact: AllContactInfo.allContactList){
            curID++;
            JSONObject tmp = new JSONObject();
            JSONObject all = new JSONObject();
            tmp.put("fn", contact.getFirstName());
            tmp.put("ln", contact.getLastName());
            JSONArray phoneTypeArray = new JSONArray();
            JSONArray phoneNumArray = new JSONArray();
            Set<String> types = contact.phoneNumberTable.keySet();
            int phoneID = -1; //starts from 0
            for(String phoneType: types){
                phoneTypeArray.put(phoneType);
                phoneID++;
                String phId = new Integer(curID).toString()+"_"+new Integer(phoneID).toString();
                phoneNumArray.put(phId);
                AllContactInfo.allPhoneIDtoPhoneNum.put(phId, contact.phoneNumberTable.get(phoneType));
            }
            tmp.put("phId", phoneNumArray);
            tmp.put("ph", phoneTypeArray);
            all.put("content", tmp);
            all.put("content_id", curID);
            AllContactInfo.allContactJsonArray.put(all);
        }
        AllContactInfo.allContactJsonObject.put("list", AllContactInfo.allContactJsonArray);
    }


}
