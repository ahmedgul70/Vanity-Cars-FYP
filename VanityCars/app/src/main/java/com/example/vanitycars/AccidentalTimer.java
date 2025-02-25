package com.example.vanitycars;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccidentalTimer extends AppCompatActivity {
    Button btn1;
    public int counter = 120;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    TextView display;
    TextView mainread;
    DatabaseReference node;
    long starttime = 0;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accidental_timer);
        this.btn1 = (Button) findViewById(R.id.button1);
        this.display = (TextView) findViewById(R.id.timer);
        this.mainread = (TextView) findViewById(R.id.FIREBASESTATUS);
        this.node = this.db.getReference("V2I");
        new CountDownTimer(120000, 1000) {
            public void onTick(long millisUntilFinished) {
                AccidentalTimer.this.display.setText(String.valueOf(AccidentalTimer.this.counter));
                AccidentalTimer accidentalTimer = AccidentalTimer.this;
                accidentalTimer.counter--;
            }

            public void onFinish() {
                AccidentalTimer.this.node.child("TimerCompleted").setValue("1");
                AccidentalTimer.this.display.setText("0");
            }
        }.start();
        this.btn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                counter = 0;
                AccidentalTimer.this.node.child("TimerCompleted").setValue("0");
                AccidentalTimer.this.node.child("accident").setValue("0");
                AccidentalTimer.this.startActivity(new Intent(AccidentalTimer.this, AccidentCar.class));
                AccidentalTimer.this.finish();
            }
        });
        FirebaseDatabase.getInstance().getReference(".info/connected").addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                if (((Boolean) snapshot.getValue(Boolean.class)).booleanValue()) {
                    AccidentalTimer.this.mainread.setText("INTERNET CONNECTED ");
                } else {
                    AccidentalTimer.this.mainread.setText("INTERNET CONNECTION FAILED");
                }
            }

            public void onCancelled(DatabaseError error) {
            }
        });
    }

    @SuppressLint("MissingSuperCall")
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle((CharSequence) "EXIT");
        builder.setMessage((CharSequence) "Do you Want To EXIT ?").setCancelable(false).setPositiveButton((CharSequence) "Yes", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                AccidentalTimer.this.finish();
                AccidentalTimer.this.startActivity(new Intent(AccidentalTimer.this, Login.class));
            }
        }).setNegativeButton((CharSequence) "No", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }
}
