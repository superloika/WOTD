package com.example.kaloi.wotd;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class WordsList extends AppCompatActivity {
    private final DBHelper db = new DBHelper(this);
    private ArrayList<String> alWords;
    private ArrayList<String> alPosDef;
    ArrayAdapter<String> aaWords;
    private ArrayList<Integer> alWordIDs;

    private EditText txtSearch;
    private ListView lvWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words_list);

        txtSearch = findViewById(R.id.txt_search);
        lvWords = findViewById(R.id.lv_words);
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                populateWordsToListView(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        populateWordsToListView("");
        registerForContextMenu(lvWords);
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateWordsToListView(txtSearch.getText().toString());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.wlist_context_menu, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    private int word_id;
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        word_id = alWordIDs.get((int) info.id);
        switch (item.getItemId()) {
            case R.id.ctx_mnu_edit:
                Intent i = new Intent(getApplicationContext(),UpdateWord.class);
                i.putExtra("id",word_id);
                startActivity(i);
                break;
            case R.id.ctx_mnu_delete:
                confirmDelete();
                break;
        }
        return super.onContextItemSelected(item);
    }
    private void confirmDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Confirm");
        builder.setMessage("Delete word?");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.deleteWord(word_id);
                showCustomToast("Word successfully deleted.");
                onResume();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.wl_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnu_add_word:
                startActivity(new Intent(getApplicationContext(), AddWord.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void populateWordsToListView(CharSequence cs) {
        alWords = db.getAllWords(cs.toString());
        alPosDef = db.getPosDef(cs.toString());
        alWordIDs = db.getAllWordIDs(cs.toString());
        //aaWords = new ArrayAdapter<String>(getApplicationContext(),R.layout.lv_words_li,alWords);
        WListAAdapter wListAAdapter = new WListAAdapter(WordsList.this, alWords, alPosDef);
        lvWords.setAdapter(wListAAdapter);
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
}