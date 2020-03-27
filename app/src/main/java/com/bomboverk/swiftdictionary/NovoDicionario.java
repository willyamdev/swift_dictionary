package com.bomboverk.swiftdictionary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.bomboverk.swiftdictionary.SpinnerAdapter.LanguagesAdapter;
import com.bomboverk.swiftdictionary.SpinnerAdapter.LanguagesItens;
import com.bomboverk.swiftdictionary.db.Dicionario;
import com.bomboverk.swiftdictionary.db.DicionarioDAO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NovoDicionario extends AppCompatActivity {

    private int maxChars = 30;

    int result = 0;

    private Toolbar mToolBar;
    private Spinner spinner;
    private EditText nomeDic;
    private TextView quantidadeCaracters;

    private TextView textTitleDic;
    private TextView dialogText;

    private Dicionario dicSelect;

    boolean editMode = false;

    String selectedLang;

    private LanguagesAdapter adapterLanguages;
    ArrayList<LanguagesItens> itensLang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getSharedPreferences("cd", MODE_PRIVATE);
        String name = prefs.getString("nightmode", "false");

        if (name.equals("true")) {
            setTheme(R.style.SwiftThemeDark);
        } else {
            setTheme(R.style.SwiftTheme);
        }
        setContentView(R.layout.activity_novo_dicionario);

        mToolBar = findViewById(R.id.toolbarNovoDic);
        spinner = findViewById(R.id.spinnerLanguage);
        nomeDic = findViewById(R.id.editNome);
        quantidadeCaracters = findViewById(R.id.textCount);
        textTitleDic = findViewById(R.id.textTitleNewDic);
        dialogText = findViewById(R.id.textDialog);

        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String[] languagesArray = getResources().getStringArray(R.array.languages);

        selectedLang = languagesArray[0];

        itensLang = new ArrayList<LanguagesItens>();
        for (String s : languagesArray) {
            itensLang.add(new LanguagesItens(s));
        }

        adapterLanguages = new LanguagesAdapter(this, itensLang);
        spinner.setAdapter(adapterLanguages);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLang = itensLang.get(position).getLanguage();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Intent intent = this.getIntent();
        dicSelect = (Dicionario) intent.getSerializableExtra("dictionary");

        if (dicSelect != null){
            textTitleDic.setText(R.string.editmodetitlepage);
            nomeDic.setText(dicSelect.getNomeDicionario());
            editMode = true;
        }

        nomeDic.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    verificaQuantidade(nomeDic.length());
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        verificaQuantidade(nomeDic.length());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void validaForm(View view) {
        if (editMode){
            editDic();
        }else{
            addDic();
        }
    }

    private void editDic(){

        if (nomeDic.getText().length() > 5) {

            DicionarioDAO dao = new DicionarioDAO(getApplicationContext());
            dicSelect.setNomeDicionario(nomeDic.getText().toString());
            dicSelect.setLinguagem(selectedLang);
            dao.updateDic(dicSelect);
            dao.close();
            result = 1;
            finish();

        }else{
            dialogText.setText(R.string.dialogerrornewdic);
            dialogText.setVisibility(View.VISIBLE);
        }
    }

    private void addDic(){

        if (nomeDic.getText().length() > 5) {

            Dicionario dic = new Dicionario();

            DicionarioDAO dao = new DicionarioDAO(getApplicationContext());

            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

            dic.setNomeDicionario(nomeDic.getText().toString());
            dic.setDataCriacao(formatter.format(date));
            dic.setLinguagem(selectedLang);

            /*String[] nulos = {"!", "@", "#", " ", "$", "%", "&", "<", ">", ";", ":", "/"};
            String table = nomeDic.getText().toString();

            for (String n : nulos) {
                if (!n.equals(" ")) {
                    table = table.replaceAll(n, "");
                }else{
                    table = table.replaceAll(n, "_");
                }
            }*/

            dao.inserirDicionario(dic);

            dao.close();
            result = 1;
            finish();

        }else{
            dialogText.setText(R.string.dialogerrornewdic);
            dialogText.setVisibility(View.VISIBLE);
        }
    }

    private void verificaQuantidade(int legn){

        if(legn < 20){
            quantidadeCaracters.setTextColor(Color.GREEN);
        }
        else if (legn > 20 && legn < 25) {
            quantidadeCaracters.setTextColor(Color.parseColor("#FF7000"));
        } else if (legn > 25) {
            quantidadeCaracters.setTextColor(Color.RED);
        }

        quantidadeCaracters.setText(legn + "/" + maxChars);

    }


    @Override
    public void finish() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result",result);
        setResult(Activity.RESULT_OK,returnIntent);
        super.finish();
    }
}
