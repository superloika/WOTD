package com.example.kaloi.wotd;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Splash extends AppCompatActivity {
    private TextView txtStatus;
    private ProgressBar pbStatus;
    private SharedPreferences sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        txtStatus = findViewById(R.id.txt_status);
        pbStatus = findViewById(R.id.pb_splash);
        //startService(new Intent(getApplicationContext(),WOTDService.class));
        sPref = getSharedPreferences(getString(R.string.settings), MODE_PRIVATE);
        if (sPref.getInt("NotFirstRun", 0) == 0) {
            new InitDb().execute("FirstRun");
        } else {
            pbStatus.setVisibility(View.INVISIBLE);
            //new InitDb().execute("NotFirstRun");
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                }
            };
            thread.start();
        }

    } // eo onCreate

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    // An inner class for database initialization
    class InitDb extends AsyncTask<String, Integer, String> {

        final DBHelper db = new DBHelper(getApplicationContext());
        String word, pos, def;
        JSONObject jsonObject, reader;
        JSONArray jsonArray;
        BufferedReader br;
        String strJSON = "";
        int numberOfWords;

        @Override
        protected String doInBackground(String... str) {
            Looper.prepare();
            if (str[0].equals("FirstRun")) {
                try {
                    br = new BufferedReader(new InputStreamReader(getAssets()
                            .open("wotd_data.json")));
                    String line;
                    while ((line = br.readLine()) != null) {
                        //stringBuffer.append(line);
                        strJSON += line;
                    }
                    //strJSON = stringBuffer.toString();
                    strJSON = strJSON.replace("null", "");
                    strJSON = strJSON.trim();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                try {
                    reader = new JSONObject(strJSON);
                    jsonArray = reader.names();
                    numberOfWords = jsonArray.length();
                    pbStatus.setMax(numberOfWords);
                    pbStatus.setIndeterminate(false);
                    for (int i = 0; i < numberOfWords; i++) {
                        word = jsonArray.getString(i);
                        jsonObject = reader.getJSONObject(word);
                        pos = jsonObject.getString("pos");
                        def = jsonObject.getString("def");
                        db.addWord(word, pos, def, 0);
                        publishProgress(i + 1);
                    }

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                } finally {
                    db.close();
                    SharedPreferences.Editor editor = sPref.edit();
                    editor.putInt("NotFirstRun", 1);
                    editor.apply();
                }
            }
            Looper.loop();
            return null;
        } // eo doInBackground

        @Override
        protected void onProgressUpdate(Integer... values) {
            txtStatus.setText("Initializing...");
            pbStatus.setProgress(values[0]);
            if (pbStatus.getProgress() == pbStatus.getMax()) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        }
    }
//    public class myReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            startActivity(new Intent(getApplicationContext(),MainActivity.class));
//        }
//    }

//     class InitDatabase extends AsyncTask<String,Integer,String> {
//
//        @Override
//        protected String doInBackground(String... strings) {
//            String word,pos,def;
//            String strJSON = "";
//            int numberOfWords;
//
//            try {
//
//                String line = "";
//                Looper.prepare();
//                while ((line = br.readLine()) != null) {
//                    strJSON += line;
//                }
//                strJSON = strJSON.replace("null","");
//                strJSON = strJSON.trim();
//            } catch (Exception e) {
//                System.out.println(e.getMessage());
//            }
//
//            try {
//                DBHelper db = new DBHelper(getApplicationContext());
//                JSONObject reader = new JSONObject(strJSON);
//                JSONObject jsonObject;
//                JSONArray jsonArray = reader.names();
//                numberOfWords = jsonArray.length();
//                pbStatus.setMax(numberOfWords);
//                Looper.prepare();
//                for(int i=0;i<numberOfWords;i++) {
//
//                    word = jsonArray.getString(i);
//                    jsonObject = reader.getJSONObject(word);
//                    pos = jsonObject.getString("pos");
//                    def = jsonObject.getString("def");
//                    txtStatus.setText("Initializing (" + word +")");
//                    db.addWord(word,pos,def,0);
//
//                    Toast.makeText(getApplicationContext(),word,Toast.LENGTH_SHORT).show();
//                    publishProgress(i);
//                }
//                //Intent i = new Intent(getApplicationContext(),MainActivity.class);
//                //startActivity(i);
//
//            } catch (Exception e) {
//                System.out.println(e.getMessage());
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... values) {
//            pbStatus.incrementProgressBy(values[0]);
//        }
//
//    } // eo InitDatabase
}