package com.example.ass_mystockwatch;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class StockDownloader extends AsyncTask<String, Void, String> {
    private MainActivity mainActivity;

    // My API KEY = pk_245cca82bab94c7b810ff14f707af946
    private final java.lang.String Url1 = "https://cloud.iexapis.com/stable/stock/";
    private final java.lang.String Url2 = "/quote?token=Your_API_KEY_HERE"; //Your_API_KEY_HERE can be generated from https://iexcloud.io/docs/api/
    private ArrayList<String> offlineContent = new ArrayList<>();
    private static final String TAG = "StockDownloader";

    // Constructor to get reference of MainActivity
    public StockDownloader(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected String doInBackground(String... strings) {

        if (strings.length == 2) {
            offlineContent.add(strings[0]);
            offlineContent.add(strings[1]);
        }
        Uri dataUri = Uri.parse(Url1 + strings[0] + Url2);
        String urlToUse = dataUri.toString();
        StringBuilder sb = new StringBuilder();

        try {
            URL url = new URL(urlToUse);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "doInBackground: ", e);
            return null;
        }
        return sb.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        if (s != null) {
            Stock stock = parseJSON(s);
            mainActivity.updateFinanceData(stock);
        } else {
            if (offlineContent.size() == 2) {
                Stock stock;
                stock = new Stock(offlineContent.get(0), offlineContent.get(1), 0, 0, 0);
                mainActivity.updateFinanceData(stock);

            }
        }
    }

    protected Stock parseJSON(String s){ //parse json string and convert to Stock Object
        try{

            JSONObject jFinance = new JSONObject(s);
            String symbol = jFinance.getString("symbol");
            String name = jFinance.getString("companyName");

            String latest = jFinance.getString("latestPrice");
            double latestPrice = 0.0;
            if (latest != null && !latest.trim().isEmpty() && !latest.trim().equals("null")){
                latestPrice = Double.parseDouble(latest.trim());
            }


            String ch = jFinance.getString("change");
            double change = 0.0;
            if (ch != null && !ch.trim().isEmpty() && !ch.trim().equals("null")){
                change = Double.parseDouble(ch.trim());
            }


            String chP = jFinance.getString("changePercent");
            double changePercent = 0.0;
            if (chP != null && !chP.trim().isEmpty() && !chP.trim().equals("null")){
                changePercent = Double.parseDouble(chP.trim());
            }

            Stock stock = new Stock(symbol, name, latestPrice, change, changePercent);
            return stock;

        }catch (Exception e) {
            Log.d(TAG, "parseJSON: error");
        }
        return null;
    }
}
