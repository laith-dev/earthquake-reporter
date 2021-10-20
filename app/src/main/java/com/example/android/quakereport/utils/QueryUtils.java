package com.example.android.quakereport.utils;

import android.util.Log;

import com.example.android.quakereport.data.Earthquake;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    private static String convertToDate(long timeInMillis) {
       /* Date date = new Date(timeInMillis);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM DD, yyyy");
        String dateToDisplay = dateFormatter.format(date); */

        return new SimpleDateFormat("MMM DD, yyyy").format(new Date(timeInMillis));
    }

    public static List<Earthquake> fetchEarthquakeData(String strUrl) {
        /* Simulate a slow internet connection by making the background thread sleeps (stops)
         * for 2 seconds.*/
        /*try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        URL url = createUrl(strUrl);

        String jsonResponse = "";
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e("TAG", "doInBackground: IOException " + e.getMessage());
        }

        List<Earthquake> earthquakeList = null;
        if (jsonResponse != null && !jsonResponse.isEmpty()) {
            earthquakeList = extractEarthquakeList(jsonResponse);
        }

        return earthquakeList;
    }

    /**
     * Return a list of {@link Earthquake} objects that has been built up from
     * parsing a JSON response.
     */
    public static List<Earthquake> extractEarthquakeList(String stringJson) {
        List<Earthquake> earthquakeList = new ArrayList<>();

        try {
            JSONObject rootJson = new JSONObject(stringJson);
            JSONArray featuresArr = rootJson.getJSONArray("features");

            for (int i = 0; i < featuresArr.length(); i++) {
                JSONObject currentFeature = featuresArr.getJSONObject(i);
                JSONObject propertiesObj = currentFeature.getJSONObject("properties");

                double mag = propertiesObj.getDouble("mag");
                String location = propertiesObj.getString("place");
                long time = propertiesObj.getLong("time");
                String url = propertiesObj.getString("url");

                earthquakeList.add(
                        new Earthquake(mag, location, time, url)
                );
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return earthquakeList;
    }


    /**
     * Returns new URL object from the given string URL.
     */
    public static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e("TAG", "createUrl: MalformedURLException " + e.getMessage());
        }

        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    public static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(0);
            urlConnection.setConnectTimeout(0);
            urlConnection.connect();

            /* If the request was successful (response code 200), read the input stream and get the
             * response. */
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e("TAG", "Problem making the HTTP request. Request Code: " +
                        urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }

        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    public static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader =
                    new InputStreamReader(inputStream, Charset.forName("UTF-8"));
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