package com.example.ass_mystockwatch;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// This is each tiny container that need to be put into recycler view with the help of adapter
public class StockContainer extends RecyclerView.ViewHolder {
    private static final String TAG = "StockContainer";
    public TextView stockNomenclature;
    public TextView companyName;
    public TextView price;
    public TextView priceChange;
    public TextView changePerView;
    public TextView updown;

    public StockContainer(@NonNull View itemView) {
        super(itemView);
        stockNomenclature = itemView.findViewById(R.id.stockSymbol);
        companyName = itemView.findViewById(R.id.companyName);
        price = itemView.findViewById(R.id.currprice);
        priceChange = itemView.findViewById(R.id.changePrice);
        changePerView = itemView.findViewById(R.id.percChange);
        updown = itemView.findViewById(R.id.updown);
    }
}
