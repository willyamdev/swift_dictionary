package com.bomboverk.swiftdictionary.SpinnerAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bomboverk.swiftdictionary.R;

import java.util.ArrayList;

public class LanguagesAdapter extends ArrayAdapter<LanguagesItens> {

    public LanguagesAdapter(Context context, ArrayList<LanguagesItens> spinnerItens){
        super(context, 0, spinnerItens);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent){

        if (convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_lang_celula, parent, false);
        }

        TextView txt = convertView.findViewById(R.id.textItemLanguage);

        LanguagesItens currentItem = getItem(position);

        if (currentItem != null){
            txt.setText(currentItem.getLanguage());
        }

        return convertView;
    }

}
