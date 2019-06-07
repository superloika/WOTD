package com.example.kaloi.wotd;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

class WListAAdapter extends ArrayAdapter<String>{

    private final Activity context;
    private final ArrayList<String> word;
    private final ArrayList<String> posdef;
    public WListAAdapter(Activity context,
                      ArrayList<String> word, ArrayList<String> posdef) {
        super(context, R.layout.lv_words_li, word);
        this.context = context;
        this.word = word;
        this.posdef = posdef;
    }
    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.lv_words_li, null, true);
        TextView tvWord = rowView.findViewById(R.id.tv_word);
        TextView tvDef = rowView.findViewById(R.id.tv_def);

        ImageView ivB =  rowView.findViewById(R.id.iv_b);
        tvWord.setText(word.get(position));
        tvDef.setText(posdef.get(position));

        ivB.setImageResource(R.drawable.b);
        return rowView;
    }
}
