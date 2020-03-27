package com.bomboverk.swiftdictionary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.bomboverk.swiftdictionary.db.Dicionario;
import com.bomboverk.swiftdictionary.db.DicionarioDAO;
import com.bomboverk.swiftdictionary.db.Palavra;

public class AdicionarPalavra extends AppCompatActivity {


    int result = 0;

    private Toolbar mToolBar;

    private TextView titlepage;
    private TextView dialog;

    private TextView txtCharWord;
    private TextView txtCharTranslate;
    private TextView txtCharDetalhes;

    private EditText editPalavra;
    private EditText editTraducao;
    private EditText editDetalhes;

    private Dicionario dic;
    private Palavra wordgetted;

    boolean type = false;

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
        setContentView(R.layout.activity_adicionar_palavra);

        mToolBar = findViewById(R.id.toolbarNPT);

        titlepage = findViewById(R.id.textViewADPalavra);
        dialog = findViewById(R.id.dialogTextTwo);

        editPalavra = findViewById(R.id.editTAddWord);
        editTraducao = findViewById(R.id.editTAddTranslate);
        editDetalhes = findViewById(R.id.editTAddetails);

        txtCharWord = findViewById(R.id.textCharWord);
        txtCharTranslate = findViewById(R.id.textCharTrans);
        txtCharDetalhes = findViewById(R.id.textCharDetalhes);

        //ADICIONANDO A ACTION BAR
        setSupportActionBar(mToolBar);
        //COLOCANDO TITULO DA ACTION BAR
        getSupportActionBar().setTitle("");
        //ADICIONA BOTAO BACK
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = this.getIntent();
        dic = (Dicionario) intent.getSerializableExtra("dicionary");

        if (dic == null){
            titlepage.setText("Editar palavra");
            type = true;
            wordgetted = (Palavra) intent.getSerializableExtra("palavra");

            editPalavra.setText(wordgetted.getPalavra());
            editTraducao.setText(wordgetted.getTraducao());

            if (!wordgetted.getDetalhes().equals("")){
                editDetalhes.setText(wordgetted.getDetalhes());
            }
        }

        quantidadeCaracters(editPalavra.length(), true, txtCharWord);
        quantidadeCaracters(editTraducao.length(), true, txtCharTranslate);
        quantidadeCaracters(editDetalhes.length(), false, txtCharDetalhes);

        verifyCharcteres();
    }

    public void addWord(View view) {
        if (!type){
            //result = 1;
            addNewWord();
        }else {
            //result = 2;
            editWord();
        }
    }

    private void addNewWord(){
        String palavra = editPalavra.getText().toString();
        String traducao = editTraducao.getText().toString();
        String detalhes = editDetalhes.getText().toString();

        if (palavra.length() > 2 && traducao.length() > 2) {

            Palavra word = new Palavra();

            word.setPalavra(palavra);
            word.setTraducao(traducao);
            word.setDetalhes(detalhes);

            DicionarioDAO dao = new DicionarioDAO(getApplicationContext());
            dao.adicionarPalavra(dic, word);
            dao.close();

            result = 1;
            finish();

        } else {
            dialog.setText(R.string.diag01);
            dialog.setVisibility(View.VISIBLE);
        }
    }

    private void editWord(){

        String palavra = editPalavra.getText().toString();
        String traducao = editTraducao.getText().toString();
        String detalhes = editDetalhes.getText().toString();

        if (palavra.length() > 2 && traducao.length() > 2) {

            wordgetted.setPalavra(palavra);
            wordgetted.setTraducao(traducao);
            wordgetted.setDetalhes(detalhes);

            DicionarioDAO dao = new DicionarioDAO(getApplicationContext());
            dao.updateWord(wordgetted);
            dao.close();

            result = 2;
            finish();

        } else {
            dialog.setText(R.string.diag01);
            dialog.setVisibility(View.VISIBLE);
        }
    }

    public void getClipboardWord(View view){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        if (clipboard.hasPrimaryClip()) {
            switch (view.getId()) {
                case R.id.imageButton:
                    editPalavra.setText(clipboard.getPrimaryClip().getItemAt(0).getText());
                    break;
                case R.id.imageButton2:
                    editTraducao.setText(clipboard.getPrimaryClip().getItemAt(0).getText());
                    //clipboard.getText()
                    break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result",result);
        returnIntent.putExtra("word",wordgetted);
        setResult(Activity.RESULT_OK,returnIntent);
        super.finish();
    }

    private void verifyCharcteres(){

        editPalavra.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    quantidadeCaracters(editPalavra.length(), true, txtCharWord);
                }else{
                    txtCharWord.setText("0/30");
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        editTraducao.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    quantidadeCaracters(editTraducao.length(), true, txtCharTranslate);
                }else{
                    txtCharTranslate.setText("0/30");
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        editDetalhes.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    quantidadeCaracters(editDetalhes.length(), false, txtCharDetalhes);
                }else{
                    txtCharDetalhes.setText("0/250");
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void quantidadeCaracters(int legh, boolean action, TextView dialogtxt){

        if (action){

            if(legh < 20){
                dialogtxt.setTextColor(Color.GREEN);
            }
            else if (legh > 20 && legh < 25) {
                dialogtxt.setTextColor(Color.parseColor("#FF7000"));
            } else if (legh > 25) {
                dialogtxt.setTextColor(Color.RED);
            }

            dialogtxt.setText(legh + "/" + 30);

        }else{

            if(legh < 200){
                dialogtxt.setTextColor(Color.GREEN);
            }
            else if (legh > 200 && legh < 225) {
                dialogtxt.setTextColor(Color.parseColor("#FF7000"));
            } else if (legh > 240) {
                dialogtxt.setTextColor(Color.RED);
            }

            dialogtxt.setText(legh + "/" + 250);
        }

    }

}
