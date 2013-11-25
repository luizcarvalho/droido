package br.com.redrails.torpedos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

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
            /* This is just a code that delays the thread execution 4 times,
             * during 850 milliseconds and updates the current progress. This
             * is where the code that is going to be executed on a background
             * thread must be placed.
             */
            try
            {
                //Get the current thread's token
                synchronized (this)
                {
                    //Initialize an integer (that will act as a counter) to zero
                    int counter = 0;
                    //While the counter is smaller than four



                    SharedPreferences prefs = getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
                    int oldVersion = prefs.getInt("currentVersion", 0);

                    DataBaseUpgrade dataUpgrade = DataBaseUpgrade.getInstance(LoadScreenActivity.this);

                    //Caso dbVersion>20 efetua upgrade e não efetua troca toda base de dados
                    //caso contrário a base não suporta upgrade e é substituida sem perdas.
                    int dbVersion = DataBaseHelper.getDbVersion();
                    Log.w("Droido", "Old Version ("+oldVersion+") < ("+dbVersion+") Database Version");
                    if(oldVersion<dbVersion){
                        if(oldVersion>=20){
                            dataUpgrade.importData();
                        }
                    }
                    SharedPreferences.Editor ed = prefs.edit();
                    ed.putInt("currentVersion", dbVersion);
                    ed.commit();
                    dataUpgrade.deleteTempDb();
                    this.wait(1);
                    publishProgress(1);
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            return null;
        }

        //Update the progress
        @Override
        protected void onProgressUpdate(Integer... values)
        {
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
    }
}
