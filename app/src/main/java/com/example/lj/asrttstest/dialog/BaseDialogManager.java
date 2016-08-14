package com.example.lj.asrttstest.dialog;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * BaseDialogManager provides access to the core NCS Ref NLU JSON response data and it's dialog elements
 *
 * Copyright (c) 2015 Nuance Communications. All rights reserved.
 */
public class BaseDialogManager implements IDialogManager {

    /** Dialog Phase is returned in the NCS Ref NLU JSON Response. topLevel let's the client know that the dialog is complete and it should reset dialog management after processing the response. */
    protected static final String DIALOG_PHASE_TOP_LEVEL				= "topLevel";

    /** Dialog Phase is returned in the NCS Ref NLU JSON Response. informationRequest informs the client that the dialog is not complete and the user needs to be prompted for more information. */
    protected static final String DIALOG_PHASE_INFORMATION_REQUEST		= "informationRequest";

    /** Dialog Phase is returned in the NCS Ref NLU JSON Response. disambiguation informs the client that the response contains multiple items, such as contact data, requiring the user to select the desired option. */
    protected static final String DIALOG_PHASE_DISAMBIGUATION			= "disambiguation";

    /** Dialog Phase is returned in the NCS Ref NLU JSON Response. confirmation informs the client that the user needs to be prompted for yes/no confirmation. */
    protected static final String DIALOG_PHASE_CONFIRMATION				= "confirmation";

    /** Action type is returned in the NCS Ref NLU JSON Response. dmState contains details about the dialog phase and request status. */
    protected static final String ACTION_TYPE_DMSTATE					= "dmState";

    /** Action type is returned in the NCS Ref NLU JSON Response. conversation contains UI prompt details. */
    protected static final String ACTION_TYPE_CONVERSATION				= "conversation";

    /** Action type is returned in the NCS Ref NLU JSON Response. tts contains TTS prompt details. */
    protected static final String ACTION_TYPE_TTS						= "tts";

    /** Action type is returned in the NCS Ref NLU JSON Response. domain contains NLU domain details. */
    protected static final String ACTION_TYPE_DOMAIN					= "domain";

    /** Action type is returned in the NCS Ref NLU JSON Response. application contains details about the target application for the selected domain */
    protected static final String ACTION_TYPE_APPLICATION				= "application";

    /** Action type is returned in the NCS Ref NLU JSON Response. reset informs the client to send a reset request to the server. */
    protected static final String ACTION_TYPE_RESET						= "reset";

    /** Action type is returned in the NCS Ref NLU JSON Response. get_data informs the client that a data exchange between client and server is required, and specifies the parameters involved. */
    protected static final String ACTION_TYPE_GETDATA					= "get_data";

    /** The NCS server response represented as a String. */
    protected String serverResponse = null;

    /** The NCS Server response represented as a JSON object. */
    protected JSONObject mJsonResponse = null;

    /** The status value returned in the NCS response. */
    protected String mStatus 				= null;

    /** The value of final_response returned in the NCS response. */
    protected int mFinalResponse 			= 0;

    /** The NLU domain returned in the NCS response. */
    protected String mDomain 				= null;

    /** The dialog phase returned in the NCS response. */
    protected String mDialogPhase 			= null;

    /** The system text returned in the NCS response to be displayed in the UI. */
    protected String mSystemText			= null;

    /** The tts text returned in the NCS response to be played back to the user. */
    protected String mTtsText				= null;

    /** A flag to track if the server has requested the dialog be reset. */
    protected boolean mResetDialog			= false;

    /** The NLU intent returned in the NCS response. */
    protected String mIntent				= null;

    /** An instance of the get_data JSON object returned in the NCS response. */
    protected JSONObject mGetData			= null;

    /** The version of the NLPS server that handled the request. */
    protected String mNlpsVersion			= null;

    /** An instance of the server_specified_settings returned in the NCS response. The client must use these in the follow-up transaction. */
    protected JSONObject mServerSpecifiedSettings	= null;

    protected JSONObject mAppServerResult = null;


    /* (non-Javadoc)
     * @see com.nuance.dragon.toolkit.sample.IDialogManager#processServerResponse(org.json.JSONObject)
     */
    @Override
    public IDialogResult processServerResponse(JSONObject response) {
        mJsonResponse = response;
        mAppServerResult = getAppServerResults();
        mStatus = parseStatus();
        mFinalResponse = parseFinalResponse();
        mDomain = parseDomain();
        mDialogPhase = parseDialogPhase();
        mSystemText = parseSystemText();
        mTtsText = parseTtsText();
        mResetDialog = parseResetDialog();
        mIntent = parseIntent();
        mGetData = parseGetData();
        mNlpsVersion = parseNlpsVersion();
        mServerSpecifiedSettings = parseServerSpecifiedSettings();
        return new DialogResult(mTtsText, mSystemText, isFinalResponse(), continueDialog(), mDialogPhase);
    }

