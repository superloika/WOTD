package com.example.kaloi.wotd;

import android.content.DialogInterface;
import android.content.Intent;
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

public class AddWord extends AppCompatActivity {
    private Spinner spnrPos;
    private EditText txtWord;
    private EditText txtDef;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        txtDef = findViewById(R.id.txt_def);
        txtWord = findViewById(R.id.txt_word);

        populatePosSpinner();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.addword_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnu_cancel_add:
                confirmCancelAdding();
                break;
            case R.id.mnu_save_add:
                saveWord();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmCancelAdding() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.getContext().setTheme(R.style.AppTheme);
        builder.setCancelable(false);
        builder.setTitle("Confirm");
        builder.setMessage("Cancel adding?");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create().show();
    }

    private void saveWord() {
        DBHelper db = new DBHelper(getApplicationContext());
        String word = txtWord.getText().toString();
        String pos = spnrPos.getSelectedItem().toString();
        String def = txtDef.getText().toString();

        if (!word.equals("") && !def.equals("")) {
            if (db.isWordExisting(word)) {
                showCustomToast(word + " already exists!");
            } else {
                db.addWord(word,pos,def,1);
                showCustomToast("New word is added!");
                //startActivity(new Intent(getApplicationContext(),WordsList.class));
                finish();
            }
        } else {
            showCustomToast("Cannot proceed saving. All fields are required.");
        }
    }

    private void populatePosSpinner() {
        ArrayAdapter<CharSequence> aaPos = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.pos,android.R.layout.simple_spinner_item);
        aaPos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrPos = findViewById(R.id.spnr_pos);
        spnrPos.setAdapter(aaPos);
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
