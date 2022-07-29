package com.example.smartorders.Adapter;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartorders.Classes.Order;
import com.example.smartorders.Dialog.DialogFragment;
import com.example.smartorders.R;

import java.time.LocalDateTime;

public class OrderAdapter extends ListAdapter<Order, OrderAdapter.OrderViewHolder> {
    OrderClickInterface orderClickInterface;
    FragmentManager fm;
    boolean showAllOrders;

    // adapter constructor -> set inter
    public OrderAdapter(@NonNull DiffUtil.ItemCallback<Order> diffCallback, OrderClickInterface orderClickInterface, FragmentManager fm, boolean showAllOrders) {
        super(diffCallback);
        this.orderClickInterface = orderClickInterface;
        this.fm = fm;
        this.showAllOrders = showAllOrders;
    }


    class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView dateTextView, attendedTextView, hour;
        ImageButton deleteBtn;
        Switch orderSwitch;

        // connect variables to xml, set on click method
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateOfReservation);
            attendedTextView = itemView.findViewById(R.id.attendedNumbers);
            hour = itemView.findViewById(R.id.timeOfReservation);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            deleteBtn.setOnClickListener(v -> { // clicking on bin btn to erase the specific item
                DialogFragment dialogFragment = new DialogFragment();
                Bundle args = new Bundle();
                args.putInt("position", getAdapterPosition());
                args.putString("Type", "Delete");
                dialogFragment.setArguments(args);
                dialogFragment.show(fm, "Custom");

            });

        }

        // bind values
        public void bind(Order order) {
            String dateUnparsed = order.getOrderDate();
            String dateParsed = dateUnparsed.split("T")[0];
            String timeParsed = dateUnparsed.split("T")[1] + ":00";
            dateTextView.setText(dateParsed);
            hour.setText(timeParsed);
            attendedTextView.setText(order.getAttendsNumber());
            LocalDateTime orderDate = order.getOrderLocalDate(order);
            LocalDateTime now = LocalDateTime.now();

                if (orderDate.compareTo(now) < 0)
                    itemView.setBackgroundColor(Color.GRAY);
                else
                    itemView.setBackgroundColor(0x0);

        }


    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    // define interface
    public interface OrderClickInterface {
        public void onDelete(int position);
    }
}
