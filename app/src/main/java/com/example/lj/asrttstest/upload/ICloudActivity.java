package com.example.lj.asrttstest.upload;

/**
 * Created by lj on 16/6/9.
 * From nuance sample code
 */
interface ICloudActivity {

    /**
     * Sets the app session id. Provide a new application session id for each new top-level dialog.
     *
     * @param appSessionId the new app session id
     */
    void setAppSessionId(String appSessionId);

    /**
     * Gets the app session id.
     *
     * @return the app session id
     */
    String getAppSessionId();

}
