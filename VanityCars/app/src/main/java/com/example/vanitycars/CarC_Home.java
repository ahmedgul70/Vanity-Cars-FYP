package com.example.vanitycars;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

public class CarC_Home extends AppCompatActivity {
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    TextView display, mainread;
    DatabaseReference displaynode, connectionnode, requestnode;
    DatabaseReference node;
    String request = "";
    Boolean requestConnected = false, requestSent = false, tempconnected = false;
    Button requestbtn, requestbtn1, requestbtn2;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_chome);
        requestbtn1 = (Button) findViewById(R.id.sentbtn1); // car A
        requestbtn2 = (Button) findViewById(R.id.sentbtn2); // car B
        requestbtn = (Button) findViewById(R.id.sentbtn);   // car D
        display = (TextView) findViewById(R.id.lcd_display);
        mainread = (TextView) findViewById(R.id.FIREBASESTATUS);
        requestbtn1.setVisibility(View.INVISIBLE);
        requestbtn2.setVisibility(View.INVISIBLE);
        requestbtn.setVisibility(View.INVISIBLE);

        node = db.getReference("Parameters");
        connectionnode = node.child("carC_Flag");
        requestnode = node.child("carC_request");
        displaynode = node.child("carC_LCD");

        displaynode.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = (String) dataSnapshot.getValue(String.class);
                if(tempconnected) {
                    if (value.equals("B") || value.equals("A") || value.equals("D")) {
                        Log.e("CarC Page : ", "To Messages Page");
                        Intent i = new Intent(CarC_Home.this, CarC_display.class);
                        i.putExtra("CarConnected", value);
                        startActivity(i);
                        finish();
                    }
                }
            }
            public void onCancelled(DatabaseError error) {
                Toast.makeText(CarC_Home.this, "Connection Error", Toast.LENGTH_SHORT).show();
            }
        });
        requestnode.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = (String) dataSnapshot.getValue(String.class);
                if(tempconnected) {
                    if (value.equals("B") || value.equals("A") || value.equals("D")) {
                        Log.e("CarC Page : ", "Request from Car");
                        Intent i = new Intent(CarC_Home.this, CarC_details_page.class);
                        i.putExtra("Car", value);
                        startActivity(i);
                        finish();
                    }
                }
            }
            public void onCancelled(DatabaseError error) {
                Toast.makeText(CarC_Home.this, "Connection Error", Toast.LENGTH_SHORT).show();
            }
        });
        requestbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (tempconnected.booleanValue() && !requestSent.booleanValue()) {
                    Log.e("CarC Page : ", "Request Sent - Select One Option");
                    display.setText("Request Pending");
                    display.setAllCaps(true);
                    requestbtn.setText("Car D");
                    requestbtn1.setVisibility(View.VISIBLE);
                    requestbtn2.setVisibility(View.VISIBLE);
                    requestSent = true;
                } else if (requestSent.booleanValue()) {
                    node.child("carD_request").setValue("C");
                }
            }
        });
        requestbtn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (requestSent.booleanValue()) {
                    node.child("carA_request").setValue("C");
                }
            }
        });
        requestbtn2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (requestSent.booleanValue()) {
                    node.child("carB_request").setValue("C");
                }
            }
        });
        connectionnode.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (((String) dataSnapshot.getValue(String.class)).equals("1")) {
                    Log.e("CarC Page : ", "Temporary Connected");
                    display.setText("Temporary Connected");
                    display.setAllCaps(true);
                    requestbtn.setText("Request Send");
                    requestbtn.setVisibility(View.VISIBLE);
                    tempconnected = true;
                    return;
                }
                Log.e("CarC Page : ", "No User in Range");
                node.child("carC_request").setValue("0");
                node.child("carC_LCD").setValue("0");
                display.setText("No User in Range");
                display.setAllCaps(true);
                requestbtn1.setVisibility(View.INVISIBLE);
                requestbtn2.setVisibility(View.INVISIBLE);
                requestbtn.setVisibility(View.INVISIBLE);
                tempconnected = false;
            }
            public void onCancelled(DatabaseError error) {
                Toast.makeText(CarC_Home.this, "Connection Error", Toast.LENGTH_SHORT).show();
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

    @SuppressLint("MissingSuperCall")
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle((CharSequence) "EXIT");
        builder.setMessage((CharSequence) "Do you Want To EXIT ?").setCancelable(false).setPositiveButton((CharSequence) "Yes", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
                startActivity(new Intent(CarC_Home.this, Login.class));
            }
        }).setNegativeButton((CharSequence) "No", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }
}
