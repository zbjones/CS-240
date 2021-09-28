package com.application.familymapclient.ui;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.application.familymapclient.R;
import com.application.familymapclient.ui.fragments.LoginFragment;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;


public class MainActivity extends AppCompatActivity {

    private LoginFragment loginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("MainActivity", "onCreate(Bundle savedInstanceState) called");
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        Iconify.with(new FontAwesomeModule());

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        //Host login fragment by default in the main Activity, switch to MapFragment from LoginFragment if success
        if (fragment == null) {
            loginFragment = new LoginFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, loginFragment).commit();
        }
    }


}