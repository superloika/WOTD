package com.example.kaloi.wotd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

class DBHelper extends SQLiteOpenHelper {

    DBHelper(Context context) {
        super(context, "m.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS tbl_words(id integer primary key, word text,pos text,def text,type integer);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tbl_words");
        onCreate(db);
    }

    void addWord(String word, String parts_of_speech, String definition, int type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("word", word);
        contentValues.put("pos", parts_of_speech);
        contentValues.put("def", definition);
        contentValues.put("type", type);
        db.insert("tbl_words", null, contentValues);
        db.close();
    }
    Cursor getData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery( "select * from tbl_words where id = " + id, null );
        result.moveToFirst();
        return result;
    }
    Cursor searchWord(String word){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery( "select * from tbl_words where word = '" + word + "'", null );
        result.moveToFirst();
        return result;
    }

    ArrayList<String> getAllWords(String word)
    {
        ArrayList<String> array_list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from tbl_words where word like '" + word + "%'", null );
        res.moveToFirst();

        while(!res.isAfterLast()){
            array_list.add(res.getString(res.getColumnIndex("word")));
            res.moveToNext();
        }
        res.close();
        return array_list;
    }
    public ArrayList<Integer> getAllWordIDs(String word) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from tbl_words where word like '" + word + "%'", null );
        res.moveToFirst();

        while(!res.isAfterLast()){
            arrayList.add(res.getInt(res.getColumnIndex("id")));
            res.moveToNext();
        }
        res.close();
        return arrayList;
    }
    public ArrayList<String> getPosDef(String word) {
        ArrayList<String> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from tbl_words where word like '" + word + "%'", null );
        res.moveToFirst();

        while(!res.isAfterLast()){
            arrayList.add("(" + res.getString(res.getColumnIndex("pos")) + ") " +
                    res.getString(res.getColumnIndex("def")));
            res.moveToNext();
        }
        res.close();
        return arrayList;
    }

    int getNumberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, "tbl_words");
    }

    public void deleteWord (Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("tbl_words",
                "id = ? ",
                new String[]{Integer.toString(id)});
    }

    public void updateWord (Integer id, String word, String parts_of_speech, String definition)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("word", word);
        contentValues.put("pos", parts_of_speech);
        contentValues.put("def", definition);

        db.update("tbl_words", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
    }

    public boolean isWordExisting(String word){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery( "select * from tbl_words where word = '" + word + "'", null );
        return result.moveToFirst();
    }

}

