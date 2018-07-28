package com.jonathanhardison.andb_project_newsapp;

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
import java.util.List;

public final class HTTPUtilities {
    private static final String LOG_TAG = "NEWSLOG";
    private static final int CONN_TIMEOUT = 15000;
    private static final int READ_TIMEOUT = 10000;

    public HTTPUtilities(){}

    /***
     * Create URL object from string.
     * @param stringUrl
     * @return
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /***
     * HTTP Request Operations
     * @param url
     * @return
     * @throws IOException
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
            urlConnection.setReadTimeout(READ_TIMEOUT /* milliseconds */);
            urlConnection.setConnectTimeout(CONN_TIMEOUT /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG,"Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            //catch exceptions and log
            Log.e(LOG_TAG, "Problem retrieving the JSON results.", e);
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


    /***
     * read Stream and convert to String.
     * @param inputStream
     * @return
     * @throws IOException
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


    /***
     * extract articles from string and create array
     * @param articleJSON
     * @return
     */
    public static ArrayList<NewsArticleType> extractArticles(String articleJSON) {

        //create array
        ArrayList<NewsArticleType> newsArticles = new ArrayList<>();


        try {
            //traverse the JSON object structure and pull relevent information.
            //root
            JSONObject baseJsonResponse = new JSONObject(articleJSON);
            //response
            JSONObject responseObject = baseJsonResponse.getJSONObject("response");
            //results array
            JSONArray articleArray = responseObject.getJSONArray("results");


            //for each object in array, pull information about article.
            for (int i = 0; i < articleArray.length(); i++) {

                //current article object
                JSONObject currentArticle = articleArray.getJSONObject(i);

                String _title = currentArticle.getString("webTitle");
                String _section = currentArticle.getString("sectionName");
                String _published = currentArticle.getString("webPublicationDate");
                String _webURL = currentArticle.getString("webUrl");

                String _author = "";
                //if there are "tags" pull it and look for contributor which is author.
                if(currentArticle.has("tags")) {
                    JSONArray currentArticleTags = currentArticle.getJSONArray("tags");
                    for(int t = 0; t < currentArticleTags.length(); t++) {

                        JSONObject tObj = currentArticleTags.getJSONObject(t);
                        if(tObj.getString("type").equals("contributor")) {

                            _author = tObj.getString("webTitle");
                        }

                    }
                }

                //create article object type
                NewsArticleType article = new NewsArticleType(_title, _section, _author, _published, _webURL);
                //add article
                newsArticles.add(article);
            }

        } catch (JSONException e) {

            Log.e(LOG_TAG, "Problem parsing the articles JSON results", e);
        }

        // Return the list
        return newsArticles;
    }


    /***
     * Fetch the articles and perform http requests.
     * @param requestUrl
     * @return
     */
    public static List<NewsArticleType> fetchArticles(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        //hold list from extractArticles method.
        List<NewsArticleType> newsArticles = extractArticles(jsonResponse);

        //return the list
        return newsArticles;
    }

}
