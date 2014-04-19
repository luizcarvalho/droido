package br.com.redrails.torpedos.parse;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import br.com.redrails.torpedos.R;
import br.com.redrails.torpedos.daos.MensagemDAO;
import br.com.redrails.torpedos.models.Mensagem;

public class SyncActivity extends ActionBarActivity {
    TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);


        Parse.initialize(this, "IjHMioV35jvHn4LUpn4Xm6aTh51qNmUKPieVqdT3", "S5LWQJYulqwvanhDlhq1gXRAhUhhhKezmDQ5fZp9");
        Button syncButton = (Button) findViewById(R.id.sync_action_button);
        result = (TextView) findViewById(R.id.sync_result);

        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrive_server_messages();
            }
        });
    }

    void verify_new_version(){

    }

    void retrive_server_messages(){
        final ParseHelper parseHelper = new ParseHelper(this);


        ParseQuery<ParseObject> query = ParseQuery.getQuery("MensagemParse");
        query.setLimit(10);

        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> mensagemList, com.parse.ParseException e) {
                String mensagem_result = "";
                if (e == null) {
                    mensagem_result = mensagemList.size() +" "+getResources().getString(R.string.sync_received_message);
                    parseHelper.updateDatabase(mensagemList);
                } else {
                    mensagem_result = "Erro: "+getResources().getString(R.string.sync_connect_error) + e.getCode();
                }
                result.setText(mensagem_result);
                Log.d("Droido", "MENSAGEM RESULT: "+mensagem_result);
            }
        });
    }






    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
