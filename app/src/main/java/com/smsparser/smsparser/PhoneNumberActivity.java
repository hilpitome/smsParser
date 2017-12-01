package com.smsparser.smsparser;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.smsparser.smsparser.utils.PrefUtils;

/**
 * Created by hilary on 11/30/17.
 */

public class PhoneNumberActivity extends AppCompatActivity {
    Button submitButton;
    EditText simNumberEt;
    PrefUtils prefutils;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);
        prefutils = new PrefUtils(this);

        submitButton = (Button) findViewById(R.id.submit_btn);
        simNumberEt = (EditText) findViewById(R.id.sim_number_et);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String simNumber = simNumberEt.getText().toString().trim();
                if(validateField()){
                    prefutils.setSimNUmber(simNumber);
                    Toast.makeText(getApplicationContext(), "phone number is set", Toast.LENGTH_LONG).show();
                    prefutils.setKeyHasPhoneNumber(true);
                    Intent i = new Intent(PhoneNumberActivity.this, MainActivity.class);
                    startActivity(i);
                }
            }
        });

    }

    private boolean validateField() {
        String simNo = simNumberEt.getText().toString().trim();
        if (TextUtils.isEmpty(simNo)) {
            simNumberEt.setError("phone number is required");
            return false;
        }
        if(simNo.length() < 9){
            simNumberEt.setError("please enter a valid phone number");
            return false;
        }
        return true;
    }
}
