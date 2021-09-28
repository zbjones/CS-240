package com.application.familymapclient.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.application.familymapclient.backend.DataCache;
import com.application.familymapclient.R;
import com.application.familymapclient.backend.Settings;

import java.util.Objects;

/**
 * Handles the UI and functionality implementation for the Settings Activity
 */
public class SettingsActivity extends AppCompatActivity {

    DataCache dataCache = DataCache.getDataCache();
    Settings settings = Settings.getSettings();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");

        //set home button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //Map switches to their names in the layout file
        Switch lifeStoryLinesSwitch = (Switch) findViewById(R.id.lifeStoryLinesSwitch);
        Switch familyTreeLinesSwitch = (Switch) findViewById(R.id.familyTreeLinesSwitch);
        Switch spouseLinesSwitch = (Switch) findViewById(R.id.spouseLinesSwitch);
        Switch fathersSideSwitch = (Switch) findViewById(R.id.fathersSideSwitch);
        Switch mothersSideSwitch = (Switch) findViewById(R.id.mothersSideSwitch);
        Switch maleEventsSwitch = (Switch) findViewById(R.id.maleEventsSwitch);
        Switch femaleEventsSwitch = (Switch) findViewById(R.id.femaleEventSwitch);
        LinearLayout logoutButton = (LinearLayout) findViewById(R.id.logoutLayout);

        //set all the switches to start checked.
        lifeStoryLinesSwitch.setChecked(settings.isLifeStoryLines());
        familyTreeLinesSwitch.setChecked(settings.isFamilyTreelines());
        spouseLinesSwitch.setChecked(settings.isSpouseLines());
        fathersSideSwitch.setChecked(settings.isFathersSide());
        mothersSideSwitch.setChecked(settings.isMothersSide());
        maleEventsSwitch.setChecked(settings.isMaleEvents());
        femaleEventsSwitch.setChecked(settings.isFemaleEvents());


        //Implement all of the listeners for user actions

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutButtonClicked();
            }
        });

        lifeStoryLinesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(getBaseContext(), "LifeStory Switch Toggled", Toast.LENGTH_SHORT).show();
                settings.setLifeStoryLines(isChecked);
                updateFilters();
            }
        });
        familyTreeLinesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(getBaseContext(), "FamilyTreeline Switch Toggled", Toast.LENGTH_SHORT).show();
                settings.setFamilyTreelines(isChecked);
                updateFilters();
            }
        });
        spouseLinesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(getBaseContext(), "SpouseLine Switch Toggled", Toast.LENGTH_SHORT).show();
                settings.setSpouseLines(isChecked);
                updateFilters();
            }
        });
        fathersSideSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(getBaseContext(), "Father's Side Switch Toggled", Toast.LENGTH_SHORT).show();
                settings.setFathersSide(isChecked);
                updateFilters();
            }
        });
        mothersSideSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(getBaseContext(), "Mother's Side Switch Toggled", Toast.LENGTH_SHORT).show();
                settings.setMothersSide(isChecked);
                updateFilters();
            }
        });
        maleEventsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(getBaseContext(), "Male Event Switch Toggled", Toast.LENGTH_SHORT).show();
                settings.setMaleEvents(isChecked);
                updateFilters();
            }
        });
        femaleEventsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(getBaseContext(), "Female Event Switch Toggled", Toast.LENGTH_SHORT).show();
                settings.setFemaleEvents(isChecked);
                updateFilters();
            }
        });

    }

    private void logoutButtonClicked() {
        Toast.makeText(getBaseContext(), "Logging out...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    //calls dataCache method to update the filtered content immediately. Called by each listener
    public void updateFilters() {
        dataCache.updateFilteredContent();
    }

    //Up button logic
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