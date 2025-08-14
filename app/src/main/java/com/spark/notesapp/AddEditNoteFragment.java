package com.spark.notesapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;



public class AddEditNoteFragment extends Fragment {

    private EditText editTextTitle, editTextDescription;
    private Button buttonSave;


    private FirebaseFirestore db;

    // If editing an existing note
    private String noteId;
    private String existingTitle;
    private String existingDescription;

    public AddEditNoteFragment() {}

    public AddEditNoteFragment(String noteId, String title, String description){
        this.noteId = noteId;
        this.existingTitle = title;
        this.existingDescription = description;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_edit_note, container, false);

        editTextTitle = view.findViewById(R.id.editTextTitle);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        buttonSave = view.findViewById(R.id.buttonSave);

        db = FirebaseFirestore.getInstance();

        // Populate fields if editing
        if(existingTitle != null && existingDescription != null){
            editTextTitle.setText(existingTitle);
            editTextDescription.setText(existingDescription);
            buttonSave.setText("Update Note");
        }

        buttonSave.setOnClickListener(v -> saveNote());

        return view;
    }

    private void saveNote() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        if(TextUtils.isEmpty(title) || TextUtils.isEmpty(description)){
            Toast.makeText(getContext(), "Please enter title and description", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> note = new HashMap<>();
        note.put("title", title);
        note.put("description", description);

        if(noteId != null){
            // Update existing note
            DocumentReference noteRef = db.collection("Notes").document(noteId);
            noteRef.set(note)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Note updated", Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().popBackStack();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            // Add new note
            db.collection("Notes")
                    .add(note)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getContext(), "Note added", Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().popBackStack();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}
