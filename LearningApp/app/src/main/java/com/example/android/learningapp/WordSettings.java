package com.example.android.learningapp;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class WordSettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }
    public static String currentZip = "30518";
    public static String searchTopic = "quiet";
    public void setZip(View view) {
        EditText newZipCode = (EditText) findViewById(R.id.zipcode);
        searchTopic = newZipCode.getText().toString();
        TextView listViewTitle = (TextView) findViewById(R.id.wordName);
    }
    public static class WordPreferenceFragment extends PreferenceFragment {

    }
}
