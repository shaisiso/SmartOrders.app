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
        //String strHour =hour -3 <10 ? "0"+( hour -3) : (hour -3)+"";
        String d1Splited []= this.getOrderDate().split("T");
        String d2Splited []= order.getOrderDate().split("T");

        String d1 = d1Splited[0]+" "+d1Splited[1]+":00";
        String d2 = d2Splited[0]+" "+d2Splited[1]+":00";

        LocalDateTime order1Date = LocalDateTime.parse(d1, DateTimeFormatter.ofPattern("d-M-yyyy HH:mm"));
        LocalDateTime order2Date = LocalDateTime.parse(d2, DateTimeFormatter.ofPattern("d-M-yyyy HH:mm"));
        return order1Date.compareTo(order2Date);
    }
}
