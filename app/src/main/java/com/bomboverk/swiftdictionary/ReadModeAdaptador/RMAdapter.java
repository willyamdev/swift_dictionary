package com.bomboverk.swiftdictionary.ReadModeAdaptador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bomboverk.swiftdictionary.MainAdaptador.ViewHolderDicionarios;
import com.bomboverk.swiftdictionary.R;
import com.bomboverk.swiftdictionary.db.Dicionario;
import com.bomboverk.swiftdictionary.db.Palavra;

import java.util.ArrayList;

public class RMAdapter extends RecyclerView.Adapter {

    ArrayList<Palavra> palavra;
    Context context;
    OnItemClickListener listener;

    public RMAdapter(ArrayList<Palavra> palavra, Context context, OnItemClickListener listener) {
        this.palavra = palavra;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.celula_palavra_readmode, parent, false);
        ReadMoreViewHolder meuView = new ReadMoreViewHolder(view);
        return meuView;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ReadMoreViewHolder meuView = (ReadMoreViewHolder) holder;

        Palavra meuWord = palavra.get(position);

        meuView.txtWord.setText(meuWord.getPalavra());
        meuView.bind(palavra.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return palavra.size();
    }

    public interface OnItemClickListener{
        void onItemClick(Palavra dic);
    }

    public void updateList(ArrayList<Palavra> word){
        palavra = new ArrayList<Palavra>();
        palavra.addAll(word);
        notifyDataSetChanged();
    }
}
