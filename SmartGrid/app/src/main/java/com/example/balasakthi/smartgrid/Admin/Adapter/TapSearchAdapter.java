package com.example.balasakthi.smartgrid.Admin.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.example.balasakthi.smartgrid.Admin.Model.Tapmodel;
import com.example.balasakthi.smartgrid.R;
import java.util.List;

public class TapSearchAdapter extends RecyclerView.Adapter<TapSearchAdapter.ViewHolder> {

    List<Tapmodel> list;

    Context context;

    List<String> idlist;

    public TapSearchAdapter(List<Tapmodel> list, Context context,List<String> idlist) {
        this.list = list;
        this.context = context;
        this.idlist = idlist;
        this.idlist.clear();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tapsearchitem,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final Tapmodel model = list.get(position);

        holder.tapid.setText(model.getId());

        holder.tapid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                holder.tapid.setChecked(isChecked);

                if(idlist.contains(model.getId())) idlist.remove(model.getId());

                else idlist.add(model.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CheckBox tapid;

        public ViewHolder(View itemView) {
            super(itemView);

            tapid = itemView.findViewById(R.id.tapsearchitem);
        }
    }
}
