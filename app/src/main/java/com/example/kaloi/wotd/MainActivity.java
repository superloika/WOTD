package com.example.kaloi.wotd;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private AutoCompleteTextView txtSearch;
    private TextView txtDef;

    private final DBHelper db = new DBHelper(this);

    //@TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, WidgetUpdater.class);
        startService(intent);

        txtSearch = findViewById(R.id.txt_search);
        txtDef = findViewById(R.id.txt_def);
        ArrayList<String> words;

        words = db.getAllWords("");
        ArrayAdapter<String> wordsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, words);
        txtSearch.setAdapter(wordsAdapter);
        txtSearch.setThreshold(1);
        txtSearch.setDropDownBackgroundResource(R.drawable.actv_dropdown_bg);
        //txtSearch.setDropDownHeight(200);
        txtSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                search();
            }
        });
        txtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search();
                    return true;
                }
                if (event.getKeyCode() == KeyEvent.KEYCODE_BREAK) {
                    search();
                    return true;
                }
                return false;
            }
        });

    }

    //        ///////////////////////////////////////////
//
//            Intent x = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//            x.addCategory(Intent.CATEGORY_OPENABLE);
//            x.setType("*/*");
//            startActivityForResult(x,PICK_DB_REQUEST);
//        ///////////////////////////////////////////
//    }

//    final int PICK_DB_REQUEST = 25;
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        //super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == PICK_DB_REQUEST) {
//            if(resultCode == RESULT_OK) {
//                String fileLoc = data.getData().toString();
//                Toast.makeText(getApplicationContext(),fileLoc,Toast.LENGTH_LONG).show();
//                txtDef.setText(fileLoc);
//            }
//        }
//    }

    private void search() {

        String word = txtSearch.getText().toString();
        try {
            Cursor cResult = db.searchWord(word);
            //cResult.moveToFirst();
            //String wrd = cResult.getString(cResult.getColumnIndex("word"));
            String def = cResult.getString(cResult.getColumnIndex("def"));
            String pos = cResult.getString(cResult.getColumnIndex("pos"));
            if(def.equals("")) {
                txtDef.setText(word + " not found...");
            } else {
                txtDef.setText(word + " (" + pos + ") - " + def);
            }
        } catch (Exception e) {
            txtDef.setText(word + " not found...");
            Log.e("KALOY SAYS",e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnu_about:
                startActivity(new Intent(this,About.class));
                break;
            case R.id.mnu_wlist:
                startActivity(new Intent(this,WordsList.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void clearSearchTextView(View v) {
        txtSearch.setText("");
    }
}
