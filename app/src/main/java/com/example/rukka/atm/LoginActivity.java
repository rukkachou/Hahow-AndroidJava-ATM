package com.example.rukka.atm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText edtAccount;
    private EditText edtPassword;
    private CheckBox chbRemAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtAccount = findViewById(R.id.edt_account);
        edtPassword = findViewById(R.id.edt_password);
        chbRemAccount = findViewById(R.id.chb_rem_account);

        edtAccount.setText(
                getSharedPreferences("atm", MODE_PRIVATE)
                        .getString("ACCOUNT", "")
        );

        chbRemAccount.setChecked(
                getSharedPreferences("atm", MODE_PRIVATE)
                        .getBoolean("REMEMBER_ACCOUNT", false)
        );
        chbRemAccount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getSharedPreferences("atm", MODE_PRIVATE)
                        .edit()
                        .putBoolean("REMEMBER_ACCOUNT", isChecked)
                        .apply();
            }
        });
    }

    public void login(View view) {
        final String userAccount = edtAccount.getText().toString();
        final String userPassword = edtPassword.getText().toString();
        FirebaseDatabase.getInstance().getReference("users").child(userAccount).child("password")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String pw = (String) dataSnapshot.getValue();
                        if (pw == null) {
                            new AlertDialog.Builder(LoginActivity.this)
                                    .setTitle("Login Result:")
                                    .setMessage("Account does not exist")
                                    .setPositiveButton("OK", null)
                                    .show();
                        } else if (pw.equals(userPassword)) {
                            boolean rememberAccount = getSharedPreferences("atm", MODE_PRIVATE).getBoolean("REMEMBER_ACCOUNT", false);
                            if (rememberAccount) {
                                getSharedPreferences("atm", MODE_PRIVATE)
                                        .edit()
                                        .putString("ACCOUNT", userAccount)
                                        .apply();
                            } else {
                                getSharedPreferences("atm", MODE_PRIVATE)
                                        .edit()
                                        .remove("ACCOUNT")
                                        .apply();
                            }
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            new AlertDialog.Builder(LoginActivity.this)
                                    .setTitle("Login Result:")
                                    .setMessage("Incorrect password")
                                    .setPositiveButton("OK", null)
                                    .show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void quit(View view) {

    }
}