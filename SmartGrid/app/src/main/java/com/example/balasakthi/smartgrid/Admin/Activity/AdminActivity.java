package com.example.balasakthi.smartgrid.Admin.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.balasakthi.smartgrid.Admin.AdminUser;
import com.example.balasakthi.smartgrid.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    DatabaseReference reference;

    EditText nameEditText,passwordEditText;

    String name,password;

    TextInputLayout empidLayout,emailLayout,passwordLayout,repasswordLayout;

    Button signupButton;

    String empid,email,ppassword,repassword;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        mAuth = FirebaseAuth.getInstance();

        reference = FirebaseDatabase.getInstance().getReference();

        nameEditText = findViewById(R.id.nameEditText);

        passwordEditText  = findViewById(R.id.passwordEditText);

        dialog = new ProgressDialog(this);
    }

    public void gotoAdmin(View view) {

        name = nameEditText.getText().toString();

        password = passwordEditText.getText().toString();

        email = "abc@sample.com";

        if (!name.isEmpty() && !password.isEmpty()){

            reference.child("User").child(name).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null) {

                        email = ((HashMap<String, String>) dataSnapshot.getValue()).get("email");

                    }

                        dialog.setMessage("Loggin in....");

                        dialog.show();

                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                dialog.dismiss();

                                if (task.isSuccessful()) {

                                    if (mAuth.getCurrentUser().isEmailVerified()) {

                                        AdminUser.setEmpid(name);

                                        Intent intent = new Intent(AdminActivity.this, AdminMainActivity.class);

                                        startActivity(intent);
                                    } else {

                                        Toast.makeText(AdminActivity.this, "Email is not verified. Email verification mail sent to the registered email.", Toast.LENGTH_SHORT).show();

                                        mAuth.getCurrentUser().sendEmailVerification();
                                    }
                                } else
                                    Toast.makeText(AdminActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });
                    }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    Toast.makeText(AdminActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

    }

        else Toast.makeText(this, "Name or password is empty", Toast.LENGTH_SHORT).show();
    }

    public void signup(View view){

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        View v = LayoutInflater.from(this).inflate(R.layout.signupfragment,null,false);

        empidLayout = v.findViewById(R.id.empidLayout);

        emailLayout = v.findViewById(R.id.emailLayout);

        passwordLayout = v.findViewById(R.id.passwordLayout);

        repasswordLayout = v.findViewById(R.id.repasswordLayout);

        signupButton = v.findViewById(R.id.signupButton);

        mAuth = FirebaseAuth.getInstance();

        signupButton.setOnClickListener(s->{

            if(validate())
            {
                dialog.setMessage("Signing up....");

                dialog.show();

                reference.child("User").child(empid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.getChildrenCount() == 0){

                            mAuth.createUserWithEmailAndPassword(email,ppassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if(task.isSuccessful()){

                                        Map<String,String> map = new HashMap<>();

                                        map.put("email",email);

                                        reference.child("User").child(empid).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                dialog.dismiss();

                                                mAuth.getCurrentUser().sendEmailVerification();

                                                alertDialog.dismiss();
                                            }
                                        });
                                    }

                                    else{

                                        dialog.dismiss();

                                        Toast.makeText(AdminActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                        else {

                            dialog.dismiss();

                            Toast.makeText(AdminActivity.this, "Empid already used..", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        alertDialog.setView(v);

        alertDialog.setTitle("New User Sign up");

        alertDialog.show();
    }

    public void resetPassword(View view){

        name = nameEditText.getText().toString();

        if(name.isEmpty()){

            Toast.makeText(this, "Enter a empid", Toast.LENGTH_SHORT).show();

            return;
        }

        reference.child("User").child(name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                email = "abc@sample.com";

                if (dataSnapshot.getValue() != null) {

                    email = ((HashMap<String, String>) dataSnapshot.getValue()).get("email");

                    mAuth.sendPasswordResetEmail(email);

                    Toast.makeText(AdminActivity.this, "Password reset link is sent to your registered email", Toast.LENGTH_LONG).show();

                }else Toast.makeText(AdminActivity.this, "Please check your empid and try again", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public boolean validate(){

        empid = empidLayout.getEditText().getText().toString().trim();

        email = emailLayout.getEditText().getText().toString().trim();

        ppassword = passwordLayout.getEditText().getText().toString().trim();

        repassword = repasswordLayout.getEditText().getText().toString().trim();

        if(empid.isEmpty() || email.isEmpty() || ppassword.isEmpty()) {
            Toast.makeText(this, "Can't leave any field empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(empid.length() > 6) {
            empidLayout.setError("EmpId cannot be more than 6 characters");
            return false;
        }

        else empidLayout.setError(null);

        if (!ppassword.equals(repassword)) {
            repasswordLayout.setError("Passwords doesn't match");
            return false;
        }

        else repasswordLayout.setError(null);

        return true;
    }
}
