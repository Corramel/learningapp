package com.example.android.learningapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Loads a list of earthquakes by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class WordLoader extends AsyncTaskLoader<List<Word>> {

    /** Tag for log messages */
    private static final String LOG_TAG = WordLoader.class.getName();

    /** Query URL */
    private String mUrl;

    /**
     * Constructs a new {@link WordLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public WordLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<Word> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of earthquakes.
        List<Word> words = QueryUtils.fetchWordData(mUrl);
        return words;
    }
}