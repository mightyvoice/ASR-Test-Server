package com.example.lj.asrttstest.info;

import java.io.File;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lj on 16/6/23.
 */
public class Global {
    public static ArrayList<String> ambiguityList = new ArrayList<String>();
    public static Integer ambiguityListChosenID = -1;

    public static String currentUserID = "-1";
    public static String currentUserName = "";
    public static Integer currentSentenceID = -1;
    public static String currentSentenceForASR = "";
    public static Integer beginingSentenceID = -1;
    public static ArrayList<String> allUserNameList = new ArrayList<String>();
    public static HashMap<String, String> allUserIdToNameTable = new HashMap<String, String>();
    public static HashMap<String, String> allUserNameToIdTable = new HashMap<String, String>();

    public static File currentAudioFile;

    public static String googleAsrResult;
    public static String nuanceAsrResult;


    public static final int SERVER_PHONE_PORT = 13458;
    public static ServerSocket serverSocket;
    public static String HOST = "192.168.1.87";
    public static String Get_All_User_Names_URL = "http://"+HOST+":5000/users";
    public static String Get_Cur_Sentence_URL = "http://"+HOST+":5000/groundTruth?id=";
}
