package com.example.lj.asrttstest.dialog;

/**
 * Created by lj on 16/6/15.
 */
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by lj on 16/5/23.
 */
public class JsonParser extends BaseDialogManager{

    private boolean reset;

    public JsonParser(JSONObject input){
        processServerResponse(input);
        getResetValue();
        Log.d("haha", "##############################\n"+
                "Dialog Phase: " + getDialogPhase()+"\n"+
                "Domain: " + getDomain()+"\n"+
                "Intent: " + getIntent()+"\n"+
                "NlpsVersion: " + getNlpsVersion()+"\n"+
                "Status: " + getStatus()+"\n"+
                "System Text: " + getSystemText()+"\n"+
                "TTS Text: " + getTtsText() + "\n"+
                "If reset: " + getReset() + "\n" +
                "##############################"
        );
    }

    public void setReset(boolean _reset){
        reset = _reset;
    }

    public boolean getReset(){
        return reset;
    }

    private void getResetValue(){
        reset = false;
        JSONArray curArray = getActions();
        for(int i = 0; i < curArray.length(); i++) {
            JSONObject curObject = curArray.optJSONObject(i);
            curObject = curObject.optJSONObject("value");
            if (curObject.has("type")) {
                curObject = curObject.optJSONObject("type");
                String type = curObject.optString("value");
                if (type.equals("reset")) {
                    reset = true;
                    return;
                }
            }
        }
    }
}
