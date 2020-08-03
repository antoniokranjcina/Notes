package com.example.notes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.adapter.NoteAdapter;
import com.example.notes.mvvm.entity.Note;
import com.example.notes.mvvm.viewmodel.NoteViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener, NoteAdapter.OnItemClickListener, PopupMenu.OnMenuItemClickListener, TextWatcher {
    public static final int ADD_NOTE_REQUEST = 1;
    public static final int EDIT_NOTE_REQUEST = 2;
    private final NoteAdapter noteAdapter = new NoteAdapter();

    // UI
    private FloatingActionButton mFloatingActionButton;
    private RecyclerView mRecyclerView;
    private RelativeLayout mRelativeLayoutSearchMode, mRelativeLayoutSettingsMode;
    private ImageButton mImageButtonBackArrow, mImageButtonSettings;
    private EditText mEditTextToolbarSearch, mEditTextSearch;
    private View mViewToolbarSpace;
    private TextView mTextViewAllNotes, mTextViewNotesAmount;

    // vars
    private NoteViewModel mNoteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findUIComponents();
        setListeners();
        setRecyclerView(noteAdapter);
        touchHelper();

        mNoteViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(NoteViewModel.class);
        mNoteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                noteAdapter.submitList(notes);
                noteAdapter.setInitList(noteAdapter.getCurrentList());
                updateNotesAmount();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floatingActionButtonAddNote:
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                startActivityForResult(intent, ADD_NOTE_REQUEST);
                break;
            case R.id.main_toolbar_back_button:
                disableEditMode();
                break;
            case R.id.toolbar_settings_button:
                threeDotsPopUpMenu();
                break;
        }
    }

    @Override
    public void onItemClick(Note note) {
        Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
        intent.putExtra(AddEditNoteActivity.EXTRA_ID, note.getId());
        intent.putExtra(AddEditNoteActivity.EXTRA_TITLE, note.getTitle());
        intent.putExtra(AddEditNoteActivity.EXTRA_DESCRIPTION, note.getDescription());
        startActivityForResult(intent, EDIT_NOTE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK && data != null) {
            String title = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
            Note note = new Note(title, description);
            mNoteViewModel.insert(note);
        } else if (requestCode == EDIT_NOTE_REQUEST && resultCode == RESULT_OK && data != null) {
            int id = data.getIntExtra(AddEditNoteActivity.EXTRA_ID, -1);

            if (id == -1) {
                Toast.makeText(this, "Note cannot be updated.", Toast.LENGTH_SHORT).show();
                return;
            }

            String title = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);

            Note note = new Note(title, description);
            note.setId(id);

            mNoteViewModel.update(note);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        enableSearchMode();
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.settings_button_item) {
            mNoteViewModel.deleteAllNotes();
            return true;
        }
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        noteAdapter.getFilter().filter(s.toString());
    }

    @Override
    public void onBackPressed() {
        if (mEditTextToolbarSearch.isFocused()) {
            disableEditMode();
        } else {
            super.onBackPressed();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListeners() {
        mFloatingActionButton.setOnClickListener(this);
        mEditTextSearch.setOnClickListener(this);
        mImageButtonSettings.setOnClickListener(this);
        mImageButtonBackArrow.setOnClickListener(this);
        mEditTextSearch.setOnTouchListener(this);
        noteAdapter.setOnItemClickListener(this);
        mEditTextToolbarSearch.addTextChangedListener(this);
    }

    private void findUIComponents() {
        mFloatingActionButton = findViewById(R.id.floatingActionButtonAddNote);
        mEditTextSearch = findViewById(R.id.editTextSearchNotes);
        mRelativeLayoutSearchMode = findViewById(R.id.main_back_button_container);
        mRelativeLayoutSettingsMode = findViewById(R.id.settings_button_container);
        mImageButtonBackArrow = findViewById(R.id.main_toolbar_back_button);
        mImageButtonSettings = findViewById(R.id.toolbar_settings_button);
        TextInputLayout textInputLayoutToolbar = findViewById(R.id.textInputLayoutToolbarSearch);
        mEditTextToolbarSearch = textInputLayoutToolbar.getEditText();
        mViewToolbarSpace = findViewById(R.id.view_toolbar_space);
        mTextViewAllNotes = findViewById(R.id.textViewAllNotes);
        mTextViewNotesAmount = findViewById(R.id.textViewNotesAmount);
    }

    private void setRecyclerView(NoteAdapter noteAdapter) {
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(noteAdapter);
    }

    private void touchHelper() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                mNoteViewModel.delete(noteAdapter.getNoteAt(viewHolder.getAdapterPosition()));
            }
        }).attachToRecyclerView(mRecyclerView);
    }

    private void enableSearchMode() {
        mRelativeLayoutSearchMode.setVisibility(View.VISIBLE);
        mRelativeLayoutSettingsMode.setVisibility(View.GONE);
        mEditTextToolbarSearch.setVisibility(View.VISIBLE);
        mViewToolbarSpace.setVisibility(View.GONE);
        mEditTextSearch.setVisibility(View.GONE);
        mFloatingActionButton.setVisibility(View.GONE);
        mTextViewAllNotes.setVisibility(View.GONE);
        mTextViewNotesAmount.setVisibility(View.GONE);
        mEditTextToolbarSearch.requestFocus();

//        showSoftKeyboard();
    }

    private void disableEditMode() {
        mRelativeLayoutSearchMode.setVisibility(View.GONE);
        mRelativeLayoutSettingsMode.setVisibility(View.VISIBLE);
        mEditTextToolbarSearch.setVisibility(View.GONE);
        mViewToolbarSpace.setVisibility(View.VISIBLE);
        mEditTextSearch.setVisibility(View.VISIBLE);
        mFloatingActionButton.setVisibility(View.VISIBLE);
        mTextViewAllNotes.setVisibility(View.VISIBLE);
        mTextViewNotesAmount.setVisibility(View.VISIBLE);

        hideSoftKeyboard();
    }

    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(mEditTextToolbarSearch.getWindowToken(), 0);
        }
    }

    private void updateNotesAmount() {
        int amount = noteAdapter.getItemCount();
        String string = "Notes: " + amount;
        mTextViewNotesAmount.setText(string);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void threeDotsPopUpMenu() {
        PopupMenu popupMenu = new PopupMenu(this, mImageButtonSettings);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.menu_activity_main);
        popupMenu.setGravity(Gravity.END);
        popupMenu.show();
    }
}