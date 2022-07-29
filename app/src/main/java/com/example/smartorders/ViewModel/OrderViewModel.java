package com.example.smartorders.ViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartorders.Classes.Order;
import com.example.smartorders.Classes.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderViewModel extends ViewModel {


    private DatabaseReference reference;
    private FirebaseUser user;
    private MutableLiveData<List<Order>> mutableLiveData;
    private User userRef;
    private String userId;

    public LiveData<List<Order>> getOrderList() {

        if (mutableLiveData == null) {
            user = FirebaseAuth.getInstance().getCurrentUser();
            userId = user.getUid();
            mutableLiveData = new MutableLiveData<>();
            userRef = User.getInstance();
            List<Order> ordersList = new ArrayList<>();
            reference = FirebaseDatabase.getInstance().getReference().child("Users");
            reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Object value = snapshot.getValue();
                    User userLogged = User.getInstance();
                    if (value != null) {
                        HashMap<String, String> hash = (HashMap<String, String>) value;
                        userLogged.setFullName(hash.get("fullName"));
                        userLogged.setPhoneNumber(hash.get("phoneNumber"));
                        String keyValueFromRes = "";
                        String valueFromOld = "";
                        userLogged.setEmailAddress(hash.get("emailAddress"));
                        Object reser = hash.get("reservations");
                        if (null != reser) {
                            HashMap<String, String> reservationHash = (HashMap<String, String>) reser;
                            if (!reservationHash.isEmpty()) {
                                userLogged.setReservations(reservationHash);
                                for (String key : reservationHash.keySet()) {
                                    String amountOfAttendeds = reservationHash.get(key);
                                    Order order = new Order();
                                    order.setAttendsNumber(amountOfAttendeds);
                                    order.setOrderDate(key);
                                    ordersList.add(order);
                                }
                            }
                        }
                        ordersList.sort(Order::compareTo);
                        mutableLiveData.setValue(ordersList);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        return mutableLiveData;
    }

    public boolean deleteOrder(int position) {
        if (null != mutableLiveData.getValue()) {
            List<Order> orders = new ArrayList<>(mutableLiveData.getValue());
            Order orderToDelete = orders.get(position);
            String orderDate = orderToDelete.getOrderDate().split("T")[0];
            int orderHour = Integer.parseInt(orderToDelete.getOrderDate().split("T")[1]);
            String[] splitted = orderDate.split("-");
            int requestedDay = Integer.parseInt(splitted[0]);
            int requestedMonth = Integer.parseInt(splitted[1]);
            int requestedYear = Integer.parseInt(splitted[2]);

            LocalDateTime timeNow = LocalDateTime.now();
            int currentYear = timeNow.getYear();
            int currentMonth = timeNow.getMonth().getValue();
            int currentDay = timeNow.getDayOfMonth();
            int currentHour = timeNow.getHour();
            if (requestedYear < currentYear) { // if year is smaller  continue
                return false;
            } else if (requestedYear == currentYear) {
                if (requestedMonth < currentMonth) { // if month is smaller continue
                    return false;
                } else if (requestedMonth == currentMonth) {
                    if (requestedDay < currentDay) {
                        return false;
                    } else if (requestedDay == currentDay && orderHour <= currentHour) {
                        return false;
                    }
                }
            }

            orders.remove(position);
            mutableLiveData.setValue(orders);
            HashMap<String, String> orderHash = new HashMap<>();
            for (Order order : orders) {
                orderHash.put(order.getOrderDate(), order.getAttendsNumber());
            }
            //set the new list of reservation without the deleted one
            userRef.setReservations(orderHash);
            reference.child(userId).child("reservations").setValue(orderHash);
            return true;

        }
        return false;

    }
}
