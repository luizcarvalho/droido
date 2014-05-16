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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Date;
import java.util.List;

import br.com.redrails.torpedos.LoadScreenActivity;
import br.com.redrails.torpedos.MainActivity;
import br.com.redrails.torpedos.R;

public class SyncActivity extends ActionBarActivity {
    TextView syncResultLabel;
    Button syncButton;
    String URI = "br.com.redrails.torpedos";
    String lastSyncLabel="lastsync";
    Date lastSync;
    SharedPreferences prefs;
    Integer successCount = 0;


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
                    successCount++;
                    finishSync();
                } else {
                    successCount--;
                    mensagem_result = "Erro: "+getResources().getString(R.string.sync_connect_error) + e.getCode();
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
                    tryAgain();
                }
                syncResultLabel.setText(syncResultLabel.getText()+"\n"+categoriaResult);
            }
        });
        return true;
    }

    void initSync(){
        lastSync = new Date(prefs.getLong(lastSyncLabel, 0));
        syncResultLabel.setText("Buscando mensagens...");
        syncButton.setEnabled(false);

    }

    boolean hasSuccess(){
        if(successCount==2)
           return true;
        return false;
    }

    void finishSync(){

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
        tryAgain.setText("Ótimo! Carrege minhas novas mensagens!");
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
        Intent i = new Intent(getApplicationContext(), LoadScreenActivity.class);
        startActivity(i);
    }

    void tryAgain(){
        syncButton.setEnabled(true);
        syncButton.setText("Tentar de novo");
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
