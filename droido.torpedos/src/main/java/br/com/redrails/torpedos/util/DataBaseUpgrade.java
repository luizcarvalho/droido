package br.com.redrails.torpedos.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.StandardExceptionParser;

import java.util.ArrayList;
import java.util.List;

import br.com.redrails.torpedos.DataBaseHelper;
import br.com.redrails.torpedos.Mensagem;
import br.com.redrails.torpedos.MensagemDAO;


public class DataBaseUpgrade {
    private static DataBaseUpgrade instance;
    private SQLiteDatabase database;
    private  Context myContext;
    private DataBaseHelper dataBaseHelperInstace;

    public static DataBaseUpgrade getInstance(Context context) {

        if(instance == null)
            instance = new DataBaseUpgrade(context);
        return instance;
    }

    private DataBaseUpgrade(Context context) {
        dataBaseHelperInstace = DataBaseHelper.getInstance(context);
        database = dataBaseHelperInstace.getWritableDatabase();
        myContext = context;
    }

    private boolean checkIntegrity(){

        try {
            database.rawQuery("PRAGMA integrity_check", null);
            //database.execSQL("SELECT _id FROM categorias LIMIT 1");
            return true;
            //*
        }catch (Exception e){
            Log.e("RedRails","Erro ao checar integridade: "+e.getMessage());

            return false;
        }
        //*/
    }


    public List<Mensagem> getData() {
        List<Mensagem> mensagens = new ArrayList();
        String sql = "SELECT slug,favoritada,enviada FROM mensagens WHERE favoritada='true' OR enviada='true'";
        Cursor cursor;

        MensagemDAO mensagemDAO = MensagemDAO.getInstance(myContext);

        try{
            cursor = database.rawQuery(sql, null);
        }catch(Exception e){
            Log.e("RedRails","Import Favoritos: "+e.getMessage());
            reportError(e);
            return mensagens;
        }

        return mensagemDAO.converterCursorEmMensagens(cursor);
    }



    public boolean importFavsESends(List<Mensagem> mensagens){

            String updateSql;
            database.beginTransaction();
            try{
                for(int i=0;i<mensagens.size();i+=1){
                    Mensagem mensagem = mensagens.get(i);
                    String favoritada = mensagem.getFavoritada().toString();
                    String enviada = mensagem.getEnviada().toString();
                    String slug = mensagem.getSlug();

                    updateSql = "UPDATE main."+MensagemDAO.NOME_TABELA+" SET "+
                            MensagemDAO.COLUNA_FAVORITADA+"='"+favoritada+"', "+
                            MensagemDAO.COLUNA_ENVIADA+"='"+enviada+"' WHERE "+
                            MensagemDAO.COLUNA_SLUG+"='"+slug+"'";
                    Log.w("RedRails", "Executando SQL de atualização " + updateSql);
                    database.execSQL(updateSql);
                }
                database.setTransactionSuccessful();
            }catch (Exception e){
                reportError(e);
            }
            finally {
                database.endTransaction();
            }
        return true;
    }

    public boolean importData(){
        boolean result = false;
        List<Mensagem> mensagens = getData();
        result =  importFavsESends(mensagens);
        return result;
    }

    public void deleteTempDb(){
        try{
            database.execSQL("DETACH DATABASE temp_db", new String[]{});
        }catch (Exception e){
            Log.e("RedRails","DeleteTempDB "+e.getMessage());
        }
        Log.w("RedRails", "Deleting temp DB: "+myContext.deleteDatabase(DataBaseHelper.TEMP_DB_NAME));
    }


    private boolean checkTempDataBase(){

        SQLiteDatabase checkDB = null;
        try{
            String myPath = DataBaseHelper.DB_PATH + DataBaseHelper.TEMP_DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }catch(SQLiteException e){
            Log.e("RedRails","CheckDatabase: Não existe");
            reportError(e);
        }
        if(checkDB != null){
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }



    private void reportError(Exception e){
        EasyTracker easyTracker = EasyTracker.getInstance(myContext);
        easyTracker.send(MapBuilder.createException(e.getMessage(), false).build()
        );
    }





}