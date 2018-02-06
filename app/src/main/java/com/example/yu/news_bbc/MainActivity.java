package com.example.yu.news_bbc;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    NewsAdapter newsAdapter;
    SwipeRefreshLayout swipe;

    private static String createStringUrl(){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .encodedAuthority("newsapi.org")
                .encodedPath("v2/everything")
                .appendQueryParameter("sources", "bbc-sport")
                .appendQueryParameter("apiKey", "99303fb4c54b4ff58683100aa3511fa4");
        String url = builder.build().toString();
        return url;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupAdapter();

        swipe.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.textColorAuthor);
        swipe.setOnRefreshListener(this);

    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setupAdapter();
                swipe.setRefreshing(false);
            }
        }, 0);
    }

    private void setupAdapter() {

        ListView list = (ListView) findViewById(R.id.list);
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);

        NewsAsyncTask newsAsyncTask = new NewsAsyncTask();
        newsAsyncTask.execute(createStringUrl());

        newsAdapter = new NewsAdapter(this, new ArrayList<News>());
        list.setAdapter(newsAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                News news = newsAdapter.getItem(i);
                Uri newsUri = Uri.parse(news.getUrl());
                Intent website = new Intent(Intent.ACTION_VIEW, newsUri);
                startActivity(website);
            }
        });
    }

    private class NewsAsyncTask extends AsyncTask<String, Void, List<News>> {

        @Override
        protected List<News> doInBackground(String... urls) {
            if(urls.length < 1 || urls[0] == null){
                return null;
            }
            List<News> result = QueryUltils.fetchBookData(urls[0]);
            return result;
        }

        @Override
        protected void onPostExecute(List<News> news) {
            newsAdapter.clear();

            if(news != null && !news.isEmpty()){
                newsAdapter.addAll(news);
            }
        }
    }


}
