package com.example.lj.asrttstest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lj.asrttstest.info.Global;

public class InputIdActivity extends AppCompatActivity {

    private EditText inputIdView;
    private Button idConfirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_id);

        inputIdView = (EditText) findViewById(R.id.sentenceIdInputView);
        idConfirmButton = (Button) findViewById(R.id.confirmSentenceIdButton);

        inputIdView.setInputType(InputType.TYPE_CLASS_NUMBER);

        idConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Global.beginingSentenceID = Integer.parseInt(inputIdView.getText().toString());
                    Log.d("sss", "input is: " + String.valueOf(Global.beginingSentenceID));
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Please input a valid number", Toast.LENGTH_LONG);
                }
            }
        });
    }
}
