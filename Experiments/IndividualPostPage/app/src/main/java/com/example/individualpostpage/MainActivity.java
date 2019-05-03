package com.example.individualpostpage;

import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView Test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Test = (TextView) findViewById(R.id.Test1);

        // Credit to this source: https://stackoverflow.com/questions/14545139/android-back-button-in-the-title-bar
        ActionBar actionBar = getActionBar();
        // actionBar.setDisplayHomeAsUpEnabled(true);

        // Test.setText(actionBar.getTitle().toString());
    }
}
