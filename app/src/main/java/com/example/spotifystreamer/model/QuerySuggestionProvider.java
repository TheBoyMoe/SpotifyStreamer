package com.example.spotifystreamer.model;


import android.content.SearchRecentSuggestionsProvider;

public class QuerySuggestionProvider extends SearchRecentSuggestionsProvider{

    public final static String AUTHORITY = "com.example.spotifystreamer.model.QuerySuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public QuerySuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }

}
