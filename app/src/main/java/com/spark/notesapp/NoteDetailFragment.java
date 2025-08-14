package com.spark.notesapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

public class NoteDetailFragment extends Fragment {

    private TextView textTitle, textDescription;
    private Button buttonEdit, buttonDelete;
    private FirebaseFirestore db;

    private String noteId;
    private String noteTitle;
    private String noteDescription;

    public NoteDetailFragment() {}

    public NoteDetailFragment(String noteId, String title, String description){
        this.noteId = noteId;
        this.noteTitle = title;
        this.noteDescription = description;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_note_detail, container, false);

        textTitle = view.findViewById(R.id.textTitle);
        textDescription = view.findViewById(R.id.textDescription);
        buttonEdit = view.findViewById(R.id.buttonEdit);
        buttonDelete = view.findViewById(R.id.buttonDelete);

        db = FirebaseFirestore.getInstance();

        // Show note details
        textTitle.setText(noteTitle);
        textDescription.setText(noteDescription);

        buttonEdit.setOnClickListener(v -> {
            // Navigate to AddEditNoteFragment in edit mode
            AddEditNoteFragment editFragment = new AddEditNoteFragment(noteId, noteTitle, noteDescription);
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, editFragment)
                    .addToBackStack(null)
                    .commit();
        });

        buttonDelete.setOnClickListener(v -> deleteNote());

        return view;
    }

    private void deleteNote() {
        if(noteId == null) return;

        db.collection("Notes").document(noteId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Note deleted", Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().popBackStack(); // Go back to HomeFragment
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
