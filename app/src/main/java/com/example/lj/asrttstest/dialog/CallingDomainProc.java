package com.example.lj.asrttstest.dialog;

import android.content.Context;
import android.util.Log;

import com.example.lj.asrttstest.info.AllContactInfo;
import com.example.lj.asrttstest.info.Global;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lj on 16/6/16.
 */
public class CallingDomainProc extends DomainProc {

    private static final String TAG = "CallingDomainProc";

    public String phoneNumber;


    public CallingDomainProc(Context _context, JSONArray _actionArray, String _ttsText) {
        super(_context, _actionArray, _ttsText);
    }

    @Override
    public void process() {
        getPhoneNumber();
        getAmbiguityList();
    }

    private void getPhoneNumber(){
        phoneNumber = "";
        JSONArray curArray = actionArray;
        for(int i = 0; i < curArray.length(); i++){
            JSONObject curObject = curArray.optJSONObject(i);
            curObject = curObject.optJSONObject("value");
            if(curObject.has("contact")){
                curObject = curObject.optJSONObject("contact");
                curObject = curObject.optJSONObject("value");
                JSONObject tmp = curObject.optJSONObject("phoneNumber");
                String tmpResult = "";
                if(tmp != null){
                    tmpResult = tmp.optString("value");
                }
                if(!tmpResult.equals("")){
                    phoneNumber = tmpResult;
                    return;
                }
                curObject = curObject.optJSONObject("phoneNumberId");
                String result = "";
                if(curObject != null){
                    result = curObject.optString("value");
                }
                if(result.equals("")) return;
                if(AllContactInfo.allPhoneIDtoPhoneNum.containsKey(result)){
                    phoneNumber = AllContactInfo.allPhoneIDtoPhoneNum.get(result);
                    return;
                }
                else return;
            }
        }
    }

    @Override
    public void getAmbiguityList(){
        Global.ambiguityList.clear();
        JSONArray curArray = actionArray;
        for(int i = 0; i < curArray.length(); i++){
            JSONObject curObject = curArray.optJSONObject(i);
            curObject = curObject.optJSONObject("value");
            if(curObject.has("entries")){
                JSONArray entries = curObject.optJSONObject("entries").optJSONArray("value");
                for(int j = 0; j < entries.length(); j++){
                    JSONObject entry = entries.optJSONObject(j);
                    entry = entry.optJSONObject("value");
                    entry = entry.optJSONObject("item");
                    entry = entry.optJSONObject("value");
                    //if there are several names
                    if(entry.has("firstName")){
                        String name = entry.optJSONObject("firstName").optString("value");
                        if(entry.has("lastName")){
                            name = name + " " + entry.optJSONObject("lastName").optString("value");
                        }
                        Global.ambiguityList.add(name);
                        continue;
                    }
                    //if there are several phone types
                    if(entry.has("type")){
                        String phoneType = entry.optJSONObject("type").optString("value");
                        Global.ambiguityList.add(phoneType);
                        continue;
                    }
                }
                break;
            }
        }
    }
}
