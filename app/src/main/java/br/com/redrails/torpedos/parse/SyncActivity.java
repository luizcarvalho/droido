package br.com.redrails.torpedos.parse;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Date;
import java.util.List;

import br.com.redrails.torpedos.MainActivity;
import br.com.redrails.torpedos.R;

public class SyncActivity extends ActionBarActivity {
    TextView syncResultLabel;
    Button syncButton;
    String URI = "br.com.redrails.torpedos";
    String lastSyncLabel="lastsync";
    Date lastSync;
    SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);

        try {
            Parse.initialize(this, "IjHMioV35jvHn4LUpn4Xm6aTh51qNmUKPieVqdT3", "S5LWQJYulqwvanhDlhq1gXRAhUhhhKezmDQ5fZp9");
        }catch(Exception e){
            finish();
        }

        prefs = this.getSharedPreferences(URI, getApplicationContext().MODE_PRIVATE);

        syncButton = (Button) findViewById(R.id.sync_action_button);
        syncResultLabel = (TextView) findViewById(R.id.sync_result);

        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrieveServerData();
            }
        });
    }

    void retrieveServerData(){
        initSync();

        ParseHelper parseHelper = new ParseHelper(this);

        retrieveCategorias(parseHelper);
        retrieveMensagens(parseHelper);

        finishSync();

    }


    boolean retrieveMensagens(final ParseHelper parseHelper){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("MensagemParse");
        query.whereGreaterThan(ParseHelper.KEY_UPDATED_AT, lastSync);

        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> mensagemList, com.parse.ParseException e) {
                String mensagem_result = "";
                if (e == null) {
                    mensagem_result = mensagemList.size() +" "+getResources().getString(R.string.sync_received_message);
                    if(mensagemList.size()>0) {
                        parseHelper.needUpdate =true;
                        parseHelper.updateMensagens(mensagemList);
                    }
                } else {
                    mensagem_result = "Erro: "+getResources().getString(R.string.sync_connect_error) + e.getMessage();
                }



                syncResultLabel.setText(syncResultLabel.getText()+"\n"+mensagem_result);
            }
        });
        return parseHelper.needUpdate;
    }

    void retrieveCategorias(final ParseHelper parseHelper){
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
                } else {
                    categoriaResult = "Erro: "+getResources().getString(R.string.sync_connect_error) + e.getCode();
                }
                syncResultLabel.setText(syncResultLabel.getText()+"\n"+categoriaResult);
            }
        });
    }

    void initSync(){
        lastSync = new Date(prefs.getLong(lastSyncLabel, 0));
        syncResultLabel.setText("Buscando mensagens...");
        syncButton.setEnabled(false);

    }

    void finishSync(){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(lastSyncLabel, new Date(System.currentTimeMillis()).getTime());
        editor.commit();
    }

    void goBackToMain(){
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                goBackToMain();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
