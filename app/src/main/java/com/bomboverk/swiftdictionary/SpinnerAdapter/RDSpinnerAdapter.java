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

public class RDSpinnerAdapter extends ArrayAdapter<RDSpinnerItens> {

    public RDSpinnerAdapter(Context context, ArrayList<RDSpinnerItens> rdSpinnerItens){
        super(context, 0, rdSpinnerItens);
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_celula_readmode, parent, false);
        }

        TextView txt = convertView.findViewById(R.id.textspinnertxt);

        RDSpinnerItens currentItem = getItem(position);

        if (currentItem != null){
            txt.setText(currentItem.getNomeDic());
        }

        return convertView;
    }
}
