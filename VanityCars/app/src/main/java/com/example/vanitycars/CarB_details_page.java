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

public class CarB_details_page extends AppCompatActivity {
    String carNumber = "ABC-123";
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
        setContentView(R.layout.activity_car_bdetails_page);
        this.yesbtn = (Button) findViewById(R.id.YES);
        this.nobtn = (Button) findViewById(R.id.NO);
        this.display = (TextView) findViewById(R.id.lcd_display);
        this.mainread = (TextView) findViewById(R.id.FIREBASESTATUS);
        DatabaseReference reference = this.db.getReference("Parameters");
        this.node = reference;
        this.requestnode = reference.child("carB_request");
        this.displaySelfnode = this.node.child("carB_LCD");
        final String getCar = getIntent().getStringExtra("Car");
        if (getCar.equals("A")) {
            this.display.setText("CAR A \n" + this.carNumber);
            this.displaynode = this.node.child("carA_LCD");
        } else if (getCar.equals("C")) {
            this.display.setText("CAR C \n" + this.carNumber);
            this.displaynode = this.node.child("carC_LCD");
        } else if (getCar.equals("D")) {
            this.display.setText("CAR D \n" + this.carNumber);
            this.displaynode = this.node.child("carD_LCD");
        }
        this.node = this.db.getReference("Parameters");
        this.yesbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CarB_details_page.this.requestnode.setValue("0");
                CarB_details_page.this.displaySelfnode.setValue(getCar);
                CarB_details_page.this.displaynode.setValue("B");
                Intent i = new Intent(CarB_details_page.this, CarB_display.class);
                i.putExtra("CarConnected", getCar);
                CarB_details_page.this.startActivity(i);
                CarB_details_page.this.finish();
            }
        });
        this.nobtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CarB_details_page.this.requestnode.setValue("0");
                CarB_details_page.this.startActivity(new Intent(CarB_details_page.this, CarB_Home.class));
                CarB_details_page.this.finish();
            }
        });
        FirebaseDatabase.getInstance().getReference(".info/connected").addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                if (((Boolean) snapshot.getValue(Boolean.class)).booleanValue()) {
                    CarB_details_page.this.mainread.setText("INTERNET CONNECTED ");
                } else {
                    CarB_details_page.this.mainread.setText("INTERNET CONNECTION FAILED");
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
