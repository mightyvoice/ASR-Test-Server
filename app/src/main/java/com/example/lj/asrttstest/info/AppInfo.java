package com.example.lj.asrttstest.info;

/**
 * Created by lj on 16/6/14.
 */
public class AppInfo {
    ////////// Settings for ASR
    //not working for NLU
//    public static final String Host = "nmsp.labs.nuance.com";
//    public static final int Port = 443;
//    public static final String AppId = "NMDPTRIAL_yujacky_tcl_com20150630145537";
//    private static final String AppKeyStr = "b24c2e5ade2e23c321b6f5c84cec8b3d80150e402b496577785c526c80031a324f498d2d77b0f19866e660b2d2ecf05201fe932c48f8a3c054f52dd4f9470748";
//    public static final byte[] AppKey = toByteArray(AppKeyStr);

//    //////////Settings for TTS
//    //// it seems that it works for ASR and NLU
    public static final String Host = "mtldev02.nuance.com";
    public static final int Port = 443;
    public static final String AppId = "NMT_EVAL_TCL_20150814";
    public static final byte[] AppKey = { (byte) 0x89, (byte) 0xe9,
            (byte) 0xb1, (byte) 0xb6, (byte) 0x19, (byte) 0xdf, (byte) 0xc7,
            (byte) 0xd6, (byte) 0x82, (byte) 0x23, (byte) 0x7e, (byte) 0x70,
            (byte) 0x1d, (byte) 0xa7, (byte) 0xad, (byte) 0xa4, (byte) 0x83,
            (byte) 0x16, (byte) 0xf6, (byte) 0x75, (byte) 0xf7, (byte) 0x3c,
            (byte) 0x5e, (byte) 0xcd, (byte) 0x23, (byte) 0xa4, (byte) 0x1f,
            (byte) 0xc4, (byte) 0x07, (byte) 0x82, (byte) 0xbc, (byte) 0x21,
            (byte) 0x2e, (byte) 0xd3, (byte) 0x56, (byte) 0x20, (byte) 0x22,
            (byte) 0xc2, (byte) 0x3e, (byte) 0x75, (byte) 0x21, (byte) 0x4d,
            (byte) 0xcb, (byte) 0x90, (byte) 0x10, (byte) 0x28, (byte) 0x6c,
            (byte) 0x23, (byte) 0xaf, (byte) 0xe1, (byte) 0x00, (byte) 0xe0,
            (byte) 0x0d, (byte) 0x44, (byte) 0x64, (byte) 0x87, (byte) 0x3e,
            (byte) 0x00, (byte) 0x4d, (byte) 0x1f, (byte) 0x4c, (byte) 0x8a,
            (byte) 0x58, (byte) 0x83 };

    ////The new setting 06-03-2016
//    public static final String Host = "mtldev11.nuance.com";
//    public static final int Port = 443;
//    public static final String AppId = "TCL_TESTING_20160307";
//    public static final byte[] AppKey = { (byte)0x60, (byte) 0x1c, (byte) 0x87,
//            (byte) 0x6b, (byte) 0x56, (byte) 0x30, (byte) 0x65, (byte) 0xfb,
//            (byte) 0x5e, (byte) 0x1b, (byte) 0x39, (byte) 0xf7, (byte) 0xb1,
//            (byte) 0x5d, (byte) 0xda, (byte) 0x6a, (byte) 0x80, (byte) 0x52,
//            (byte) 0xc3, (byte) 0xc6, (byte) 0x3b, (byte) 0x60, (byte) 0x2b,
//            (byte) 0xec, (byte) 0x22, (byte) 0xa7, (byte) 0xae, (byte) 0xc1,
//            (byte) 0xea, (byte) 0xd9, (byte) 0x3e, (byte) 0xa3, (byte) 0x43,
//            (byte) 0x4d, (byte) 0x59, (byte) 0x6d, (byte) 0x76, (byte) 0x12,
//            (byte) 0x34, (byte) 0xe9, (byte) 0xa9, (byte) 0x77, (byte) 0x17,
//            (byte) 0x63, (byte) 0xb1, (byte) 0x44, (byte) 0x12, (byte) 0xea,
//            (byte) 0x65, (byte) 0x51, (byte) 0xed, (byte) 0x2c, (byte) 0x17,
//            (byte) 0xa4, (byte) 0xf7, (byte) 0x18, (byte) 0x39, (byte) 0xaf,
//            (byte) 0x9a, (byte) 0xac, (byte) 0x95, (byte) 0x9e, (byte) 0x15,
//            (byte) 0x66};

    public static String IMEInumber;
    public static String applicationSessionID;

    public static String nooo;

    private static byte[] toByteArray(String appKey)
    {
        String trimmedAppKey = appKey.trim();
        byte[] keyInBytes = new byte[trimmedAppKey.length()/2];

        for (int i = 0; i < trimmedAppKey.length()/2; i++)
        {
            String key = trimmedAppKey.substring(i*2, i*2 + 2);
            keyInBytes[i] = (byte)Integer.parseInt(key, 16);
        }

        return keyInBytes;
    }
}
