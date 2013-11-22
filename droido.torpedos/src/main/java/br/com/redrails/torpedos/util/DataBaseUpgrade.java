package br.com.redrails.torpedos.util;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import br.com.redrails.torpedos.DataBaseHelper;
import br.com.redrails.torpedos.Mensagem;
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

    private void importFavsESends(){
        String sql = "SELECT slug,favoritada,enviada FROM temp_db.mensagens WHERE favoritada='true' OR enviada='true'";
        Cursor cursor = database.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            int indexFavoritada = cursor.getColumnIndex(MensagemDAO.COLUNA_FAVORITADA);
            int indexEnviada = cursor.getColumnIndex(MensagemDAO.COLUNA_ENVIADA);
            int indexSlug = cursor.getColumnIndex(MensagemDAO.COLUNA_SLUG);

            String favoritada = cursor.getString(indexFavoritada);
            String enviada = cursor.getString(indexEnviada);
            String slug = cursor.getString(indexSlug);
            String updateSql;

            do {
                updateSql = "UPDATE main."+MensagemDAO.NOME_TABELA+" SET "+
                        MensagemDAO.COLUNA_FAVORITADA+"='"+favoritada+"', "+
                        MensagemDAO.COLUNA_ENVIADA+"='"+enviada+"' WHERE "+
                        MensagemDAO.COLUNA_SLUG+"='"+slug+"'";
                Log.w("Droido", "Executando SQL de atualização " + updateSql);
                database.execSQL(updateSql);
            } while (cursor.moveToNext());
        }
    }

    public void importData(){

        Log.w("Droido", "ATAACCHIINNGGG");
        database.execSQL("attach database ? as temp_db", new String[]{DataBaseHelper.DB_PATH+DataBaseHelper.TEMP_DB_NAME});

        importFavsESends();
        Log.w("Droido", "Deleting temp DB: "+myContext.deleteDatabase(DataBaseHelper.TEMP_DB_NAME));

    }





}