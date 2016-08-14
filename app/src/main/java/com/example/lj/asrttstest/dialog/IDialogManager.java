package com.example.lj.asrttstest.dialog;

import org.json.JSONObject;

/**
 * The Interface IDialogManager.
 *
 * Copyright (c) 2015 Nuance Communications. All rights reserved.
 */
interface IDialogManager {

    /**
     * Process server response.
     *
     * @param response the response
     * @return the dialog result
     */
    IDialogResult processServerResponse(JSONObject response);

    /**
     * Parses the status.
     *
     * @return the string
     */
    String parseStatus();

    /**
     * Gets the status.
     *
     * @return the status
     */
    String getStatus();

    /**
     * Parses the final response.
     *
     * @return the int
     */
    int parseFinalResponse();

    /**
     * Checks if is final response.
     *
     * @return true, if is final response
     */
    boolean isFinalResponse();

    /**
     * Parses the domain.
     *
     * @return the string
     */
    String parseDomain();

    /**
     * Gets the domain.
     *
     * @return the domain
     */
    String getDomain();

    /**
     * Parses the dialog phase.
     *
     * @return the string
     */
    String parseDialogPhase();

    /**
     * Gets the dialog phase.
     *
     * @return the dialog phase
     */
    String getDialogPhase();

    /**
     * Parses the system text.
     *
     * @return the string
     */
    String parseSystemText();

    /**
     * Gets the system text.
     *
     * @return the system text
     */
    String getSystemText();

    /**
     * Parses the intent.
     *
     * @return the string
     */
    String parseIntent();

    /**
     * Gets the intent.
     *
     * @return the intent
     */
    String getIntent();

    /**
     * Parses the tts text.
     *
     * @return the string
     */
    String parseTtsText();

    /**
     * Gets the tts text.
     *
     * @return the tts text
     */
    String getTtsText();

    /**
     * Parses the reset dialog.
     *
     * @return true, if successful
     */
    boolean parseResetDialog();

    /**
     * Reset dialog.
     *
     * @return true, if successful
     */
    boolean resetDialog();

    /**
     * Continue dialog.
     *
     * @return true, if successful
     */
    boolean continueDialog();

    /**
     * Parses the get data.
     *
     * @return the JSON object
     */
    JSONObject parseGetData();

    /**
     * Gets the gets the data.
     *
     * @return the gets the data
     */
    JSONObject getGetData();

    /**
     * Parses the nlps version.
     *
     * @return the string
     */
    String parseNlpsVersion();

    /**
     * Gets the nlps version.
     *
     * @return the nlps version
     */
    String getNlpsVersion();

    /**
     * Parses the server specified settings.
     *
     * @return the JSON object
     */
    JSONObject parseServerSpecifiedSettings();

    /**
     * Gets the server specified settings.
     *
     * @return the server specified settings
     */
    JSONObject getServerSpecifiedSettings();

}
