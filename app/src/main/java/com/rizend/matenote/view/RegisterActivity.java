package com.rizend.matenote.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rizend.matenote.databinding.ActivityRegisterBinding;
import com.rizend.matenote.utils.UIUtils;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private Context mContext;
    private UIUtils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mContext = this;
        initView();
    }

    private void initView() {
        utils = new UIUtils(mContext);
        binding.btnSetLogin.setOnClickListener(view -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
        binding.btnSignUp.setOnClickListener(view -> {
            registerUser();
        });
    }

    private void registerUser(){
        String userName = binding.etUserName.getText().toString();
        String userEmail = binding.etEmail.getText().toString();
        String userPass = binding.etPassword.getText().toString();
        if((!userEmail.isEmpty() && !userPass.isEmpty()) && (userPass.length() > 6)){
            mAuth.createUserWithEmailAndPassword(userEmail, userPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        String id = mAuth.getCurrentUser().getUid();
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", id);
                        map.put("userName", userName);
                        map.put("userEmail", userEmail);
                        map.put("userPass", userPass);
                        mFirestore.collection("user").document(id).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                startActivity(new Intent(mContext, LoginActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                utils.myToast("Error al registrar!");
                            }
                        });
                        Toast.makeText(getApplicationContext(), "Registrado!", Toast.LENGTH_LONG).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    utils.myToast("Fallo el registro, intente nuevamente!");
                }
            });
        }else{
            utils.myToast("Datos incorrectos!");
            //utils.myToast();
        }
    }


}