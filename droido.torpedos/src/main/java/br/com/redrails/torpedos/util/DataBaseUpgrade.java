package br.com.redrails.torpedos.util;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import br.com.redrails.torpedos.Mensagem;
import br.com.redrails.torpedos.MensagemDAO;


public class DataBaseUpgrade {
    public DataBaseUpgrade(){

    }

    public static void importUserData(String path, String tempDbName, String newDbName){
        String pathNewdb = path+newDbName;
        String pathtempDb = path+tempDbName;
        SQLiteDatabase newDatabase =  SQLiteDatabase.openDatabase(pathNewdb, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        SQLiteDatabase tempDatabase =  SQLiteDatabase.openDatabase(pathtempDb, null, SQLiteDatabase.OPEN_READONLY);
        //importFavsESends(tempDatabase, newDatabase);
        //testData(newDatabase, pathtempDb);
    }

    private static void importFavsESends(SQLiteDatabase tempDatabase, SQLiteDatabase newDatabase){
        String sql = "SELECT slug,favoritada,enviada FROM mensagens WHERE favoritada='true' OR enviada='true'";
         Cursor cursor = tempDatabase.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            int cols = cursor.getColumnCount();
            int indexFavoritada = cursor.getColumnIndex(MensagemDAO.COLUNA_FAVORITADA);
            int indexEnviada = cursor.getColumnIndex(MensagemDAO.COLUNA_ENVIADA);
            int indexSlug = cursor.getColumnIndex(MensagemDAO.COLUNA_SLUG);

            String favoritada = cursor.getString(indexFavoritada);
            String enviada = cursor.getString(indexEnviada);
            String slug = cursor.getString(indexSlug);
            String updateSql;

            do {
                updateSql = "UPDATE OR IGNORE "+MensagemDAO.NOME_TABELA+" SET "+
                        MensagemDAO.COLUNA_FAVORITADA+"='"+favoritada+"', "+
                        MensagemDAO.COLUNA_ENVIADA+"='"+enviada+"' WHERE "+
                        MensagemDAO.COLUNA_SLUG+"='"+slug+"'";
                Log.w("Droido", "Executando SQL de atualização" + updateSql);
                newDatabase.execSQL(updateSql);
            } while (cursor.moveToNext());
        }
    }

    private static void testData(SQLiteDatabase database, String tempPathDb){

        String attach = "Attach 'database_temp.sqlite' as temp_db";
        Log.w("Droido", "ATAACCHIINNGGG");
        database.execSQL("attach database ? as temp_db", new String[]{tempPathDb});
        database.beginTransaction();
        String str = "SELECT texto FROM temp_db.mensagens";
        Cursor c = database.rawQuery(str, null);
        c.moveToFirst();
        database.setTransactionSuccessful();
        database.endTransaction();
        database.close();

    }





}