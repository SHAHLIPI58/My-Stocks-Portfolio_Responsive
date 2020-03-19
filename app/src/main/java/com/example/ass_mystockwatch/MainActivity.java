package com.example.ass_mystockwatch;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener, View.OnLongClickListener {
    private static final String TAG = "MainActivity";
    private MainActivity mainActivity;
    private SwipeRefreshLayout swiper;
    private RecyclerView recyclerView;

    private StocksAdapter stocksAdapter;
    private DatabaseHandler dbController;

    private final List<Stock> stockViewList = new ArrayList<>();
    private HashMap<String, String> map = new HashMap<String, String>();
    private final ArrayList<String[]> stockList = new ArrayList<>();
    private final String weburlStock = "http://www.marketwatch.com/investing/stock/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycler);
        stocksAdapter = new StocksAdapter(stockViewList, this);
        recyclerView.setAdapter(stocksAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        swiper = findViewById(R.id.swiper);
        dbController = new DatabaseHandler(this);
        if (checkNtwConnectivity()) {
            new NameDownloader(this).execute();
        } else {
            errorDialog("No Network Connection",
                    "Stocks Cannot Be Updated Without A Network Connection");
        }
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (checkNtwConnectivity() == true)
                    loadStocksFromDB();
                else {
                    errorDialog("No Network Connection",
                            "Stocks Cannot Be Updated Without A Network Connection");
                    swiper.setRefreshing(false);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        if (stockList.isEmpty()) {
            getDataFromDB();
        } else if (checkNtwConnectivity()) {
            loadStocksFromDB();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        dbController.shutDown();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.addmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addStock:
                if (!checkNtwConnectivity()) {
                    errorDialog("No Internet Connection",
                            "Stocks Cannot Be Updated Without A Network Connection");
                } else {
                    if (map.isEmpty()) {
                        new NameDownloader(this).execute();
                    }
                    stockSymbolSelDial();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        Stock stock = stockViewList.get(pos);
        String marketWatchUrl = weburlStock + stock.getStockSymbol();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(marketWatchUrl));
        startActivity(intent);
    }

    @Override
    public boolean onLongClick(View v) {
        final int pos = recyclerView.getChildLayoutPosition(v);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.baseline_delete_black_24);
        builder.setTitle("Delete Stock");
        builder.setMessage("Delete Stock Symbol " + stockViewList.get(pos).getStockSymbol() + "?");
        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dbController.deleteStock(stockViewList.get(pos).getStockSymbol());
                stockViewList.remove(pos);
                Collections.sort(stockViewList);
                stocksAdapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }

    public void getDataFromDB() {
        ArrayList<String[]> list = dbController.loadStocks();
        stockList.clear();
        stockList.addAll(list);
        stockViewList.clear();
        stocksAdapter.notifyDataSetChanged();
        for (int j = 0; j < list.size(); j++) {
            dbController.deleteStock(list.get(j)[0]);
        }
        for (int i = 0; i < list.size(); i++) {
            new StockDownloader(MainActivity.this)
                    .execute(list.get(i)[0].trim(), list.get(i)[1].trim());
        }
    }


    public void loadStocksFromDB() {

        ArrayList<String[]> list = dbController.loadStocks();
        stockViewList.clear();
        stocksAdapter.notifyDataSetChanged();

        for (int j = 0; j < list.size(); j++) {
            dbController.deleteStock(list.get(j)[0]);
        }

        for (int i = 0; i < list.size(); i++) {
            new StockDownloader(MainActivity.this).execute(list.get(i)[0].trim());
        }

        swiper.setRefreshing(false);
    }


    public void updateData(HashMap<String, String> sMap) {
        map.putAll(sMap);
//        if (map != null) {
//            Log.d(TAG, "updateData: Loaded " + map.size() + " stock symbols.");
//        }
    }

    public void updateFinanceData(Stock stock) {
        stockViewList.add(stock);
        dbController.addstock(stock);
        Collections.sort(stockViewList);
        stocksAdapter.notifyDataSetChanged();

    }

    // Check whether Network connectivity on or off
    public boolean checkNtwConnectivity() {
        ConnectivityManager connection_manager = (ConnectivityManager) getApplicationContext().
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connection_manager.getActiveNetworkInfo();

        if (netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()) {
            return false;
        }
        return true;
    }

    public void stockSymbolSelDial() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText inputPrompt = new EditText(this);
        inputPrompt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        inputPrompt.setFilters(new InputFilter[]{characterFilter}); // Capital character filter
        inputPrompt.setGravity(Gravity.CENTER);
        builder.setView(inputPrompt);
        builder.setTitle("Stock Selection");
        builder.setMessage("Please enter a Stock Symbol:");
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                openSelectionDialog(inputPrompt.getText().toString());
            }

        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    InputFilter characterFilter = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            String filtered = "";
            for (int i = start; i < end; i++) {
                char character = source.charAt(i);
                if (Character.isWhitespace(character) || Character.isLetter(character)) {
                    filtered += character;
                }
            }
            return filtered.toUpperCase();
        }

    };

    public void openSelectionDialog(String stockSymbol)
    {
        final ArrayList<String> selecArr = new ArrayList<>();

        for (String key : map.keySet()) {
            if (key.contains(stockSymbol.trim())) {
                selecArr.add(key + " - " + map.get(key));
            }
        }
        ///get stock names (values() of map)
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue().contains(stockSymbol.trim())) {
                selecArr.add(entry.getKey() + " - " + entry.getValue());
            }
        }

        // remove duplicate from arraylist
        Set<String> set = new HashSet<>(selecArr);
        selecArr.clear();
        selecArr.addAll(set);

        if (selecArr.size() == 0){
            errorDialog("Symbol Not Found: " + stockSymbol.trim(),
                        "Data for stock symbol");
        } else if (selecArr.size() == 1) {
            checkIdenticalStockExists(0, selecArr);
        } else if (selecArr.size() > 1) {
            Collections.sort(selecArr);
            CharSequence symChars[] = selecArr.toArray(new CharSequence[selecArr.size()]);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Make a selection");
            builder.setItems(symChars, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (checkNtwConnectivity())
                        checkIdenticalStockExists(which, selecArr);
                    else
                        getDataFromDB();
                }
            });
            builder.setNegativeButton("NEVERMIND", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public void errorDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void checkIdenticalStockExists(int which, ArrayList<String> selecArr) {
        //Checking duplicate Stocks
        Stock stock;
        ArrayList<String> symbolList = new ArrayList<>();
        for (int index = 0; index < stockViewList.size(); index++) {
            stock = stockViewList.get(index);
            symbolList.add(stock.getStockSymbol());
        }
        if (symbolList.contains(selecArr.get(which).split("-")[0].trim())) {
            alertingDialog(selecArr.get(which).split("-")[0].trim());
        } else {
            new StockDownloader(MainActivity.this).
                    execute(selecArr.get(which).split("-")[0].trim());
            //Refreshing the stocks while adding new one
            loadStocksFromDB();
        }
    }

    public void alertingDialog(String symbol) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.baseline_warning_24);
        builder.setTitle("Duplicate Stock");
        builder.setMessage("Stock Symbol " + symbol + " is already displayed");
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}