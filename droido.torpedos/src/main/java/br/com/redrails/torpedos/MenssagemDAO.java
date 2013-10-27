package br.com.redrails.torpedos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luiz on 10/26/13.
 */
public class MenssagemDAO {


    public static final String NOME_TABELA = "menssagens";
    public static final String COLUNA_ID = "_id";
    public static final String COLUNA_TEXTO = "texto";
    public static final String COLUNA_FAVORITADA = "favoritada";
    public static final String COLUNA_ENVIADA = "enviada";


    private SQLiteDatabase dataBase = null;


    private static MenssagemDAO instance;

    public static MenssagemDAO getInstance(Context context) {
        if(instance == null)
            instance = new MenssagemDAO(context);
        return instance;
    }

    private MenssagemDAO(Context context) {
        DataBaseHelper persistenceHelper = DataBaseHelper.getInstance(context);
        dataBase = persistenceHelper.getWritableDatabase();
    }

    public void salvar(Menssagem veiculo) {
        ContentValues values = gerarContentValeuesVeiculo(veiculo);
        dataBase.insert(NOME_TABELA, null, values);
    }

    public List<Menssagem> recuperarTodos() {
        String queryReturnAll = "SELECT * FROM " + NOME_TABELA + " LIMIT 100";
        Cursor cursor = dataBase.rawQuery(queryReturnAll, null);
        List<Menssagem> menssagens = construirMenssagemPorCursor(cursor);

        return menssagens;
    }

    public void deletar(Menssagem menssagem) {

        String[] valoresParaSubstituir = {
                String.valueOf(menssagem.getId())
        };

        dataBase.delete(NOME_TABELA, COLUNA_ID + " =  ?", valoresParaSubstituir);
    }

    public void editar(Menssagem menssagem) {
        ContentValues valores = gerarContentValeuesVeiculo(menssagem);

        String[] valoresParaSubstituir = {
                String.valueOf(menssagem.getId())
        };

        dataBase.update(NOME_TABELA, valores, COLUNA_ID + " = ?", valoresParaSubstituir);
    }

    public void fecharConexao() {
        if(dataBase != null && dataBase.isOpen())
            dataBase.close();
    }


    private List<Menssagem> construirMenssagemPorCursor(Cursor cursor) {
        List<Menssagem> veiculos = new ArrayList<Menssagem>();
        if(cursor == null)
            return veiculos;

        try {

            if (cursor.moveToFirst()) {
                do {

                    int indexID = cursor.getColumnIndex(COLUNA_ID);
                    int indexTexto = cursor.getColumnIndex(COLUNA_TEXTO);
                    int indexFavoritada = cursor.getColumnIndex(COLUNA_FAVORITADA);
                    int indexEnviada = cursor.getColumnIndex(COLUNA_ENVIADA);

                    int id = cursor.getInt(indexID);
                    String texto = cursor.getString(indexTexto);
                    String favoritada = cursor.getString(indexFavoritada);
                    String enviada = cursor.getString(indexEnviada);

                    Menssagem menssagem = new Menssagem(id, texto);

                    veiculos.add(menssagem);

                } while (cursor.moveToNext());
            }

        } finally {
            cursor.close();
        }
        return veiculos;
    }

    private ContentValues gerarContentValeuesVeiculo(Menssagem menssagem) {
        ContentValues values = new ContentValues();
        values.put(COLUNA_TEXTO, menssagem.getTexto());
        values.put(COLUNA_FAVORITADA, menssagem.getFavoritada());
        values.put(COLUNA_ENVIADA, menssagem.getEnviada());

        return values;
    }
}
