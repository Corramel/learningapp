
package com.example.android.learningapp;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class WordAdapter extends ArrayAdapter<Word> {

    /**
     * The part of the location string from the USGS service that we use to determine
     * whether or not there is a location offset present ("5km N of Cairo, Egypt").
     */
    private static final String LOCATION_SEPARATOR = " of ";

   
    public WordAdapter(Context context, List<Word> earthquakes) {
        super(context, 0, earthquakes);
    }

    /**
     * Returns a list item view that displays information about the earthquake at the given position
     * in the list of earthquakes.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.word_list_item, parent, false);
        }

        // Find the earthquake at the given position in the list of earthquakes
        Word currentWord = getItem(position);

        // Find the TextView with view ID location
        TextView definition = (TextView) listItemView.findViewById(R.id.definition);
        definition.setText(currentWord.getDefinition());
        /// / Display the location of the current earthquake in that TextView
      //  primaryLocationView.setText(currentWord.getLocation());

        // Find the TextView with view ID location offset
        TextView cinnamon = (TextView) listItemView.findViewById(R.id.synonym);
        String synonymsInfo = "Synonyms: " + Arrays.toString(currentWord.getSynonyms()).replace("[","").replace("]","").replace(", null", "");
        if(synonymsInfo.equals("Synonyms: null")){
            synonymsInfo = "No synonyms were found.";
        }
        cinnamon.setText(synonymsInfo);
        // Display the location offset of the current earthquake in that TextView
      //  locationOffsetView.setText(currentWord.getCondition());

        // Create a new Date object from the time in milliseconds of the earthquake
        // Date dateObject = new Date(currentWord.getTime() * 1000);

      /*  ImageView iconView = (ImageView) listItemView.findViewById(R.id.magnitude);
        iconView.setBackgroundResource(getImageResource(currentWord.getConditionCode()));
        */

        // Find the TextView with view ID date
        TextView number = (TextView) listItemView.findViewById(R.id.number);
        if(position == 0){
            number.setText("1");
        }
        number.setText("" + (position+1));

        // Find the TextView with view ID time
       // TextView timeView = (TextView) listItemView.findViewById(R.id.time);
        // Format the time string (i.e. "4:30PM")
        //String formattedTime = formatTime());
        // Display the time of the current earthquake in that TextView
     //   timeView.setText(currentWord.getDateTime());

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }

}

