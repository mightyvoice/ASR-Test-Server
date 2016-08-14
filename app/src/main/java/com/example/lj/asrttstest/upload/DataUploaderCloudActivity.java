package com.example.lj.asrttstest.upload;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.lj.asrttstest.R;
import com.example.lj.asrttstest.info.AppInfo;
import com.nuance.dragon.toolkit.cloudservices.DictionaryParam;
import com.nuance.dragon.toolkit.cloudservices.Transaction;
import com.nuance.dragon.toolkit.cloudservices.TransactionError;
import com.nuance.dragon.toolkit.cloudservices.TransactionResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.nuance.dragon.toolkit.data.Data.Dictionary;
import com.nuance.dragon.toolkit.data.Data.Sequence;

/**
 * DataUploaderCloudActivity supports the data upload command for uploading per-user grammar content
 * such as contacts, song data, and device applications
 *
 * Copyright (c) 2015 Nuance Communications. All rights reserved.
 */
class DataUploaderCloudActivity extends BaseCloudActivity {
    /** The tag used when logging to logcat. */
    private static final String TAG = "DataUploadActivity";

    /** The NCS Command for data uploads. The command name is DRAGON_NLU_DATA_UPLOAD_CMD. */
    private static final String DEFAULT_DATAUPLOAD_COMMAND = "DRAGON_NLU_DATA_UPLOAD_CMD";

    /** The NCS Command for resetting user profile data. The command name is NVC_RESET_USER_PROFILE_CMD. */
    private static final String DEFAULT_RESET_USER_PROFILE_COMMAND = "NVC_RESET_USER_PROFILE_CMD";

    /** The dictation type. */
    private String mDictationType = null;

    /** The list of application grammars. */
    private List<Grammar> mGrammars = new ArrayList<Grammar>();

    public String resultStatus = "";

    /**
     * Gets the grammars.
     *
     * @return the list of configured Grammars
     */
    public List<Grammar> getGrammars() {
        return mGrammars;
    }

    /**
     * Sets the grammars.
     *
     * @param mGrammars the list of Grammars to set
     */
    public void setGrammars(List<Grammar> mGrammars) {
        if( mGrammars != null)
            this.mGrammars = mGrammars;
        else
            this.mGrammars.clear();
    }

    /**
     * Instantiates a new data uploader cloud activity.
     *
     * @param c the application mContext
     */
    public DataUploaderCloudActivity( Context c )
    {
        super(c);
        initCloudServices();
    }

