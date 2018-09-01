package com.example.balasakthi.smartgrid.Admin.Activity;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.balasakthi.smartgrid.Admin.Adapter.TapAdapter;
import com.example.balasakthi.smartgrid.Admin.AdminUser;
import com.example.balasakthi.smartgrid.Admin.Model.Tapmodel;
import com.example.balasakthi.smartgrid.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TapsScreen extends AppCompatActivity implements ValueEventListener {

    RecyclerView recyclerView;

    RecyclerView.Adapter adapter;

    List<Tapmodel> list = new ArrayList<>();

    DatabaseReference reference;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taps_screen);

        dialog = new ProgressDialog(this);

        reference = FirebaseDatabase.getInstance().getReference();

        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new GridLayoutManager(this,5));

        adapter = new TapAdapter(list,this);

        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadtapsdetails(AdminUser.getPincode());
    }

    public void loadtapsdetails(final String pincode){

        dialog.setMessage("Loading...");

        dialog.show();

        reference.child("Taps").child(pincode).addValueEventListener(this);

    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        dialog.dismiss();

        list.clear();

        for(DataSnapshot snapshot:dataSnapshot.getChildren()){

            Tapmodel tapmodel = snapshot.getValue(Tapmodel.class);

            list.add(tapmodel);

            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

        dialog.dismiss();

        Toast.makeText(this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
