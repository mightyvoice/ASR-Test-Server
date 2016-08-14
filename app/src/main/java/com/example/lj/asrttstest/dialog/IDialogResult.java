package com.example.lj.asrttstest.dialog;

/**
 * The Interface IDialogResult.
 *
 * Copyright (c) 2015 Nuance Communications. All rights reserved.
 */
public interface IDialogResult {

    /**
     * Gets the tts playback text.
     *
     * @return the tts playback text
     */
    String getTtsPlaybackText();

    /**
     * Gets the ui display text.
     *
     * @return the ui display text
     */
    String getUiDisplayText();

    /**
     * Checks if is final response.
     *
     * @return true, if is final response
     */
    boolean isFinalResponse();

    /**
     * Continue dialog.
     *
     * @return true, if successful
     */
    boolean continueDialog();

    /**
     * Gets the dialog phase.
     *
     * @return the dialog phase
     */
    String getDialogPhase();

}