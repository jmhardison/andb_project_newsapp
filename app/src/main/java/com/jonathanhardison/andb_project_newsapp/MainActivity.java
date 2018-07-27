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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<NewsArticleType>>{

    //move this to strings?
    private static final String NEWS_API_URL = "http://content.guardianapis.com/search?q=&api-key=test&show-tags=contributor&section=business";
    private TextView emptyStateTextView;
    private ProgressBar progressBarView;
    private static final int NEWS_LOADER_ID = 1;
    private NewsAdapter customAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //connectivity check
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();


        //pull references
        ListView newsArticleListView = (ListView) findViewById(R.id.list);
        emptyStateTextView = (TextView) findViewById(R.id.empty_view);


        newsArticleListView.setEmptyView(emptyStateTextView);
        progressBarView = (ProgressBar) findViewById(R.id.indeterminateBar);


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


        if(!isConnected)
        {
            emptyStateTextView.setText(R.string.no_connection);
            progressBarView.setVisibility(View.GONE);
        }
        else
        {
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);

        }

    }

    @Override
    public Loader<List<NewsArticleType>> onCreateLoader(int i, Bundle bundle) {
        return new NewsArticleLoader(this, NEWS_API_URL);
    }

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

    @Override
    public void onLoaderReset(Loader<List<NewsArticleType>> loader) {
        customAdapter.clear();
    }
}
