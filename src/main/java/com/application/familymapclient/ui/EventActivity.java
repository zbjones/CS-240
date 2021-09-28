package com.application.familymapclient.ui;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.application.familymapclient.R;
import com.application.familymapclient.ui.fragments.MapFragment;

/**
 * Handles the implementation of a full screen event activity
 */
public class EventActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        //get and set the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.eventToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Event Details");

        switchToMap();
    }

    //Handles logic to open the map fragment in the Event Activity
    public void switchToMap() {
        MapFragment eventMapFragment = new MapFragment(false);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.eventActivityMapFragment, eventMapFragment);
        fragmentTransaction.addToBackStack("eventMapFragment");

        fragmentTransaction.commit();
    }

    //Implement back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }
}