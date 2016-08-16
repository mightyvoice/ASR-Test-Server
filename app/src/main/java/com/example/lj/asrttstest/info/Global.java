package com.example.lj.asrttstest.info;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lj on 16/6/23.
 */
public class Global {
    public static ArrayList<String> ambiguityList = new ArrayList<String>();
    public static Integer ambiguityListChosenID = -1;
    public static Integer currentUserID = -1;
    public static String currentUserName = "";
    public static Integer currentSentenceID = -1;
    public static Integer beginingSentenceID = -1;
    public static ArrayList<String> allUserNameList = new ArrayList<String>();
    public static HashMap<Integer, String> allUserIdToNameTable = new HashMap<Integer, String>();
    public static HashMap<String, Integer> allUserNameToIdTable = new HashMap<String, Integer>();
}
