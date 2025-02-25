package com.example.vanitycars;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CarA_display extends AppCompatActivity {
    DatabaseReference connectionnode;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    TextView display;
    DatabaseReference displaynode;
    DatabaseReference displayselfnode;
    TextView mainread;
    Button msg1btn;
    Button msg2btn;
    Button msg3btn;
    Button msg4btn;
    Button msg5btn;
    DatabaseReference node;
    DatabaseReference requestnode;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_adisplay);
        msg1btn = (Button) findViewById(R.id.button1);
        msg2btn = (Button) findViewById(R.id.button2);
        msg3btn = (Button) findViewById(R.id.button3);
        msg4btn = (Button) findViewById(R.id.button4);
        msg5btn = (Button) findViewById(R.id.button5);
        display = (TextView) findViewById(R.id.lcd_display);
        mainread = (TextView) findViewById(R.id.FIREBASESTATUS);

        node = db.getReference("Parameters");
        connectionnode = node.child("carA_Flag");
        requestnode = node.child("carA_LCD");

        if (getIntent().getStringExtra("CarConnected").equals("B")) {
            displaynode = node.child("carB_LCD_Msg");
        } else if (getIntent().getStringExtra("CarConnected").equals("C")) {
            displaynode = node.child("carC_LCD_Msg");
        } else if (getIntent().getStringExtra("CarConnected").equals("D")) {
            displaynode = node.child("carD_LCD_Msg");
        }
        connectionnode.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (((String) dataSnapshot.getValue(String.class)).equals("0")) {
                    Intent i = new Intent(CarA_display.this, CarA_Home.class);
                    requestnode.setValue("0");
                    startActivity(i);
                    finish();
                }
            }

            public void onCancelled(DatabaseError error) {
                Toast.makeText(CarA_display.this, "Connection Error", Toast.LENGTH_SHORT).show();
            }
        });
        displayselfnode = node.child("carA_LCD_Msg");
        displayselfnode.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                display.setText((String) dataSnapshot.getValue(String.class));
            }

            public void onCancelled(DatabaseError error) {
                Toast.makeText(CarA_display.this, "Connection Error", Toast.LENGTH_SHORT).show();
            }
        });
        msg1btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                display.setText("TRAFFIC JAM");
                displaynode.setValue("TRAFFIC JAM");
            }
        });
        msg2btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                display.setText("NEAR HOSPITAL");
                displaynode.setValue("NEAR HOSPITAL");
            }
        });
        msg3btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                display.setText("NEAR MALL");
                displaynode.setValue("NEAR MALL");
            }
        });
        msg4btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                display.setText("NEAR PARK");
                displaynode.setValue("NEAR PARK");
            }
        });
        msg5btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                display.setText("FOG REPORTING");
                displaynode.setValue("FOG REPORTING");
            }
        });
        FirebaseDatabase.getInstance().getReference(".info/connected").addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                if (((Boolean) snapshot.getValue(Boolean.class)).booleanValue()) {
                    mainread.setText("INTERNET CONNECTED ");
                } else {
                    mainread.setText("INTERNET CONNECTION FAILED");
                }
            }

            public void onCancelled(DatabaseError error) {
            }
        });
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
