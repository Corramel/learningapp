
package com.example.android.learningapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

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

import static com.example.android.learningapp.R.layout.word_activity;

/**
 * Helper methods related to requesting and receiving word data from USGS.
 */
public final class QueryUtils {

    public static String mPronunciation = "example";
    public static List<Word> words = new ArrayList<>();
    private QueryUtils() {

    }
    /**
     * Return an {@link Word} object by parsing out information
     * about the first word from the input wordJSON string.
     */
    public static List<Word> fetchWordData(String mUrl) {

        //List<Word> words = extractFeatureFromJson(mUrl);
        AndroidNetworking.get(mUrl)
                .addHeaders("X-Mashape-Key", "zBYDYo6VEHmshoz3H2z35g4EbHzpp17feQLjsnDPmlApMmzZLU")
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {


                            String name = response.getString("word");
                            JSONArray wordArray = response.getJSONArray("results");
                            String pronunciation = response.getJSONObject("pronunciation").getString("all");
                            Log.v("A pronunciation?: ", mPronunciation);
                            if(!(words.size() == 0)) {
                                words.clear();
                            }
                            for (int i = 0; i < wordArray.length()-1; i++) {
                                String definition = "";
                                JSONObject currentWord = wordArray.getJSONObject(i);
                                try {
                                    definition = currentWord.getString("definition");
                                } catch(JSONException e){
                                    Log.v("No def", "lmao");
                                }
                                try {
                                    JSONArray arraySynonyms = currentWord.getJSONArray("synonyms");
                                    String[] synonyms = new String[arraySynonyms.length()];
                                    // Log.v("Synonyms length:", "" + synonyms.length);

                                    if (arraySynonyms.length() > 0) {
                                        for (int j = 0; j < arraySynonyms.length() - 1; j++) {
                                            synonyms[j] = arraySynonyms.getString(j);
                                        }
                                        String partOfSpeech = currentWord.getString("partOfSpeech");

                                        //may need to check if synonyms should be a string array or not

                                        Word word = new Word(name, definition, synonyms, partOfSpeech, pronunciation);

                                        words.add(word);



                                    }
                                } catch (org.json.JSONException e) {
                                    Log.v("No stuff: ", "nice meme");
                                }

                            }
                            if(!(words.size() == 0)){
                                mPronunciation = words.get(0).getPronunciation();
                            }
                        } catch (JSONException e) {
                            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Error: ", "oh snap?");
                    }
                });
        // Return the list of {@link Word}s
        return words;
    }

    private static List<Word> extractFeatureFromJson(String mUrl) {
        // If the JSON string is empty or null, then return early.

        AndroidNetworking.get(mUrl)
                .addHeaders("X-Mashape-Key", "zBYDYo6VEHmshoz3H2z35g4EbHzpp17feQLjsnDPmlApMmzZLU")
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {


                            String name = response.getString("word");
                            JSONArray wordArray = response.getJSONArray("results");
                            String pronunciation = response.getJSONObject("pronunciation").getString("all");
                            Log.v("A pronunciation?: ", mPronunciation);
                            if(!(words.size() == 0)) {
                                words.clear();
                            }
                            for (int i = 0; i < wordArray.length()-1; i++) {
                                String definition = "";
                                JSONObject currentWord = wordArray.getJSONObject(i);
                                try {
                                    definition = currentWord.getString("definition");
                                } catch(JSONException e){
                                    Log.v("No def", "lmao");
                                }
                                try {
                                    JSONArray arraySynonyms = currentWord.getJSONArray("synonyms");
                                    String[] synonyms = new String[arraySynonyms.length()];
                                   // Log.v("Synonyms length:", "" + synonyms.length);

                                    if (arraySynonyms.length() > 0) {
                                        for (int j = 0; j < arraySynonyms.length() - 1; j++) {
                                            synonyms[j] = arraySynonyms.getString(j);
                                        }
                                        String partOfSpeech = currentWord.getString("partOfSpeech");

                                        //may need to check if synonyms should be a string array or not

                                        Word word = new Word(name, definition, synonyms, partOfSpeech, pronunciation);

                                        words.add(word);



                                    }
                                } catch (org.json.JSONException e) {
                                    Log.v("No stuff: ", "nice meme");
                                }

                            }
                            if(!(words.size() == 0)){
                                mPronunciation = words.get(0).getPronunciation();
                            }
                        } catch (JSONException e) {
                            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Error: ", "oh snap?");
                    }
                });
        Log.v("A pronunciation?2: ", mPronunciation);
        Log.v("words length ", "" + words.size());
        return words;
    }


}

