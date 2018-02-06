package com.example.yu.news_bbc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by cpu11268-local on 05/02/2018.
 */

public class NewsAdapter extends ArrayAdapter<News> {
    public NewsAdapter(@NonNull Context context, List<News> news) {
        super(context, 0, news);
    }
    ImageView image;
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder = null;

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_news, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.author = (TextView) convertView.findViewById(R.id.author);
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.date = (TextView) convertView.findViewById(R.id.publishDate);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
            viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.progress);
            viewHolder.time = (TextView) convertView.findViewById(R.id.publishTime);
            convertView.setTag(viewHolder);
        }

        viewHolder = (ViewHolder) convertView.getTag();

        News currentNews = getItem(position);
        viewHolder.title.setText(currentNews.getTitle());
        String tempAuthor = currentNews.getAuthor().substring(0,4);
        if(currentNews.getAuthor().isEmpty() || currentNews.getAuthor().equals("null") || tempAuthor.equalsIgnoreCase("http")){
            viewHolder.author.setText("No Name");
        }else{
            viewHolder.author.setText(currentNews.getAuthor());
        }
        viewHolder.date.setText(formatDate(currentNews.getDate()));
        viewHolder.time.setText(formatTime(currentNews.getDate()));
        viewHolder.imageURL = currentNews.getUrlImage();

        DownloadImage downloadImage = new DownloadImage();
        downloadImage.execute(viewHolder);


        return convertView;
    }

    class ViewHolder{
        private TextView title;
        private TextView author;
        private TextView date;
        private ImageView image;
        private String imageURL;
        private Bitmap bitmap;
        private ProgressBar progressBar;
        private TextView time;
    }

    //Yu: format publish date to string
    private static String formatDate(String rawDate) {
        String jsonDatePattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        SimpleDateFormat jsonFormatter = new SimpleDateFormat(jsonDatePattern, Locale.US);
        try {
            Date parsedJsonDate = jsonFormatter.parse(rawDate);
            String finalDatePattern = "dd-MM-yyyy";
            SimpleDateFormat finalDateFormatter = new SimpleDateFormat(finalDatePattern, Locale.US);
            return finalDateFormatter.format(parsedJsonDate);
        } catch (ParseException e) {
            Log.e("QueryUtils", "Error parsing JSON date: ", e);
            return "";
        }
    }

    //Yu: format publish date to string
    private static String formatTime(String time) {
        String jsonDatePattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        SimpleDateFormat jsonFormatter = new SimpleDateFormat(jsonDatePattern, Locale.US);
        try {
            Date parsedJsonDate = jsonFormatter.parse(time);
            String finalDatePattern = "HH:mm";
            SimpleDateFormat finalDateFormatter = new SimpleDateFormat(finalDatePattern, Locale.US);
            return finalDateFormatter.format(parsedJsonDate);
        } catch (ParseException e) {
            Log.e("QueryUtils", "Error parsing JSON date: ", e);
            return "";
        }
    }

    private class DownloadImage extends AsyncTask<ViewHolder, Void, ViewHolder> {

        @Override
        protected ViewHolder doInBackground(ViewHolder... urls) {
            if(urls.length < 1 || urls[0] == null){
                return null;
            }
            Log.e("test", urls[0].imageURL);
            URL url = null;
            try {
                url = new URL(urls[0].imageURL);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;

            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                    urls[0].bitmap = myBitmap;
                    return urls[0];
                } else {
                    Log.e("Load image ", "Error reponse code: " + urlConnection.getResponseCode());
                }
            }catch (IOException e){
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(ViewHolder news) {
            news.progressBar.setVisibility(View.GONE);
            if(news.bitmap == null){
                news.image.setImageResource(R.drawable.ic_launcher_background);
            }else {
                news.image.setImageBitmap(news.bitmap);
            }
        }
    }
}
