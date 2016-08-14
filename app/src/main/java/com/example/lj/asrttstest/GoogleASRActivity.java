package com.example.lj.asrttstest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class GoogleASRActivity extends AppCompatActivity {

    private static  final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
    private EditText metTextHint;
    private ListView mlvTextMatches;
    private Spinner msTextMatches;
    private Button mbtSpeak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_recognition);

        metTextHint = (EditText) findViewById(R.id.etTextHint);
        mlvTextMatches = (ListView) findViewById(R.id.lvTextMatches);
        msTextMatches = (Spinner) findViewById(R.id.sNoOfMatches);
        mbtSpeak = (Button) findViewById(R.id.btSpeak);

    }

    public  void  CheckVoiceRecognition(){
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH),0);
        if (activities.size()==0){
            mbtSpeak.setEnabled(false);
            Toast.makeText(this,"Voice recognizer not present",Toast.LENGTH_LONG).show();
        }
    }

    public void speak(View view){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getClass().getPackage().getName());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,metTextHint.getText().toString());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        if (msTextMatches.getSelectedItemPosition()== AdapterView.INVALID_POSITION){
            Toast.makeText(this,"Please select No. of Matches from Spinner",Toast.LENGTH_LONG).show();
            return;
        }
        int noOfMatches = Integer.parseInt(msTextMatches.getSelectedItem().toString());
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,noOfMatches);
        startActivityForResult(intent,VOICE_RECOGNITION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if (resultCode == RESULT_OK){
            ArrayList<String> textMatchlist = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            if (!textMatchlist.isEmpty()){
                if (textMatchlist.get(0).contains("search")){
                    String searchQuery = textMatchlist.get(0).replace("search"," ");
                    Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
                    search.putExtra(SearchManager.QUERY,searchQuery);
                    startActivity(search);
                }
                else {
                    mlvTextMatches.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,textMatchlist));
                }
            }
        }
        else if (resultCode == RecognizerIntent.RESULT_AUDIO_ERROR){
            showToastMessage("Audio Error");

        }
        else if ((resultCode == RecognizerIntent.RESULT_CLIENT_ERROR)){
            showToastMessage("Client Error");

        }
        else if (resultCode == RecognizerIntent.RESULT_NETWORK_ERROR){
            showToastMessage("Network Error");
        }
        else if (resultCode == RecognizerIntent.RESULT_NO_MATCH){
            showToastMessage("No Match");
        }
        else if (resultCode == RecognizerIntent.RESULT_SERVER_ERROR){
            showToastMessage("Server Error");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    void  showToastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_voice_recognition, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
