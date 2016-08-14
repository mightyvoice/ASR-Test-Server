package com.example.lj.asrttstest.dialog;

/**
 * The Class DialogResult.
 *
 * A data container for important dialog result state information
 *
 * Copyright (c) 2015 Nuance Communications. All rights reserved.
 */
public class DialogResult implements IDialogResult {

    /** The tts playback text. */
    private String mTtsPlaybackText = null;

    /** The ui display text. */
    private String mUiDisplayText = null;

    /** The final response flag. */
    private boolean mFinalResponse = false;

    /** The continue dialog flag. */
    private boolean mContinueDialog= false;

    /** The dialog phase. */
    private String mDialogPhase = null;

    /**
     * Instantiates a new dialog result.
     *
     * @param ttsText the tts text
     * @param uiText the ui text
     * @param finalResponse the final response
     * @param continueDialog the continue dialog flag
     * @param dialogPhase the dialog phase
     */
    public DialogResult(String ttsText, String uiText, boolean finalResponse, boolean continueDialog, String dialogPhase) {
        mTtsPlaybackText = ttsText;
        mUiDisplayText = uiText;
        mFinalResponse = finalResponse;
        mContinueDialog = continueDialog;
        mDialogPhase = dialogPhase;
    }

    /* (non-Javadoc)
     * @see com.nuance.dragon.toolkit.sample.IDialogResult#getTtsPlaybackText()
     */
    @Override
    public String getTtsPlaybackText() { return mTtsPlaybackText; }

    /* (non-Javadoc)
     * @see com.nuance.dragon.toolkit.sample.IDialogResult#getUiDisplayText()
     */
    @Override
    public String getUiDisplayText() { return mUiDisplayText; }

    /* (non-Javadoc)
     * @see com.nuance.dragon.toolkit.sample.IDialogResult#isFinalResponse()
     */
    @Override
    public boolean isFinalResponse() { return mFinalResponse; }

    /* (non-Javadoc)
     * @see com.nuance.dragon.toolkit.sample.IDialogResult#continueDialog()
     */
    @Override
    public boolean continueDialog() { return mContinueDialog; }

    /* (non-Javadoc)
     * @see com.nuance.dragon.toolkit.sample.IDialogResult#getDialogPhase()
     */
    @Override
    public String getDialogPhase() { return mDialogPhase; }
}
