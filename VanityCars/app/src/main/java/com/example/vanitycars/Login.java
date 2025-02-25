package com.example.vanitycars;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {
    private FirebaseAuth auth;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    public SharedPreferences.Editor editor;
    Intent intent;
    private Button loginButton;
    /* access modifiers changed from: private */
    public EditText loginEmail;
    /* access modifiers changed from: private */
    public EditText loginPassword;
    private TextView signupRedirectText;
    /* access modifiers changed from: private */
    public EditText userID;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.loginEmail = (EditText) findViewById(R.id.username);
        this.loginPassword = (EditText) findViewById(R.id.password);
        this.loginButton = (Button) findViewById(R.id.loginButton);
        this.userID = (EditText) findViewById(R.id.userid);
        this.signupRedirectText = (TextView) findViewById(R.id.signupText);
        this.auth = FirebaseAuth.getInstance();
        this.loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String email = Login.this.loginEmail.getText().toString();
                String pass = Login.this.loginPassword.getText().toString();
                String id = Login.this.userID.getText().toString();
                if (email.isEmpty()) {
                    Login.this.loginEmail.setError("Email cannot be empty");
                    Login.this.loginEmail.requestFocus();
                } else if (pass.isEmpty()) {
                    Login.this.loginPassword.setError("Password cannot be empty");
                    Login.this.loginPassword.requestFocus();
                } else if (id.isEmpty()) {
                    Login.this.userID.setError("Please enter User ID");
                    Login.this.userID.requestFocus();
                } else {
                    Login.this.SignIn(id, email, pass);
                }
            }
        });
        this.signupRedirectText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Login.this.startActivity(new Intent(Login.this, SignUp.class));
            }
        });
    }

    public void SignIn(final String id, String email, String pass) {
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            this.loginEmail.setError("Please Enter Correct Email");
        } else {
            this.auth.signInWithEmailAndPassword(email, pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                public void onSuccess(AuthResult authResult) {
                    Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    if (id.equals("1")) {
                        Login.this.intent = new Intent(Login.this, CarA_Home.class);
                    } else if (id.equals("2")) {
                        Login.this.intent = new Intent(Login.this, CarB_Home.class);
                    } else if (id.equals("3")) {
                        Login.this.intent = new Intent(Login.this, CarC_Home.class);
                    } else if (id.equals("4")) {
                        Login.this.intent = new Intent(Login.this, CarD_Home.class);
                    } else if (id.equals("5")) {
                        Login.this.intent = new Intent(Login.this, AccidentCar.class);
                    } else if (id.equals("6")) {
                        Login.this.intent = new Intent(Login.this, ThreatCar.class);
                    }
                    Login login = Login.this;
                    login.startActivity(login.intent);
                    Login.this.finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                public void onFailure(Exception e) {
                    Toast.makeText(Login.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    @SuppressLint("MissingSuperCall")

    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("EXIT");
        builder.setMessage("Are you sure you want to Exit ?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Login.this.finish();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }
}
