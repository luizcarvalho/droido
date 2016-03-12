package br.com.redrails.torpedos.parse;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.*;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Date;
import java.util.List;

import br.com.redrails.torpedos.MainApplication;
import br.com.redrails.torpedos.MainActivity;
import br.com.redrails.torpedos.R;
import br.com.redrails.torpedos.daos.MensagemDAO;

public class SyncActivity extends ActionBarActivity {
    TextView syncResultLabel;
    Button syncButton;
    String URI = "br.com.redrails.torpedos";
    String lastSyncLabel="lastsync";
    Date lastSync;
    SharedPreferences prefs;
    Integer successCount = 0;
    ProgressDialog progressBar;
    private Tracker mTracker;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        MainApplication application = (MainApplication) getApplication();
        mTracker = application.getDefaultTracker();

        AdView adView = (AdView)this.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("502D84F4ECF3CB4A69829D7372AA0B5B")
                .build();
        adView.loadAd(adRequest);

        prefs = this.getSharedPreferences(URI, getApplicationContext().MODE_PRIVATE);
        syncButton = (Button) findViewById(R.id.sync_action_button);
        syncResultLabel = (TextView) findViewById(R.id.sync_result);

        if(recentTry()){
            syncResultLabel.setText("Você tentou atualizar a pouco tempo. Tente novamente daqui a pouco! :)");
        }

        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOnline()) {
                    retrieveServerData();
                }else{
                    syncResultLabel.setText(getString(R.string.need_connection));
                    tryAgain();
                }
            }
        });

    }

    void retrieveServerData(){
        if(!recentTry()) {
            initSync();
            ParseHelper parseHelper = new ParseHelper(this);
            retrieveCategorias(parseHelper);
        }else{
            syncResultLabel.setText("Espere um tempo antes de tentar atualizar novamente!");
            tryAgain();
        }
    }

    void showProgressBar(){
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(true);
        progressBar.setMessage("Baixando mensagens, isso pode levar algum tempo..");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setCancelable(false); //setcancelable(false);
        progressBar.show();
    }


    boolean retrieveMensagens(final ParseHelper parseHelper){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("MensagemParse");
        query.whereGreaterThan(ParseHelper.KEY_UPDATED_AT, lastSync);
        query.setLimit(1000);

        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> mensagemList, com.parse.ParseException e) {
                String mensagem_result = "";
                if (e == null) {
                    mensagem_result = mensagemList.size() +" "+getResources().getString(R.string.sync_received_message);
                    successCount++;
                    if(mensagemList.size()>0) {
                        parseHelper.needUpdate =true;
                        new UpdateMessages().execute(mensagemList);
                    }else{
                        finishSync();
                    }

                } else {
                    successCount--;
                    mensagem_result = "Erro: "+getResources().getString(R.string.sync_connect_error) + e.getCode();
                    finishSync();
                    tryAgain();
                }



                syncResultLabel.setText(syncResultLabel.getText()+"\n"+mensagem_result);
            }
        });
        return parseHelper.needUpdate;
    }


    boolean retrieveCategorias(final ParseHelper parseHelper){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("CategoriaParse");
        query.whereGreaterThan(ParseHelper.KEY_UPDATED_AT, lastSync);


        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> categoriaList, com.parse.ParseException e) {
                String categoriaResult = "";
                if (e == null) {
                    categoriaResult = categoriaList.size() + " " + getResources().getString(R.string.sync_received_categories);
                    if (categoriaList.size() > 0){
                        parseHelper.needUpdate=true;
                        parseHelper.updateCategorias(categoriaList);
                    }
                    retrieveMensagens(parseHelper);
                    successCount++;
                } else {
                    categoriaResult = "Erro: "+getResources().getString(R.string.sync_connect_error) + e.getCode();
                    successCount--;
                    finishSync();
                    tryAgain();
                }
                syncResultLabel.setText(syncResultLabel.getText()+"\n"+categoriaResult);
            }
        });
        return true;
    }

    boolean recentTry(){
        //long delay = 300000; // 5 min
        long delay = 0;
        long lastSyncLong = prefs.getLong(lastSyncLabel, 0);
        long nowLong = new Date(System.currentTimeMillis()).getTime();
        return (nowLong-delay)<lastSyncLong;

    }

    void initSync(){
        lastSync = new Date(prefs.getLong(lastSyncLabel, 0));
        syncResultLabel.setText("Buscando mensagens...");
        syncButton.setEnabled(false);
        showProgressBar();
    }

    boolean hasSuccess(){
        if(successCount==2)
           return true;
        return false;
    }

    void finishSync(){
        progressBar.dismiss();
        if(hasSuccess()) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(lastSyncLabel, new Date(System.currentTimeMillis()).getTime());
            editor.commit();
            addButonToGoBack();
        }
    }

    void addButonToGoBack(){
        RelativeLayout mainLayout = (RelativeLayout)findViewById(R.id.activity_sync);
        Button tryAgain = new Button(this);
        tryAgain.setText(getString(R.string.reload_mensagens));
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.BELOW, syncResultLabel.getId());
        mainLayout.addView(tryAgain, lp);


        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToMain();
            }
        });
    }

    void goBackToMain(){
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.putExtra("ordem", MensagemDAO.ORDEM_DATA);
        startActivity(i);
    }

    void tryAgain(){
        successCount=0;
        syncButton.setEnabled(true);
        syncButton.setText("Tentar de novo");
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.sync, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                goBackToMain();
                return true;
            case R.id.reset_counter:
                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong(lastSyncLabel, 0);
                editor.commit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private class UpdateMessages extends AsyncTask<List<ParseObject>, Void, Void>{


        @Override
        protected void onPreExecute() {
            progressBar.setMessage("Atualizando suas mensagens!");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(List<ParseObject>... params) {
            ParseHelper parseHelper = new ParseHelper(SyncActivity.this);
            parseHelper.updateMensagens(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            finishSync();
            super.onPostExecute(aVoid);
        }


    }

    @Override
    public void onStart() {
        super.onStart();
        mTracker.setScreenName("SyncActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


}
