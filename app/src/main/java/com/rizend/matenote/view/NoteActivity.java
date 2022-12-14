package com.rizend.matenote.view;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.rizend.matenote.R;
import com.rizend.matenote.adapter.NoteAdapter;
import com.rizend.matenote.databinding.ActivityNoteBinding;
import com.rizend.matenote.model.Note;
import com.rizend.matenote.utils.UIUtils;

import java.util.HashMap;
import java.util.Map;

public class NoteActivity extends AppCompatActivity {
    private ActivityNoteBinding binding;
    private FirebaseFirestore mFirestore;
    private RecyclerView mRecycler;
    private NoteAdapter mAdapter;
    private FirebaseAuth mAuth;
    private UIUtils utils;
    private String idUser;
    private GoogleSignInOptions signInOptions;
    private GoogleSignInClient signInClient;
    private GoogleSignInAccount signInAccount;
    private Boolean flagG = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        signInClient = GoogleSignIn.getClient(this, signInOptions);
        signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        initView();
    }

    public void initView(){
        if(getIntent().getBooleanExtra("signInGoogle", false)){
            idUser = signInAccount.getId();
            flagG  = true;
            registerUserGoogle(idUser, signInAccount.getDisplayName(), signInAccount.getEmail());
            System.out.println("OSCAR GOOGLE -----> "+idUser);
        }else{
            idUser = getIntent().getStringExtra("idUser");
        }
        utils = new UIUtils(this);
        loadRecycler();
        binding.btnNavMenu.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.menu_user:
                    dataUser();
                    break;
                case R.id.menu_add_note:
                    Dialog dialogNote = utils.dialogCreateNote();
                    Button btnOK = dialogNote.findViewById(R.id.btn_dialog_ok);
                    EditText etTitle = dialogNote.findViewById(R.id.et_title_note);
                    EditText etNote = dialogNote.findViewById(R.id.et_note);
                    btnOK.setOnClickListener(view1 -> {
                        addNote(dialogNote, etTitle.getText().toString(), etNote.getText().toString());
                    });
                    break;
            }
            return false;
        });
    }

    private void loadRecycler(){
        mRecycler = findViewById(R.id.recyclerNotes);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        Query query = mFirestore.collection("note").whereEqualTo("userNote", idUser);
        FirestoreRecyclerOptions<Note> fireAdapter = new FirestoreRecyclerOptions.Builder<Note>().setQuery(query, Note.class).build();
        mAdapter = new NoteAdapter(fireAdapter, this);
        mAdapter.notifyDataSetChanged();
        mRecycler.setAdapter(mAdapter);
    }

    private void dataUser(){
        Dialog userDialog = utils.dialogUser();
        Button btnSignOut = userDialog.findViewById(R.id.btnSingOut);
        TextView tvUser = userDialog.findViewById(R.id.tvUser);
        TextView tvEmail = userDialog.findViewById(R.id.tvEmail);
        if(idUser != null){
            if(!flagG){
                mFirestore.collection("user").document(idUser).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        tvUser.setText("Usuario: "+documentSnapshot.get("userName"));
                        tvEmail.setText("Email: "+mAuth.getCurrentUser().getEmail());
                    }
                });
            }else{
                tvUser.setText("Usuario: "+signInAccount.getDisplayName());
                tvEmail.setText("Email: "+signInAccount.getEmail());
            }
        }
        btnSignOut.setOnClickListener(view -> {
            if(flagG){
                signInClient.signOut();
                flagG = false;
            } else
                mAuth.signOut();
            userDialog.dismiss();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void addNote(Dialog dialog, String titleNote, String dataNote){
        if(!titleNote.isEmpty() && !dataNote.isEmpty()){
            Map<String, Object> map = new HashMap<>();
            map.put("userNote", idUser);
            map.put("titleNote", titleNote);
            map.put("dataNote", dataNote);
            mFirestore.collection("note").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    utils.myToast("Creada exitosamente!");
                    dialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    utils.myToast("Error al añadir nota!");
                    System.out.println("Exception: "+e.toString());
                }
            });
        }else
            utils.myToast("Debe llenar los campos!");
    }

    @Override
    public void onBackPressed() {}

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    private void registerUserGoogle(String id, String userName, String userEmail){
        if((!userEmail.isEmpty() && !userName.isEmpty())){
            mAuth.createUserWithEmailAndPassword(userEmail, userName.trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", id);
                        map.put("userName", userName);
                        map.put("userEmail", userEmail);
                        map.put("userPass", userName);
                        mFirestore.collection("user").document(id).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                utils.myToast("Error!");
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }
    }
}