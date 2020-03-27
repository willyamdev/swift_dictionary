package com.bomboverk.swiftdictionary;

import android.app.AlertDialog;
import android.content.DialogInterface;


import com.bomboverk.swiftdictionary.db.Dicionario;
import com.bomboverk.swiftdictionary.db.DicionarioDAO;
import com.bomboverk.swiftdictionary.db.Palavra;

public class DialogDeleteData {


    ListaPalavras lp;
    VerPalavra vp;
    String titulo;
    String descricao;
    String acao;
    Dicionario dic;
    Palavra word;

    public DialogDeleteData(ListaPalavras lp, VerPalavra vp, String titulo, String descricao, String acao, Dicionario dic, Palavra word) {
        this.lp = lp;
        this.vp = vp;
        this.titulo = titulo;
        this.descricao = descricao;
        this.acao = acao;
        this.dic = dic;
        this.word = word;
    }

    public void createDialog(int place) {

        if (place == 1){
            AlertDialog.Builder builder = new AlertDialog.Builder(lp);
            builder.setMessage(descricao)
                    .setTitle(titulo)
                    .setIcon(R.drawable.baseline_error_black_48)
                    .setPositiveButton(R.string.dialogsim, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            DicionarioDAO dao = new DicionarioDAO(lp);

                            if (acao.equals("dic")) {
                                lp.updateList = 2;
                                dao.deleteDicionario(dic);
                                lp.finish();
                            } else if (acao.equals("word")) {
                                lp.updateList = 1;
                                dao.deleteWord(word);
                                lp.createSnack(lp.getApplicationContext().getResources().getString(R.string.snackpalavraremoved));
                                lp.clear();
                            }
                            dao.close();
                        }
                    })
                    .setNegativeButton(R.string.dialognao, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
            builder.create();
            builder.show();
        }else if (place == 2){

            AlertDialog.Builder builder = new AlertDialog.Builder(vp);
            builder.setMessage(descricao)
                    .setTitle(titulo)
                    .setIcon(R.drawable.baseline_error_black_48)
                    .setPositiveButton(R.string.dialogsim, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            DicionarioDAO dao = new DicionarioDAO(vp);
                            dao.deleteWord(word);
                            dao.close();
                            vp.result = 3;
                            vp.finish();
                        }
                    })
                    .setNegativeButton(R.string.dialognao, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
            builder.create();
            builder.show();

        }
    }

}
