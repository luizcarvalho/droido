package br.com.redrails.torpedos.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.StandardExceptionParser;

import br.com.redrails.torpedos.DataBaseHelper;
import br.com.redrails.torpedos.MensagemDAO;


public class DataBaseUpgrade {
    private static DataBaseUpgrade instance;
    private SQLiteDatabase database;
    private  Context myContext;

    public static DataBaseUpgrade getInstance(Context context) {

        if(instance == null)
            instance = new DataBaseUpgrade(context);
        return instance;
    }

    private DataBaseUpgrade(Context context) {
        DataBaseHelper persistenceHelper = DataBaseHelper.getInstance(context);
        database = persistenceHelper.getWritableDatabase();
        myContext = context;
    }

    private boolean importFavsESends(){
        String sql = "SELECT slug,favoritada,enviada FROM temp_db.mensagens WHERE favoritada='true' OR enviada='true'";
        Cursor cursor;
        try{
            cursor = database.rawQuery(sql, null);
        }catch(Exception e){
            Log.e("Droido","Import Favoritos: "+e.getMessage());
            reportError(e);
            return false;
        }

        if (cursor!= null && cursor.moveToFirst()) {
            int indexFavoritada = cursor.getColumnIndex(MensagemDAO.COLUNA_FAVORITADA);
            int indexEnviada = cursor.getColumnIndex(MensagemDAO.COLUNA_ENVIADA);
            int indexSlug = cursor.getColumnIndex(MensagemDAO.COLUNA_SLUG);

            String updateSql;
            database.beginTransaction();
            try{
                do {

                    String favoritada = cursor.getString(indexFavoritada);
                    String enviada = cursor.getString(indexEnviada);
                    String slug = cursor.getString(indexSlug);
                    updateSql = "UPDATE main."+MensagemDAO.NOME_TABELA+" SET "+
                            MensagemDAO.COLUNA_FAVORITADA+"='"+favoritada+"', "+
                            MensagemDAO.COLUNA_ENVIADA+"='"+enviada+"' WHERE "+
                            MensagemDAO.COLUNA_SLUG+"='"+slug+"'";
                    Log.w("Droido", "Executando SQL de atualização " + updateSql);
                    database.execSQL(updateSql);
                } while (cursor.moveToNext());
                database.setTransactionSuccessful();
            }finally {
                database.endTransaction();
            }
        }
        return true;
    }

    public boolean importData(){


        Log.w("Droido", "ATAACCHIINNGGG");
        boolean dbExist = checkTempDataBase();
        if(dbExist){
            database.execSQL("attach database ? as temp_db", new String[]{DataBaseHelper.DB_PATH+DataBaseHelper.TEMP_DB_NAME});
            try{
                database.rawQuery("SELECT _id FROM temp_db.mensagens LIMIT 1", new String[]{});
            }catch (Exception e){
                Log.e("Droido","Import Data: "+e.getMessage());
                reportError(e);
                return false;
            }
        }else{
            Log.e("Droido","CheckDatabase: Não existe");
            return false;
        }

        return importFavsESends();
    }

    public void deleteTempDb(){
        try{
            database.execSQL("DETACH DATABASE temp_db", new String[]{});
        }catch (Exception e){
            Log.e("Droido","DeleteTempDB "+e.getMessage());
            reportError(e);
        }
        Log.w("Droido", "Deleting temp DB: "+myContext.deleteDatabase(DataBaseHelper.TEMP_DB_NAME));
    }


    private boolean checkTempDataBase(){

        SQLiteDatabase checkDB = null;
        try{
            String myPath = DataBaseHelper.DB_PATH + DataBaseHelper.TEMP_DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }catch(SQLiteException e){
            Log.e("Droido","CheckDatabase: Não existe");
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