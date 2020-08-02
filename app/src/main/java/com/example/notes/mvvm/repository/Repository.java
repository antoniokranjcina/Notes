package com.example.notes.mvvm.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.notes.mvvm.dao.NoteDao;
import com.example.notes.mvvm.database.NoteDatabase;
import com.example.notes.mvvm.entity.Note;

import java.util.List;

public class Repository {
    private NoteDao mNoteDao;
    private LiveData<List<Note>> mAllNotes;

    public Repository(Application application) {
        NoteDatabase database = NoteDatabase.getInstance(application);
        mNoteDao = database.noteDao();
        mAllNotes = mNoteDao.getAllNotes();
    }

    public void insert(Note note) {
        new InsertNoteAsyncTask(mNoteDao).execute(note);
    }

    public void update(Note note) {
        new UpdateNoteAsyncTask(mNoteDao).execute(note);
    }

    public void delete(Note note) {
        new DeleteNoteAsyncTask(mNoteDao).execute(note);
    }

    public void deleteAllNotes() {
        new DeleteAllNotesNoteAsyncTask(mNoteDao).execute();
    }

    public LiveData<List<Note>> getAllNotes() {
        return mAllNotes;
    }

    private static class InsertNoteAsyncTask extends AsyncTask<Note, Void, Void> {
        private NoteDao mNoteDao;

        public InsertNoteAsyncTask(NoteDao noteDao) {
            this.mNoteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            mNoteDao.insert(notes[0]);
            return null;
        }
    }

    private static class UpdateNoteAsyncTask extends AsyncTask<Note, Void, Void> {
        private NoteDao mNoteDao;

        public UpdateNoteAsyncTask(NoteDao noteDao) {
            this.mNoteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            mNoteDao.update(notes[0]);
            return null;
        }
    }

    private static class DeleteNoteAsyncTask extends AsyncTask<Note, Void, Void> {
        private NoteDao mNoteDao;

        public DeleteNoteAsyncTask(NoteDao noteDao) {
            this.mNoteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            mNoteDao.delete(notes[0]);
            return null;
        }
    }

    private static class DeleteAllNotesNoteAsyncTask extends AsyncTask<Void, Void, Void> {
        private NoteDao mNoteDao;

        public DeleteAllNotesNoteAsyncTask(NoteDao noteDao) {
            this.mNoteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mNoteDao.deleteAllNotes();
            return null;
        }
    }
}