    /**
     * Gets the app server results.
     *
     * @return the app server results
     */
    protected JSONObject getAppServerResults() {
        if (mJsonResponse != null) {
            mAppServerResult = mJsonResponse
                    .optJSONObject("value")
                    .optJSONObject("appserver_results")
                    .optJSONObject("value");
            return mAppServerResult;
        }
        return null;
    }

    /**
     * Gets the payload.
     *
     * @return the payload
     */
    protected JSONObject getPayload() {
        JSONObject payload = null;

        if (mAppServerResult != null)
            payload = mAppServerResult
                    .optJSONObject("payload")
                    .optJSONObject("value");

        return payload;
    }

    /**
     * Gets the actions.
     *
     * @return the actions
     */
    public JSONArray getActions() {
        JSONArray actions = null;
        JSONObject payload = getPayload();
        if (payload != null)
            actions = payload.optJSONObject("actions")
                    .optJSONArray("value");

        return actions;
    }

    /**
     * Find action by type.
     *
     * @param t the action type name
     * @return the action as a JSON object
     */
    protected JSONObject findActionByType(String t) {
        JSONArray actions = getActions();
        if (actions == null) return null;
        for(int i = 0; i < actions.length(); i++){
            JSONObject o = actions.optJSONObject(i).optJSONObject("value");
            String oType = o.optJSONObject("type").optString("value");

            if( oType.equalsIgnoreCase(t)) return o;
        }

        return null;
    }

    /**
     * Find action by type.
     *
     * @param searchFromBottom this flag specifies whether or not to search for
     *                         the action in the server response from bottom up or top down
     * @return the JSON object
     */
    protected JSONObject findActionByType(boolean searchFromBottom) {
        if( !searchFromBottom )
            return findActionByType(BaseDialogManager.ACTION_TYPE_DMSTATE);

        JSONArray actions = getActions();
        if (actions == null) return null;

        for(int i = actions.length(); i > 0; i--){
            JSONObject o = actions.optJSONObject(i-1).optJSONObject("value");
            String oType = o.optJSONObject("type").optString("value");

            if( oType.equalsIgnoreCase(BaseDialogManager.ACTION_TYPE_DMSTATE)) return o;
        }

        return null;
    }

    /* (non-Javadoc)
     * @see com.nuance.dragon.toolkit.sample.IDialogManager#parseStatus()
     */
    @Override
    public String parseStatus() {
        mStatus = null;
        if (mAppServerResult != null)
            mStatus = mAppServerResult.optJSONObject("status").optString("value");

        return mStatus;
    }

    /* (non-Javadoc)
     * @see com.nuance.dragon.toolkit.sample.IDialogManager#getStatus()
     */
    @Override
    public String getStatus() {
        return mStatus;
    }

    /* (non-Javadoc)
     * @see com.nuance.dragon.toolkit.sample.IDialogManager#parseFinalResponse()
     */
    @Override
    public int parseFinalResponse() {
        mFinalResponse = 0;
        if (mAppServerResult != null)
            mFinalResponse = mAppServerResult.optJSONObject("final_response").optInt("value");

        return mFinalResponse;
    }

    /* (non-Javadoc)
     * @see com.nuance.dragon.toolkit.sample.IDialogManager#isFinalResponse()
     */
    @Override
    public boolean isFinalResponse() {
        return (mFinalResponse != 0);
    }

    /* (non-Javadoc)
     * @see com.nuance.dragon.toolkit.sample.IDialogManager
     */
    @Override
    public String parseDomain() {
        mDomain = null;
        JSONObject action = findActionByType(BaseDialogManager.ACTION_TYPE_DOMAIN);
        if (action != null)
            mDomain = action.optJSONObject("app").optString("value");

        return mDomain;
    }

    /* (non-Javadoc)
     * @see com.nuance.dragon.toolkit.sample.IDialogManager#getDomain()
     */
    @Override
    public String getDomain() {
        return mDomain;
    }

    /* (non-Javadoc)
     * @see com.nuance.dragon.toolkit.sample.IDialogManager#parseDialogPhase()
     */
    @Override
    public String parseDialogPhase() {
        mDialogPhase = "";
        JSONObject action = findActionByType(true);
        if (action != null)
            mDialogPhase = action.optJSONObject("dialogPhase").optString("value");

        return mDialogPhase;
    }

    /* (non-Javadoc)
     * @see com.nuance.dragon.toolkit.sample.IDialogManager#getDialogPhase()
     */
    @Override
    public String getDialogPhase() {
        return mDialogPhase;
    }

