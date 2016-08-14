package com.example.lj.asrttstest.upload;

/**
 * Created by lj on 16/6/9.
 */

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.example.lj.asrttstest.info.AppInfo;
import com.nuance.dragon.toolkit.audio.AudioType;
import com.nuance.dragon.toolkit.cloudservices.CloudConfig;
import com.nuance.dragon.toolkit.cloudservices.CloudServices;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
//import com.nuance.dragon.toolkit.oem.api.NMTContext;
//import com.nuance.dragon.toolkit.util.Factory2;

/**
 * BaseCloudActivity handles core connectivity and transactions with Nuance Cloud Services.
 *
 * Copyright (c) 2015 Nuance Communications. All rights reserved.
 */
public class BaseCloudActivity implements ICloudActivity {

    /** The tag used when logging to logcat. */
    private static final String TAG = "NMT-BaseCloudActivity";

    /** The application mContext. */
    protected final Context mContext;

    /** The default language to use. Set to eng-USA. Override available languages in the configuration file. */
    private static final String DEFAULT_LANGUAGE = "eng-USA";

    /** The default dictation type to use. Set to nma_dm_main. Override available dictation types in the configuration file. */
    static final String DEFAULT_DICTATION_TYPE = "nma_dm_main";

    /** The default NCS Reference Profile Name to use. Override available profiles in the configuration file. */
    //protected static final String DEFAULT_NLU_PROFILE = "REFERENCE_NCS";

    /** The default timezone to use for time-dependent domains (example: Clock) */
    //protected static final String DEFAULT_TIME_ZONE = "0";

    /** The default geo-coordinates of the device. Disabled by default. */
    //protected static final String DEFAULT_LOCATION = null;

    /** An instance of CloudServices. */
    protected CloudServices mCloudServices;

    /** The application session id. */
    String mAppSessionId = null;

    /** An instance of the selected language to use. */
    private String mLanguage = null;

    /** The audio type. */
    protected AudioType mAudioType = AudioType.OPUS_WB;  //.SPEEX_WB;  //.PCM_16k;

    /** The audio type playback. */
    protected AudioType mAudioTypePlayback = AudioType.SPEEX_WB;  //.PCM_16k;

    /** The list of application grammars. */
    //private List<Grammar> mGrammars = new ArrayList<Grammar>();

    /**
     * Instantiates a new base cloud activity.
     *
     * @param c A handle to the application mContext
     */
    public BaseCloudActivity(Context c) {
        mContext = c;
    }

    /* (non-Javadoc)
	 * @see com.nuance.dragon.toolkit.sample.ICloudActivity#setAppSessionId(java.lang.String)
	 */
    @Override
    public void setAppSessionId( String appSessionId ) {
        mAppSessionId = appSessionId;
    }

    /* (non-Javadoc)
	 * @see com.nuance.dragon.toolkit.sample.ICloudActivity#getAppSessionId()
	 */
    @Override
    public String getAppSessionId() {
        return mAppSessionId;
    }


    /**
     * Release cloud recognition resources.
     */
    void releaseCloudServices() {

        if (mCloudServices != null) {
            mCloudServices.release();
            mCloudServices = null;
        }
    }

    /**
     * Initialize the cloud services resources.
     *
     * @return true, if successful
     */
    protected boolean initCloudServices() {

        releaseCloudServices();

        CloudConfig config;
            config = new CloudConfig(
                    AppInfo.Host,
                    AppInfo.Port,
                    AppInfo.AppId,
                    AppInfo.AppKey,
                    mAudioType,
                    mAudioTypePlayback);

        mCloudServices = CloudServices.createCloudServices(mContext, config);
        Log.d(TAG, "Device Id: " + config.deviceId);
        return true;
    }

    /**
     * Converts a Geo-Location object to formatted string.
     *
     * @param location the geo-location object
     * @return the formatted string
     */
    private String locationToString(Location location) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("<");
            sb.append(location.getLatitude());
            sb.append(", ");
            sb.append(location.getLongitude());
            sb.append(">");

            if (location.hasAccuracy()) {
                sb.append(" +/- ");
                sb.append(location.getAccuracy());
                sb.append("m");
            }

            return sb.toString();
        }
        catch (Exception e) {
            Log.d(TAG, "Failed to create location string: " + e.getLocalizedMessage());
            return "";
        }
    }
    /**
     * Gets the device's time zone.
     *
     * @return the time zone
     */
    protected String getTimeZone() {
        TimeZone tz = TimeZone.getDefault();

        Log.d(TAG, "Device timezone is: " + tz.getID());
        return tz.getID();	//tz.getDisplayName();
    }

    /**
     * Gets the current time.
     *
     * @return the current time
     */
    protected String getCurrentTime() {
        String format = "yyyy-MM-dd'T'HH:mm:ssZ";

        TimeZone tz = TimeZone.getDefault();	//.getTimeZone("UTC");
        Log.d(TAG, "Device timezone is: " + tz.getID());	//tz.getDisplayName());

        DateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        dateFormat.setTimeZone(tz);

        Date date = new Date();
        Log.d(TAG, "Device timestamp is: " + dateFormat.format(date));

        return dateFormat.format(date);
    }

    /**
     * Gets the dictation language.
     *
     * @return the dictation language
     */
    protected String getLanguage() {
        if( mLanguage != null )
            return mLanguage;

        return DEFAULT_LANGUAGE;
    }

    /**
     * Sets the dictation language.
     *
     * @param language the new dictation language
     */
    void setLanguage(String language) {
        mLanguage = language;
    }

}
