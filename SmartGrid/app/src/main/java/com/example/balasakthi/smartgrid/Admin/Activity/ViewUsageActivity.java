package com.example.balasakthi.smartgrid.Admin.Activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.balasakthi.smartgrid.Admin.AdminUser;
import com.example.balasakthi.smartgrid.Admin.Model.ViewUsageModel;
import com.example.balasakthi.smartgrid.R;
import com.github.lzyzsd.circleprogress.CircleProgress;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewUsageActivity extends AppCompatActivity {

    static List<ViewUsageModel> list = new ArrayList<>();

    static TextView allocated,date,savings;

    static CircleProgress progress;

    static int usage = 0;

    static int provided = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_usage);

        allocated = findViewById(R.id.allocatedTextView);

        date = findViewById(R.id.dateTextView);

        savings = findViewById(R.id.savingsTexView);

        progress = findViewById(R.id.circleProgress);

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);

        date.setText(String.valueOf(day)+"-"+
                String.valueOf(month)+"-"+String.valueOf(year));

        loadModelList();
    }

    public static void loadModelList(){

        Log.i("date",date.getText().toString());

        FirebaseDatabase.getInstance().getReference()
                .child("Usage")
                .child(date.getText().toString())
                .child(AdminUser.getPincode())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        usage = 0;
                        provided = 1;

                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            Log.i("snapshot",snapshot.toString());

                            ViewUsageModel model = snapshot.getValue(ViewUsageModel.class);

                            usage += Integer.valueOf(model.getWater_used());

                            provided += Integer.valueOf(model.getWater_provided());
                        }

                        allocated.setText(String.valueOf(provided-1));

                        savings.setText(String.valueOf(provided-usage-1));

                        progress.setProgress(((provided-usage-1)*100)/provided);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user

            month = month+1;

            date.setText(String.valueOf(day)+"-"+
            String.valueOf(month)+"-"+String.valueOf(year));

            loadModelList();
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
}
