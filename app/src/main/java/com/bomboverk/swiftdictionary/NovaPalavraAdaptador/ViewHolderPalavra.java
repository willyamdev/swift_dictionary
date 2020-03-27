package com.bomboverk.swiftdictionary.NovaPalavraAdaptador;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bomboverk.swiftdictionary.R;
import com.bomboverk.swiftdictionary.db.Palavra;

public class ViewHolderPalavra extends RecyclerView.ViewHolder {

    TextView palavra;
    ImageButton delete;

    public ViewHolderPalavra(@NonNull View itemView) {
        super(itemView);

        palavra = itemView.findViewById(R.id.palavra);
        delete = itemView.findViewById(R.id.btnRemove);
    }

    public void removeButton(final Palavra item, final AdaptadorPalavras.OnItemClickListener listener){

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onButtonRemove(item);
            }
        });
    }

    public void bind(final Palavra item, final AdaptadorPalavras.OnItemClickListener listener){

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(item);
            }
        });
    }
}
