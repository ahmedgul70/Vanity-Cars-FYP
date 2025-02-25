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

public class CarB_Home extends AppCompatActivity {
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    TextView display,mainread;
    DatabaseReference displaynode,connectionnode,requestnode;
    DatabaseReference node;
    String request = "";
    Boolean requestConnected = false,requestSent = false,tempconnected = false;
    Button requestbtn,requestbtn1,requestbtn2;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_bhome);
        requestbtn1 = (Button) findViewById(R.id.sentbtn1); // car A
        requestbtn2 = (Button) findViewById(R.id.sentbtn2); // car C
        requestbtn = (Button) findViewById(R.id.sentbtn);   // car D
        display = (TextView) findViewById(R.id.lcd_display);
        mainread = (TextView) findViewById(R.id.FIREBASESTATUS);
        requestbtn1.setVisibility(View.INVISIBLE);
        requestbtn2.setVisibility(View.INVISIBLE);
        requestbtn.setVisibility(View.INVISIBLE);

        node = db.getReference("Parameters");
        connectionnode = node.child("carB_Flag");
        requestnode = node.child("carB_request");
        displaynode = node.child("carB_LCD");


        displaynode.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = (String) dataSnapshot.getValue(String.class);
                if(tempconnected) {
                    if (value.equals("A") || value.equals("C") || value.equals("D")) {
                        Log.e("CarB Page : ", "To Messages Page");
                        Intent i = new Intent(CarB_Home.this, CarB_display.class);
                        i.putExtra("CarConnected", value);
                        startActivity(i);
                        finish();
                    }
                }
            }

            public void onCancelled(DatabaseError error) {
                Toast.makeText(CarB_Home.this, "Connection Error", Toast.LENGTH_SHORT).show();
            }
        });
         requestnode.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = (String) dataSnapshot.getValue(String.class);
                if(tempconnected) {
                    if (value.equals("A")) {
                        Log.e("CarB Page : ", "Request from Car A");
                        Intent i = new Intent(CarB_Home.this, CarB_details_page.class);
                        i.putExtra("Car", value);
                        startActivity(i);
                        finish();
                    } else if (value.equals("C")) {
                        Log.e("CarB Page : ", "Request from Car C");
                        Intent i2 = new Intent(CarB_Home.this, CarB_details_page.class);
                        i2.putExtra("Car", value);
                        startActivity(i2);
                        finish();
                    } else if (value.equals("D")) {
                        Log.e("CarB Page : ", "Request from Car D");
                        Intent i3 = new Intent(CarB_Home.this, CarB_details_page.class);
                        i3.putExtra("Car", value);
                        startActivity(i3);
                        finish();
                    }
                }
            }

            public void onCancelled(DatabaseError error) {
                Toast.makeText(CarB_Home.this, "Connection Error", Toast.LENGTH_SHORT).show();
            }
        });
         requestbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if ( tempconnected.booleanValue() && ! requestSent.booleanValue()) {
                    Log.e("CarB Page : ", "Request Sent - Select One Option");
                     display.setText("Request Pending");
                     display.setAllCaps(true);
                     requestbtn1.setText("Car A");
                     requestbtn2.setText("Car C");
                     requestbtn.setText("Car D");
                     requestbtn1.setVisibility(View.VISIBLE);
                     requestbtn2.setVisibility(View.VISIBLE);
                     requestSent = true;
                } else if ( requestSent.booleanValue()) {
                     request = "D";
                     requestConnected = true;
                     node.child("carD_request").setValue("B");
                }
            }
        });
         requestbtn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if ( requestSent.booleanValue()) {
                     request = "A";
                     requestConnected = true;
                     node.child("carA_request").setValue("B");
                }
            }
        });
         requestbtn2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if ( requestSent.booleanValue()) {
                     request = "C";
                     requestConnected = true;
                     node.child("carC_request").setValue("B");
                }
            }
        });
        connectionnode.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (((String) dataSnapshot.getValue(String.class)).equals("1")) {
                    Log.e("CarB Page : ", "Temporary Connected");
                     display.setText("Temporary Connected");
                     display.setAllCaps(true);
                     requestbtn.setText("Request Send");
                     requestbtn.setVisibility(View.VISIBLE);
                     tempconnected = true;
                    return;
                }
                Log.e("CarB Page : ", "No User in Range");
                 node.child("carB_request").setValue("0");
                 node.child("carB_LCD").setValue("0");
                 display.setText("No User in Range");
                 display.setAllCaps(true);
                 requestbtn.setVisibility(View.INVISIBLE);
                 tempconnected = false;
            }

            public void onCancelled(DatabaseError error) {
                Toast.makeText(CarB_Home.this, "Connection Error", Toast.LENGTH_SHORT).show();
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
                 startActivity(new Intent(CarB_Home.this, Login.class));
            }
        }).setNegativeButton((CharSequence) "No", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }
}
