package com.bomboverk.swiftdictionary.MainAdaptador;


import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bomboverk.swiftdictionary.R;
import com.bomboverk.swiftdictionary.db.Dicionario;

public class ViewHolderDicionarios extends RecyclerView.ViewHolder {

    TextView dicionarioNome;
    TextView lingua;
    TextView quantidadePalavras;
    TextView datacricao;
    public ConstraintLayout constraintBack;

    public ViewHolderDicionarios(@NonNull View itemView) {
        super(itemView);
        dicionarioNome = itemView.findViewById(R.id.celulaNomeDic);
        lingua = itemView.findViewById(R.id.celulaLinguaDic);
        quantidadePalavras = itemView.findViewById(R.id.celulaNumeroDic);
        datacricao = itemView.findViewById(R.id.celulaDataCriacaoDic);
        constraintBack = itemView.findViewById(R.id.constraintCelulaBack);
    }


    public void bind(final Dicionario item, final AdaptadorDicionarios.OnItemClickListener listener) {

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(item, ViewHolderDicionarios.this, getAdapterPosition());
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //constraintBack.setSelected(true);
                listener.OnLongPress(item, ViewHolderDicionarios.this, getAdapterPosition());
                return true;
            }
        });
    }

}
