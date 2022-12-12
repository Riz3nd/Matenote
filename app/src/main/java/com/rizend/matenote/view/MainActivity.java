package com.rizend.matenote.view;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.rizend.matenote.R;
import com.rizend.matenote.databinding.ActivityMainBinding;
import com.rizend.matenote.model.Note;
import com.rizend.matenote.utils.UIUtils;
import com.rizend.matenote.adapter.NoteAdapter;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FirebaseFirestore mFirestore;
    private RecyclerView mRecycler;
    private NoteAdapter mAdapter;
    public Boolean status = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mFirestore = FirebaseFirestore.getInstance();
        initView();
    }

    public void initView(){
        UIUtils uiUtils = new UIUtils(this);
        loadRecycler();
        binding.btnAddNote.setOnClickListener(view -> {
            Dialog dialogNote = uiUtils.dialogCreateNote();
            Button btnOK = dialogNote.findViewById(R.id.btn_dialog_ok);
            EditText etTitle = dialogNote.findViewById(R.id.et_title_note);
            EditText etNote = dialogNote.findViewById(R.id.et_note);
            btnOK.setOnClickListener(view1 -> {
                addNote(dialogNote, etTitle.getText().toString(), etNote.getText().toString());
            });
        });
    }

    private void loadRecycler(){
        mRecycler = findViewById(R.id.recyclerNotes);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        Query query = mFirestore.collection("note");
        FirestoreRecyclerOptions<Note> fireAdapter = new FirestoreRecyclerOptions.Builder<Note>().setQuery(query, Note.class).build();
        mAdapter = new NoteAdapter(fireAdapter, this);
        mAdapter.notifyDataSetChanged();
        mRecycler.setAdapter(mAdapter);
    }

    private void addNote(Dialog dialog, String titleNote, String dataNote){
        if(!titleNote.isEmpty() && !dataNote.isEmpty()){
            Map<String, Object> map = new HashMap<>();
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