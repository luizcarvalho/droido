package br.com.redrails.torpedos.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.com.redrails.torpedos.Mensagem;
import br.com.redrails.torpedos.MensagemDAO;

/**
 * Created by desenvolvimento on 12/16/13.
 */
public class MemoryUpgrade {
    public MemoryUpgrade(){

    }

    public List<Mensagem> importFavsESends(SQLiteDatabase database){
        String sql = "SELECT slug,favoritada,enviada FROM mensagens WHERE favoritada='true' OR enviada='true'";
        Cursor cursor;
        List<Mensagem> mensagens = new ArrayList<Mensagem>();

        try{
            cursor = database.rawQuery(sql, null);
        }catch(Exception e){
            Log.e("RedRails", "Import Favoritos: " + e.getMessage());
            return mensagens;
        }

        if (cursor!= null && cursor.moveToFirst()) {
            int indexFavoritada = cursor.getColumnIndex(MensagemDAO.COLUNA_FAVORITADA);
            int indexEnviada = cursor.getColumnIndex(MensagemDAO.COLUNA_ENVIADA);
            int indexSlug = cursor.getColumnIndex(MensagemDAO.COLUNA_SLUG);
            do {
                boolean favoritada = cursor.getString(indexFavoritada).equalsIgnoreCase("true");
                boolean enviada = cursor.getString(indexEnviada).equalsIgnoreCase("true");
                String slug = cursor.getString(indexSlug);
                Mensagem mensagem = new Mensagem(0,"", favoritada, enviada, null, slug);
                Log.w("Redrails", "MENSAGEM: "+mensagem);
                mensagens.add(mensagem);
            } while (cursor.moveToNext());
        }
        //database.execSQL("DROP TABLE mensagens");
        return mensagens;
    }


    public boolean insertFavsESends(SQLiteDatabase database, List<Mensagem> mensagens){
        String updateSql;

        try{
            database.beginTransaction();
            for(int i=0;i<mensagens.size();i+=1){
                Mensagem mensagem = mensagens.get(i);
                updateSql = "UPDATE main."+MensagemDAO.NOME_TABELA+" SET "+
                        MensagemDAO.COLUNA_FAVORITADA+"='"+mensagem.getFavoritada()+"', "+
                        MensagemDAO.COLUNA_ENVIADA+"='"+mensagem.getEnviada()+"' WHERE "+
                        MensagemDAO.COLUNA_SLUG+"='"+mensagem.getSlug()+"'";
                Log.w("RedRails", "Executando SQL de atualização " + updateSql);
                database.execSQL(updateSql);
            }
            database.setTransactionSuccessful();
        }finally {
            database.endTransaction();

        }
        return true;
    }

}
