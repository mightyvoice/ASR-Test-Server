package com.example.lj.asrttstest.dialog;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lj on 16/6/16.
 */
public abstract class DomainProc {
    protected JSONArray actionArray = null;
    protected String ttsText = null;
    protected Context context = null;

    //if there is ambuguity, put all the returned value in this list
    public ArrayList<String> ambiguityList = null;

    public DomainProc(Context _context, JSONArray _actionArray, String _ttsText){
        context = _context;
        actionArray = _actionArray;
        ttsText = _ttsText;
    }

    public void resetActionArray(Context _context, JSONArray _actionArray, String _ttsText){
        context = _context;
        actionArray = _actionArray;
        ttsText = _ttsText;
    }

    public String getTtsText(){
        return ttsText;
    }

    //parse json to get all useful information
    public abstract void process();

    //parse json to get ambiguity list
    public abstract void getAmbiguityList();

}
