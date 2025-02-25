package com.example.vanitycars;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Maps extends AppCompatActivity {
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    /* access modifiers changed from: private */
    public ImageView mImageView;
    DatabaseReference mapsnode;
    DatabaseReference node;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        this.mImageView = (ImageView) findViewById(R.id.imageView2);
        DatabaseReference reference = this.db.getReference("V2I");
        this.node = reference;
        DatabaseReference child = reference.child("points");
        this.mapsnode = child;
        child.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = (String) dataSnapshot.getValue(String.class);
                if (value.equals("0")) {
                    Maps.this.mImageView.setImageResource(R.drawable.poa);
                } else if (value.equals("1")) {
                    Maps.this.mImageView.setImageResource(R.drawable.map1);
                } else if (value.equals("2")) {
                    Maps.this.mImageView.setImageResource(R.drawable.pob);
                } else if (value.equals("3")) {
                    Maps.this.mImageView.setImageResource(R.drawable.poc);
                }
            }

            public void onCancelled(DatabaseError error) {
                Toast.makeText(Maps.this, "Connection Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @SuppressLint("MissingSuperCall")

    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle((CharSequence) "EXIT");
        builder.setMessage((CharSequence) "Do you Want To EXIT ?").setCancelable(false).setPositiveButton((CharSequence) "Yes", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Maps.this.startActivity(new Intent(Maps.this, AccidentCar.class));
                Maps.this.finish();
            }
        }).setNegativeButton((CharSequence) "No", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }
}
