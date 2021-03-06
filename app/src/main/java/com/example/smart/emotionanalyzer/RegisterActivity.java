package com.example.smart.emotionanalyzer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.FirebaseDatabase;

import java.io.FileNotFoundException;

public class RegisterActivity extends AppCompatActivity {
    private String email;
    private String name;
    private String password;
    private String confirmPassword;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private ActivityManager activityManager;
    private UserManager userManager;
    private ImageView profilePicture;
    private TextView selectPicture;
    private Uri profilePictureUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        activityManager = new ActivityManager(this);
        mAuth = FirebaseAuth.getInstance();
        final EditText emailView = (EditText) findViewById(R.id.email);
        final EditText nameView = (EditText) findViewById(R.id.name);
        final EditText passwordView = (EditText) findViewById(R.id.password);
        final EditText confirmPasswordView = (EditText) findViewById(R.id.confirm_password);
        profilePicture = findViewById(R.id.profile_pic);
        selectPicture = findViewById(R.id.select_pic);
        database = FirebaseDatabase.getInstance();

        Button registerButton = (Button) findViewById(R.id.register);
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                email = emailView.getText().toString();
                password = passwordView.getText().toString();
                confirmPassword = confirmPasswordView.getText().toString();
                name = nameView.getText().toString();
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Passwords do not match",
                            Toast.LENGTH_SHORT).show();
                }
                else if (isEmailValid(email) && isPasswordValid(password)) {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        userManager = new UserManager();
                                        userManager.addUserInfo(name);
                                        if (profilePictureUri != null) {
                                            userManager.updateProfilePicture(profilePictureUri);
                                        }
                                        Bundle bundle = new Bundle();
                                        bundle.putString("fragment", "MainFeedFragment");
                                        activityManager.changeActivty(MainActivity.class, bundle);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        Button cancelButton = (Button) findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activityManager.changeActivty(LoginActivity.class, null);
            }
        });

        selectPicture.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activityManager.getPictureFromGallery();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            profilePictureUri = data.getData();
            profilePicture.setImageURI(profilePictureUri);
        }
    }


    private boolean isEmailValid(String email) {
        if (email.length() >= 7 && email.contains("@") && email.contains(".")) {
            return true;
        }
        else {
            String invalidMessage = getString(R.string.error_invalid_email);
            Toast.makeText(RegisterActivity.this, invalidMessage,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean isPasswordValid(String password) {
        if (password.length() >= 8 && password.matches(".*[A-Z].*") && password.matches(".*[0-9].*")) {
            return true;
        }
        else {
            String invalidMessage = "";
            if (password.length() < 8) {
                invalidMessage = "Password is too short";
            }
            else if (!password.matches(".*[A-Z].*")) {
                invalidMessage = "Password must have a capital letter";
            }
            else {
                invalidMessage = "Password must have a numeral";
            }
            Toast.makeText(RegisterActivity.this, invalidMessage,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
