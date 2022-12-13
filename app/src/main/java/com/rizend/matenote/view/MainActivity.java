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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.rizend.matenote.R;
import com.rizend.matenote.adapter.NoteAdapter;
import com.rizend.matenote.databinding.ActivityMainBinding;
import com.rizend.matenote.model.Note;
import com.rizend.matenote.utils.UIUtils;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FirebaseFirestore mFirestore;
    private RecyclerView mRecycler;
    private NoteAdapter mAdapter;
    private FirebaseAuth mAuth;
    private UIUtils uiUtils;
    private String idUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        idUser = getIntent().getStringExtra("idUser");
        initView();
    }

    public void initView(){
        uiUtils = new UIUtils(this);
        loadRecycler();
        binding.btnNavMenu.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.menu_user:
                    dataUser();
                    break;
                case R.id.menu_add_note:
                    Dialog dialogNote = uiUtils.dialogCreateNote();
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
        Dialog userDialog = uiUtils.dialogUser();
        Button btnSignOut = userDialog.findViewById(R.id.btnSingOut);
        TextView tvUser = userDialog.findViewById(R.id.tvUser);
        TextView tvEmail = userDialog.findViewById(R.id.tvEmail);
        System.out.println("USUARIO ID MAIN: "+getIntent().getStringExtra("idUser"));
        if(idUser != null){
            mFirestore.collection("user").document(idUser).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    tvUser.setText("Usuario: "+documentSnapshot.get("userName"));
                    tvEmail.setText("Email: "+mAuth.getCurrentUser().getEmail());
                }
            });
        }
        btnSignOut.setOnClickListener(view -> {
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
                    Toast.makeText(getApplicationContext(),"Creada exitosamente!", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),"Error al añadir nota!", Toast.LENGTH_LONG).show();
                    System.out.println("Exception: "+e.toString());
                }
            });
        }else
            Toast.makeText(getApplicationContext(),"Debe llenar los campos!", Toast.LENGTH_LONG).show();
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
}