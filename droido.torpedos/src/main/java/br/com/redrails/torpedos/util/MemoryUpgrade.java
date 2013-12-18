package br.com.redrails.torpedos.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

import java.util.ArrayList;
import java.util.List;

import br.com.redrails.torpedos.Mensagem;
import br.com.redrails.torpedos.MensagemDAO;

/**
 * Created by desenvolvimento on 12/16/13.
 */
public class MemoryUpgrade {
    Context myContext;
    public MemoryUpgrade(Context context){
        myContext = context;
    }

    public List<Mensagem> importFavsESends(SQLiteDatabase database){
        String sql = "SELECT slug,favoritada,enviada FROM mensagens WHERE favoritada='true' OR enviada='true'";
        Cursor cursor;
        List<Mensagem> mensagens = new ArrayList<Mensagem>();

        try{
            cursor = database.rawQuery(sql, null);
        }catch(Exception e){
            Log.e("RedRails", "Import Favoritos: " + e.getMessage());
            reportError(e);
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
                mensagens.add(mensagem);
            } while (cursor.moveToNext());
        }
        if(cursor!=null) {
            cursor.close();
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
                //Log.w("RedRails", "Executando SQL de atualização " + updateSql);
                database.execSQL(updateSql);
            }
            database.setTransactionSuccessful();
        }catch (Exception e){
            reportError(e);
        }finally {
            database.endTransaction();

        }
        return true;
    }
    private void reportError(Exception e){
        EasyTracker easyTracker = EasyTracker.getInstance(myContext);
        easyTracker.send(MapBuilder.createException("ERRO :"+e, false).build()
        );
    }



}
