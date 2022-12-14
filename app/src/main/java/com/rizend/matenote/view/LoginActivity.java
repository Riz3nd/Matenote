package com.rizend.matenote.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rizend.matenote.databinding.ActivityLoginBinding;
import com.rizend.matenote.utils.UIUtils;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private Context mContext;
    private UIUtils utils;
    private GoogleSignInOptions signInOptions;
    private GoogleSignInClient signInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        signInClient = GoogleSignIn.getClient(this, signInOptions);
        mContext = this;
        initView();
    }

    private void initView(){
        utils = new UIUtils(mContext);
        binding.btnLogin.setOnClickListener(view -> {
            initAuth();
        });
        binding.btnSignUp.setOnClickListener(view -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });
        binding.btnGoogle.setOnClickListener(view -> {
            signInGoogle();
        });
    }

    private void initAuth(){
        String email = binding.etEmail.getText().toString();
        String pass = binding.etPassword.getText().toString();
        if(!email.isEmpty() && !pass.isEmpty()){
            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isComplete()){
                        if(task.isSuccessful()){
                            Intent i = new Intent(LoginActivity.this, NoteActivity.class);
                            i.putExtra("idUser", mAuth.getUid());
                            System.out.println("OSCAR ->>>>>>>>>>>>>> "+mAuth.getUid());
                            startActivity(i);
                            finish();
                        }
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    utils.myToast("Datos incorrectos!");
                }
            });
        }else{
            utils.myToast("Datos incompletos!");
        }
    }

    private void signInGoogle(){
       //GoogleSignInAccount gSignIn = GoogleSignIn.
        Intent i = signInClient.getSignInIntent();
        startActivityForResult(i, 100);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            Intent i = new Intent(this, NoteActivity.class);
            i.putExtra("idUser", mAuth.getUid());
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                task.getResult(ApiException.class);
                Intent i = new Intent(mContext, NoteActivity.class);
                i.putExtra("signInGoogle",true);
                startActivity(i);
                finish();
            }catch (ApiException e){
                utils.myToast("Error al iniciar sesion!");
            }
        }
    }
}