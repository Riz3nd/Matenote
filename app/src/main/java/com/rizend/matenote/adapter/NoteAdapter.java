package com.rizend.matenote.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rizend.matenote.R;
import com.rizend.matenote.model.Note;
import com.rizend.matenote.utils.UIUtils;

import java.util.HashMap;
import java.util.Map;

public class NoteAdapter extends FirestoreRecyclerAdapter<Note, NoteAdapter.ViewHolder> {
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private Context mContext;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public NoteAdapter(FirestoreRecyclerOptions<Note> options, Context context) {
        super(options);
        this.mContext = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Note note) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        final String id = documentSnapshot.getId();
        holder.title.setText(note.getTitleNote());
        holder.note.setText(note.getDataNote());
        holder.deleteNote.setOnClickListener(view -> {
            deleteNote(id);
        });
        holder.editNote.setOnClickListener(view -> {
            updateNote(id, note.getTitleNote(), note.getDataNote());
        });
    }
/*
    private void getNote(String id){
        mFirestore.collection("note").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String titleNote = documentSnapshot.getString("titleNote");
                String dataNote = documentSnapshot.getString("dataNote");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
*/

    @Override
    public int getItemCount() {
        if (super.getItemCount() != 0)
            return super.getItemCount();
        else
            return 0;
    }

    private void updateNote(String id, String title, String data) {
        UIUtils uiUtils = new UIUtils(mContext);
        Dialog dialog = uiUtils.dialogEditNote();
        Button btnOk = dialog.findViewById(R.id.btn_dialog_ok);
        EditText etTitle = dialog.findViewById(R.id.et_title_note);
        EditText etNote = dialog.findViewById(R.id.et_note);
        etTitle.setText(title);
        etNote.setText(data);
        btnOk.setOnClickListener(view -> {
            if(!etTitle.getText().toString().isEmpty() && !etNote.getText().toString().isEmpty()){
                Map<String, Object> map = new HashMap<>();
                map.put("titleNote", etTitle.getText().toString());
                map.put("dataNote", etNote.getText().toString());
                mFirestore.collection("note").document(id).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(mContext,"Editada exitosamente!", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext,"Error al editar nota!", Toast.LENGTH_LONG).show();
                        System.out.println("Exception: "+e.toString());
                    }
                });
            }else
                Toast.makeText(mContext,"Debe completar los campos!", Toast.LENGTH_LONG).show();
        });
    }

    private void deleteNote(String id){
        mFirestore.collection("note").document(id).delete();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_element_notes, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, note;
        ImageView deleteNote, editNote;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_title_note);
            note = itemView.findViewById(R.id.tv_note);
            deleteNote = itemView.findViewById(R.id.delete_note);
            editNote = itemView.findViewById(R.id.edit_note);
        }
    }

}
