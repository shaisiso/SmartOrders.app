package com.example.smartorders.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.smartorders.Framgents.ResturantFullFragment;
import com.example.smartorders.Framgents.SuccessfulFragment;
import com.example.smartorders.R;

public class SuccessFailureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_failure);
        Intent intent = getIntent();
        String isComplete = intent.getStringExtra("IsComplete");
        if(isComplete.equals("true")){ // if reservation saved show success fragment
            replaceFragment(new SuccessfulFragment());
        }else{ // show failure fragment
            replaceFragment(new ResturantFullFragment());
        }

    }


    public void replaceFragment(Fragment frag){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,frag);
        fragmentTransaction.commit();

    }
}