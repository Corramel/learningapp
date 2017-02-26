package com.example.android.learningapp;

/**
 * Created by Corr on 2/25/2017.
 */

public class Word {
    public String name;

    public String definition;

    public String pronunciation;

    public String partOfSpeech;

    public String typeOf;

    public String hasTypes;


    public String[] synonyms;

    public String[] Syllables;

    public Word(String mName, String mDefinition, String[] mSynonyms, String mPartOfSpeech){
        name = mName;
        definition = mDefinition;
        synonyms = mSynonyms;
        partOfSpeech = mPartOfSpeech;

    }
    public Word(String mName, String mDefinition, String[] mSynonyms, String mPartOfSpeech, String mPronunciation){
        name = mName;
        definition = mDefinition;
        synonyms = mSynonyms;
        partOfSpeech = mPartOfSpeech;
        pronunciation = mPronunciation;

    }

    public String getName() {
        return name;
    }

    public String getDefinition() {
        return definition;
    }

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public String getTypeOf() {
        return typeOf;
    }

    public String getHasTypes() {
        return hasTypes;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public String[] getSynonyms() {
        return synonyms;
    }

    public String[] getSyllables() {
        return Syllables;
    }



}
