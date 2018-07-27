package com.jonathanhardison.andb_project_newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class NewsArticleLoader extends AsyncTaskLoader<List<NewsArticleType>> {
    private String urlHold;

    public NewsArticleLoader(Context context, String inUrl) {
        super(context);
        urlHold = inUrl;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<NewsArticleType> loadInBackground() {
        if (urlHold == null) {
            return null;
        }

        List<NewsArticleType> newsArticles = HTTPUtilities.fetchArticles(urlHold);
        return newsArticles;
    }

}
