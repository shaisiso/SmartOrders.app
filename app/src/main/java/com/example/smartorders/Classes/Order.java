package com.example.smartorders.Classes;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Order implements Comparable<Order>{
    public String orderDate;
    public String attendsNumber;


        public static DiffUtil.ItemCallback<Order> itemCallback = new DiffUtil.ItemCallback<Order>() {
        @Override
        public boolean areItemsTheSame(@NonNull Order oldItem, @NonNull Order newItem) {
            return oldItem.orderDate.equals(newItem.orderDate);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Order oldItem, @NonNull Order newItem) {
            return oldItem.orderDate.equals(newItem.orderDate);
        }
    };

    public String getOrderDate() { return orderDate; }

    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }

    public String getAttendsNumber() { return attendsNumber; }

    public void setAttendsNumber(String attendsNumber) { this.attendsNumber = attendsNumber; }

    @Override
    public int compareTo(Order order) {
        LocalDateTime thisOrderDate =  getOrderLocalDate(this);
        LocalDateTime otherOrderDate =  getOrderLocalDate(order);
        return thisOrderDate.compareTo(otherOrderDate);
    }

    public LocalDateTime getOrderLocalDate(Order order){
        String dateSplited []= order.getOrderDate().split("T");
        String dateFormated = dateSplited[0]+" "+dateSplited[1]+":00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-M-yyyy HH:mm");
        LocalDateTime orderLocalDate = LocalDateTime.parse(dateFormated, formatter);
        return orderLocalDate;
    }
}
