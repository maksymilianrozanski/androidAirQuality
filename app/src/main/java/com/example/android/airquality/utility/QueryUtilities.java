package com.example.android.airquality.utility;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class QueryUtilities {

    private static final String LOG_TAG = QueryUtilities.class.getSimpleName();

    private QueryUtilities() {
    }

    public static String retryMakingHttpRequestIfException(URL url) throws IOException {
        String jsonResponse;
        for (int i = 0; i < 5; ) {
            try {
                jsonResponse = makeHttpRequest(url);
                return jsonResponse;
            } catch (IOException e) {
                i++;
            }
        }
        Log.e(LOG_TAG, "http request not succeed, after retrying.");
        throw new IOException();
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
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

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the JSON results.");
            throw e;
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

    public static String getStringFromJSONObject(JSONObject jsonObject, String jsonKey) {
        String stringToReturn;
        try {
            if (jsonObject.getString(jsonKey) != null) {
                stringToReturn = jsonObject.getString(jsonKey);
            } else {
                stringToReturn = "not specified";
            }
        } catch (JSONException e) {
            Log.v("getStringFromJSONObject", "JSONException when passing string");
            stringToReturn = "not specified";
            return stringToReturn;
        }
        return stringToReturn;
    }
}
