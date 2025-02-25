package com.example.vanitycars;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccidentCar extends AppCompatActivity {
    Button btn1;
    Button btn2;
    Button btn3;
    Button btn4;
    Button btn5;
    Button btn6;
    DatabaseReference connectionnode;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    TextView display;
    public SharedPreferences.Editor editor;
    TextView mainread;
    DatabaseReference node;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accident_car);
        this.btn1 = (Button) findViewById(R.id.button1);
        this.btn2 = (Button) findViewById(R.id.button2);
        this.btn3 = (Button) findViewById(R.id.button3);
        this.btn4 = (Button) findViewById(R.id.button4);
        this.btn5 = (Button) findViewById(R.id.button5);
        this.btn6 = (Button) findViewById(R.id.button6);
        this.display = (TextView) findViewById(R.id.lcd_display);
        this.mainread = (TextView) findViewById(R.id.FIREBASESTATUS);
        DatabaseReference reference = this.db.getReference("V2I");
        this.node = reference;
        DatabaseReference child = reference.child("accident");
        this.connectionnode = child;
        child.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (((String) dataSnapshot.getValue(String.class)).equals("1")) {
                    AccidentCar.this.startActivity(new Intent(AccidentCar.this, AccidentalTimer.class));
                    AccidentCar.this.finish();
                }
            }

            public void onCancelled(DatabaseError error) {
                Toast.makeText(AccidentCar.this, "Connection Error", Toast.LENGTH_SHORT).show();
            }
        });
        this.btn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AccidentCar.this.display.setText("TRAFFIC JAM");
            }
        });
        this.btn2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AccidentCar.this.display.setText("NEAR HOSPITAL");
            }
        });
        this.btn3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AccidentCar.this.display.setText("NEAR MALL");
            }
        });
        this.btn4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AccidentCar.this.display.setText("NEAR PARK");
            }
        });
        this.btn5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AccidentCar.this.display.setText("FOG REPORTING");
            }
        });
        this.btn6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AccidentCar.this.startActivity(new Intent(AccidentCar.this, Maps.class));
                AccidentCar.this.finish();
            }
        });
        FirebaseDatabase.getInstance().getReference(".info/connected").addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                if (((Boolean) snapshot.getValue(Boolean.class)).booleanValue()) {
                    AccidentCar.this.mainread.setText("INTERNET CONNECTED ");
                } else {
                    AccidentCar.this.mainread.setText("INTERNET CONNECTION FAILED");
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
                AccidentCar.this.finish();
                AccidentCar.this.startActivity(new Intent(AccidentCar.this, Login.class));
            }
        }).setNegativeButton((CharSequence) "No", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }
}
