package com.bomboverk.swiftdictionary.NovaPalavraAdaptador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bomboverk.swiftdictionary.R;
import com.bomboverk.swiftdictionary.db.Palavra;

import java.util.ArrayList;

public class AdaptadorPalavras extends RecyclerView.Adapter {

    ArrayList<Palavra> listaPalavras;
    Context context;
    OnItemClickListener listener;


    public AdaptadorPalavras(ArrayList<Palavra> listaPalavras, Context context, OnItemClickListener listener) {
        this.listaPalavras = listaPalavras;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.celula_palavra, parent, false);
        ViewHolderPalavra meuView = new ViewHolderPalavra(view);
        return meuView;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ViewHolderPalavra meuViewHolder = (ViewHolderPalavra) holder;

        Palavra word = listaPalavras.get(position);

        meuViewHolder.palavra.setText(word.getPalavra());

        meuViewHolder.bind(listaPalavras.get(position), listener);

        meuViewHolder.removeButton(listaPalavras.get(position), listener);


    }

    @Override
    public int getItemCount() {
        return listaPalavras.size();
    }

    public interface OnItemClickListener{
        void onItemClick(Palavra word);
        void onButtonRemove(Palavra btnremove);
    }

    public void updateList(ArrayList<Palavra> word){

        listaPalavras = new ArrayList<Palavra>();
        listaPalavras.addAll(word);
        notifyDataSetChanged();

    }


}
