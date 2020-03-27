package com.bomboverk.swiftdictionary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import com.bomboverk.swiftdictionary.Backup.BackupDialogs;

public class ModoLeituraInfo extends AppCompatActivity {

    private Toolbar mToolBar;

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
        setContentView(R.layout.activity_modo_leitura_info);

        mToolBar = findViewById(R.id.toolbarmodoleituraintro);
        //ADICIONANDO A ACTION BAR
        setSupportActionBar(mToolBar);
        //COLOCANDO TITULO DA ACTION BAR
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public void openReadMode(View view) {
        if (BuildConfig.PAID_VERSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!Settings.canDrawOverlays(this)) {
                    askPermission();
                } else {
                    startReadMode();
                }
            } else {
                dialogInfo(true, getString(R.string.dtrm1), getString(R.string.drm1));
            }
        } else {
            dialogInfo(false, getString(R.string.dbt7), getString(R.string.drm2));
        }
    }

    public void askPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (Settings.canDrawOverlays(this)) {
                    startReadMode();
                }else{
                    dialogInfo(true, getString(R.string.dbt5), getString(R.string.p4));
                }
            }
        }
    }

    private void startReadMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(ModoLeituraInfo.this, FloatingWindow.class));
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void dialogInfo(boolean action, String titulo, String mensagem) {

        if (action) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(mensagem)
                    .setTitle(titulo)
                    .setCancelable(false)
                    .setIcon(R.drawable.baseline_error_black_48)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });

            builder.create();
            builder.show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(mensagem)
                    .setTitle(titulo)
                    .setCancelable(false)
                    .setIcon(R.drawable.baseline_error_black_48)
                    .setPositiveButton(R.string.dtrm2, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.bomboverk.swiftdictionary.premium")));
                        }
                    }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            builder.create();
            builder.show();
        }
    }
}