    /**
     * Upload data.
     *
     * @param settings the transaction settings
     * @param name the grammar name
     * @param p the first set of command parameters
     * @param p2 the second set of command parameters (may be null)
     * @param listener the data upload transaction listener
     */
    private void uploadData(Dictionary settings, final String name, DictionaryParam p, DictionaryParam p2, Transaction.Listener listener)
    {

        Log.d(TAG, "Creating Transaction...");

        Transaction dut;

        Toast.makeText(mContext, "Uploading " + name, Toast.LENGTH_SHORT).show();

        if( listener != null )
            dut = new Transaction(DEFAULT_DATAUPLOAD_COMMAND, settings, listener, 3000, true);
        else
            dut = new Transaction(DEFAULT_DATAUPLOAD_COMMAND, settings, new Transaction.Listener() {

                @Override
                public void onTransactionStarted(Transaction arg0) {
                    Log.d(TAG, "Transaction Started...");
                    onDataUploadStarted(name, arg0);
                }

                @Override
                public void onTransactionProcessingStarted(Transaction transaction) {

                }

                @Override
                public void onTransactionResult(Transaction arg0, TransactionResult arg1,
                                                boolean arg2) {
                    Log.d(TAG, "Upload Completed...");
                    JSONObject results = arg1.getContents().toJSON();
                    try {
//                        Log.d("res", results.toString(4));
                        resultStatus = results
                                .optJSONObject("value")
                                .optJSONObject("result_list")
                                .optJSONArray("value")
                                .getJSONObject(0)
                                .getJSONObject("value")
                                .getJSONObject("status")
                                .getString("value");
                        Log.d("res", resultStatus);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onTransactionError(Transaction arg0, TransactionError arg1) {
                    Log.d(TAG, "Transaction Error...");
                    onDataUploadError(arg0, arg1.toJSON());
                }

                @Override
                public void onTransactionIdGenerated(String s) {

                }
            }, 3000, true);

        this.mCloudServices.addTransaction(dut, 1);
        dut.addParam(p);
        if(p2 != null ) dut.addParam(p2);
        dut.finish();
    }

    /**
     * Delete all data.
     *
     * @param listener the delete data transaction listener
     */
    private void deleteAllData(Transaction.Listener listener) {
        Dictionary settings = this.createCommandSettings("NVC_RESET_USER_PROFILE_CMD", getLanguage());

        Transaction dut;

        Toast.makeText(mContext, "Deleting all data...", Toast.LENGTH_SHORT).show();

        if( listener != null )
            dut = new Transaction(DEFAULT_RESET_USER_PROFILE_COMMAND, settings, listener, 3000, true);
        else
            dut = new Transaction(DEFAULT_RESET_USER_PROFILE_COMMAND, settings, new Transaction.Listener() {

                @Override
                public void onTransactionStarted(Transaction arg0) {
                    Log.d(TAG, "Transaction Started...");
                    onDataUploadStarted("Deleting all data...", arg0);
                }

                @Override
                public void onTransactionProcessingStarted(Transaction transaction) {

                }

                @Override
                public void onTransactionResult(Transaction arg0, TransactionResult arg1,
                                                boolean arg2) {
                    Log.d(TAG, "Transaction Completed...");
                    //onDataUploadResult(arg0, JSONObjectFactory.createFromDictionary(arg1.getContents()));
                    onDataUploadResult(arg0, arg1.getContents().toJSON());

                }

                @Override
                public void onTransactionError(Transaction arg0, TransactionError arg1) {
                    Log.d(TAG, "Transaction Error...");
                    onDataUploadError(arg0, arg1.toJSON());
                }

                @Override
                public void onTransactionIdGenerated(String s) {

                }
            }, 3000, true);

        this.mCloudServices.addTransaction(dut, 1);
        dut.finish();
    }

    /**
     * Upload data.
     * Ji Li's code for upload
     * Just call like this: TclUploadData(AllContactInfo.allContactInfoObject, null, null)
     * @param contactList the contact info in a json object: AllContactInfo.allContactInfoObject
     * @param uploadListener the upload data transaction listener
     * @param deleteListener the delete data transaction listener
     */
    public void TclUploadData(JSONObject contactList, Transaction.Listener uploadListener, Transaction.Listener deleteListener) {
        boolean deleteAll = false;

        deleteAllData(deleteListener);

        Dictionary settings = this.createCommandSettings(DEFAULT_DATAUPLOAD_COMMAND, getLanguage());

        String id = "contacts";
        String type = "structured_content";
        String category = "contacts";

        Dictionary d = this.createDataUploadRequestBegin("contacts", type, category, contactList, deleteAll);
        if( d == null ) {
            Log.e(TAG, "Could not create the data block to upload to server. Please check logs for more details.");
            return;
        }
        DictionaryParam DataBlock = new DictionaryParam("DATA_BLOCK", d);
        DictionaryParam UploadDone = new DictionaryParam("UPLOAD_DONE", this.createDataUploadRequestDone(id, type, category));

        uploadData(settings, id, DataBlock, UploadDone, uploadListener);

    }

    // pass in content name to pull from config file...
    /**
     * Creates the data upload request begin.
     *
     * @param id the grammar id
     * @param type the grammar type
     * @param category the grammar content category
     * @param list the list of entries to upload
     * @param deleteAll specify whether or not to delete all user profile data prior to uploading the new grammars.
     * @return the data upload request begin dictionary
     */
    private Dictionary createDataUploadRequestBegin(String id, String type, String category, JSONObject list, boolean deleteAll) {
        try {
            Dictionary dataToUpload = new Dictionary();
            if( deleteAll )
                dataToUpload.put("delete_all", 1);

            Sequence dataList = new Sequence();

            // build out the data list...
            // 1. Create Grammar Entry
            Dictionary grammarEntry = new Dictionary();
            grammarEntry.put("id", id);			// Set this from config file
            grammarEntry.put("type", type);		// Set this from config file
            if( type.equalsIgnoreCase("structured_content") )
                grammarEntry.put("structured_content_category", category);		// Set this from config file
            grammarEntry.put("checksum", "0");

            // 2. Start creating actions to add to grammar entry
            Sequence actions = new Sequence();

            // 2a. Create clear_all action
            Dictionary clearContent = new Dictionary();
            clearContent.put("action", "clear_all");
            actions.add(clearContent);

            // 2b. Create add action (with list of data items)
            Dictionary addContent = new Dictionary();

            // Build this sequence of data items from config file...
            Sequence dataItems = new Sequence();

            JSONArray _array = list.getJSONArray("list");

            if( type.equalsIgnoreCase("structured_content")) {
                if( category.equalsIgnoreCase("music")) {
                    dataItems = this.createMusicSequenceFromJson(_array);
                }
                else if( category.equalsIgnoreCase("contacts")) {
                    dataItems = this.createContactsSequenceFromJson(_array);	    		}
                else if( category.equalsIgnoreCase("generic_text")) {
                    dataItems = this.createGenericTextSequenceFromJson(_array);
                }
                else {
                    Log.e(TAG, "Unknown structured content category {"+ category +"}. Cannot create data block.");
                    return null;
                }
            }
            else if( type.equalsIgnoreCase("custom_words")) {
                Log.e(TAG, "Grammar type {"+ type +"} not supported yet. Cannot create data block.");
                return null;
            }
            else if( type.equalsIgnoreCase("contacts")) {
                Log.e(TAG, "Grammar type {"+ type +"} not supported yet. Cannot create data block.");
                return null;
            }
            else {
                Log.e(TAG, "Unknown grammar type {"+ type +"}. Cannot create data block.");
                return null;
            }

            //Sequence dataItems = Sequence.createFromJSON(list);
            //Sequence dataItems = new Sequence();
            //dataItems.createFromJSON(list);
            //dataItems.add("phrase 1");
            //dataItems.add("phrase 2");
            //dataItems.add("phrase 3");

            // ...End

            Dictionary dataEntries = new Dictionary();
            dataEntries.put("list", dataItems);

            addContent.put("action", "add");
            addContent.put("content", dataEntries);
            actions.add(addContent);

            // 3. Add actions to grammar entry
            grammarEntry.put("actions", actions);

            // 4. Add grammar entry to data list sequence
            dataList.add(grammarEntry);

            // end data list build out

            dataToUpload.put("data_list", dataList);
            return dataToUpload;
        } catch (Exception e) {
            //Log.e(TAG, e.getMessage());
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Creates the data upload request done.
     *
     * @param id the grammar id
     * @param type the grammar type
     * @param category the grammar category
     * @return the data upload request done dictionary
     */
    private Dictionary createDataUploadRequestDone(String id, String type, String category) {
        Dictionary uploadDoneParam = new Dictionary();
        uploadDoneParam.put("num_data_blocks", 1);

        Sequence checksumsSeq = new Sequence();

        Dictionary checksum = new Dictionary();
        checksum.put("id", id);
        checksum.put("type", type);
        if( type.equalsIgnoreCase("structured_content") )
            checksum.put("structured_content_category", category);		// Set this from config file
        checksum.put("current_checksum", "0");
        checksum.put("new_checksum", String.valueOf(System.currentTimeMillis()));

        checksumsSeq.add(checksum);

        uploadDoneParam.put("checksums", checksumsSeq);

        return uploadDoneParam;
    }

    /**
     * Creates the command settings.
     *
     * @param commandName the command name
     * @param language the dictation language
     * @return the command dictionary
     */
    private Dictionary createCommandSettings(String commandName, String language) {

        Dictionary settings = new Dictionary();

        settings.put("command", commandName);
        settings.put("nmaid", AppInfo.AppId);
        settings.put("dictation_language", language);
        settings.put("carrier", "ATT");
        settings.put("dictation_type", "Dictation");
        settings.put("application_name",  mContext.getString(R.string.app_name));
        settings.put("application_session_id", ((this.mAppSessionId == null) ? UUID.randomUUID().toString() : this.mAppSessionId) );
        settings.put("application_state_id", "45");
//        settings.put("location", getLastKnownLocation());
        settings.put("utterance_number", "5");
        settings.put("audio_source", "SpeakerAndMicrophone");

        //my code to set other parameters
        settings.put("uid", AppInfo.IMEInumber);
        return settings;
    }

    /**
     * Creates the generic text sequence from json.
     *
     * @param list the list of grammar phrases
     * @return the formatted grammar data
     */
    private Sequence createGenericTextSequenceFromJson(JSONArray list) {

        Sequence seq = new Sequence();

        for(int i = 0; i < list.length(); i++) {
            JSONObject j = list.optJSONObject(i);
            JSONObject jcontent = j.optJSONObject("content");
            JSONArray jt = jcontent.optJSONArray("t");

            Dictionary entry = new Dictionary();

            Dictionary content = new Dictionary();
            Sequence t = new Sequence();
            for(int a = 0; a < jt.length(); a++) {
                String phrase = jt.optString(a);
                if( phrase != null && phrase.length() > 0 )
                    t.add(phrase);
            }
            content.put("t", t);

            int id = j.optInt("content_id");

            entry.put("content", content);
            entry.put("content_id", id);

            seq.add(entry);
        }

        return seq;
    }

    /**
     * Creates the music sequence from json.
     *
     * @param list the list of music data
     * @return the formatted music grammar data
     */
    private Sequence createMusicSequenceFromJson(JSONArray list) {

        Sequence seq = new Sequence();

        for(int i = 0; i < list.length(); i++) {
            JSONObject j = list.optJSONObject(i);
            JSONObject jcontent = j.optJSONObject("content");

            String al = jcontent.optString("al");
            String ar = jcontent.optString("ar");
            String g = jcontent.optString("g");
            String r = jcontent.optString("r");
            String s = jcontent.optString("s");
            int id = j.optInt("content_id");

            Dictionary entry = new Dictionary();

            Dictionary content = new Dictionary();
            content.put("al", al);
            content.put("ar", ar);
            content.put("g", g);
            content.put("r", r);
            content.put("s", s);

            entry.put("content", content);
            entry.put("content_id", id);

            seq.add(entry);
        }

        return seq;
    }

    /**
     * Creates the contacts sequence from json.
     *
     * @param list the list of contact grammar data
     * @return the formatted contact grammar data
     */
    private Sequence createContactsSequenceFromJson(JSONArray list) {

        Log.d("+++++++++", list.toString());
        Sequence seq = new Sequence();

        for(int i = 0; i < list.length(); i++) {
            JSONObject j = list.optJSONObject(i);
            JSONObject jcontent = j.optJSONObject("content");

            // Extract all the possible sub-data elements for contact content...
            String fn = jcontent.optString("fn");
            String ln = jcontent.optString("ln");
            String nn = jcontent.optString("nn");

            JSONArray jim = jcontent.optJSONArray("im");
            JSONArray jimId = jcontent.optJSONArray("imId");
            JSONArray jimp = jcontent.optJSONArray("imp");
            String defImId = jcontent.optString("defImId");
            JSONArray jph = jcontent.optJSONArray("ph");
            JSONArray jphId = jcontent.optJSONArray("phId");
            JSONArray jem = jcontent.optJSONArray("em");
            JSONArray jemId = jcontent.optJSONArray("emId");
            JSONArray jr = jcontent.optJSONArray("r");


            int id = j.optInt("content_id");

            Dictionary entry = new Dictionary();

            Dictionary content = new Dictionary();

            if( fn != null) content.put("fn", fn);
            if( ln != null) content.put("ln", ln);
            if( nn != null) content.put("nn", nn);

            if( jim != null && jimId != null && jim.length() == jimId.length() ) {
                Sequence sim = new Sequence();
                Sequence simId = new Sequence();

                for(int a = 0; a < jim.length(); a++) {
                    sim.add(jim.optString(a));
                    simId.add(jimId.optString(a));
                }

                content.put("im", sim);
                content.put("imId", simId);
            }

            if( jimp != null ) {
                Sequence sim = new Sequence();

                for(int a = 0; a < jimp.length(); a++) {
                    sim.add(jimp.optString(a));
                }

                content.put("imp", sim);
            }

            if( defImId != null) content.put("defImId", defImId);

            if( jph != null && jphId != null && jph.length() == jphId.length() ) {
                Sequence sim = new Sequence();
                Sequence simId = new Sequence();

                for(int a = 0; a < jph.length(); a++) {
                    sim.add(jph.optString(a));
                    simId.add(jphId.optString(a));
                }

                content.put("ph", sim);
                content.put("phId", simId);
            }

            if( jem != null && jemId != null && jem.length() == jemId.length() ) {
                Sequence sim = new Sequence();
                Sequence simId = new Sequence();

                for(int a = 0; a < jem.length(); a++) {
                    sim.add(jem.optString(a));
                    simId.add(jemId.optString(a));
                }

                content.put("em", sim);
                content.put("emId", simId);
            }

            if( jr != null ) {
                Sequence sim = new Sequence();

                for(int a = 0; a < jr.length(); a++) {
                    sim.add(jr.optString(a));
                }

                content.put("r", sim);
            }

            entry.put("content", content);
            entry.put("content_id", id);

            seq.add(entry);
        }

        return seq;
    }

    /**
     * On data upload started.
     *
     * @param name the grammar name
     * @param t the transaction object
     */
    private void onDataUploadStarted(String name, Transaction t) {

    }

    /**
     * On data upload result.
     *
     * @param t the transaction object
     * @param result the server response
     */
    private void onDataUploadResult(Transaction t, JSONObject result) {

    }

    /**
     * On data upload error.
     *
     * @param t the transaction object
     * @param error the server error response
     */
    private void onDataUploadError(Transaction t, JSONObject error) {

    }

    /**
     * On get data started.
     *
     * @param t the transaction object
     */
    protected void onGetDataStarted(Transaction t) {

    }

    /**
     * On get data result.
     *
     * @param t the transaction object
     * @param result the server response
     */
    protected void onGetDataResult(Transaction t, JSONObject result) {

    }

    /**
     * On get data error.
     *
     * @param t the transaction object
     * @param error the server error response
     */
    protected void onGetDataError(Transaction t, JSONObject error) {

    }

}

