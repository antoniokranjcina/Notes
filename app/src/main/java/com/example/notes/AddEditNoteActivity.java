package com.example.notes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AddEditNoteActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener, TextWatcher {
    public static final String EXTRA_ID = "com.example.notes.EXTRA_ID";
    public static final String EXTRA_TITLE = "com.example.notes.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION = "com.example.notes.EXTRA_DESCRIPTION";
    private static final String TAG = "AddEditNoteActivity";
    // UI components
    private ImageButton mImageButtonBackArrow, mImageButtonUndo, mImageButtonRedo, mImageButtonSave;
    private EditText mEditTextTitle, mEditTextDescription;
    private TextView mTextViewTime;
    private boolean isAddMode = true;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_note);

        setToolbar();
        findUIComponents();
        setListeners();
        setTime();
        toggleButtons(false, 75);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ID)) {
            mEditTextTitle.setText(intent.getStringExtra(EXTRA_TITLE));
            mEditTextDescription.setText(intent.getStringExtra(EXTRA_DESCRIPTION));
            isAddMode = false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_arrow:
                imageButtonBackArrow();
                break;
            case R.id.toolbar_undo_button:
                imageButtonUndo();
                break;
            case R.id.toolbar_redo_button:
                imageButtonRedo();
                break;
            case R.id.toolbar_save_button:
                imageButtonSave();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.toString().isEmpty() || s.toString().length() == 0) {
            toggleButtons(false, 75);
        } else {
            toggleButtons(true, 255);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    private void imageButtonBackArrow() {
        finish();
    }

    private void imageButtonUndo() {

    }

    private void imageButtonRedo() {

    }

    private void imageButtonSave() {
        String title = mEditTextTitle.getText().toString().trim();
        String description = mEditTextDescription.getText().toString().trim();

        if (title.isEmpty()) {
            if (description.length() >= 16) {
                title = description.substring(0, 16);
            } else {
                title = description;
                mEditTextTitle.setText(title);
            }
        }

        mImageButtonUndo.setVisibility(View.GONE);
        mImageButtonRedo.setVisibility(View.GONE);
        mImageButtonSave.setVisibility(View.GONE);
        hideSoftKeyboard();
        mEditTextTitle.clearFocus();
        mEditTextDescription.clearFocus();

        Intent intent = new Intent(AddEditNoteActivity.this, MainActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_DESCRIPTION, description);

        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if (id != -1) {
            isAddMode = false;
            intent.putExtra(EXTRA_ID, id);
        }
        setResult(RESULT_OK, intent);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListeners() {
        mImageButtonBackArrow.setOnClickListener(this);
        mImageButtonUndo.setOnClickListener(this);
        mImageButtonRedo.setOnClickListener(this);
        mImageButtonSave.setOnClickListener(this);
        mEditTextTitle.setOnTouchListener(this);
        mEditTextDescription.setOnTouchListener(this);
        mEditTextTitle.addTextChangedListener(this);
        mEditTextDescription.addTextChangedListener(this);
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_add_edit);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        mImageButtonBackArrow = toolbar.findViewById(R.id.toolbar_back_arrow);
        mImageButtonUndo = toolbar.findViewById(R.id.toolbar_undo_button);
        mImageButtonRedo = toolbar.findViewById(R.id.toolbar_redo_button);
        mImageButtonSave = toolbar.findViewById(R.id.toolbar_save_button);
    }

    private void findUIComponents() {
        mEditTextTitle = findViewById(R.id.editTextTitle);
        mEditTextDescription = findViewById(R.id.editTextDescription);
        mTextViewTime = findViewById(R.id.textViewTime);

        mEditTextDescription.requestFocus();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setTime() {
        String string = "";

//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now = LocalDateTime.now();
        String str = dtf.format(now);

        if (isAddMode) {
            string = "Today " + str;
        }
        mTextViewTime.setText(string);
    }

    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(mEditTextTitle.getWindowToken(), 0);
            inputMethodManager.hideSoftInputFromWindow(mEditTextDescription.getWindowToken(), 0);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mImageButtonUndo.setVisibility(View.VISIBLE);
        mImageButtonRedo.setVisibility(View.VISIBLE);
        mImageButtonSave.setVisibility(View.VISIBLE);
        return false;
    }

    private void toggleButtons(boolean enable, int alpha) {
        mImageButtonUndo.setEnabled(enable);
        mImageButtonRedo.setEnabled(enable);
        mImageButtonSave.setEnabled(enable);

        mImageButtonUndo.setClickable(enable);
        mImageButtonRedo.setClickable(enable);
        mImageButtonSave.setClickable(enable);

        mImageButtonSave.setImageAlpha(alpha);
        mImageButtonUndo.setImageAlpha(alpha);
        mImageButtonRedo.setImageAlpha(alpha);
    }
}