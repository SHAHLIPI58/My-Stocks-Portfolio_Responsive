package com.example.ass_mystockwatch;

// stock class with Comparable interface

public class Stock implements Comparable<Stock>   {

    public String stockSymbol;
    public String nameOfCompany;
    public double price;
    public double changeInPrice ;
    public double changeInPercentage;


    // ALT + INSERT
    public Stock(String stockSymbol, String nameOfCompany, double price, double changeInPrice, double changeInPercentage) {
        this.stockSymbol = stockSymbol;
        this.nameOfCompany = nameOfCompany;
        this.price = price;
        this.changeInPrice = changeInPrice;
        this.changeInPercentage = changeInPercentage;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public String getNameOfCompany() {
        return nameOfCompany;
    }

    public void setNameOfCompany(String nameOfCompany) {
        this.nameOfCompany = nameOfCompany;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getChangeInPrice() {
        return changeInPrice;
    }

    public void setChangeInPrice(double changeInPrice) {
        this.changeInPrice = changeInPrice;
    }

    public double getChangeInPercentage() {
        return changeInPercentage;
    }

    public void setChangeInPercentage(double changeInPercentage) {
        this.changeInPercentage = changeInPercentage;
    }

    @Override
    public int compareTo(Stock o) {
        // sort accordingly company symbol..
        return getStockSymbol().compareTo(o.getStockSymbol());
    }
}


