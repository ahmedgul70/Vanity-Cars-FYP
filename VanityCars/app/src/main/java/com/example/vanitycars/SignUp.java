package com.example.vanitycars;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUp extends AppCompatActivity {
    /* access modifiers changed from: private */
    public FirebaseAuth auth;
    private TextView loginRedirectText;
    private Button signupButton;
    /* access modifiers changed from: private */
    public EditText signupEmail;
    /* access modifiers changed from: private */
    public EditText signupPassword;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        this.auth = FirebaseAuth.getInstance();
        this.signupEmail = (EditText) findViewById(R.id.signup_email);
        this.signupPassword = (EditText) findViewById(R.id.signup_password);
        this.signupButton = (Button) findViewById(R.id.signup_button);
        this.loginRedirectText = (TextView) findViewById(R.id.loginRedirectText);
        this.signupButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String user = SignUp.this.signupEmail.getText().toString().trim();
                String pass = SignUp.this.signupPassword.getText().toString().trim();
                if (user.isEmpty()) {
                    SignUp.this.signupEmail.setError("Email cannot be empty");
                    SignUp.this.signupEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(user).matches()) {
                    SignUp.this.signupEmail.setError("Email format is invalid");
                    Toast.makeText(SignUp.this, "Enter Valid Email", Toast.LENGTH_SHORT).show();
                    SignUp.this.signupEmail.requestFocus();
                } else if (pass.isEmpty()) {
                    SignUp.this.signupPassword.setError("Password cannot be empty");
                    SignUp.this.signupPassword.requestFocus();
                } else {
                    SignUp.this.auth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        public void onComplete(Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignUp.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                                SignUp.this.startActivity(new Intent(SignUp.this, Login.class));
                                return;
                            }
                            Toast.makeText(SignUp.this, "SignUp Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        this.loginRedirectText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SignUp.this.startActivity(new Intent(SignUp.this, Login.class));
            }
        });
    }
}
