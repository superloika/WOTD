package com.example.kaloi.wotd;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateWord extends AppCompatActivity {
    private EditText txtWord;
    private EditText txtDef;
    private Spinner spnrPos;
    private int word_id;
    private String cur_word;
    private String cur_pos;
    private String cur_def;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_word);
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.update_word_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnu_save_update:
                saveChanges();
                break;
            case R.id.mnu_cancel_update:
                startActivity(new Intent(getApplicationContext(), WordsList.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        DBHelper db = new DBHelper(getApplicationContext());

        Intent i = getIntent();
        Bundle bFromWList = i.getExtras();

        txtWord = findViewById(R.id.txt_word);
        txtDef = findViewById(R.id.txt_def);
        spnrPos = findViewById(R.id.spnr_pos);

        word_id = bFromWList.getInt("id");
        Cursor cWord = db.getData(word_id);
        cur_word = cWord.getString(cWord.getColumnIndex("word"));
        txtWord.setText(cur_word);
        cur_def = cWord.getString(cWord.getColumnIndex("def"));
        txtDef.setText(cur_def);
        cur_pos = cWord.getString(cWord.getColumnIndex("pos"));
        populatePosSpinner();
        switch (cur_pos) {
            case "noun":
                spnrPos.setSelection(0);
                break;
            case "pronoun":
                spnrPos.setSelection(1);
                break;
            case "verb":
                spnrPos.setSelection(2);
                break;
            case "adverb":
                spnrPos.setSelection(3);
                break;
            case "adj":
                spnrPos.setSelection(4);
                break;
        }

    }

    private void populatePosSpinner() {
        ArrayAdapter<CharSequence> aaPos = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.pos, android.R.layout.simple_spinner_item);
        aaPos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrPos = findViewById(R.id.spnr_pos);
        spnrPos.setAdapter(aaPos);
    }

    private void saveChanges() {
        final DBHelper db = new DBHelper(this);
        final String new_word, new_pos, new_def;
        new_word = txtWord.getText().toString();
        new_pos = spnrPos.getSelectedItem().toString();
        new_def = txtDef.getText().toString();

        if (!new_word.equals("") && !new_pos.equals("") && !new_def.equals("")) {
            if (new_word.toLowerCase().equals(cur_word.toLowerCase())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setTitle("Confirm");
                builder.setMessage("Save changes?");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.updateWord(word_id, new_word, new_pos, new_def);
                        showCustomToast("Done");
                        finish();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();
            } else {
                if (db.isWordExisting(new_word)) {
                    showCustomToast("Unable to update. The word already exists.");
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setCancelable(false);
                    builder.setTitle("Confirm");
                    builder.setMessage("Save changes?");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.updateWord(word_id, new_word, new_pos, new_def);
                            showCustomToast("Done");
                            finish();
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.create().show();
                }
            }
        } else {
            showCustomToast("Please fill all the required fields");
        }
    }

    private void showCustomToast(String msg) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) findViewById(R.id.custom_toast_container));

        TextView text = layout.findViewById(R.id.text);
        text.setText(msg);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
