package com.jonathanhardison.andb_project_newsapp;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<NewsArticleType>>{

    //at time of testing, "test" was at query limit. May need to use a user generated api key.
    //change api-key=test to api-key=actualkey
    private static final String NEWS_API_URL = "http://content.guardianapis.com/search?q=&api-key=test&show-tags=contributor&section=business";

    private TextView emptyStateTextView;
    private ProgressBar progressBarView;
    private static final int NEWS_LOADER_ID = 1;
    private NewsAdapter customAdapter;


    /***
     * initial oncreate
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //connectivity check
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();


        //pull references to views.
        ListView newsArticleListView = (ListView) findViewById(R.id.list);
        emptyStateTextView = (TextView) findViewById(R.id.empty_view);

        newsArticleListView.setEmptyView(emptyStateTextView);
        progressBarView = (ProgressBar) findViewById(R.id.indeterminateBar);

        //create custom adapter for news articles.
        customAdapter = new NewsAdapter(this, new ArrayList<NewsArticleType>());
        newsArticleListView.setAdapter(customAdapter);


        // Set an item click listener on the ListView, which sends an intent to a web browser
        newsArticleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                NewsArticleType currentArticle = customAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri articleUri = Uri.parse(currentArticle.getWebURLForArticle());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, articleUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        // Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = getLoaderManager();


        //if connected load, otherwise show no internet.
        if(!isConnected)
        {
            emptyStateTextView.setText(R.string.no_connection);
            progressBarView.setVisibility(View.GONE);
        }
        else
        {
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        }

    }

    /***
     * creation of loader for background work.
     * @param i
     * @param bundle
     * @return
     */
    @Override
    public Loader<List<NewsArticleType>> onCreateLoader(int i, Bundle bundle) {
        return new NewsArticleLoader(this, NEWS_API_URL);
    }

    /***
     * loader finished operations
     * @param loader
     * @param newsArticles
     */
    @Override
    public void onLoadFinished(Loader<List<NewsArticleType>> loader, List<NewsArticleType> newsArticles) {
        emptyStateTextView.setText(R.string.no_articles);
        progressBarView.setVisibility(View.GONE);

        // Clear the adapter
        customAdapter.clear();

        if (newsArticles != null && !newsArticles.isEmpty()) {
            customAdapter.addAll(newsArticles);
        }
    }

    /***
     * reset loader, clear screen.
     * @param loader
     */
    @Override
    public void onLoaderReset(Loader<List<NewsArticleType>> loader) {
        customAdapter.clear();
    }
}
