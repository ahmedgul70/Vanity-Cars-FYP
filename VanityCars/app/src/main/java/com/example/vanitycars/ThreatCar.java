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

public class ThreatCar extends AppCompatActivity {
    DatabaseReference connectionnode;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    TextView display;
    public SharedPreferences.Editor editor;
    TextView mainread;
    DatabaseReference node;
    DatabaseReference node2;
    String request = "";
    Button requestbtn;
    Boolean tempconnected = false;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_threat_car);
        this.requestbtn = (Button) findViewById(R.id.sentbtn2);
        this.display = (TextView) findViewById(R.id.lcd_display);
        this.mainread = (TextView) findViewById(R.id.FIREBASESTATUS);
        this.requestbtn.setVisibility(View.INVISIBLE);
        this.node = this.db.getReference("Security");
//        this.node2 = this.db.getReference("Parameters");
        this.connectionnode = this.node.child("CarFlag");
        this.requestbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ThreatCar.this.tempconnected.booleanValue()) {
                    Log.e("Threat Page : ", "Request Sent - Select One Option");
                    ThreatCar.this.display.setText("Request Pending");
                    ThreatCar.this.display.setAllCaps(true);
                    ThreatCar.this.node2.child("carA_request").setValue("Threat");
                }
            }
        });
        this.connectionnode.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (((String) dataSnapshot.getValue(String.class)).equals("1")) {
                    Log.e("Threat Page : ", "Temporary Connected");
                    ThreatCar.this.display.setText("Temporary Connected");
                    ThreatCar.this.display.setAllCaps(true);
                    ThreatCar.this.requestbtn.setVisibility(View.VISIBLE);
                    ThreatCar.this.tempconnected = true;
                    return;
                }
                Log.e("Threat Page : ", "No User in Range");
                ThreatCar.this.display.setText("No User in Range");
                ThreatCar.this.display.setAllCaps(true);
                ThreatCar.this.requestbtn.setVisibility(View.INVISIBLE);
                ThreatCar.this.tempconnected = false;
            }

            public void onCancelled(DatabaseError error) {
                Toast.makeText(ThreatCar.this, "Connection Error", Toast.LENGTH_SHORT).show();
            }
        });
        FirebaseDatabase.getInstance().getReference(".info/connected").addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                if (((Boolean) snapshot.getValue(Boolean.class)).booleanValue()) {
                    ThreatCar.this.mainread.setText("INTERNET CONNECTED ");
                } else {
                    ThreatCar.this.mainread.setText("INTERNET CONNECTION FAILED");
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
                ThreatCar.this.finish();
                ThreatCar.this.startActivity(new Intent(ThreatCar.this, Login.class));
            }
        }).setNegativeButton((CharSequence) "No", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }
}
