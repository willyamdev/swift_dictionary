package com.bomboverk.swiftdictionary.ReadModeAdaptador;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bomboverk.swiftdictionary.R;
import com.bomboverk.swiftdictionary.db.Palavra;

public class ReadMoreViewHolder extends RecyclerView.ViewHolder {

    TextView txtWord;

    public ReadMoreViewHolder(@NonNull View itemView) {
        super(itemView);
        txtWord = itemView.findViewById(R.id.wordreadmode);
    }

    public void bind(final Palavra item, final RMAdapter.OnItemClickListener listener) {

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(item);
            }
        });
    }
}
