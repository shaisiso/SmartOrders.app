package com.example.smartorders.Activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.os.StrictMode;
import android.util.CloseGuard;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartorders.Broadcast.BroadCastRec;
import com.example.smartorders.Classes.Order;
import com.example.smartorders.Classes.User;
import com.example.smartorders.Dialog.DialogFragment;
import com.example.smartorders.ForeGround.Service;
import com.example.smartorders.ViewModel.OrderViewModel;
import com.example.smartorders.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Closeable;
import java.util.HashMap;
import java.util.List;

public class HomePageActivity extends AppCompatActivity implements DialogFragment.OnInputListener {
    private OrderViewModel orderViewModel;
    private FirebaseUser user;
    private DatabaseReference reference;
    private Button newRes;
    private Button allRes;
    private ActionBar actionBar;
    private BroadCastRec broadCastRec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Home");


        // find views from layout xml
        newRes = findViewById(R.id.newReservationBtn);
        allRes = findViewById(R.id.allReservationButtons);

        // get user instance from db
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        String userId = user.getUid(); // user unique id

        // register broadcast receiver
        IntentFilter broad = new IntentFilter("Broad");
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        LiveData<List<Order>> orderList = orderViewModel.getOrderList();
        broadCastRec = new BroadCastRec(orderList);
        registerReceiver(broadCastRec, broad);


        // register service
        Intent serviceIntent = new Intent(this, Service.class);
        if (!isMyServiceRunning(Service.class)) { // if service not running -> start
            startService(serviceIntent);
        }

        // initialize user class
        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Object value = snapshot.getValue();
                HashMap<String, String> hash = (HashMap<String, String>) value;
                String emailAddress = hash.get("emailAddress");
                String userName = hash.get("fullName");
                String phoneNumber = hash.get("phoneNumber");
                Object reservationObj = hash.get("reservations");
                HashMap<String, String> reservations = new HashMap<>();
                if (null != reservationObj) { // if has reservations -> get reservations
                    reservations = (HashMap<String, String>) reservationObj;
                }
                // initialize user class
                User user = User.getInstance(); // first instance
                user.setPhoneNumber(phoneNumber);
                user.setEmailAddress(emailAddress);
                user.setFullName(userName);
                user.setReservations(reservations);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // new reservation btn clicked
        newRes.setOnClickListener(v -> startActivity(new Intent(getBaseContext(), NewReservationActivity.class)));

        // show all reservations btn clicked
        allRes.setOnClickListener(v -> startActivity(new Intent(getBaseContext(),
                MyReservationsActivity.class)));
    }

    // initialize option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_x, menu);
        return true;
    }

    // define action when clicked on options
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settingsBtn:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;

            case R.id.logoutItem:
                FragmentManager fm1 = getSupportFragmentManager();
                DialogFragment dialogFragment1 = new DialogFragment();
                Bundle args1 = new Bundle();
                args1.putString("Type", "Logout");
                dialogFragment1.setArguments(args1);
                dialogFragment1.show(fm1, "Custom");
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    // dialog fragment listener implementation -> get back the answer
    @Override
    public void sendInput(String input, int position) {
        if (input == "YES") {
            FirebaseAuth.getInstance().signOut();
            finish();
        }
    }

    // check weather the desired service already running
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}