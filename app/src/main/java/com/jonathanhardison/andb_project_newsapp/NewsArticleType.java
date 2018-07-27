package com.jonathanhardison.andb_project_newsapp;




import android.text.TextUtils;
import android.util.Log;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewsArticleType {
    private static final String LOG_TAG = "NEWSLOG";
    private String articleTitle;
    private String sectionName;
    private String author;
    private String publishedDate;
    private String webURLForArticle;

    /***
     * News article type instantiation.
     * @param inTitle
     * @param inSectionName
     * @param inAuthor
     * @param inPublishedDate
     */
    public NewsArticleType(String inTitle, String inSectionName, String inAuthor,String inPublishedDate, String inWebURLForArticle) {

        articleTitle = inTitle;
        sectionName = inSectionName;
        author = inAuthor;
        webURLForArticle = inWebURLForArticle;

        //check if published is not empty, then convert it to desired string date representation and store it.
        if(!TextUtils.isEmpty(inPublishedDate)) {
            try {
                Date inputDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(inPublishedDate);
                publishedDate = new SimpleDateFormat("LLL dd, yyyy").format(inputDate).toString();
            }
            catch (ParseException e){
                Log.e(LOG_TAG, "Error parsing date field.", e);
            }
        }
        else
        {
            publishedDate = "";
        }

    }

    /***
     * returns the title of the article
     * @return
     */
    public String getArticleTitle(){
        return articleTitle;
    }

    /***
     * returns the Section Name
     * @return
     */
    public String getSectionName(){
        return sectionName;
    }

    /***
     * returns the author name
     * @return
     */
    public String getAuthor(){
        return author;
    }

    /***
     * returns the published date as string
     * @return
     */
    public String getPublishedDate(){
        return publishedDate.toString();
    }

    /***
     * returns the url fo the article
     * @return
     */
    public String getWebURLForArticle(){
        return webURLForArticle;
    }
}
