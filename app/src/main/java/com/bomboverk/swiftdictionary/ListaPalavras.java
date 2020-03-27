package com.bomboverk.swiftdictionary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bomboverk.swiftdictionary.Ads.AdsSystemManager;
import com.bomboverk.swiftdictionary.NovaPalavraAdaptador.AdaptadorPalavras;
import com.bomboverk.swiftdictionary.db.Dicionario;
import com.bomboverk.swiftdictionary.db.DicionarioDAO;
import com.bomboverk.swiftdictionary.db.Palavra;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class ListaPalavras extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private Toolbar mToolBar;
    private RecyclerView meuRecycler;

    TextView dialogList;

    int updateList = 0;

    AdaptadorPalavras adaptador;

    Dicionario dicSelected;
    ArrayList<Palavra> palavraArrayList;

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
        setContentView(R.layout.activity_lista_palavras);

        mToolBar = findViewById(R.id.toolbarNP);
        meuRecycler = findViewById(R.id.palavrasRecycle);

        dialogList = findViewById(R.id.textDialogWords);

        AdView adView = findViewById(R.id.adBannerLisPalavras);

        //ADICIONANDO A ACTION BAR
        setSupportActionBar(mToolBar);
        //COLOCANDO TITULO DA ACTION BAR
        getSupportActionBar().setTitle("");
        //ADICIONA BOTAO BACK
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = this.getIntent();
        dicSelected = (Dicionario) intent.getSerializableExtra("dicionary");

        loadRecycle();

        if (!BuildConfig.PAID_VERSION){
            AdsSystemManager adsSystemManager = new AdsSystemManager(getApplicationContext());
            adsSystemManager.createBanner(adView);
        }
    }

    private void loadRecycle(){

        DicionarioDAO dao = new DicionarioDAO(getApplicationContext());
        //dao.adicionarPalavra(dicSelected.getNomeDicionario(), dicSelected.getDicionarioID(), "sleep", "dormir");
        palavraArrayList = dao.getPalavrasDicionario(dicSelected);
        dao.close();

        if (palavraArrayList.size() == 0){
            dialogList.setVisibility(View.VISIBLE);
        }else{
            dialogList.setVisibility(View.INVISIBLE);
        }

        adaptador = new AdaptadorPalavras(palavraArrayList, this, new AdaptadorPalavras.OnItemClickListener() {
            @Override
            public void onItemClick(Palavra word) {
                Intent intent = new Intent(getApplicationContext(), VerPalavra.class);
                intent.putExtra("word", word);
                startActivityForResult(intent, 2);
            }

            @Override
            public void onButtonRemove(Palavra btnremove) {
                DialogDeleteData datadelete = new DialogDeleteData(ListaPalavras.this, null, getString(R.string.dt1), getString(R.string.dd1), "word", null, btnremove);
                datadelete.createDialog(1);
            }
        });

        meuRecycler.setAdapter(adaptador);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        meuRecycler.setLayoutManager(layoutManager);
    }

    public void AdicionarPalavra(View view){
        Intent intent = new Intent(getApplicationContext(), AdicionarPalavra.class);
        intent.putExtra("dicionary", dicSelected);
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                int result = data.getIntExtra("result", 0);

                if (result == 1) {
                    updateList = 1;
                    clear();
                    createSnack(getString(R.string.snackpalavraadded));
                }else if (result == 2){
                    clear();
                }else if (result == 3){
                    createSnack(getString(R.string.snackpalavraremoved));
                    clear();
                }
            }
        }else if (requestCode == 3){

            if (resultCode == Activity.RESULT_OK) {

                int result = data.getIntExtra("result", 0);

                if (result == 1) {
                    updateList = 1;
                    clear();
                    createSnack(getString(R.string.snackdicedited));
                }
            }
        }
    }

    public void createSnack(String toast) {
        Snackbar.make(findViewById(android.R.id.content), toast, Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }).show();
    }

    public void clear() {

        int size = palavraArrayList.size();
        palavraArrayList.clear();
        adaptador.notifyItemRangeRemoved(0, size);

        loadRecycle();
    }

    @Override
    public void finish() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result",updateList);
        setResult(Activity.RESULT_OK,returnIntent);
        super.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menunovapalavra, menu);

        MenuItem menuItem = menu.findItem(R.id.searchIcon);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint(getString(R.string.searchwordicon));
        //searchView.setBackgroundColor(getResources().getColor(R.color.backgroundApp));
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        String userInput = newText.toLowerCase();
        ArrayList<Palavra> newList = new ArrayList<Palavra>();

        for (Palavra word : palavraArrayList){
            if (word.getPalavra().toLowerCase().contains(userInput) || word.getTraducao().toLowerCase().contains(userInput)|| word.getDetalhes().toLowerCase().contains(userInput)){
                newList.add(word);
            }
        }

        adaptador.updateList(newList);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.deleteIcon:
                DialogDeleteData datadelete = new DialogDeleteData(this, null,getString(R.string.dt2), getString(R.string.dd2), "dic", dicSelected, null);
                datadelete.createDialog(1);
                break;

            case R.id.editDicionary:
                Intent intent = new Intent(getApplicationContext(), NovoDicionario.class);
                intent.putExtra("dictionary", dicSelected);
                startActivityForResult(intent, 3);
                break;


            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
