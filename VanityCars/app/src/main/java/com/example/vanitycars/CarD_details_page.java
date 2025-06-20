package com.example.vanitycars;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CarD_details_page extends AppCompatActivity {
    String carNumber = "RST-256";
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    TextView display;
    DatabaseReference displaySelfnode;
    String displayText = " ";
    DatabaseReference displaynode;
    TextView mainread;
    Button nobtn;
    DatabaseReference node;
    DatabaseReference requestnode;
    Button yesbtn;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_ddetails_page);
        yesbtn = (Button) findViewById(R.id.YES);
        nobtn = (Button) findViewById(R.id.NO);
        display = (TextView) findViewById(R.id.lcd_display);
        mainread = (TextView) findViewById(R.id.FIREBASESTATUS);

        node = db.getReference("Parameters");
        requestnode = node.child("carD_request");
        displaySelfnode = node.child("carD_LCD");

        final String getCar = getIntent().getStringExtra("Car");
        if (getCar.equals("B")) {
            display.setText("CAR B \n" + carNumber);
            displaynode = node.child("carB_LCD");
        } else if (getCar.equals("C")) {
            display.setText("CAR C \n" + carNumber);
            displaynode = node.child("carC_LCD");
        } else if (getCar.equals("A")) {
            display.setText("CAR A \n" + carNumber);
            displaynode = node.child("carA_LCD");
        }

        yesbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                requestnode.setValue("0");
                displaySelfnode.setValue(getCar);
                displaynode.setValue("D");
                Intent i = new Intent(CarD_details_page.this, CarD_display.class);
                i.putExtra("CarConnected", getCar);
                startActivity(i);
                finish();
            }
        });
        this.nobtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                requestnode.setValue("0");
                startActivity(new Intent(CarD_details_page.this, CarD_Home.class));
                finish();
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
