package com.example.smartorders.Activities;

import android.os.Bundle;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartorders.Adapter.OrderAdapter;
import com.example.smartorders.Classes.Order;
import com.example.smartorders.Dialog.DialogFragment;
import com.example.smartorders.ViewModel.OrderViewModel;
import com.example.smartorders.R;

import java.util.List;

public class MyReservationsActivity extends AppCompatActivity implements OrderAdapter.OrderClickInterface, DialogFragment.OnInputListener {

    private OrderViewModel orderViewModel;
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private ActionBar actionBar;
    private Switch orderSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reservations);


        // set action bar text and back btn option
        actionBar = getSupportActionBar();
        actionBar.setTitle("My Reservations");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        recyclerView = findViewById(R.id.recycleView);
        orderSwitch = findViewById(R.id.orderSwitch);
        // register the recycle view adapter
        orderAdapter = new OrderAdapter(Order.itemCallback, this, getSupportFragmentManager(),
                orderSwitch.isChecked());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(orderAdapter);
        // get view model instance -> pass list of reservations to recycle view adapter
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        orderViewModel.getOrderList()
                .observe(this, orders -> orderAdapter.submitList(orders));
        orderSwitch.setOnClickListener(view -> {
            if (orderSwitch.isChecked())
                orderViewModel.sortDown();
            else
                orderViewModel.sortUp();
            orderAdapter.notifyDataSetChanged();
        });

    }

    // implementation of the recycle view interface (OrderClickInterface)
    @Override
    public void onDelete(int position) {
        if (!orderViewModel.deleteOrder(position)) {
            Toast toast = Toast.makeText(getApplicationContext(), "Can't delete reservation that has been passed", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    // implementation of the dialog fragment  (OnInputListener)
    @Override
    public void sendInput(String input, int position) {
        closeOptionsMenu();
        if (input.equals("YES") && !orderViewModel.deleteOrder(position)) {
            Toast toast = Toast.makeText(getApplicationContext(), "Can't delete reservation that has been passed", Toast.LENGTH_SHORT);
            toast.show();

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}