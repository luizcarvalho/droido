package br.com.redrails.torpedos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabaseCorruptException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

import java.io.IOException;

import br.com.redrails.torpedos.util.DataBaseUpgrade;

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
                    //------
                    try {
                        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(LoadScreenActivity.this);
                        dataBaseHelper.countRows("mensagens");
                    }catch (SQLiteDatabaseCorruptException e){
                        Log.e("RedRails","#####\n#####\n#####\n ERRROO"+e.getStackTrace().toString());
                        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(LoadScreenActivity.this);
                        dataBaseHelper.copyAndNotUpdate();
                    }catch (Exception e){                        
                        Log.e("RedRails","#####\n#####\n#####\n ERRROO"+e.getMessage());
                        reportError(e);
                        publishProgress(0);
                        this.wait(2500);
                    }

                    int dbVersion = DataBaseHelper.getDbVersion();
                    Log.w("RedRails", "Old Version ("+oldVersion+") < ("+dbVersion+") Database Version");
                    if(oldVersion<dbVersion){
                        publishProgress(1);
                        ed.putBoolean("newVersion", true);
                    }

                    this.wait(1500);
                    ed.putInt("currentVersion", dbVersion);
                    ed.commit();
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            return null;
        }


        private void tryAgain(){

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
            //set the current progress of the progress dialog
             //workingSprite.setImageResource(R.drawable.working2);

        }

        //after executing the code in the thread
        @Override
        protected void onPostExecute(Void result)
        {
            //close the progress dialog
            //progressDialog.dismiss();
            //initialize the View
            Intent intent = new Intent(LoadScreenActivity.this, MainActivity.class);
            startActivity(intent);
        }

        private void reportError(Exception e){
            EasyTracker easyTracker = EasyTracker.getInstance(LoadScreenActivity.this);
            easyTracker.send(MapBuilder.createException(e.getStackTrace().toString(), false).build()
            );
        }

    }
    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);  // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);  // Add this method.
    }
}