    /* (non-Javadoc)
     * @see com.nuance.dragon.toolkit.sample.IDialogManager#parseSystemText()
     */
    @Override
    public String parseSystemText() {
        mSystemText = null;
        JSONObject action = findActionByType(BaseDialogManager.ACTION_TYPE_CONVERSATION);
        if (action != null)
            mSystemText = action.optJSONObject("text").optString("value");

        return mSystemText;
    }

    /* (non-Javadoc)
     * @see com.nuance.dragon.toolkit.sample.IDialogManager#getSystemText()
     */
    @Override
    public String getSystemText() {
        return mSystemText;
    }

    /* (non-Javadoc)
     * @see com.nuance.dragon.toolkit.sample.IDialogManager#parseIntent()
     */
    @Override
    public String parseIntent() {
        mIntent = "";
        JSONObject action = findActionByType(BaseDialogManager.ACTION_TYPE_APPLICATION);
        if (action != null)
            mIntent = action.optJSONObject("action").optString("value");

        return mIntent;
    }

    /* (non-Javadoc)
     * @see com.nuance.dragon.toolkit.sample.IDialogManager#getIntent()
     */
    @Override
    public String getIntent() {
        return mIntent;
    }

    /* (non-Javadoc)
     * @see com.nuance.dragon.toolkit.sample.IDialogManager#parseTtsText()
     */
    @Override
    public String parseTtsText() {
        mTtsText= null;
        JSONObject action = findActionByType(BaseDialogManager.ACTION_TYPE_TTS);
        if (action != null)
            mTtsText = action.optJSONObject("text").optString("value");

        return mTtsText;
    }

    /* (non-Javadoc)
     * @see com.nuance.dragon.toolkit.sample.IDialogManager#getTtsText()
     */
    @Override
    public String getTtsText() {
        return mTtsText;
    }

    /* (non-Javadoc)
     * @see com.nuance.dragon.toolkit.sample.IDialogManager#parseResetDialog()
     */
    @Override
    public boolean parseResetDialog() {
        mResetDialog = false;
        JSONObject action = findActionByType(BaseDialogManager.ACTION_TYPE_RESET);
        if (action != null)
            mResetDialog = true;

        return mResetDialog;
    }

    /* (non-Javadoc)
     * @see com.nuance.dragon.toolkit.sample.IDialogManager#resetDialog()
     */
    @Override
    public boolean resetDialog() {
        return mResetDialog;
    }

    /* (non-Javadoc)
     * @see com.nuance.dragon.toolkit.sample.IDialogManager#continueDialog()
     */
    @Override
    public boolean continueDialog() {
        return mDialogPhase != null && !mDialogPhase.equalsIgnoreCase(DIALOG_PHASE_TOP_LEVEL);
    }

    /* (non-Javadoc)
     * @see com.nuance.dragon.toolkit.sample.IDialogManager#parseGetData()
     */
    @Override
    public JSONObject parseGetData() {
        mGetData = null;
        JSONObject action = findActionByType(BaseDialogManager.ACTION_TYPE_GETDATA);
        if (action != null)
            mGetData = action;	//action.optJSONObject("payload");

        return mGetData;
    }

    /* (non-Javadoc)
     * @see com.nuance.dragon.toolkit.sample.IDialogManager#getGetData()
     */
    @Override
    public JSONObject getGetData() {
        return mGetData;
    }

    /* (non-Javadoc)
     * @see com.nuance.dragon.toolkit.sample.IDialogManager#parseNlpsVersion()
     */
    @Override
    public String parseNlpsVersion() {
        mNlpsVersion = null;

        JSONObject payload = getPayload();
        if (payload != null)
            mNlpsVersion = payload.optJSONObject("nlps_version").optString("value");

        return mNlpsVersion;
    }

    /* (non-Javadoc)
     * @see com.nuance.dragon.toolkit.sample.IDialogManager#getNlpsVersion()
     */
    @Override
    public String getNlpsVersion() {
        return mNlpsVersion;
    }

    /* (non-Javadoc)
     * @see com.nuance.dragon.toolkit.sample.IDialogManager#parseServerSpecifiedSettings()
     */
    @Override
    public JSONObject parseServerSpecifiedSettings() {
        mServerSpecifiedSettings = null;

        JSONObject payload = getPayload();
        if (payload != null)
            mServerSpecifiedSettings = payload.optJSONObject("server_specified_settings");

        return mServerSpecifiedSettings;
    }

    /* (non-Javadoc)
     * @see com.nuance.dragon.toolkit.sample.IDialogManager#getServerSpecifiedSettings()
     */
    @Override
    public JSONObject getServerSpecifiedSettings() {
        return mServerSpecifiedSettings;
    }
}

