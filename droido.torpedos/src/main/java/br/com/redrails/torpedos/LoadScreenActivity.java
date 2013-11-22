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


        //Initialize a LoadViewTask object and call the execute() method
        new LoadViewTask().execute();

    }

    //To use the AsyncTask, it must be subclassed
    private class LoadViewTask extends AsyncTask<Void, Integer, Void>
    {
        List<Mensagem> mensagens;
        String FILE_NAME = "mensagens.csv";
        int TEXTO=0;
        int SLUG = 1;
        int CATEGORIAS=2;
        int AVALIACAO=3;
        int AUTOR = 5;


    public void updateOrCreate(String[] dados){
        Mensagem mensagem = new Mensagem(0,dados[TEXTO],false, false,dados[AUTOR], dados[SLUG]);
        mensagemDao.createOrUpdate(mensagem, null);
        //mensagemDao.salvar(mensagem);
    }

    public void loadResorces() throws IOException {

        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            LoadScreenActivity.this.getAssets().open(FILE_NAME), "UTF-8"));

            // do reading, usually loop until end of file reading
            String mLine = reader.readLine();
            while (mLine != null) {
                String[] result = mLine.split(";");
                updateOrCreate(result);
                mLine = reader.readLine();
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }




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
                    dataUpgrade.importData();

                    //Caso dbVersion>20 efetua upgrade e não efetua troca toda base de dados
                    //caso contrário a base não suporta upgrade e é substituida sem perdas.
                    int dbVersion = DataBaseHelper.getDbVersion();
                    if(oldVersion<dbVersion){
                        if(dbVersion>=20){
                            SharedPreferences.Editor ed = prefs.edit();
                            ed.putInt("currentVersion", dbVersion);
                            ed.commit();

                            //mensagemDao = MensagemDAO.getInstance(LoadScreenActivity.this);



                            //loadResorces();



                        }
                    }


                        //Wait 850 milliseconds
                        this.wait(1);
                        //Increment the counter
                        //Set the current progress.
                        //This value is going to be passed to the onProgressUpdate() method.
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
