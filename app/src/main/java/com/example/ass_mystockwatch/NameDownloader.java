package com.example.ass_mystockwatch;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

// this class is for Async Task
public class NameDownloader extends AsyncTask<String, Void, String> {
    private static final String TAG = "NameDownloader";
    private MainActivity mainActivity;
    //link of share market symbol and name of company
    private final String stocksymbolUrl = "https://api.iextrading.com/1.0/ref-data/symbols";
    private HashMap<String, String> symbolnameHashMap = new HashMap<String, String>();

    //constructor to get reference of MainActivity
    public NameDownloader(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected String doInBackground(String... strings) {
        String res = getURLData(stocksymbolUrl);
        return res;
    }

    @Override
    protected void onPostExecute(String s) {
        HashMap<String, String> symbnameMap = parseUrlJsonString(s);
        mainActivity.updateData(symbnameMap);
    }


    private String getURLData(String sUrl) {
        Uri getData = Uri.parse(sUrl); // parse Url to get perfect string if in case of space %20
        String urlLink = getData.toString();
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlLink);
            // get connection and set Get method on top of it
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            
            // get input stream
            InputStream inputS = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(inputS)));
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getURLData: "+sb.toString() );
        return sb.toString();
    }


    private HashMap<String, String> parseUrlJsonString(String s) {
        try{
            JSONArray jObjMain = new JSONArray(s);
            for (int i = 0; i < jObjMain.length(); i++){
                JSONObject jsonS = (JSONObject) jObjMain.get(i);
                String symbol = jsonS.getString("symbol");
                String name = jsonS.getString("name");
                symbolnameHashMap.put(symbol, name);
            }
            return symbolnameHashMap;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}

