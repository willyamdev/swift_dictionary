package com.bomboverk.swiftdictionary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bomboverk.swiftdictionary.Ads.AdsSystemManager;
import com.bomboverk.swiftdictionary.Backup.BackupDialogs;
import com.bomboverk.swiftdictionary.DriveHelper.DriveCloudHelper;
import com.bomboverk.swiftdictionary.DriveHelper.GoogleDriveFileHolder;
import com.bomboverk.swiftdictionary.MainAdaptador.AdaptadorDicionarios;
import com.bomboverk.swiftdictionary.MainAdaptador.SelectedMainItens;
import com.bomboverk.swiftdictionary.MainAdaptador.ViewHolderDicionarios;
import com.bomboverk.swiftdictionary.db.Dicionario;
import com.bomboverk.swiftdictionary.db.DicionarioDAO;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;


public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    //ITENS TOOLBAR
    private Toolbar mToolBar;
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navigationView;
    private MenuItem deletedicsicon;
    private MenuItem searchmainicon;

    //RECYCLERVIEWS
    private RecyclerView meuRecycler;

    //ADAPTADORES
    private AdaptadorDicionarios adaptador;

    //LISTA
    private ArrayList<Dicionario> dicionarios;
    private TextView listaVaziaTxt;

    //GOOGLE DRIVE
    DriveCloudHelper cloudHelper;
    boolean authenticate = false;
    int actioncloud = 0;

    //DATABASE
    DicionarioDAO database;

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
        setContentView(R.layout.activity_main);

        mToolBar = findViewById(R.id.oficialToolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.mainDrawer);
        navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.bringToFront();

        meuRecycler = findViewById(R.id.dicList);

        listaVaziaTxt = findViewById(R.id.textListavazia);

        AdView adView = findViewById(R.id.adBannerMain);

        //ADICIONANDO A ACTION BAR
        setSupportActionBar(mToolBar);
        //COLOCANDO TITULO DA ACTION BAR
        getSupportActionBar().setTitle(R.string.app_name);
        //CONFIGURANDO A NAVIGATION VIEW
        mToggle = new ActionBarDrawerToggle(this, mDrawer, R.string.open, R.string.close);
        mDrawer.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_hamburger_new);//your icon here

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navSetBackup:
                        fazerBackupLocal();
                        break;
                    case R.id.navGetBackup:
                        recuperarBackup();
                        break;
                    case R.id.navSetBackupDrive:
                        navSetBackupDriveButton();
                        break;
                    case R.id.navGetBackupDrive:
                        navGetBackupDriveButton();
                        break;
                    case R.id.switchdarkmode:
                        if (BuildConfig.PAID_VERSION) {
                            ((Switch) menuItem.getActionView()).toggle();
                        } else {
                            BackupDialogs bk = new BackupDialogs(MainActivity.this, getString(R.string.dbt7), getString(R.string.dvd13), 1);
                            bk.createInfoDialog();
                        }
                        break;
                    case R.id.readmodemenuicon:
                        startActivity(new Intent(MainActivity.this, ModoLeituraInfo.class));
                        break;
                }
                return true;
            }
        });


        navigationView.getMenu().findItem(R.id.switchdarkmode).setActionView(new Switch(this));
        Switch vibrateSwitch = (Switch) navigationView.getMenu().findItem(R.id.switchdarkmode).getActionView();

        if (name.equals("true")) {
            vibrateSwitch.setChecked(true);
        } else {
            vibrateSwitch.setChecked(false);
        }

        vibrateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (BuildConfig.PAID_VERSION) {
                    SharedPreferences.Editor editor = getSharedPreferences("cd", MODE_PRIVATE).edit();
                    if (isChecked) {
                        editor.putString("nightmode", "true");
                    } else {
                        editor.putString("nightmode", "false");
                    }
                    editor.apply();
                    restartApp();
                } else {
                    BackupDialogs bk = new BackupDialogs(MainActivity.this, getString(R.string.dbt7), getString(R.string.dvd13), 1);
                    bk.createInfoDialog();
                }
            }
        });

        database = new DicionarioDAO(getApplicationContext());

        loadList();

        if (!BuildConfig.PAID_VERSION) {
            vibrateSwitch.setClickable(false);
            AdsSystemManager adsSystemManager = new AdsSystemManager(getApplicationContext());
            adsSystemManager.createBanner(adView);
        }
    }

    private void navSetBackupDriveButton() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.GET_ACCOUNTS}, 3);
        } else if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
        } else {
            if (!authenticate) {
                actioncloud = 2;
                requestSignIn();
            } else {
                //sendProccess();
            }
        }
    }

    private void navGetBackupDriveButton() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.GET_ACCOUNTS}, 4);
        } else if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 4);
        } else {
            if (!authenticate) {
                actioncloud = 1;
                requestSignIn();
            } else {
                //downloadProccess();
            }
        }
    }

    public void fazerBackupLocal() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            BackupDialogs bk = new BackupDialogs(this, getString(R.string.dbt1), getString(R.string.dbd1), 1);
            bk.createDialog();
        }

        mDrawer.closeDrawers();
    }

    public void recuperarBackup() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        } else {
            BackupDialogs bk = new BackupDialogs(this, getString(R.string.dbt2), getString(R.string.dbd2), 0);
            bk.createDialog();
        }
        mDrawer.closeDrawers();
    }

    public void selectFolderToSave() {
        startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), 5);
    }

    public void showChooseFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        startActivityForResult(intent, 3); //3(LOCAL) ou 5(CLOUD)
    }

    private void restoreBackupDic(Uri uri) {
        try {
            boolean action = database.importDatabase(uri, this);

            if (action) {
                BackupDialogs dk = new BackupDialogs(this, getString(R.string.dbt3), getString(R.string.dbd3), 0);
                dk.createInfoDialog();
            } else {
                BackupDialogs dk = new BackupDialogs(this, getString(R.string.dbt4), getString(R.string.dbd4), 1);
                dk.createInfoDialog();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showAddDic(View view) {
        Intent intent = new Intent(MainActivity.this, NovoDicionario.class);
        startActivityForResult(intent, 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);

        searchmainicon = menu.findItem(R.id.searchDic);
        deletedicsicon = menu.findItem(R.id.deletedicsicon);
        SearchView searchView = (SearchView) searchmainicon.getActionView();
        searchView.setQueryHint(getString(R.string.searchdicicon));
        //searchView.setBackgroundColor(getResources().getColor(R.color.backgroundApp));
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.deletedicsicon) {

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                    .setTitle(getString(R.string.dt3))
                    .setIcon(R.drawable.baseline_error_black_48)
                    .setCancelable(false)
                    .setMessage(getString(R.string.dd3) + " " + adaptador.selectedItems.size() + " " + getString(R.string.dd4))
                    .setPositiveButton(getString(R.string.del), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteAllSelecteds();
                        }
                    }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            decelectAll();
                        }
                    });

            builder.create();
            builder.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllSelecteds() {
        for (int i = 0; i < adaptador.selectedItems.size(); i++) {
            database.deleteDicionario(dicionarios.get(adaptador.selectedItems.keyAt(i)));
        }

        adaptador.selectedItems.clear();
        prepareToSelect();
        clear();
        createSnack(getString(R.string.snackdeldicsselected));
    }

    private void decelectAll() {
        adaptador.selectedItems.clear();
        prepareToSelect();
        clear();
    }

    private void prepareToSelect() {
        searchmainicon.setVisible(!searchmainicon.isVisible());
        deletedicsicon.setVisible(!deletedicsicon.isVisible());
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        String userInput = newText.toLowerCase();
        ArrayList<Dicionario> newList = new ArrayList<Dicionario>();

        for (Dicionario dic : dicionarios) {
            if (dic.getNomeDicionario().toLowerCase().contains(userInput)) {
                newList.add(dic);
            }
        }

        adaptador.updateList(newList);

        return false;
    }

    private void loadList() {
        dicionarios = database.getDicionarios();

        if (dicionarios.size() == 0) {
            listaVaziaTxt.setVisibility(View.VISIBLE);
        } else {
            listaVaziaTxt.setVisibility(View.INVISIBLE);
        }

        adaptador = new AdaptadorDicionarios(dicionarios, this, new AdaptadorDicionarios.OnItemClickListener() {
            @Override
            public void onItemClick(Dicionario dic, ViewHolderDicionarios viewHolderDic, int position) {
                if (adaptador.selectedItems.size() > 0) {

                    if (adaptador.selectedItems.get(position, false)) {
                        adaptador.selectedItems.delete(position);
                        viewHolderDic.constraintBack.setSelected(false);
                    } else {
                        adaptador.selectedItems.put(position, true);
                        viewHolderDic.constraintBack.setSelected(true);
                    }

                    if (adaptador.selectedItems.size() == 0) {
                        prepareToSelect();
                    }

                } else {
                    Intent intent = new Intent(MainActivity.this, ListaPalavras.class);
                    intent.putExtra("dicionary", dic);
                    startActivityForResult(intent, 2);
                }
            }

            @Override
            public void OnLongPress(Dicionario dic, ViewHolderDicionarios viewHolders, int position) {
                if (!adaptador.selectedItems.get(position, false)) {
                    if (adaptador.selectedItems.size() == 0) {
                        prepareToSelect();
                    }

                    adaptador.selectedItems.put(position, true);
                    viewHolders.constraintBack.setSelected(true);
                }


            }
        });

        meuRecycler.setAdapter(adaptador);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        meuRecycler.setLayoutManager(layoutManager);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                int result = data.getIntExtra("result", 0);

                if (result == 1) {
                    clear();
                    createSnack(getString(R.string.snackdiccreated));
                }
            }
        } else if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                int result = data.getIntExtra("result", 0);
                if (result == 1) {
                    clear();
                } else if (result == 2) {
                    clear();
                    createSnack(getString(R.string.snackdicremoved));
                }
            }
        } else if (requestCode == 3) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = null;
                if (data != null) {
                    uri = data.getData();
                    restoreBackupDic(uri);
                }
            }
        } else if (requestCode == 4) {
            if (resultCode == RESULT_OK) {
                handleSignInIntent(data);
            }
        } else if (requestCode == 5) {
            if (resultCode == RESULT_OK) {
                Uri treeUri = data.getData();
                String result = database.doBackupFile(treeUri, this);

                if (!result.equals("")) {
                    BackupDialogs bk = new BackupDialogs(this, getApplicationContext().getResources().getString(R.string.dbt3), getString(R.string.dbd14) +" '"+ result +"' " + getString(R.string.dbd15), 1);
                    bk.createInfoDialog();
                }else{
                    BackupDialogs bk = new BackupDialogs(this, getApplicationContext().getResources().getString(R.string.dbt4), getApplicationContext().getResources().getString(R.string.dbd8), 1);
                    bk.createInfoDialog();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fazerBackupLocal();
                } else {
                    BackupDialogs bk = new BackupDialogs(this, getString(R.string.dbt5), getString(R.string.dbd5), 1);
                    bk.createInfoDialog();
                }
                break;
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    recuperarBackup();
                } else {
                    BackupDialogs bk = new BackupDialogs(this, getString(R.string.dbt5), getString(R.string.dbd6), 1);
                    bk.createInfoDialog();
                }
                break;
            case 3:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    navSetBackupDriveButton();
                } else {
                    BackupDialogs bk = new BackupDialogs(this, getString(R.string.dbt5), getString(R.string.dbd9), 1);
                    bk.createInfoDialog();
                }
                break;
            case 4:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    navGetBackupDriveButton();
                } else {
                    BackupDialogs bk = new BackupDialogs(this, getString(R.string.dbt5), getString(R.string.dbd9), 1);
                    bk.createInfoDialog();
                }
                break;
        }
    }

    public void clear() {
        dicionarios.clear();
        adaptador.notifyDataSetChanged();

        loadList();
    }

    private void createSnack(String toast) {
        Snackbar.make(findViewById(android.R.id.content), toast, Snackbar.LENGTH_SHORT).setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }).show();
    }

    public void restartApp() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
    }

    private void requestSignIn() {

        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(DriveScopes.DRIVE_FILE)).build();

        GoogleSignInClient client = GoogleSignIn.getClient(MainActivity.this, signInOptions);

        startActivityForResult(client.getSignInIntent(), 4);

    }

    private void handleSignInIntent(Intent data) {

        GoogleSignIn.getSignedInAccountFromIntent(data)
                .addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                    @Override
                    public void onSuccess(GoogleSignInAccount googleSignInAccount) {

                        authenticate = true;

                        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(MainActivity.this, Collections.singleton(DriveScopes.DRIVE_FILE));

                        credential.setSelectedAccount(googleSignInAccount.getAccount());

                        com.google.api.services.drive.Drive googleDriveService = new Drive.Builder(
                                AndroidHttp.newCompatibleTransport(),
                                new GsonFactory(),
                                credential)
                                .setApplicationName("Swift Dictionary")
                                .build();

                        cloudHelper = new DriveCloudHelper(googleDriveService);
                        createSnack(getString(R.string.snacklogged));
                        //checkAction();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                createSnack(getString(R.string.snackerrorlogin));
            }
        });
    }

    /*private void checkAction() {
        if (actioncloud == 1) {
            downloadProccess();
        } else if (actioncloud == 2) {
            sendProccess();
        }
    }*/


    /*private void sendProccess() {

        Dialog dialog = exibeDialogGDInfo(getString(R.string.ctd1));
        TextView infoTitleDialog = (TextView) dialog.findViewById(R.id.textInfoDialogCustom);

        DicionarioDAO dao = new DicionarioDAO(getApplicationContext());
        String path = "";
        try {
            path = dao.createFileToSendToCloud(MainActivity.this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        dao.close();
        String finalPath = path;

        infoTitleDialog.setText(R.string.ctd2);
        cloudHelper.searchFolder("SwiftDictionary Backup", "application/vnd.google-apps.folder").addOnSuccessListener(new OnSuccessListener<GoogleDriveFileHolder>() {
            @Override
            public void onSuccess(GoogleDriveFileHolder googleDriveFileHolder) {
                if (googleDriveFileHolder.getId() == null || googleDriveFileHolder.getId().equals("")) {

                    infoTitleDialog.setText(R.string.ctd3);
                    cloudHelper.createFolder("SwiftDictionary Backup", null).addOnSuccessListener(new OnSuccessListener<GoogleDriveFileHolder>() {
                        @Override
                        public void onSuccess(GoogleDriveFileHolder googleDriveFileHolderr) {
                            uploadAchive(finalPath, googleDriveFileHolderr, dialog);
                        }
                    });
                } else {
                    uploadAchive(finalPath, googleDriveFileHolder, dialog);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                BackupDialogs bk = new BackupDialogs(MainActivity.this, getString(R.string.dbt4), getString(R.string.dbd10), 5);
                bk.createInfoDialog();
            }
        });
    }*/

    /*private void uploadAchive(String path, GoogleDriveFileHolder googleDriveFileHolder, Dialog dialog) {

        TextView infoTitleDialog = (TextView) dialog.findViewById(R.id.textInfoDialogCustom);
        infoTitleDialog.setText(R.string.ctd4);

        cloudHelper.searchFile("swiftbackup.swb", "application/octet-stream").addOnSuccessListener(new OnSuccessListener<GoogleDriveFileHolder>() {
            @Override
            public void onSuccess(GoogleDriveFileHolder googleDriveFileHoldersearch) {
                if (googleDriveFileHoldersearch.getId() == null || googleDriveFileHoldersearch.getId().equals("")) {
                    cloudHelper.uploadFile(new File(path), "application/octet-stream", googleDriveFileHolder.getId()).addOnSuccessListener(new OnSuccessListener<GoogleDriveFileHolder>() {
                        @Override
                        public void onSuccess(GoogleDriveFileHolder googleDriveFileHolder) {
                            dialog.dismiss();
                            createSnack(getString(R.string.snacksuccessbackupsend));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            createSnack(getString(R.string.snackerrorbackupsend));
                        }
                    });
                } else {
                    cloudHelper.deleteFolderFile(googleDriveFileHoldersearch.getId()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            cloudHelper.uploadFile(new File(path), "application/octet-stream", googleDriveFileHolder.getId()).addOnSuccessListener(new OnSuccessListener<GoogleDriveFileHolder>() {
                                @Override
                                public void onSuccess(GoogleDriveFileHolder googleDriveFileHolder) {
                                    dialog.dismiss();
                                    createSnack(getString(R.string.snacksuccessbackupsend));
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dialog.dismiss();
                                    createSnack(getString(R.string.snackerrorbackupsend));
                                }
                            });
                        }
                    });
                }
            }
        });
    }*/

    /*private void downloadProccess() {
        Dialog dialog = exibeDialogGDInfo(getString(R.string.ctd5));

        cloudHelper.searchFile("swiftbackup.swb", "application/octet-stream").addOnSuccessListener(new OnSuccessListener<GoogleDriveFileHolder>() {
            @Override
            public void onSuccess(GoogleDriveFileHolder googleDriveFileHolder) {

                if (googleDriveFileHolder.getId() == null || googleDriveFileHolder.getId().equals("")) {
                    dialog.dismiss();
                    BackupDialogs bk = new BackupDialogs(MainActivity.this, getString(R.string.dbt6), getString(R.string.dbd11), 5);
                    bk.createInfoDialog();
                } else {
                    cloudHelper.downloadFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "swiftbackup.swb"), googleDriveFileHolder.getId()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dialog.dismiss();
                            DicionarioDAO dao = new DicionarioDAO(getApplicationContext());

                            boolean filetest;

                            try {
                                filetest = dao.importDatabase(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/swiftbackup.swb", MainActivity.this);

                                if (filetest) {
                                    BackupDialogs dk = new BackupDialogs(MainActivity.this, getString(R.string.dbt3), getString(R.string.dbd3), 0);
                                    dk.createInfoDialog();
                                } else {
                                    BackupDialogs dk = new BackupDialogs(MainActivity.this, getString(R.string.dbt4), getString(R.string.dbd4), 1);
                                    dk.createInfoDialog();
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            BackupDialogs bk = new BackupDialogs(MainActivity.this, getString(R.string.dbt4), getString(R.string.dbd12), 5);
                            bk.createInfoDialog();
                        }
                    });
                }
            }
        });
    }*/

    private Dialog exibeDialogGDInfo(String infoTitle) {
        final Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.customalertdialog);

        TextView infoTitleDialog = (TextView) dialog.findViewById(R.id.textInfoDialogCustom);

        infoTitleDialog.setText(infoTitle);

        //define o título do Dialog
        dialog.setTitle("Google Drive");
        dialog.setCancelable(false);

        //exibe na tela o dialog
        dialog.show();

        return dialog;
    }
}

