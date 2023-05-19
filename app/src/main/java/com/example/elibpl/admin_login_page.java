package com.example.elibpl;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class admin_login_page extends AppCompatActivity {

    private Button loginButton;
    private EditText usernameEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login_page);

        loginButton = findViewById(R.id.login_button);
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);

        // Set up text change listeners for the username and password fields
        usernameEditText.addTextChangedListener(loginTextWatcher);
        passwordEditText.addTextChangedListener(loginTextWatcher);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click event here
                goToAdminHomePage();
            }
        });
    }

    private void goToAdminHomePage() {
        Intent intent = new Intent(this, admin_homepage.class);
        startActivity(intent);
        // Finish the current activity if needed
        finish();
    }

    // TextWatcher to monitor changes in the username and password fields
    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            // Check if both username and password fields are filled
            boolean isUsernameFilled = usernameEditText.getText().length() > 0;
            boolean isPasswordFilled = passwordEditText.getText().length() > 0;

            // Enable or disable the login button based on the fields' status
            loginButton.setEnabled(isUsernameFilled && isPasswordFilled);
        }
    };
}
