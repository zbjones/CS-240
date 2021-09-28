package com.application.familymapclient.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.application.familymapclient.R;
import com.application.familymapclient.tasks.LoginTask;
import com.application.familymapclient.tasks.RegisterTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Requests.LoginRequest;
import Requests.RegisterRequest;

/**
 * Fragment that handles both logging and registering as a new user
 */
public class LoginFragment extends Fragment {

    private static final String LOGIN_RESULT_KEY = "LoginResultKey";
    private static final String REGISTER_RESULT_KEY = "RegisterResultKey";

    private Button loginButton;
    private Button registerButton;

    private Button maleButton;
    private Button femaleButton;

    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    private EditText ipAddress;
    private EditText serverPort;
    private EditText username;
    private EditText password;
    private EditText firstName;
    private EditText lastName;
    private EditText email;

    private String userGender = "";

    private TextWatcher textWatcher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        textWatcher = new TextWatcherWithChecks();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        ipAddress = view.findViewById(R.id.serverHostInput);
        ipAddress.addTextChangedListener(textWatcher);

        serverPort = view.findViewById(R.id.serverPortInput);
        serverPort.addTextChangedListener(textWatcher);

        username = view.findViewById(R.id.usernameInput);
        username.addTextChangedListener(textWatcher);

        password = view.findViewById(R.id.passwordInput);
        password.addTextChangedListener(textWatcher);

        firstName = view.findViewById(R.id.firstNameInput);
        firstName.addTextChangedListener(textWatcher);

        lastName = view.findViewById(R.id.lastNameInput);
        lastName.addTextChangedListener(textWatcher);

        email = view.findViewById(R.id.emailInput);
        email.addTextChangedListener(textWatcher);

        maleButton = view.findViewById(R.id.maleButton);
        maleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userGender = "m";
                checkButtonsEnabled();
            }
        });

        femaleButton = view.findViewById(R.id.femaleButton);
        femaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userGender = "f";
                checkButtonsEnabled();
            }
        });

        loginButton = view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    loginRequest = new LoginRequest(username.getText().toString(), password.getText().toString());

                    @SuppressLint("HandlerLeak") Handler uiThreadMessageHandler = new Handler() {
                        @Override
                        public void handleMessage(Message message) {
                            Bundle bundle = message.getData();
                            String success = bundle.getString(LOGIN_RESULT_KEY, "");
                            //login fail
                            if (success.contains("Failure") || success.contains("error") || success.contains("ERROR")) {
                                Toast.makeText(getContext(), "Sign-in failure: " + success, Toast.LENGTH_SHORT).show();
                            }
                            //login success
                            else {
                                Toast.makeText(getContext(), success, Toast.LENGTH_SHORT).show();
                                switchToMap();
                            }
                        }
                    };
                    //logic to run loginTask in a separate thread
                    LoginTask loginTask = new LoginTask(uiThreadMessageHandler, ipAddress.getText().toString(),
                            serverPort.getText().toString(), loginRequest);
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.submit(loginTask);

                    Toast.makeText(getContext(), "Sending Login Request", Toast.LENGTH_SHORT).show();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });

        registerButton = view.findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    registerRequest = new RegisterRequest(username.getText().toString(), password.getText().toString(),
                            email.getText().toString(), firstName.getText().toString(), lastName.getText().toString(), userGender);

                    @SuppressLint("HandlerLeak") Handler uiThreadMessageHandler = new Handler() {
                        @Override
                        public void handleMessage(Message message) {
                            Bundle bundle = message.getData();
                            String success = bundle.getString(REGISTER_RESULT_KEY, "");
                            //register fail case
                            if (success.contains("Failure") || success.contains("ERROR")) {
                                Toast.makeText(getContext(), "Register failure: " + success, Toast.LENGTH_SHORT).show();
                            }
                            //register success case
                            else {
                                Toast.makeText(getContext(), success, Toast.LENGTH_SHORT).show();
                                switchToMap();
                            }
                        }
                    };
                    //Logic to run registerTask in a separate thread
                    RegisterTask registerTask = new RegisterTask(uiThreadMessageHandler, ipAddress.getText().toString(),
                            serverPort.getText().toString(), registerRequest);
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.submit(registerTask);
                    Toast.makeText(getContext(), "Sending Register Request", Toast.LENGTH_SHORT).show();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkButtonsEnabled();
    }

    private class TextWatcherWithChecks implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkButtonsEnabled();
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    //Calls canLogin and canRegister helper functions to make sure all of the required fields are populated
    private void checkButtonsEnabled() {
        loginButton.setEnabled(canLogin());
        registerButton.setEnabled(canRegister());
    }

    //returns true if the textField is empty
    private boolean textFieldIsEmpty(EditText text) {
        return TextUtils.isEmpty(text.getText());
    }

    private boolean canLogin() {
        return !textFieldIsEmpty(username) && !textFieldIsEmpty(password) && !textFieldIsEmpty(serverPort)
                && !textFieldIsEmpty(ipAddress);
    }

    private boolean canRegister() {
        return !textFieldIsEmpty(username) && !textFieldIsEmpty(password) && !textFieldIsEmpty(serverPort)
                && !textFieldIsEmpty(ipAddress) && !textFieldIsEmpty(firstName) && !textFieldIsEmpty(lastName)
                && !userGender.isEmpty() && !textFieldIsEmpty(email);
    }

    //Logic to switch to the map fragment upon successful login or register
    public void switchToMap() {
        MapFragment mapFragment = new MapFragment(true);

        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, mapFragment);
        fragmentTransaction.addToBackStack("mapFragment");

        fragmentTransaction.commit();
    }
}