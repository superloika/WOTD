package com.example.kaloi.wotd;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Calendar;
import java.util.Random;

public class WidgetUpdater extends Service {

	public void MyFucntion() {
		System.out.println("Hello World");
	}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        RemoteViews view = null;
        try {
            DBHelper db = new DBHelper(this);
            SharedPreferences sPref = getApplicationContext()
                    .getSharedPreferences(getString(R.string.settings), MODE_PRIVATE);
            int allWordsCount = db.getNumberOfRows();
            //int word_id = new Random().nextInt(allWordsCount); //sPref.getInt("WordID",1);
            Calendar calendar = Calendar.getInstance();
            String dateToday = calendar.get(Calendar.MONTH) + "-" +
                    calendar.get(Calendar.DAY_OF_MONTH) + "-" + calendar.get(Calendar.YEAR);
            int word_id = sPref.getInt("WordID", 1);
            String date = sPref.getString("Date", "gg");

           /* if the word's assigned date is not equal to the current date,it is the time
              to randomize another word to be displayed by the next widget update*/
            if (!date.equals(dateToday)) {
                word_id = new Random().nextInt(allWordsCount);
                if (word_id == 0) {
                    word_id++;
                }
            }
            SharedPreferences.Editor e = sPref.edit();
            e.putInt("WordID", word_id);
            e.putString("Date", dateToday);
            e.apply();

            Log.i("KALOY SAYS", "word_id = " + word_id + "");
            Cursor cWords = db.getData(word_id);
            cWords.moveToFirst();
            String pos = cWords.getString(cWords.getColumnIndex("pos"));
            String word = cWords.getString(cWords.getColumnIndex("word"));
            String def = cWords.getString(cWords.getColumnIndex("def"));
            Log.i("KALOY SAYS", "word is = " + word);

//            word_id = new Random().nextInt(allWordsCount);
//            SharedPreferences.Editor e = sPref.edit();
//            e.putInt("WordID", word_id);
//            e.commit();
//            Log.i("KALOY SAYS", "New word_id = " + word_id);

            view = new RemoteViews(getPackageName(), R.layout.wotd_appwidget_layout);
            view.setTextViewText(R.id.txt_word, word);
            view.setTextViewText(R.id.txt_pos, " (" +pos + ")");
            view.setTextViewText(R.id.txt_des, def);
            db.close();

        } catch (Exception e) {
            Log.e("KALOY SAYS", e.getMessage());
        }

        ComponentName theWidget = new ComponentName(this, WOTDWidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        manager.getAppWidgetIds(theWidget);
        manager.updateAppWidget(theWidget, view);
        //return super.onStartCommand(intent, flags, startId);
        //onTrimMemory(TRIM_MEMORY_COMPLETE);

        //stopSelf();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
