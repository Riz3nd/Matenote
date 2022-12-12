package com.rizend.matenote.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.rizend.matenote.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        initView();
    }

    private void initView(){
        binding.btnLogin.setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            //initAuth();
        });
        binding.btnSignUp.setOnClickListener(view -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });
    }

    private void initAuth(){
        String email = binding.etEmail.getText().toString();
        String pass = binding.etPassword.getText().toString();
        if(!email.isEmpty() && !pass.isEmpty()){
            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isComplete())
                        Toast.makeText(getApplicationContext(),"Registrado!",Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),"Fallo el registro!",Toast.LENGTH_LONG).show();
                }
            });
        }else{
            Toast.makeText(this,"Datos incompletos!",Toast.LENGTH_LONG).show();
        }
    }
}