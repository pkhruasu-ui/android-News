package com.example.android.news;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NYTNews>> {

    private NewsAdaptor newsAdaptor;
    private TextView emptyView;
    private ProgressBar loadingSpinner;

    //define callback interface
    interface SearchCallbackInterface {
        void onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emptyView = (TextView) findViewById(R.id.empty_view);
        loadingSpinner = (ProgressBar) findViewById(R.id.loading_spinner);

        ListView listView = (ListView) findViewById(R.id.news_list);

        //init adaptor
        newsAdaptor = new NewsAdaptor(this, new ArrayList<NYTNews>());

        listView.setAdapter(newsAdaptor);
        listView.setEmptyView(emptyView);
        // do api call
        search(new SearchCallbackInterface() {
            @Override
            public void onStart() {
                getSupportLoaderManager().initLoader(1, null, MainActivity.this);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                NYTNews news = newsAdaptor.getItem(position);

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(news.getLinkUrl()));

                Intent chooser = Intent.createChooser(intent, "Select browser");

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(chooser);
                }
            }
        });
    }

    @Override
    public Loader<List<NYTNews>> onCreateLoader(int i, Bundle args) {
        String query = "";

        if (args != null) {
            query = args.getString("query");
        }
        return new NYTNewsLoader(this, query);
    }

    @Override
    public void onLoadFinished(Loader<List<NYTNews>> loader, List<NYTNews> news) {
        // clear old list
        newsAdaptor.clear();
        loadingSpinner.setVisibility(View.GONE);

        if (news != null && !news.isEmpty()) {
            newsAdaptor.addAll(news);
            newsAdaptor.notifyDataSetChanged();
        } else {
            emptyView.setText(R.string.no_result);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<NYTNews>> news) {
        newsAdaptor.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Hook view
        getMenuInflater().inflate(R.menu.menu_search, menu);
        // Bind view
        MenuItem menuItem = menu.findItem(R.id.news_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Bundle args = new Bundle();
                args.putString("query", query);
                final Bundle aa = args;

                search(new SearchCallbackInterface() {
                    @Override
                    public void onStart() {
                        newsAdaptor.clear();
                        emptyView.setText("");
                        getSupportLoaderManager().restartLoader(1, aa, MainActivity.this);
                        searchView.clearFocus();
                    }
                });

                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (searchView.getQuery().length() == 0) {
                    search(new SearchCallbackInterface() {
                        @Override
                        public void onStart() {
                            newsAdaptor.clear();
                            emptyView.setText("");
                            getSupportLoaderManager().restartLoader(1, null, MainActivity.this);
                        }
                    });
                }
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Helper function for checking internet connectivity
     *
     * @return true/false
     */
    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    /**
     * Setup and start/restart the loader base on the callback.
     *
     * @param callback SearchCallbackInterface to start/restart the loader
     */
    private void search(SearchCallbackInterface callback) {

        if (isConnected()) {
            loadingSpinner.setVisibility(View.VISIBLE);

            callback.onStart();
        } else {
            loadingSpinner.setVisibility(View.GONE);
            emptyView.setText(R.string.no_internet_connection);
        }
    }
}
