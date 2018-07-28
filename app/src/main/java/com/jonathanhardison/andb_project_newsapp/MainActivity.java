package com.jonathanhardison.andb_project_newsapp;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<NewsArticleType>>{

    //at time of testing, "test" was at query limit. May need to use a user generated api key.
    //change api-key=test to api-key=actualkey
    // &section=business is the filter for category
    private static final String NEWS_API_URL = "http://content.guardianapis.com/search?q=&api-key=test&show-tags=contributor";

    private TextView emptyStateTextView;
    private ProgressBar progressBarView;
    private static final int NEWS_LOADER_ID = 1;
    private NewsAdapter customAdapter;

    /***
     * Inflate the options menu for settings menu.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /***
     * method to handle an options menu item being selected.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //get id of the item selected.
        int id = item.getItemId();
        //if it's our action_settings
        if(id==R.id.action_settings){
            //create an intent to launch our activity
            Intent intentSettings = new Intent(this, SettingsActivity.class);
            startActivity(intentSettings);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

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
        ListView newsArticleListView = findViewById(R.id.list);
        emptyStateTextView = findViewById(R.id.empty_view);

        newsArticleListView.setEmptyView(emptyStateTextView);
        progressBarView = findViewById(R.id.indeterminateBar);

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
        //do uri builder work and sharedpreferences loading here
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        //get value for key or default.
        String prefSelectedCategory = sharedPref.getString(getString(R.string.settings_selected_category_key), getString(R.string.settings_selected_category_default));
        String prefSelectedOrderBy = sharedPref.getString(getString(R.string.settings_orderby_key), getString(R.string.settings_orderby_default));

        //URI work
        Uri uriBase = Uri.parse(NEWS_API_URL);
        Uri.Builder uriBuild = uriBase.buildUpon();
        // &section=business is the filter for category
        uriBuild.appendQueryParameter("section", prefSelectedCategory);
        uriBuild.appendQueryParameter("order-by", prefSelectedOrderBy);

        return new NewsArticleLoader(this, uriBuild.toString());
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
