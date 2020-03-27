package com.bomboverk.swiftdictionary.MainAdaptador;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bomboverk.swiftdictionary.R;
import com.bomboverk.swiftdictionary.db.Dicionario;

import java.util.ArrayList;

public class AdaptadorDicionarios extends RecyclerView.Adapter {

    ArrayList<Dicionario> dicionario;
    Context context;
    OnItemClickListener listener;

    public SparseBooleanArray selectedItems;

    public AdaptadorDicionarios(ArrayList<Dicionario> dicionario, Context context, OnItemClickListener listener) {
        this.dicionario = dicionario;
        this.context = context;
        this.listener = listener;
        selectedItems = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.celula_dicionario, parent, false);
        ViewHolderDicionarios meuView = new ViewHolderDicionarios(view);
        return meuView;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolderDicionarios meuView = (ViewHolderDicionarios) holder;

        Dicionario meuDic = dicionario.get(position);

        String quantPalavras = Integer.toString(meuDic.getQuantidadePalavras());

        meuView.dicionarioNome.setText(meuDic.getNomeDicionario()+".");
        meuView.lingua.setText(context.getResources().getString(R.string.adapdiclang )+" "+ meuDic.getLinguagem()+".");
        meuView.quantidadePalavras.setText(context.getResources().getString(R.string.adapquantword)+" "+quantPalavras+".");
        meuView.datacricao.setText(context.getResources().getString(R.string.adapcriationdate)+" " +meuDic.getDataCriacao()+".");

        meuView.constraintBack.setSelected(selectedItems.get(position, false));

        meuView.bind(dicionario.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return dicionario.size();
    }

    public interface OnItemClickListener{
        void onItemClick(Dicionario dic, ViewHolderDicionarios vholder, int position);
        void OnLongPress(Dicionario dic, ViewHolderDicionarios vholder, int position);
    }

    public void updateList(ArrayList<Dicionario> dic){
        dicionario = new ArrayList<Dicionario>();
        dicionario.addAll(dic);
        notifyDataSetChanged();
    }

}
