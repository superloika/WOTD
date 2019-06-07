package com.example.kaloi.wotd;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.util.Calendar;


public class WOTDWidgetProvider extends AppWidgetProvider {
    private PendingIntent pendingIntent;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //SharedPreferences sPref = context.getSharedPreferences("com.example.kaloi.wotd.SETTINGS",Context.MODE_PRIVATE);
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //final AlarmManager alarmManager2 = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final Intent i = new Intent(context,WidgetUpdater.class);
        //final Intent i2 = new Intent(context,AlarmReceiver.class);
        if(pendingIntent == null) {
            pendingIntent = PendingIntent.getService(context,0,i,PendingIntent.FLAG_CANCEL_CURRENT);
            //PI = PendingIntent.getBroadcast(context,0,i2,0);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY,0);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND,0);
            calendar.set(Calendar.MILLISECOND,0);
            //alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),60000,pendingIntent);
            //if (alarmManager != null) {
                //alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(),pendingIntent);
            //}
            alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
            //alarmManager2.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,PI);
        }

        //super.onUpdate(context, appWidgetManager, appWidgetIds);
        //final int N = appWidgetIds.length;

        //Calendar calendar = Calendar.getInstance();

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context,MainActivity.class);
            pendingIntent = PendingIntent.getActivity(context, 0,intent,0);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.wotd_appwidget_layout);
            views.setOnClickPendingIntent(R.id.img_bulb,pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
        //Toast.makeText(context,"Widget updated",Toast.LENGTH_SHORT).show();
//        int word_count = sPref.getInt("WordCount",1);
//        SharedPreferences.Editor e = sPref.edit();
//        e.putInt("WordID",new Random().nextInt(word_count));
//        e.commit();
    }

}
