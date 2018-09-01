package com.example.balasakthi.smartgrid.Admin.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.balasakthi.smartgrid.Admin.Adapter.TapSearchAdapter;
import com.example.balasakthi.smartgrid.Admin.AdminUser;
import com.example.balasakthi.smartgrid.Admin.Model.Tapmodel;
import com.example.balasakthi.smartgrid.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TapSearchActivity extends AppCompatActivity implements ValueEventListener {

    TextView amount,selectedTaps;

    List<Tapmodel> list = new ArrayList<>();

    RecyclerView.Adapter adapter;

    AlertDialog.Builder alert;

    ProgressDialog dialog;

    List<String> idlist = new ArrayList<>();

    int water_remaining=0;

    int waterprovided=0;

    int water_alloc=0;

    int water_added = 0;

    String date;

    Map<String,Map<String,String>> waterinfo = new HashMap<>();

    Map<String,Map<String,String>> usage_info = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_search);

        amount = findViewById(R.id.amountEditText);

        selectedTaps = findViewById(R.id.tapsSelectedTextView);

        dialog = new ProgressDialog(this);

        dialog.setMessage("Loading...");
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);

        date = String.valueOf(day)+"-"+
                String.valueOf(month)+"-"+String.valueOf(year);

        FirebaseDatabase.getInstance().getReference()
                .child("Taps")
                .child(AdminUser.getPincode())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot snapshot:dataSnapshot.getChildren()){

                            Map<String,String> map = new HashMap<>();

                            map.put("water_remaining",snapshot.child("water_remaining").getValue(String.class));

                            map.put("water_allocated",snapshot.child("water_allocated").getValue(String.class));

                            waterinfo.put(snapshot.child("id").getValue(String.class),map);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        FirebaseDatabase.getInstance().getReference()
                .child("Usage")
                .child(date)
                .child(AdminUser.getPincode())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                            Map<String,String> map = new HashMap<>();

                            map.put("water_provided",snapshot.child("water_provided").getValue(String.class));

                            map.put("water_used",snapshot.child("water_used").getValue(String.class));

                            usage_info.put(snapshot.getKey(),map);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void addWater(View view){

        for(String id:idlist) {

            Log.i(id,waterinfo.get(id).toString());

            //Log.i(id,usage_info.get(id).toString());

            water_remaining = Integer.parseInt(waterinfo.get(id).get("water_remaining"));

            water_alloc = Integer.parseInt(waterinfo.get(id).get("water_allocated"));

            water_added = Integer.valueOf(amount.getText().toString());

            Log.i("Total",String.valueOf(water_remaining+water_added));

            if ((water_remaining + water_added) > water_alloc)

                Toast.makeText(TapSearchActivity.this, "Entered amount is exceeding the allocated water level for tap "+ id, Toast.LENGTH_SHORT).show();

            else {

                Map<String,Object> map = new HashMap<>();

                map.put("water_remaining",String.valueOf(water_added+water_remaining));

                FirebaseDatabase.getInstance().getReference()
                        .child("Taps")
                        .child(AdminUser.getPincode())
                        .child(id)
                        .updateChildren(map);

                map.clear();

                if(usage_info.containsKey(id)){

                    waterprovided = Integer.parseInt(usage_info.get(id).get("water_provided"));

                    map.put("water_provided",String.valueOf(water_added+waterprovided));

                    FirebaseDatabase.getInstance().getReference()
                            .child("Usage")
                            .child(date)
                            .child(AdminUser.getPincode())
                            .child(id)
                            .updateChildren(map);
                }else {

                    map.clear();

                    map.put("water_provided",String.valueOf(water_added));

                    map.put("water_used",String.valueOf(0));

                    FirebaseDatabase.getInstance().getReference()
                            .child("Usage")
                            .child(date)
                            .child(AdminUser.getPincode())
                            .child(id)
                            .setValue(map);
                }
            }
        }
    }

    public void selectTaps(View view){

        adapter = new TapSearchAdapter(list,this,idlist);

        View alertView = LayoutInflater.from(this).inflate(R.layout.selecttaps,null,false);

        final EditText searchbar = alertView.findViewById(R.id.searchbar);

        ImageButton searchbutton = alertView.findViewById(R.id.searchButton);

        final RecyclerView recyclerView = alertView.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new GridLayoutManager(this,3));

        recyclerView.setAdapter(adapter);

        loadTaps();

        searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<Tapmodel> list1 = new ArrayList<>();

                RecyclerView.Adapter adapter1;

                for (Tapmodel model : list){

                    if(model.getId().contains(searchbar.getText().toString())){

                        list1.add(model);

                    }
                }

                adapter1 = new TapSearchAdapter(list1,TapSearchActivity.this,idlist);

                recyclerView.setAdapter(adapter1);
            }
        });

        alert = new AlertDialog.Builder(this);

        alert.setView(alertView);

        alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                selectedTaps.setText(String.valueOf(idlist.size()));
            }
        });

        alert.show();
    }

    public void loadTaps(){

        FirebaseDatabase.getInstance().getReference()
                .child("Taps")
                .child(AdminUser.getPincode())
                .addValueEventListener(this);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        list.clear();

        for(DataSnapshot snapshot : dataSnapshot.getChildren()){

            Tapmodel tapmodel = snapshot.getValue(Tapmodel.class);

            list.add(tapmodel);

            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
