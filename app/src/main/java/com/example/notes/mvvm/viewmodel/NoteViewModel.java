package com.example.notes.mvvm.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.notes.mvvm.entity.Note;
import com.example.notes.mvvm.repository.Repository;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {
    private Repository mNoteRepository;
    private LiveData<List<Note>> mAllNotes;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        mNoteRepository = new Repository(application);
        mAllNotes = mNoteRepository.getAllNotes();
    }

    public void insert(Note note) {
        mNoteRepository.insert(note);
    }

    public void update(Note note) {
        mNoteRepository.update(note);
    }

    public void delete(Note note) {
        mNoteRepository.delete(note);
    }

    public void deleteAllNotes() {
        mNoteRepository.deleteAllNotes();
    }

    public LiveData<List<Note>> getAllNotes() {
        return mAllNotes;
    }
}
