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
     * ViewHolder class to reduce view lookups.
     */
    static class ViewHolder {
        private TextView articleTitleView;
        private TextView articleCategoryView;
        private TextView articleDateView;
        private TextView articleAuthorView;
    }

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

        ViewHolder holder;

        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.article_list_item, parent, false);

            holder = new ViewHolder();

            holder.articleTitleView = listItemView.findViewById(R.id.articletitle);
            holder.articleCategoryView = listItemView.findViewById(R.id.articlecategory);
            holder.articleDateView = listItemView.findViewById(R.id.articledate);
            holder.articleAuthorView = listItemView.findViewById(R.id.articleauthor);

            listItemView.setTag(holder);
        }
        else{
            holder = (ViewHolder) listItemView.getTag();
        }

        //get current article
        NewsArticleType currentArticle = getItem(position);

        //pull view references and set data.
        holder.articleTitleView.setText(currentArticle.getArticleTitle());
        holder.articleCategoryView.setText(currentArticle.getSectionName());
        holder.articleDateView.setText(currentArticle.getPublishedDate());
        holder.articleAuthorView.setText(currentArticle.getAuthor());

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }
}
