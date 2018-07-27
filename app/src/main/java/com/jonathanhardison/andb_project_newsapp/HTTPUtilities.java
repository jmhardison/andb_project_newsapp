package com.jonathanhardison.andb_project_newsapp;

import android.text.TextUtils;
import android.util.Log;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class HTTPUtilities {
    private static final String LOG_TAG = "NEWSLOG";

    public HTTPUtilities(){}

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG,"Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }


    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
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







    public static ArrayList<NewsArticleType> extractArticles(String articleJSON) {


        ArrayList<NewsArticleType> newsArticles = new ArrayList<>();


        try {
            JSONObject baseJsonResponse = new JSONObject(articleJSON);
            JSONObject responseObject = baseJsonResponse.getJSONObject("response");
            JSONArray articleArray = responseObject.getJSONArray("results");



            for (int i = 0; i < articleArray.length(); i++) {


                JSONObject currentArticle = articleArray.getJSONObject(i);

                String _title = currentArticle.getString("webTitle");
                String _section = currentArticle.getString("sectionName");
                String _published = currentArticle.getString("webPublicationDate");
                String _webURL = currentArticle.getString("webUrl");

                String _author = "";
                if(currentArticle.has("tags")) {
                    JSONArray currentArticleTags = currentArticle.getJSONArray("tags");
                    for(int t = 0; t < currentArticleTags.length(); t++) {

                        JSONObject tObj = currentArticleTags.getJSONObject(t);
                        if(tObj.getString("type").equals("contributor")) {

                            _author = tObj.getString("webTitle");
                        }

                    }
                }

                NewsArticleType article = new NewsArticleType(_title, _section, _author, _published, _webURL);

                newsArticles.add(article);
            }

        } catch (JSONException e) {

            Log.e(LOG_TAG, "Problem parsing the articles JSON results", e);
        }

        // Return the list of earthquakes
        return newsArticles;
    }


    public static List<NewsArticleType> fetchArticles(String requestUrl) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        List<NewsArticleType> newsArticles = extractArticles(jsonResponse);

        return newsArticles;
    }

}
