package com.example.android.news;

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
 * Created by Prajak Khruasuwan on 11/12/17.
 * Utitlity class for fetching api.
 */

public final class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getName();
    private static final String NYT_HOST_URL = "http://www.nytimes.com/";
    private static final int READ_TIMEOUT = 10000;
    private static final int TIMEOUT = 15000;
    private static final int RESPONSE_OK = 200;

    private QueryUtils() {
        //An empty private constructor makes sure that the class is not going to be initialised.
    }

    /**
     * Query the Google dataset and return a list of {@link NYTNews} objects.
     */
    public static List<NYTNews> fetchNewsData(String url) {

        if (url == null) return null;

        URL google_url = createUrl(url);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(google_url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        return extractNewsFromJson(jsonResponse);
    }

    /**
     * Return a list of {@link NYTNews} objects that has been built up from
     * parsing a JSON response.
     */
    private static ArrayList<NYTNews> extractNewsFromJson(String newsJSON) {

        // Create an empty ArrayList that we can start adding books to
        ArrayList<NYTNews> news = new ArrayList<>();

        if (newsJSON == null) return null;

        try {
            // build up a list of Book objects with the corresponding data.
            JSONObject jsonObject = new JSONObject(newsJSON);

            JSONObject response = jsonObject.optJSONObject("response");
            if (response != null) {
                JSONArray docs = response.optJSONArray("docs");
                // loop through each news
                for (int i = 0; i < docs.length(); i++) {

                    JSONObject doc = docs.getJSONObject(i);
                    //get headline ==> title
                    JSONObject headline = doc.getJSONObject("headline");
                    String title = headline.getString("main");

                    //get snippet ==> description
                    String description = doc.getString("snippet");

                    //get original ==> source
                    JSONObject byline = doc.optJSONObject("byline");
                    String source = "";
                    if (byline != null) {
                        source = byline.getString("original");
                    }

                    //get pub_date => publishDate
                    String publishDate = doc.optString("pub_date");

                    //get web_url ==> link to original
                    String url = doc.getString("web_url");
                    //get img url if there is any
                    JSONArray multimedia = doc.optJSONArray("multimedia");
                    String imageUrl = "";
                    for (int j = 0; j < multimedia.length(); j++) {
                        JSONObject media = multimedia.getJSONObject(j);
                        if (media.getString("type").equals("image") && media.getString("subtype").equals("wide")) {
                            imageUrl = NYT_HOST_URL + media.getString("url");
                            break;
                        }
                    }
                    news.add(new NYTNews(title, description, imageUrl, url, source, publishDate));
                }
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the book JSON results", e);
        }

        // Return the list of books
        return news;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(READ_TIMEOUT /* milliseconds */);
            urlConnection.setConnectTimeout(TIMEOUT /* milliseconds */);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == RESPONSE_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Response is bad!" + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Response is bad!" + e);
            jsonResponse = "";
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
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
}
