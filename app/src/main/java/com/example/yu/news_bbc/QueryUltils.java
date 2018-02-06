package com.example.yu.news_bbc;

import android.net.Uri;
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
import java.util.List;

/**
 * Created by cpu11268-local on 05/02/2018.
 */

public class QueryUltils {
    private static final String LOG_TAG = QueryUltils.class.getSimpleName();

    //Constructor
    private QueryUltils(){
    }

    // Return URL object from URL string
    private static URL createUrl(String mUrl){
        URL url = null;
        try{
            url = new URL(mUrl);
        }catch(MalformedURLException ex){
            Log.e(LOG_TAG, "Lỗi khi build URL");
        }
        return url;
    }

    //Convert Inputstream thành String
    private static String readFromStream(InputStream inputStream) throws IOException{
        StringBuilder output = new StringBuilder();
        if(inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null){
                output.append(line);
                line = bufferedReader.readLine();
            }
        }
        return output.toString();
    }

    //Nhận 1 URL và trả về 1 chuỗi String từ reponse của URL đó.
    private static String makeHttpRequest(URL url) throws IOException{
        String jsonResponse = "";
        //Kiểm tra null URL
        if(url == null){
            return jsonResponse;
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
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error reponse code: " + urlConnection.getResponseCode());
            }
        }catch(IOException e){
            Log.e(LOG_TAG, "Error when retrieving news Json reponse: " , e);
        }finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            if(inputStream != null){
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    //Trả về 1 list book sau khi parsing chuỗi string từ JSON reponse
    private static List<News> extractFeatureFromJson(String newsJson){
        List<News> listOfNews = new ArrayList<>();
        //Yu: If String bookJson is null or empty, return null
        if(TextUtils.isEmpty(newsJson)){
            return null;
        }
        try{
            JSONObject baseJsonReponse = new JSONObject(newsJson);

            JSONArray newsArray = baseJsonReponse.getJSONArray("articles");

            for(int i = 0; i < newsArray.length(); i++){
                JSONObject currentNews = newsArray.getJSONObject(i);


                String title = currentNews.getString("title");

                String author = currentNews.getString("author");

                String publishDate = currentNews.getString("publishedAt");

                String image = currentNews.getString("urlToImage");

                String url = currentNews.getString("url");

                News news = new News(author, title, url, image, publishDate);

                listOfNews.add(news);
            }
        }catch(JSONException e){
            Log.e(LOG_TAG, "Problem parsing Json reponse results: ", e);
        }
        return listOfNews;
    }


    public static List<News> fetchBookData(String result){
        URL url = createUrl(result);
        String jsonReponse = null;
        try {
            //return String when inputstream return
            jsonReponse =  makeHttpRequest(url);
        }catch (IOException e){
            Log.e(LOG_TAG, "Problem when making http request", e);
        }
        //Yu: return list book when parsing String from JSON reponse
        List<News> listOfNews = extractFeatureFromJson(jsonReponse);

        return listOfNews;
    }
}
