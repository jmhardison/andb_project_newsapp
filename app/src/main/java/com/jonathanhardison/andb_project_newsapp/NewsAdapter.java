package com.jonathanhardison.andb_project_newsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class NewsAdapter extends ArrayAdapter<NewsArticleType> {

    /***
     * instantiate
     * @param context
     * @param articles
     */
    public NewsAdapter(@NonNull Context context, List<NewsArticleType> articles) {
        super(context, 0, articles);
    }

    /***
     * getView method for filling screen data.
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.article_list_item, parent, false);
        }

        //get current article
        NewsArticleType currentArticle = getItem(position);

        //pull view references and set data.
        TextView articleTitleView = (TextView) listItemView.findViewById(R.id.articletitle);
        articleTitleView.setText(currentArticle.getArticleTitle());

        TextView articleCategoryView = (TextView) listItemView.findViewById(R.id.articlecategory);
        articleCategoryView.setText(currentArticle.getSectionName());

        TextView articleDateView = (TextView) listItemView.findViewById(R.id.articledate);
        articleDateView.setText(currentArticle.getPublishedDate());

        TextView articleAuthorView = (TextView) listItemView.findViewById(R.id.articleauthor);
        articleAuthorView.setText(currentArticle.getAuthor());

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }
}
