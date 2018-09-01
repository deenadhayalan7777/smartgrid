package com.example.balasakthi.smartgrid.Admin.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.balasakthi.smartgrid.Admin.AdminUser;
import com.example.balasakthi.smartgrid.Admin.Model.Tapmodel;
import com.example.balasakthi.smartgrid.GetLatLngActivity;
import com.example.balasakthi.smartgrid.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminMainActivity extends AppCompatActivity implements ValueEventListener {

    int count = 0;

    String capacity;

    String lat,lon;

    TextInputLayout latitudeLayout;

    TextInputLayout longitudeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        FirebaseDatabase.getInstance().getReference()
                .child("User")
                .child(AdminUser.getEmpid())
                .addValueEventListener(this);
    }

    public void viewTaps(View view){

        Intent intent= new Intent(this,TapsScreen.class);
        startActivity(intent);
    }

    public void viewUsage(View view){

        Intent intent= new Intent(this,ViewUsageActivity.class);
        startActivity(intent);
    }

    public void viewReports(View view){

        Intent intent= new Intent(this,TapsScreen.class);
        //startActivity(intent);
    }

    public void addWater(View view){

        Intent intent= new Intent(this,TapSearchActivity.class);
        startActivity(intent);

    }

    public void installTaps(View view) {

        View dialog = getLayoutInflater().inflate(R.layout.activity_install_taps, null);

        TextInputLayout capacityLayout = dialog.findViewById(R.id.capacityLayout);

        latitudeLayout = dialog.findViewById(R.id.latitudeLayout);

        longitudeLayout = dialog.findViewById(R.id.longituteLayout);

        ImageView locationImageView = dialog.findViewById(R.id.locationImageView);

        locationImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AdminMainActivity.this, GetLatLngActivity.class);

                startActivity(intent);
            }
        });

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Install Tank")
                .setView(dialog)
                .setPositiveButton("Install", null)
                .setNegativeButton("Cancel", null)
                .create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        boolean isValid = true;

                        capacity = capacityLayout.getEditText().getText().toString().trim();

                        lat = latitudeLayout.getEditText().getText().toString().trim();

                        lon = longitudeLayout.getEditText().getText().toString().trim();

                        latitudeLayout.setError(null);

                        longitudeLayout.setError(null);

                        capacityLayout.setError(null);

                        if(lat.isEmpty()) {

                            latitudeLayout.setError("Field cannot be empty");

                            isValid = false;
                        }

                            if(lon.isEmpty()) {

                                longitudeLayout.setError("Field cannot be empty");

                                isValid = false;
                            }

                                if(capacity.isEmpty()) {

                                    capacityLayout.setError("Field cannot be empty");

                                    isValid = false;
                                }

                                if (!isValid) return;

                        Tapmodel tapmodel = new Tapmodel();

                        tapmodel.setWater_remaining("0");
                        tapmodel.setWater_allocated(capacity);
                        tapmodel.setStatus("false");
                        tapmodel.setPincode(AdminUser.getPincode());
                        tapmodel.setLongitude(lon);
                        tapmodel.setLatitude(lat);
                        tapmodel.setId(String.valueOf(count + 1));

                        FirebaseDatabase.getInstance().getReference()
                                .child("Taps")
                                .child(AdminUser.getPincode())
                                .child(String.valueOf(count + 1))
                                .setValue(tapmodel, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                                        if (databaseError == null) {

                                            Toast.makeText(AdminMainActivity.this, "Tank Installed Successfully", Toast.LENGTH_SHORT).show();

                                            dialog.dismiss();

                                        } else {

                                            Toast.makeText(AdminMainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                                            dialog.dismiss();
                                        }
                                    }
                                });
                    }
                });

                ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();
                    }
                });
            }
        });

        alertDialog.show();
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        AdminUser.setEmail(dataSnapshot.child("email").getValue(String.class));

        AdminUser.setPincode(dataSnapshot.child("pincode").getValue(String.class));

        AdminUser.setName(dataSnapshot.child("name").getValue(String.class));

        Toast.makeText(this, "Welcome back "+AdminUser.getName(), Toast.LENGTH_SHORT).show();

        FirebaseDatabase.getInstance().getReference()
                .child("Taps")
                .child(AdminUser.getPincode())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Long l = new Long(dataSnapshot.getChildrenCount());

                        count = l.intValue();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
