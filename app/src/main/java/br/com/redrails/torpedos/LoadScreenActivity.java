package br.com.redrails.torpedos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import br.com.redrails.torpedos.daos.MensagemDAO;

public class LoadScreenActivity extends Activity
{
    //A ProgressDialog object
    private ProgressDialog progressDialog;
    MensagemDAO mensagemDao;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_screen);
        new LoadViewTask().execute();
    }

    //To use the AsyncTask, it must be subclassed
    private class LoadViewTask extends AsyncTask<Void, Integer, Void>
    {

        //Before running code in separate thread
        @Override
        protected void onPreExecute()
        {


        }

        //The code to be executed in a background thread.
        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                synchronized (this)
                {
                    SharedPreferences prefs = getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
                    int oldVersion = prefs.getInt("currentVersion", 0);
                    SharedPreferences.Editor ed = prefs.edit();
                    DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(LoadScreenActivity.this);
                    int dbVersion = DataBaseHelper.getDbVersion();
                    //------
                    if(oldVersion<dbVersion){
                        publishProgress(1);
                        ed.putBoolean("newVersion", true);
                    }
                    this.wait(1000);
                    ed.putInt("currentVersion", dbVersion);
                    ed.commit();
                }
            }
            catch (InterruptedException e)
            {
                publishProgress(0);
            }

            return null;
        }

        //Update the progress
        @Override
        protected void onProgressUpdate(Integer... values)
        {

            if(values[0]==1){
                TextView loadText = (TextView) findViewById(R.id.load_messages);
                loadText.setText("Ebaa Mensagens novas!!");
            }else{
                TextView loadText = (TextView) findViewById(R.id.load_messages);
                loadText.setText("Infelizmente um erro gravíssimo aconteceu! \n Caso o erro persista, por favor desinstale e instale o Droido novamente!");

            }
        }

        //after executing the code in the thread
        @Override
        protected void onPostExecute(Void result)
        {
            Intent intent = new Intent(LoadScreenActivity.this, MainActivity.class);
            startActivity(intent);
        }

    }

}
