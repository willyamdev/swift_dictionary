package com.bomboverk.swiftdictionary.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.documentfile.provider.DocumentFile;

import com.bomboverk.swiftdictionary.Backup.BackupDialogs;
import com.bomboverk.swiftdictionary.Backup.FileUtils;
import com.bomboverk.swiftdictionary.MainActivity;
import com.bomboverk.swiftdictionary.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;


public class DicionarioDAO extends SQLiteOpenHelper {

    private static final String DATABASE = "db_dicionarios";
    private static final String TBDICS = "tb_infodics";
    private static final String WORDSTABLE = "tb_words";
    private static final int VERSAO = 1;


    public DicionarioDAO(Context context) {
        super(context, DATABASE, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String dbcreate = "CREATE TABLE IF NOT EXISTS " + TBDICS
                + "(id INTEGER PRIMARY KEY,"
                + "nome TEXT NOT NULL,"
                + "lingua TEXT NOT NULL,"
                + "datacricao TEXT)";

        db.execSQL(dbcreate);

        String tbchildcreate = "CREATE TABLE IF NOT EXISTS " + WORDSTABLE
                + "(id INTEGER PRIMARY KEY,"
                + "id_tabela INTEGER NOT NULL,"
                + "palavra TEXT NOT NULL,"
                + "traducao TEXT NOT NULL,"
                + "detalhes TEXT NOT NULL,"
                + "FOREIGN KEY(id_tabela) REFERENCES " + TBDICS + "(id))";

        db.execSQL(tbchildcreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void inserirDicionario(Dicionario dic) {

        ContentValues values = new ContentValues();

        values.put("nome", dic.getNomeDicionario());
        values.put("lingua", dic.getLinguagem());
        values.put("datacricao", dic.getDataCriacao());

        getWritableDatabase().insert(TBDICS, null, values);

    }

    public ArrayList<Palavra> getPalavrasDicionario(Dicionario dic) {

        ArrayList<Palavra> palavras = new ArrayList<Palavra>();
        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM " + WORDSTABLE + " WHERE id_tabela = " + dic.getDicionarioID(), null);

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {

                Palavra word = new Palavra();

                word.setWordId(c.getLong(c.getColumnIndex("id")));
                word.setPalavra(c.getString(c.getColumnIndex("palavra")));
                word.setTraducao(c.getString(c.getColumnIndex("traducao")));
                word.setDetalhes(c.getString(c.getColumnIndex("detalhes")));

                palavras.add(word);

                //Log.i("cudede", c.getString(c.getColumnIndex("palavra")));

                c.moveToNext();
            }
        }
        c.close();

        return palavras;
    }

    public void adicionarPalavra(Dicionario dicionary, Palavra word) {

        ContentValues values = new ContentValues();

        values.put("id_tabela", dicionary.getDicionarioID());
        values.put("palavra", word.getPalavra());
        values.put("traducao", word.getTraducao());
        values.put("detalhes", word.getDetalhes());

        getWritableDatabase().insert(WORDSTABLE, null, values);
    }

    public ArrayList<Dicionario> getDicionarios() {

        ArrayList<Dicionario> dic = new ArrayList<Dicionario>();
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + TBDICS, null);

        while (cursor.moveToNext()) {
            Dicionario dicionario = new Dicionario();

            dicionario.setDicionarioID(cursor.getLong(cursor.getColumnIndex("id")));
            dicionario.setNomeDicionario(cursor.getString(cursor.getColumnIndex("nome")));
            dicionario.setLinguagem(cursor.getString(cursor.getColumnIndex("lingua")));
            //dicionario.setQuantidadePalavras(cursor.getInt(cursor.getColumnIndex("quantidadecaracter")));
            dicionario.setDataCriacao(cursor.getString(cursor.getColumnIndex("datacricao")));

            Cursor c = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM " + WORDSTABLE + " WHERE id_tabela = " + dicionario.getDicionarioID(), null);
            while (c.moveToNext()) {
                dicionario.setQuantidadePalavras(c.getInt(0));
            }

            dic.add(dicionario);
        }
        cursor.close();

        return dic;
    }

    public void deleteWord(Palavra word) {
        SQLiteDatabase db = getWritableDatabase();
        String[] args = {word.getWordId().toString()};
        db.delete(WORDSTABLE, "id=?", args);
    }

    public void deleteDicionario(Dicionario dicionario) {
        SQLiteDatabase db = getWritableDatabase();
        String[] args = {dicionario.getDicionarioID().toString()};
        db.delete(WORDSTABLE, "id_tabela=?", args);
        db.delete(TBDICS, "id=?", args);

        getPalavrasDicionario(dicionario);
    }

    public void updateWord(Palavra word) {

        ContentValues values = new ContentValues();

        values.put("palavra", word.getPalavra());
        values.put("traducao", word.getTraducao());
        values.put("detalhes", word.getDetalhes());

        String[] idParaAlterar = {word.getWordId().toString()};
        getWritableDatabase().update(WORDSTABLE, values, "id=?", idParaAlterar);
    }

    public void updateDic(Dicionario dic) {
        ContentValues values = new ContentValues();

        values.put("nome", dic.getNomeDicionario());
        values.put("lingua", dic.getLinguagem());

        String[] idParaAlterar = {dic.getDicionarioID().toString()};
        getWritableDatabase().update(TBDICS, values, "id=?", idParaAlterar);
    }


    public String doBackupFile(Uri uri, MainActivity ctx){

        File currentDB = ctx.getApplicationContext().getDatabasePath(DATABASE);
        DocumentFile pickedDir = DocumentFile.fromTreeUri(ctx, uri);

        DocumentFile a = pickedDir.createFile("*/*", "swiftbackup.swb");

        try {
            FileInputStream in = new FileInputStream(currentDB);
            OutputStream out = ctx.getContentResolver().openOutputStream(a.getUri());
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            out.flush();
            out.close();

            return pickedDir.getName();
        }catch (Exception e){
            return "";
        }
    }

    public boolean importDatabase(Uri dbPath, MainActivity ctx) throws IOException {
        close();
        File oldDb = ctx.getApplicationContext().getDatabasePath(DATABASE);

        FileOutputStream outStream = new FileOutputStream(oldDb);
        InputStream in = ctx.getContentResolver().openInputStream(dbPath);
        OutputStream out = outStream;
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        in.close();
        out.flush();
        out.close();

        return true;
    }

        /*public void copyAppDbToDownloadFolder(MainActivity ctx) throws IOException {
        File backupDB = new File(ctx.getExternalFilesDir("backup"), "swiftbackup.swb"); // for example "my_data_backup.db"
        File currentDB = ctx.getApplicationContext().getDatabasePath(DATABASE);
        if (currentDB.exists()) {
            FileChannel src = new FileInputStream(currentDB).getChannel();
            FileChannel dst = new FileOutputStream(backupDB).getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();
            BackupDialogs bk = new BackupDialogs(ctx, ctx.getApplicationContext().getResources().getString(R.string.dbt3), ctx.getApplicationContext().getResources().getString(R.string.dbd7), 1);
            bk.createInfoDialog();
        } else {
            BackupDialogs bk = new BackupDialogs(ctx, ctx.getApplicationContext().getResources().getString(R.string.dbt4), ctx.getApplicationContext().getResources().getString(R.string.dbd8), 1);
            bk.createInfoDialog();
        }
    }*/

    /*public String createFileToSendToCloud(MainActivity ctx) throws IOException {
        /*File backupDB = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "swiftbackup.swb"); // for example "my_data_backup.db"
        File currentDB = ctx.getApplicationContext().getDatabasePath(DATABASE);
        if (currentDB.exists()) {
            FileChannel src = new FileInputStream(currentDB).getChannel();
            FileChannel dst = new FileOutputStream(backupDB).getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();
            return backupDB.toString();
        } else {
           return "error";
        }
        return "";
    }*/


        /*private ArrayList<String> recNomeDicionarios(){

        ArrayList<String> tb_names = new ArrayList<String>();

        Cursor c = getReadableDatabase().rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name!='android_metadata' ", null);

        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                tb_names.add(c.getString(0));
                Log.i("tables", "Table Name=> "+c.getString(0));
                c.moveToNext();
            }
        }
        return tb_names;
    }*/

}
