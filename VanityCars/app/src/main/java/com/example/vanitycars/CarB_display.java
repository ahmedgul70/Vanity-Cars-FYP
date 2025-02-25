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

public class CarB_display extends AppCompatActivity {
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
        setContentView(R.layout.activity_car_bdisplay);
        this.msg1btn = (Button) findViewById(R.id.button1);
        this.msg2btn = (Button) findViewById(R.id.button2);
        this.msg3btn = (Button) findViewById(R.id.button3);
        this.msg4btn = (Button) findViewById(R.id.button4);
        this.msg5btn = (Button) findViewById(R.id.button5);
        this.display = (TextView) findViewById(R.id.lcd_display);
        this.mainread = (TextView) findViewById(R.id.FIREBASESTATUS);
        DatabaseReference reference = this.db.getReference("Parameters");
        this.node = reference;
        this.connectionnode = reference.child("carB_Flag");
        this.requestnode = this.node.child("carB_LCD");
        this.displayselfnode = this.node.child("carB_LCD_Msg");
        if (getIntent().getStringExtra("CarConnected").equals("A")) {
            displaynode = node.child("carA_LCD_Msg");
        } else if (getIntent().getStringExtra("CarConnected").equals("C")) {
            displaynode = node.child("carC_LCD_Msg");
        } else if (getIntent().getStringExtra("CarConnected").equals("D")) {
            displaynode = node.child("carD_LCD_Msg");
        }
        this.connectionnode.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (((String) dataSnapshot.getValue(String.class)).equals("0")) {
                    Intent i = new Intent(CarB_display.this, CarB_Home.class);
                    CarB_display.this.requestnode.setValue("0");
                    CarB_display.this.startActivity(i);
                    CarB_display.this.finish();
                }
            }

            public void onCancelled(DatabaseError error) {
                Toast.makeText(CarB_display.this, "Connection Error", Toast.LENGTH_SHORT).show();
            }
        });
        this.displayselfnode.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                CarB_display.this.display.setText((String) dataSnapshot.getValue(String.class));
            }

            public void onCancelled(DatabaseError error) {
                Toast.makeText(CarB_display.this, "Connection Error", Toast.LENGTH_SHORT).show();
            }
        });
        this.msg1btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CarB_display.this.display.setText("TRAFFIC JAM");
                CarB_display.this.displaynode.setValue("TRAFFIC JAM");
            }
        });
        this.msg2btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CarB_display.this.display.setText("NEAR HOSPITAL");
                CarB_display.this.displaynode.setValue("NEAR HOSPITAL");
            }
        });
        this.msg3btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CarB_display.this.display.setText("NEAR MALL");
                CarB_display.this.displaynode.setValue("NEAR MALL");
            }
        });
        this.msg4btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CarB_display.this.display.setText("NEAR PARK");
                CarB_display.this.displaynode.setValue("NEAR PARK");
            }
        });
        this.msg5btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CarB_display.this.display.setText("FOG REPORTING");
                CarB_display.this.displaynode.setValue("FOG REPORTING");
            }
        });
        FirebaseDatabase.getInstance().getReference(".info/connected").addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                if (((Boolean) snapshot.getValue(Boolean.class)).booleanValue()) {
                    CarB_display.this.mainread.setText("INTERNET CONNECTED ");
                } else {
                    CarB_display.this.mainread.setText("INTERNET CONNECTION FAILED");
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
