package com.example.arduino_scope;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class How_to extends AppCompatActivity {


    TextView editText ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to);

        editText = (TextView) findViewById(R.id.howto_edit);
       // editText.setEnabled(false);
    }
}
