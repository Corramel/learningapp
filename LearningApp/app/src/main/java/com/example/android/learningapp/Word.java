package com.example.android.learningapp;

/**
 * Created by Corr on 2/25/2017.
 */

public class Word {
    public String name;

    public String definition;

    public String partOfSpeech;

    public String typeOf;

    public String hasTypes;

    public String derivation;

    public String[] synonyms;

    public String[] Syllables;

    public Word(String mName, String mDefinition, String[] mSynonyms, String mPartOfSpeech){
        name = mName;
        definition = mDefinition;
        synonyms = mSynonyms;
        partOfSpeech = mPartOfSpeech;

    }
    public Word(String mName, String mDefinition, String[] mSynonyms, String mPartOfSpeech, String mDerivation){
        name = mName;
        definition = mDefinition;
        synonyms = mSynonyms;
        partOfSpeech = mPartOfSpeech;
        derivation = mDerivation;

    }


}
