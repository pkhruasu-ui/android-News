package com.example.android.news;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

/**
 * Created by prajakkhruasuwan on 11/19/17.
 * Class responsible for NYT related api
 */

public class NYTNewsLoader extends AsyncTaskLoader<List<NYTNews>>{
    private String NYT_API_URL = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
    private String query;

    public NYTNewsLoader(Context context, String query) {
        super(context);
        this.query = query;
    }

    @Override
    public void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<NYTNews> loadInBackground() {

        List<NYTNews> news = null;
        if (query != null) {
            String url = getQueryUrl(query);

            try {
                news = QueryUtils.fetchNewsData(url);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }

        return news;
    }

    private String getQueryUrl(String query) {

        if (query == null) {
            return null;
        }

        Uri baseUri = Uri.parse(NYT_API_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        if(!query.equals("")){
            uriBuilder.appendQueryParameter("q", query);
        }
        // NOTE: Security issue if left like this but I don't have a choice. I don't have server :(
        uriBuilder.appendQueryParameter("api-key", "2e3e37e06d2641e8aa850ec6766b9ddf");

        return uriBuilder.toString();
    }
}
