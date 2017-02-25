
package com.example.android.learningapp;

import android.os.AsyncTask;
import android.util.Log;
import android.text.TextUtils;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Helper methods related to requesting and receiving word data from USGS.
 */
public final class QueryUtils {

    /** Sample JSON response for a USGS query */
    private static String WEATHER_REQUEST_URL = "http://api.openweathermap.org/data/2.5/forecast?q=30518&mode=json&units=imperial&appid=9d456f29174a2626137d59075d2737a4";
    private static String SAMPLE_JSON_RESPONSE = "";


    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {

    }



    /**
     * Return a list of {@link Word} objects that has been built up from
     * parsing a JSON response.
     */
    /**
     * Returns new URL object from the given string URL.
     */
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
    public static List<Word> fetchWordData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e("XD", "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Word}s
        List<Word> words = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link Word}s
        return words;
    }

    private static List<Word> extractFeatureFromJson(String wordJSON) {
        // If the JSON string is empty or null, then return early.
        final ArrayList<Word> words = new ArrayList<>();
        if (TextUtils.isEmpty(wordJSON)) {
            return null;
        }
        AndroidNetworking.get("https://wordsapiv1.p.mashape.com/words/{word}")
                .addPathParameter("word", WordSettings.searchTopic)
                .addHeaders("X-Mashape-Key", "zBYDYo6VEHmshoz3H2z35g4EbHzpp17feQLjsnDPmlApMmzZLU")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            JSONObject dataResponse = response;
                            String name = dataResponse.getString("word");
                            JSONArray wordArray = dataResponse.getJSONArray("results");

                            for (int i = 0; i < wordArray.length(); i++) {

                                JSONObject currentWord = wordArray.getJSONObject(i);

                                String definition = currentWord.getString("definition");

                                JSONArray arraySynonyms = currentWord.getJSONArray("synonyms");
                                String[] synonyms = new String[arraySynonyms.length()];
                                if (arraySynonyms.length() > 0) {
                                    for (int j = 0; j < arraySynonyms.length(); j++) {
                                        synonyms[i] = arraySynonyms.getString(i);
                                    }
                                    String partOfSpeech = currentWord.getString("partOfSpeech");

                                        //may need to check if synonyms should be a string array or not

                                    Word word = new Word(name, definition, synonyms, partOfSpeech);

                                    words.add(word);


                                }

                            }
                        } catch (JSONException e) {
                            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Error: ", "oh shit?");
                    }
                });
        return words;
    }
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
            JSONArray wordArray = baseJsonResponse.getJSONArray("list");

            // For each word in the wordArray, create an {@link Word} object
            for (int i = 0; i < wordArray.length(); i++) {

                // Get a single word at position i within the list of words
                JSONObject currentWord = wordArray.getJSONObject(i);

                // For a given word, extract the JSONObject associated with the
                // key called "properties", which represents a list of all properties
                // for that word.
                JSONObject weather = currentWord.getJSONObject("weather");

                // Extract the value for the key called "mag"
                String currentStatus = weather.getString("main");

                // Extract the value for the key called "place"
                String location = currentWord.getString("name");

                // Extract the value for the key called "time"
                long time = currentWord.getLong("dt");

                // Extract the value for the key called "url"
                //String url = properties.getString("url");

                // Create a new {@link Word} object with the magnitude, location, time,
                // and url from the JSON response.
                //Word word = new Word(currentStatus, time, location);

                // Add the new {@link Word} to the list of words.
                //words.add(word);
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

