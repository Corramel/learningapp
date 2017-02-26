package com.example.android.learningapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;

/**
 * Loads a list of earthquakes by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class WordLoader extends AsyncTaskLoader<List<Word>> {

    /** Tag for log messages */
    private static final String LOG_TAG = WordLoader.class.getName();

    private String mUrl;



    public WordLoader(Context context, String url) {
        super(context);
        mUrl = url;

    }

    @Override
    protected void onStartLoading() {

        forceLoad();

    }


    @Override
    public List<Word> loadInBackground() {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        // Perform the network request, parse the response, and extract a list of earthquakes.
        List<Word> words = QueryUtils.fetchWordData(mUrl);
        return words;
    }

}