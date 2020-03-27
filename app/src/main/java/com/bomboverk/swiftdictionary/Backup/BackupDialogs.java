package com.bomboverk.swiftdictionary.Backup;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.bomboverk.swiftdictionary.MainActivity;
import com.bomboverk.swiftdictionary.R;
import com.bomboverk.swiftdictionary.db.DicionarioDAO;

import java.io.IOException;

public class BackupDialogs {

    MainActivity main;
    String titulo;
    String mensagem;
    int action = 0;

    public BackupDialogs(MainActivity main, String titulo, String mensagem, int action) {
        this.main = main;
        this.titulo = titulo;
        this.mensagem = mensagem;
        this.action = action;
    }

    public void createDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(main);
        builder.setMessage(mensagem)
                .setTitle(titulo)
                .setIcon(R.drawable.baseline_error_black_48)
                .setPositiveButton(R.string.dialogsim, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        DicionarioDAO dao = new DicionarioDAO(main);

                        if (action == 1) {
                            titulo = main.getString(R.string.dbt8);
                            mensagem = main.getString(R.string.dbd16);
                            action = 2134;
                            createInfoDialog();
                            //main.selectFolderToSave();
                            //dao.copyAppDbToDownloadFolder(main);

                        } else {
                            main.showChooseFile();
                        }
                    }
                })
                .setNegativeButton(R.string.dialognao, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.create();
        builder.show();
    }

    public void createInfoDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(main);
        builder.setMessage(mensagem)
                .setTitle(titulo)
                .setIcon(R.drawable.baseline_error_black_48)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (action == 0) {
                            main.restartApp();
                        }else if(action == 2134){
                            main.selectFolderToSave();
                        }
                    }
                }).setCancelable(false);
        builder.show();
    }

}
