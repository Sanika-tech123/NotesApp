package com.spark.notesapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<Note> noteList;
    private NoteAdapter adapter;
    private FirebaseFirestore db;
    private FloatingActionButton fabAdd;

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewNotes);
        fabAdd = view.findViewById(R.id.fab_add_note);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        noteList = new ArrayList<>();

        // Initialize adapter with listener
        adapter = new NoteAdapter(getContext(), noteList, note -> {
            // Open NoteDetailFragment on item click
            NoteDetailFragment detailFragment = new NoteDetailFragment(
                    note.getId(), note.getTitle(), note.getDescription()
            );
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });

        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadNotes();

        fabAdd.setOnClickListener(v -> {
            // Open Add/Edit Note Fragment
            AddEditNoteFragment addFragment = new AddEditNoteFragment();
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, addFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void loadNotes() {
        db.collection("Notes")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        noteList.clear();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Note note = doc.toObject(Note.class);
                            note.setId(doc.getId()); // Set document ID
                            noteList.add(note);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
