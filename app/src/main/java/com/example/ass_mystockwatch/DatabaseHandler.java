package com.example.ass_mystockwatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "StockAppDB";
    private static final String TABLE_NAME = "StockWatchTable";
    private static final int DATABASE_VERSION = 1;
    private static final String SYMBOL = "StockSymbol";
    private static final String COMPANY = "CompanyName";

    private static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
            " (" + SYMBOL + " TEXT not null unique," + COMPANY + " TEXT not null)";

    private SQLiteDatabase database;

    //default constructor
    public DatabaseHandler(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        database = getWritableDatabase();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //close the connection
    public void shutDown()
    {
        database.close();
    }

   // add the stock in contentValue object (Hashmap)
    public void addstock(Stock stock){
        ContentValues values=new ContentValues();
        values.put(SYMBOL,stock.getStockSymbol());
        values.put(COMPANY,stock.getNameOfCompany());
        database.insert(TABLE_NAME,null,values);
    }

    // delete stock
    public void deleteStock(String stock_symbol)
    {
        int numRows = database.delete(TABLE_NAME,SYMBOL+" =?",new String[]{stock_symbol});
    }

    //load stock
    public ArrayList<String[]> loadStocks(){
        ArrayList<String[]> stocks= new ArrayList<>();
        Cursor cursor = database.query(TABLE_NAME, new String[]{SYMBOL, COMPANY}, null, null, null, null, SYMBOL);
        if (cursor != null)
        {
            cursor.moveToFirst();

            for (int j = 0; j < cursor.getCount(); j++)
            {
                String symbol = cursor.getString(0);
                String company = cursor.getString(1);

                stocks.add(new String[]{symbol, company});
                cursor.moveToNext();
            }
            cursor.close();
        }
        return stocks;
    }


}
