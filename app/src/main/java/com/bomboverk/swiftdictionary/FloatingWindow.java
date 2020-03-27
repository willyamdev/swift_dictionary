package com.bomboverk.swiftdictionary;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bomboverk.swiftdictionary.ReadModeAdaptador.RMAdapter;
import com.bomboverk.swiftdictionary.SpinnerAdapter.RDSpinnerAdapter;
import com.bomboverk.swiftdictionary.SpinnerAdapter.RDSpinnerItens;
import com.bomboverk.swiftdictionary.db.Dicionario;
import com.bomboverk.swiftdictionary.db.DicionarioDAO;
import com.bomboverk.swiftdictionary.db.Palavra;

import java.util.ArrayList;

public class FloatingWindow extends Service {

    private WindowManager wm;
    private ImageButton exitButton;
    private EditText editReadMoreWord;
    private RecyclerView recyclerViewWords;
    private Spinner spinnerDics;
    private LinearLayout layoutInfo;
    private TextView txtPalavra;
    private TextView txtTraducao;

    private RMAdapter rmAdapter;
    private ArrayList<Palavra> palavras;
    private ArrayList<Dicionario> dicionarios;
    private ArrayList<RDSpinnerItens> spinnerItens;

    private RDSpinnerAdapter rdSpinnerAdapter;
    DicionarioDAO dao;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "swappchannel";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("MODO LEITURA ATIVADO!")
                    .setContentText("").build();

            startForeground(1, notification);
        }

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.UNKNOWN);
        params.x = 0;
        params.y = 0;
        params.gravity = Gravity.CENTER;

        final View popupView = View.inflate(new ContextThemeWrapper(getApplicationContext(), R.style.SwiftTheme),R.layout.assistentlayout, null);
        wm.addView(popupView, params);

        exitButton = popupView.findViewById(R.id.closeReadModeButton);
        editReadMoreWord = popupView.findViewById(R.id.editTextReadMode);
        spinnerDics = popupView.findViewById(R.id.spinnerReadMode);
        recyclerViewWords = popupView.findViewById(R.id.recycleReadMode);
        layoutInfo = popupView.findViewById(R.id.infoWordPanelRM);
        txtPalavra = popupView.findViewById(R.id.textWordRM);
        txtTraducao = popupView.findViewById(R.id.textTransRM);

        dao = new DicionarioDAO(getApplicationContext());

        dicionarios = dao.getDicionarios();
        spinnerItens = new ArrayList<>();
        for (int i=0; i < dicionarios.size(); i++){
            spinnerItens.add(new RDSpinnerItens(dicionarios.get(i).getDicionarioID(), dicionarios.get(i).getNomeDicionario()));
        }

        palavras = new ArrayList<>();
        rmAdapter = new RMAdapter(palavras, getApplicationContext(), new RMAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Palavra dic) {
                clearList();
                createPopUpDetails(dic.getPalavra(), dic.getTraducao());
            }
        });

        recyclerViewWords.setAdapter(rmAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewWords.setLayoutManager(layoutManager);

        rdSpinnerAdapter = new RDSpinnerAdapter(this, spinnerItens);
        spinnerDics.setAdapter(rdSpinnerAdapter);
        spinnerDics.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                clearList();
                palavras.clear();
                palavras = dao.getPalavrasDicionario(dicionarios.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        editReadMoreWord.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    String userInput = editReadMoreWord.getText().toString().toLowerCase();
                    ArrayList<Palavra> newList = new ArrayList<Palavra>();

                    for (Palavra word : palavras){
                        if (word.getPalavra().toLowerCase().contains(userInput) || word.getTraducao().toLowerCase().contains(userInput)|| word.getDetalhes().toLowerCase().contains(userInput)){
                            newList.add(word);
                        }
                    }
                    rmAdapter.updateList(newList);
                }else{
                    clearList();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                if (layoutInfo.getVisibility() == View.VISIBLE){
                    layoutInfo.setVisibility(View.GONE);
                }
            }
        });

        popupView.setOnTouchListener(new View.OnTouchListener() {

            private WindowManager.LayoutParams updatedParameters = params;
            int x,y;
            float touchedX, touchedY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        x = updatedParameters.x;
                        y = updatedParameters.y;

                        touchedX = event.getRawX();
                        touchedY = event.getRawY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        updatedParameters.x = (int)(x + (event.getRawX() - touchedX));
                        updatedParameters.y = (int)(y + (event.getRawY() - touchedY));
                        wm.updateViewLayout(popupView, updatedParameters);
                default:
                    break;
                }
                return false;
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wm.removeView(popupView);
                stopSelf();
            }
        });

    }

    private void createPopUpDetails(String word, String trans){
        txtPalavra.setText(word);
        txtTraducao.setText(trans);
        layoutInfo.setVisibility(View.VISIBLE);
    }

    private void clearList(){
        ArrayList<Palavra> newList = new ArrayList<Palavra>();
        rmAdapter.updateList(newList);
    }
}
