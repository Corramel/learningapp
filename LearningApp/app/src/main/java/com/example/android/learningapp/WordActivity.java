package com.example.android.learningapp;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.example.android.learningapp.QueryUtils.WeatherAsyncTask;
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

        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_activity);
        ListView wordListView = (ListView) findViewById(R.id.list);
        adapter = new WordAdapter(this, new ArrayList<Word>());
        AndroidNetworking.initialize(getApplicationContext());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        wordListView.setAdapter(adapter);
        //WeatherAsyncTask task = new WeatherAsyncTask();
        //task.execute();
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void openList(View view) {
        Intent intent = new Intent(this, WordActivity.class);
        startActivity(intent);

    }
    @Override
    public Loader<List<Word>> onCreateLoader(int i, Bundle bundle) {
        WEATHER_REQUEST_URL = "http://content.guardianapis.com/search?q=" + WordSettings.searchTopic + "&api-key=815487d3-5cdf-46d4-b0b1-93393237b40a";
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


    public class WeatherAsyncTask extends AsyncTask<URL, Void, ArrayList<Word>> {

        @Override
        protected ArrayList<Word> doInBackground(URL... urls) {
            // Create URL object
            URL url = createUrl(WEATHER_REQUEST_URL);

            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);

            } catch (IOException e) {
                // TODO Handle the IOException
            }


            // Extract relevant fields from the JSON response and create an {@link Word} object
            return extractWords(jsonResponse);
        }



        @Override
        protected void onPostExecute(ArrayList<Word> word) {
            if (word == null) {
                return;
            }
            ListView wordListViewB = (ListView) findViewById(R.id.list);
            wordListView = wordListViewB;
            if (adapter == null) {
                adapter = new WordAdapter(getApplicationContext(), word);
                wordListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            } else {
                wordListView.invalidateViews();
                wordListView.refreshDrawableState();
                // Create a new adapter that takes the list of words as input
                adapter = new WordAdapter(getApplicationContext(), word);
                // Set the adapter on the {@link ListView}
                // so the list can be populated in the user interface
                wordListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                //updateUi(word);
            }
        }
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

    /**
     * Return an {@link Word} object by parsing out information
     * about the first word from the input wordJSON string.
     */
    public static ArrayList<Word> extractWords(String wordJSON) {


        // Create an empty ArrayList that we can start adding words to
        ArrayList<Word> words = new ArrayList<>();
        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the SAMPLE_JSON_RESPONSE string
            JSONObject baseJsonResponse = new JSONObject(wordJSON);
            // Extract the JSONArray associated with the key called "features",
            // which represents a list of features (or words).
            JSONObject dataResponse = baseJsonResponse.getJSONObject("response");
            JSONArray wordArray = dataResponse.getJSONArray("results");

            // For each word in the wordArray, create an {@link Word} object
            for (int i = 0; i < wordArray.length(); i++) {


                // Get a single word at position i within the list of words
                JSONObject currentWord = wordArray.getJSONObject(i);

                // For a given word, extract the JSONObject associated with the
                // key called "properties", which represents a list of all properties
                // for that word.
                //JSONArray weather = currentWord.getJSONArray("weather");
                // JSONObject temp = currentWord.getJSONObject("temp");

                // Extract the value for the key called "mag"
                //JSONObject currentWeather = weather.getJSONObject(0);
                String city = currentWord.getString("webTitle");
                //String currentStatus = currentWord.getString("main");
                //int weatherCode = currentWeather.getInt("id");
                String currentTemp = currentWord.getString("webPublicationDate");

                // Extract the value for the key called "place"
                String location = currentWord.getString("sectionName");

                String mUrl = currentWord.getString("webUrl").replace("\\/", "/");

                // Extract the value for the key called "time"
                //long time = currentWord.getLong("dt");
                String time = currentWord.getString("webPublicationDate");
                String origTimeFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'";
                String newTimeFormat = "EEE, MM d, yyyy";
                String newDateTimeFormat = "HH:mm a";
                SimpleDateFormat readingFormat = new SimpleDateFormat(origTimeFormat);
                SimpleDateFormat dateTimeFormat = new SimpleDateFormat(newDateTimeFormat);
                SimpleDateFormat timeFormat = new SimpleDateFormat(newTimeFormat);

                try{
                    Date date = readingFormat.parse(time);
                    String eventDate = timeFormat.format(date);
                    String eventTimeDate = dateTimeFormat.format(date);
                    Log.d("Time:", eventDate + " " + eventTimeDate);
                    //Word word = new Word(city, location, currentTemp, mUrl, eventDate, eventTimeDate);

                    //words.add(word);
                } catch (ParseException e){
                    Log.e("XD", "Whoops.");
                   // Word word = new Word(city, location, currentTemp, mUrl);

                   // words.add(word);
                }

                // Extract the value for the key called "url"
                //String url = properties.getString("url");

                // Create a new {@link Word} object with the magnitude, location, time,
                // and url from the JSON response.

            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the word JSON results", e);
        }

        // Return the list of words
        return words;
    }
}
