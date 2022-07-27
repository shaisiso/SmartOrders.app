package com.example.smartorders.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartorders.Broadcast.RemindertBroadcast;
import com.example.smartorders.Classes.DbOrder;
import com.example.smartorders.Classes.Order;
import com.example.smartorders.Classes.User;
import com.example.smartorders.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.HashMap;

public class NewReservationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private DatePickerDialog.OnDateSetListener setListener;
    private Button chooseDateBtn;
    private Spinner spinner;
    private Button submitBtn;
    private DatabaseReference reference;
    private FirebaseUser user;
    private DatabaseReference userReferance;
    private String fullDateWithTimeStamp = "";
    private String dateFetched;
    private String timeFetched;
    private EditText amountOfAttendeds;
    private TextView chooseDateTV;
    private ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reservation);

        // set action bar text and back btn option
        actionBar = getSupportActionBar();
        actionBar.setTitle("New Reservation");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // find views from layout xml
        chooseDateTV = findViewById(R.id.dateChosenET);
        chooseDateBtn = findViewById(R.id.chooseDateBtn);
        submitBtn = findViewById(R.id.submitReservationBtn);
        amountOfAttendeds = findViewById(R.id.numberOfPeopleET);
        spinner = findViewById(R.id.spinner);

        // get logged user from db
        user = FirebaseAuth.getInstance().getCurrentUser();
        userReferance = FirebaseDatabase.getInstance().getReference().child("users");
        reference = FirebaseDatabase.getInstance().getReference().child("Orders");


        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        // create spinner with pre-defined values (defined in values -> strings.xml
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.hours, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


        // choose date btn clicked
        chooseDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // show date picker
                DatePickerDialog datePickerDialog = new DatePickerDialog(NewReservationActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, setListener, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();

            }
        });


        setListener = new DatePickerDialog.OnDateSetListener() { // date picked listener
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = dayOfMonth + "-" + month + "-" + year;
                Log.i("Date", date);
                dateFetched = date;
                chooseDateTV.setText(dateFetched);

            }
        };


        /**
         * After submitting the data need to perform:
         * 1. Check if there is instance in firebase that matches the date with the time
         * 2. if so, check how many people already there in the order
         * 3. if there is a place, or there is no instance on firebase:
         *  - Create new instance (if there is no instance)
         *  - show the correct fragment (if yes or no)
         *
         */
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create the string from date and time
                StringBuilder sb = new StringBuilder();

                if (null == dateFetched || timeFetched.equals("Select") || amountOfAttendeds.getText().toString().isEmpty()) { // check validty
                    Toast toast = Toast.makeText(getApplicationContext(), "One of the fields is empty!!", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (isValidDate()) {
                    sb.append(dateFetched).append("T").append(timeFetched);
                    fullDateWithTimeStamp = sb.toString();


                    // check in db
                    reference.child(fullDateWithTimeStamp).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) { // there is  a reservation for the specific time and date
                                // getting the instance from the db
                                Object value = snapshot.getValue();

                                HashMap<String, String> orderMap = (HashMap<String, String>) value;
                                String amountOfPeople = "0";
                                amountOfPeople = orderMap.get("amountOfPeople");
                                User userLogged = User.getInstance();
                                HashMap<String, String> reservations = userLogged.getReservations();
                                int newAmount = Integer.parseInt(amountOfAttendeds.getText().toString()) + Integer.parseInt(amountOfPeople);;
                                int numOfReservations=Integer.parseInt(orderMap.get("amountOfReservation"));
                                if (!reservations.containsKey(fullDateWithTimeStamp)){
                                    //new reservation
                                    numOfReservations+=1;
                                }
                                else{
                                    //update existing reservation
                                    newAmount-=Integer.parseInt(reservations.get(fullDateWithTimeStamp));
                                }



                                if (newAmount > 50) { // restaurant full
                                    startActivity(new Intent(getApplicationContext(), SuccessFailureActivity.class).putExtra("IsComplete", "false"));
                                } else { // reservation can be saved

                                    orderMap.put("amountOfPeople",newAmount+"");
                                    orderMap.put("amountOfReservation",numOfReservations+"");
                                    reference.child(fullDateWithTimeStamp)
                                            .setValue(orderMap);

                                    reservations.put(fullDateWithTimeStamp, amountOfAttendeds.getText().toString());
                                    FirebaseDatabase.getInstance().getReference("Users")
                                            .child(user.getUid())
                                            .setValue(userLogged).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                }
                                            });
                                    createNotificationChannel();
                                    setAlarmReminder(day,month,year,Integer.parseInt(timeFetched));

                                    startActivity(new Intent(getApplicationContext(), SuccessFailureActivity.class).putExtra("IsComplete", "true"));
                                }


                            } else { // first reservation for that date
                                DbOrder order = new DbOrder(String.valueOf(amountOfAttendeds.getText().toString()), String.valueOf(1));
                                FirebaseDatabase.getInstance().getReference("Orders")
                                        .child(fullDateWithTimeStamp)
                                        .setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) { // if created the reservation use the successful activity -> fragment
                                                    if (Integer.parseInt(amountOfAttendeds.getText().toString()) > 10) {

                                                        startActivity(new Intent(getApplicationContext(), SuccessFailureActivity.class).putExtra("IsComplete", "false"));
                                                    } else {
                                                        User userLogged = User.getInstance();
                                                        HashMap<String, String> reservations = userLogged.getReservations();
                                                        reservations.put(fullDateWithTimeStamp, amountOfAttendeds.getText().toString());
                                                        Order o = new Order();
                                                        o.setAttendsNumber(amountOfAttendeds.getText().toString());
                                                        o.setOrderDate(fullDateWithTimeStamp);

                                                        FirebaseDatabase.getInstance().getReference("Users")
                                                                .child(user.getUid()).child("reservations")
                                                                .setValue(reservations).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                    }
                                                                });
                                                        createNotificationChannel();
                                                        setAlarmReminder(day,month,year,Integer.parseInt(timeFetched));

                                                        startActivity(new Intent(getApplicationContext(), SuccessFailureActivity.class).putExtra("IsComplete", "true"));
                                                    }


                                                }
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else { // the reservation date has passed
                    Toast toast = Toast.makeText(getApplicationContext(), "Failed: Please choose valid time", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    private void setAlarmReminder(int day,int month,int year,int hour) {
        // set alarm manager for timed notification
        Context context = getApplicationContext();
        Intent intent =new Intent(context, RemindertBroadcast.class);
        intent.setAction("Reminder");
        intent.putExtra("Message", "This is a reminder for your reservation today at "
                +hour+":00 for "+amountOfAttendeds.getText().toString()+" people.");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,hour,intent,PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        Calendar myAlarmDate = Calendar.getInstance();
        myAlarmDate.set(year,month-1,day,hour,0);
        long timeOfReservation = myAlarmDate.getTimeInMillis();
        long time3HoursMs = 3*3600* 1000;

        alarmManager.set(AlarmManager.RTC_WAKEUP, timeOfReservation-time3HoursMs,pendingIntent);
    }

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >Build.VERSION_CODES.O){
            CharSequence name = "ReminderChannel";
            String description = "Channel for reservation reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyReservation",name,importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }

    private boolean isValidDate() {
        String[] splitted = dateFetched.split("-"); // date format : dd-mm-yyyy
        int requestedDay = Integer.parseInt(splitted[0]);
        int requestedMonth = Integer.parseInt(splitted[1]);
        int requestedYear = Integer.parseInt(splitted[2]);
        int orderHour = Integer.parseInt(timeFetched);
        LocalDateTime timeNow = LocalDateTime.now();
        int currentYear = timeNow.getYear();
        int currentMonth = timeNow.getMonth().getValue();
        int currentDay = timeNow.getDayOfMonth();
        //
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
        return true;
    }


    // spinner save selected value
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.i("Spinner", parent.getItemAtPosition(position).toString());
        String timeSplited = parent.getItemAtPosition(position).toString().split(":")[0];
        if (timeSplited != null) {
            timeFetched = timeSplited;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

}
