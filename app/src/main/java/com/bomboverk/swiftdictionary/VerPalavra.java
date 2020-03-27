package com.bomboverk.swiftdictionary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bomboverk.swiftdictionary.db.DicionarioDAO;
import com.bomboverk.swiftdictionary.db.Palavra;
import com.google.android.material.snackbar.Snackbar;

public class VerPalavra extends AppCompatActivity {

    Toolbar mToolBar;

    TextView txtPalavra;
    TextView txtTraducao;
    TextView txtDetalhes;

    ConstraintLayout cons;

    Palavra palavra;

    int result = 0;

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
        setContentView(R.layout.activity_ver_palavra);

        mToolBar = findViewById(R.id.toolbarPalavra);

        txtPalavra = findViewById(R.id.palavraTxt);
        txtTraducao = findViewById(R.id.traducaoTxt);
        txtDetalhes = findViewById(R.id.detalhesTxt);

        cons = findViewById(R.id.detailConst);

        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = this.getIntent();
        palavra = (Palavra) intent.getSerializableExtra("word");

        txtPalavra.setText(palavra.getPalavra());
        txtTraducao.setText(palavra.getTraducao());

        if(palavra.getDetalhes().length() == 0){
            cons.setVisibility(View.INVISIBLE);
        }else{
            txtDetalhes.setText(palavra.getDetalhes());
            cons.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ver_palavra, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.trashWord:
                DialogDeleteData datadelete = new DialogDeleteData(null, this,getString(R.string.dt1), getString(R.string.dd1), "word", null, palavra);
                datadelete.createDialog(2);
                break;

            case R.id.editWord:
                Intent intent = new Intent(getApplicationContext(), AdicionarPalavra.class);
                intent.putExtra("palavra", palavra);
                startActivityForResult(intent, 4);
                break;

            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 4) {
            if (resultCode == Activity.RESULT_OK) {
                int result = data.getIntExtra("result", 0);

                if (result == 2) {
                    Palavra wordchanged = (Palavra) data.getSerializableExtra("word");

                    txtPalavra.setText(wordchanged.getPalavra());
                    txtTraducao.setText(wordchanged.getTraducao());
                    txtDetalhes.setText(wordchanged.getDetalhes());

                    this.result = 2;
                    createSnack(getString(R.string.snackwordatualizada));
                }
            }
        }
    }

    private void createSnack(String toast) {
        Snackbar.make(findViewById(android.R.id.content), toast, Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }).show();
    }

    @Override
    public void finish() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result",result);
        setResult(Activity.RESULT_OK,returnIntent);
        super.finish();
    }
}
