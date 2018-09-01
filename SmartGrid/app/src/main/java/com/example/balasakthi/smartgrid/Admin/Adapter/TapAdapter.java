package com.example.balasakthi.smartgrid.Admin.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.balasakthi.smartgrid.Admin.Activity.MapsActivity;
import com.example.balasakthi.smartgrid.Admin.Model.Tapmodel;
import com.example.balasakthi.smartgrid.R;
import com.github.lzyzsd.circleprogress.CircleProgress;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TapAdapter extends RecyclerView.Adapter<TapAdapter.ViewHolder> {

    List<Tapmodel> list;

    Context context;

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tapid;

        ImageView tap,info;

        ConstraintLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);

            tapid = itemView.findViewById(R.id.tapid);

            tap = itemView.findViewById(R.id.tapitem);

            layout = itemView.findViewById(R.id.layout);

            info = itemView.findViewById(R.id.infobutton);
        }
    }

    public TapAdapter(List<Tapmodel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public TapAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tap,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TapAdapter.ViewHolder holder, int position) {

        final Boolean[] status = new Boolean[1];

        final Tapmodel tapmodel = list.get(position);

        holder.tapid.setText(tapmodel.getId());

        status[0] = Boolean.parseBoolean(tapmodel.getStatus());

        if(status[0]) holder.tap.setImageDrawable(context.getDrawable(R.drawable.faucet_enabled));

        else holder.tap.setImageDrawable(context.getDrawable(R.drawable.faucet));

        holder.tap.setOnClickListener(v -> {

            status[0] = !status[0];

            setStatus(tapmodel.getPincode(),tapmodel.getId(),holder,status[0]);
        });

        holder.info.setOnClickListener(v -> {

            View view = LayoutInflater.from(context).inflate(R.layout.tapinfo,null);

            TextView allocated = view.findViewById(R.id.allocatedTextView);

            Switch tapswitch = view.findViewById(R.id.tapSwitch);

            CircleProgress waterlevelbar = view.findViewById(R.id.waterLevelBar);

            TextView tapid = view.findViewById(R.id.tapidTextView);

            ImageView location = view.findViewById(R.id.locationButton);

            Button viewreports = view.findViewById(R.id.viewReportsButton);

            allocated.setText(tapmodel.getWater_allocated());

            if(status[0])  tapswitch.setChecked(true);

            else tapswitch.setChecked(false);

            int waterpercent = (Integer.parseInt(tapmodel.getWater_remaining())*100) / Integer.parseInt(tapmodel.getWater_allocated());

            waterlevelbar.setProgress(waterpercent);

            tapid.setText(tapmodel.getId());

            location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context,MapsActivity.class);

                    intent.putExtra("tapid",tapmodel.getId());

                    intent.putExtra("lat",Float.valueOf(tapmodel.getLatitude()));

                    intent.putExtra("lon",Float.valueOf(tapmodel.getLongitude()));

                    intent.putExtra("isTapOn",tapswitch.isChecked());

                    context.startActivity(intent);
                }
            });

            viewreports.setOnClickListener(v1 -> {

            });

            tapswitch.setOnCheckedChangeListener((buttonView, isChecked) -> {

                setStatus(tapmodel.getPincode(),tapmodel.getId(),holder,isChecked);

            });

            AlertDialog.Builder alert = new AlertDialog.Builder(context);

            alert.setView(view);

            alert.show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setStatus(String pincode,final String id, final ViewHolder holder, final boolean status){

        Map<String,Object> map = new HashMap<>();

        map.put("status",String.valueOf(status));

        FirebaseDatabase.getInstance().getReference()
                .child("Taps")
                .child(pincode)
                .child(id)
                .updateChildren(map, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                        if(databaseError == null)

                        if(status) holder.tap.setImageDrawable(context.getDrawable(R.drawable.faucet_enabled));

                        else holder.tap.setImageDrawable(context.getDrawable(R.drawable.faucet));

                        else Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
