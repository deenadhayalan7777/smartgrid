package com.example.balasakthi.smartgrid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.example.balasakthi.smartgrid.Admin.Activity.AdminActivity;
import com.example.balasakthi.smartgrid.People.PeopleActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goToAdmin(View view){

        Intent intent = new Intent(this,AdminActivity.class);

        startActivity(intent);
    }

    public void goToPeople(View view){

        Intent intent = new Intent(this,PeopleActivity.class);

        startActivity(intent);
    }
}
