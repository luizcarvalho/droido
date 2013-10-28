package br.com.redrails.torpedos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Criado por luiz em 10/26/13.
 * Todos os direitos reservados para RedRails
 */
public class MenssagemDAO {


    public static final String NOME_TABELA = "menssagens";
    public static final String COLUNA_ID = "_id";
    public static final String COLUNA_TEXTO = "texto";
    public static final String COLUNA_FAVORITADA = "favoritada";
    public static final String COLUNA_ENVIADA = "enviada";

    public static final int ORDEM_AVALIACAO = 1;
    public static final int ORDEM_FAVORITOS = 2;
    public static final int ORDEM_DATA = 3;
    public static final int ORDEM_ENVIADAS = 4;
    public static final int ORDEM_NAO_ENVIADAS = 5;

    private static long QUANTIDADE_TOTAL=0;
    public static int QUANTIDADE_POR_PAGINA=20;

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

    public void salvar(Menssagem menssagem) {
        ContentValues values = gerarContentValeuesMenssagem(menssagem);
        dataBase.insert(NOME_TABELA, null, values);
    }

    public List<Menssagem> recuperarTodos() {
        String queryReturnAll = "SELECT * FROM " + NOME_TABELA;
        Cursor cursor = dataBase.rawQuery(queryReturnAll, null);

        return converterCursorEmMenssagens(cursor);
    }

    public List<Menssagem> getMenssagens(int pagina, int ordem) {
        String parametros = parametrize(pagina);
        String ordem_clausula = ordering(ordem);
        String queryReturnPaginate = "SELECT * FROM " + NOME_TABELA +ordem_clausula+parametros;
        Log.w("Droido","Executando SQL: "+queryReturnPaginate);
        Cursor cursor = dataBase.rawQuery(queryReturnPaginate, null);
        return converterCursorEmMenssagens(cursor);
    }

    public void deletar(Menssagem menssagem) {
        String[] valoresParaSubstituir = {
                String.valueOf(menssagem.getId())
        };
        dataBase.delete(NOME_TABELA, COLUNA_ID + " =  ?", valoresParaSubstituir);
    }

    public void editar(Menssagem menssagem) {
        ContentValues valores = gerarContentValeuesMenssagem(menssagem);

        String[] valoresParaSubstituir = {
                String.valueOf(menssagem.getId())
        };

        dataBase.update(NOME_TABELA, valores, COLUNA_ID + " = ?", valoresParaSubstituir);
    }

    
    public long getQuantidadeTotal() {
        if(QUANTIDADE_TOTAL == 0){
            String queryCountTotal = "SELECT COUNT(*) FROM "+ NOME_TABELA;
            QUANTIDADE_TOTAL =DatabaseUtils.longForQuery(dataBase,queryCountTotal , null);
        }
            return QUANTIDADE_TOTAL;
    }

    public void fecharConexao() {
        if(dataBase != null && dataBase.isOpen())
            dataBase.close();
    }


    private List<Menssagem> converterCursorEmMenssagens(Cursor cursor) {
        List<Menssagem> menssagens = new ArrayList<Menssagem>();
        if(cursor == null)
            return menssagens;

        try {

            if (cursor.moveToFirst()) {
                do {

                    int indexID = cursor.getColumnIndex(COLUNA_ID);
                    int indexTexto = cursor.getColumnIndex(COLUNA_TEXTO);
                    int indexFavoritada = cursor.getColumnIndex(COLUNA_FAVORITADA);
                    int indexEnviada = cursor.getColumnIndex(COLUNA_ENVIADA);

                    int id = cursor.getInt(indexID);
                    String texto = cursor.getString(indexTexto);
                    boolean favoritada = cursor.getInt(indexFavoritada)>0;
                    boolean enviada = cursor.getInt(indexEnviada)>0;

                    Menssagem menssagem = new Menssagem(id,texto, favoritada, enviada);

                    menssagens.add(menssagem);

                } while (cursor.moveToNext());
            }

        } finally {
            cursor.close();
        }
        return menssagens;
    }

    private ContentValues gerarContentValeuesMenssagem(Menssagem menssagem) {
        ContentValues values = new ContentValues();
        values.put(COLUNA_TEXTO, menssagem.getTexto());
        values.put(COLUNA_FAVORITADA, menssagem.getFavoritada());
        values.put(COLUNA_ENVIADA, menssagem.getEnviada());

        return values;
    }
    private String parametrize(int pagina){
        String offset_sql = " OFFSET ";
        String limit_sql = " LIMIT ";
        int limit = pagina*QUANTIDADE_POR_PAGINA;
        int offset = limit-QUANTIDADE_POR_PAGINA;
        if(offset!=0){
            offset_sql = offset_sql+offset;
        }else{
            offset_sql="";
        }
        limit_sql +=limit;

        return limit_sql+offset_sql;
    }

    private String ordering(int ordem){
        String ordemClausula = " ORDER BY ";
        switch (ordem){
            case ORDEM_FAVORITOS:
                return ordemClausula+" favoritada DESC ";
            case ORDEM_DATA:
                return ordemClausula+" data DESC ";
            case ORDEM_ENVIADAS:
                return ordemClausula+" enviada DESC ";
            case ORDEM_NAO_ENVIADAS:
                return ordemClausula+" enviada ASC ";
            default:
                return ordemClausula+" avaliacao DESC ";

        }

    }
}
