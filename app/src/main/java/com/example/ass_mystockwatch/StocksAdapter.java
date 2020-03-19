package com.example.ass_mystockwatch;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StocksAdapter extends RecyclerView.Adapter<StockContainer> {

    private static final String TAG = "StocksAdapter";
    private List<Stock> stockTable;
    private MainActivity mainActivity;

    public StocksAdapter(List<Stock> stockTable, MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.stockTable = stockTable;
    }

    @NonNull
    @Override
    public StockContainer onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.stock_view, viewGroup, false);
        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);
        return new StockContainer(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull StockContainer holder, int position) {
        Stock stock = stockTable.get(position);
        Log.d(TAG, "onBindViewHolder: " + stock.getChangeInPrice());
        if (stock.getChangeInPrice() > 0) {
            holder.updown.setTextColor(Color.parseColor("#00ff00"));
            holder.updown.setText("▲");
            changeTextColor("#00ff00", stock, holder); //green
        } else if (stock.getChangeInPrice() < 0) {
            holder.updown.setTextColor(Color.parseColor("#ff0000"));
            holder.updown.setText("▼");
            changeTextColor("#ff0000", stock, holder); //red
        } else {
            holder.updown.setText("");
            holder.priceChange.setText(String.format("%.2f", stock.getChangeInPrice()));
            changeTextColor("#ffffff", stock, holder); // white if no changes
        }
        holder.stockNomenclature.setText(stock.getStockSymbol());
        holder.companyName.setText(stock.getNameOfCompany());
        holder.price.setText(Double.toString(stock.getPrice()));
        holder.priceChange.setText(String.format("%.2f", stock.getChangeInPrice()));
        holder.changePerView.setText("(" + String.format("%.2f", stock.getChangeInPercentage()) + "%)");
    }

    public void changeTextColor(String colorCode, Stock stock, StockContainer holder) {
        holder.stockNomenclature.setTextColor(Color.parseColor(colorCode));
        holder.companyName.setTextColor(Color.parseColor(colorCode));
        holder.price.setTextColor(Color.parseColor(colorCode));
        holder.priceChange.setTextColor(Color.parseColor(colorCode));
        holder.changePerView.setTextColor(Color.parseColor(colorCode));
    }

    @Override
    public int getItemCount() {
        return stockTable.size();
    }
}

