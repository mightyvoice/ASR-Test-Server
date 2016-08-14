package com.example.lj.asrttstest.info;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by lj on 16/6/13.
 */
public class AllContactInfo {
    public static ArrayList<ContactInfo> allContactList = new ArrayList<ContactInfo>();
    public static JSONArray allContactJsonArray = new JSONArray();
    public static JSONObject allContactJsonObject = new JSONObject();
    public static Hashtable<String, String> allPhoneIDtoPhoneNum = new Hashtable<String, String>();
    public static int contactNum = 0;
}
