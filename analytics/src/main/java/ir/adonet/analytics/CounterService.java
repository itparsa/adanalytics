package ir.adonet.analytics;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import static ir.adonet.analytics.Constants.ADO_NET;
import static ir.adonet.analytics.Constants.END_POINT;
import static ir.adonet.analytics.Constants.IS_COUNT_SUBMITTED;
import static ir.adonet.analytics.Constants.JSON_PARAM;

/**
 * Created by itparsa on 2/6/18.
 */

public class CounterService extends IntentService {

    private final String TAG = "counter_service";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public CounterService() {
        super("counter_service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            logAndEnd("something went wrong and null", false, null);
        } else {
            String json = bundle.getString(JSON_PARAM);
            startRequest(json, END_POINT);

        }
    }

    @SuppressLint("LogNotTimber")
    private void logAndEnd(String logMessage, boolean isError, Throwable throwable) {
        if (isError && throwable != null)
            Log.e(TAG, logMessage, throwable);
        else if (isError) {
            Log.e(TAG, logMessage);
        } else
            Log.d(TAG, logMessage);
        stopSelf();
    }

    private void startRequest(String json, String url) {
        HttpURLConnection connection = null;
        InputStream responseStream = null;
        String response = null;

        connection = createConnection(url);
        if (connection != null)
            responseStream = makeRequest(json, connection);
        else
            logAndEnd("connection was null", false, null);

        if (responseStream != null)
            response = parseResponse(responseStream);
        else
            logAndEnd("responseStream was null", false, null);

        if (response != null){
            Log.d(TAG, "final response received: "+response);
            saveFirstTime();
        }else
            logAndEnd("response was null", false, null);
    }

    private HttpURLConnection createConnection(String urlString) {
        URL url = null;
        HttpURLConnection connection = null;

        try {
            url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type","application/json");
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

        } catch (MalformedURLException e) {
            logAndEnd("malformed url", true, e);
        } catch (IOException e) {
            if (e instanceof ProtocolException) {
                logAndEnd("wrong protocol requested", true, e);
            } else
                logAndEnd("IO Connection Exception occured", true, e);
        }

        return connection;
    }

    private InputStream makeRequest(String json, HttpURLConnection connection) {
        OutputStream os = null;
        BufferedWriter writer = null;
        InputStream responseInputStream = null;
        try {
            os = connection.getOutputStream();
            writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            writer.write(json);


            writer.flush();
            writer.close();
            os.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK)
                responseInputStream = connection.getInputStream();
            else {
                logAndEnd("response was not '200'", false, null);
            }

        } catch (IOException e) {
            if (e instanceof UnsupportedEncodingException)
                logAndEnd("output stream encoding is wrong", true, e);
            else
                logAndEnd("io exception happened while sending data", true, e);
        }

        return responseInputStream;
    }

    @SuppressWarnings("StringBufferMayBeStringBuilder")
    private String parseResponse(InputStream stream) {
        BufferedReader in = new BufferedReader(new InputStreamReader(stream));
        StringBuffer sb = new StringBuffer("");
        String line = "";

        try {
            if ((line = in.readLine()) != null) {
                sb.append(line);
            }
            in.close();
        } catch (IOException e) {
            logAndEnd("io exception happened while receiving data", true, e);
        }

        if (sb.length() > 0)
            return sb.toString();
        else
            return null;
    }

    private void saveFirstTime(){
        getSharedPreferences(ADO_NET, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(IS_COUNT_SUBMITTED, true)
                .apply();
    }

}
