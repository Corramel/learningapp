package com.example.android.learningapp;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Loader;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.android.learningapp.Word;
import com.example.android.learningapp.WordAdapter;
import com.example.android.learningapp.WordLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WordActivity extends AppCompatActivity implements LoaderCallbacks<List<Word>> {

    public static ListView wordListView;
    WordAdapter adapter;

    public static final String LOG_TAG = WordActivity.class.getName();
    private static String WEATHER_REQUEST_URL = "https://wordsapiv1.p.mashape.com/words/" + WordSettings.searchTopic;
    private static final int EARTHQUAKE_LOADER_ID = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidNetworking.initialize(getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_activity);
        LoaderManager loaderManager = getLoaderManager();
        final LoaderCallbacks meme = this;
        loaderManager.restartLoader(EARTHQUAKE_LOADER_ID, null, this).forceLoad();
        TextView pronunciation = (TextView) findViewById(R.id.pronounce);
        TextView wordTitle = (TextView) findViewById(R.id.wordName);

        ListView wordListView = (ListView) findViewById(R.id.list);
        adapter = new WordAdapter(this, new ArrayList<Word>());
        wordTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLoaderManager().restartLoader(EARTHQUAKE_LOADER_ID, null, meme).forceLoad();
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                            public void run(){
                        addNotification();
                    }

                }, 2000);
            }
        });




        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        wordListView.setAdapter(adapter);
        wordTitle.setText(WordSettings.searchTopic);
        pronunciation.setText(QueryUtils.mPronunciation);

       // wordListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //@Override
           /* public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Word currentWord = adapter.getItem(i);
                String currentWordObj = currentWord.newsURL;
                Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentWordObj));
                startActivity(urlIntent);
            } */
       // });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.action_settings){
            Intent settingsIntent = new Intent(this, WordSettings.class);
            startActivity(settingsIntent);
            adapter.clear();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void openList(View view) {
        Intent intent = new Intent(this, WordActivity.class);
        startActivity(intent);


    }

    private void addNotification(){
        NotificationCompat.Builder mBuilder =
                (android.support.v7.app.NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.word_list)
                        .setContentTitle("WLA")
                        .setContentText("Have you used " + WordSettings.searchTopic + " or any of its synonyms?")
                .setPriority(Notification.PRIORITY_HIGH);
        Intent notificationIntent = new Intent(this, WordActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setVibrate(new long[] { 0, 1000 });
        mBuilder.setLights(Color.DKGRAY, 2000, 1000);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, mBuilder.build());
    }

    @Override
    public Loader<List<Word>> onCreateLoader(int i, Bundle bundle) {
        WEATHER_REQUEST_URL = "https://wordsapiv1.p.mashape.com/words/" + WordSettings.searchTopic;
        // Create a new loader for the given URL
        Log.d("Log1:", WEATHER_REQUEST_URL);
        return new WordLoader(this, WEATHER_REQUEST_URL);
    }
    @Override
    public void onLoadFinished(Loader<List<Word>> loader, List<Word> words) {
        // Clear the adapter of previous word data
        Log.d("Log1:", "Clearing the adapter of prev. word data");
        adapter.clear();

        // If there is a valid list of {@link Word}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (words != null && !words.isEmpty()) {
            Log.d("Log1:", "Adding words" + words);
            adapter.addAll(words);
        }

    }
    @Override
    public void onLoaderReset(Loader<List<Word>> loader) {
        // Loader reset, so we can clear out our existing data.
        Log.d("Log1:", "Reset loader");
        adapter.clear();
    }

    public void refreshMaybe(View view){
        adapter.notifyDataSetChanged();
    }




    private void updateUi(ArrayList<Word> words) {
        ListView wordListViewB = (ListView) findViewById(R.id.list);
        wordListView = wordListViewB;


        // Create a new adapter that takes the list of words as input
        adapter = new WordAdapter(this, words);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        wordListView.setAdapter(adapter);

    }

    public static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e("XD", "Error with creating URL", exception);
            return null;
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    public static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            jsonResponse = readFromStream(inputStream);
        } catch (IOException e) {
            // TODO: Handle the exception
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    public static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

}
