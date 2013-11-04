package br.com.redrails.torpedos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Criado por luiz em 10/26/13.
 * Todos os direitos reservados para RedRails
 */
public class MensagemDAO {


    public static final String NOME_TABELA = "mensagens";
    public static final String COLUNA_ID = "_id";
    public static final String COLUNA_TEXTO = "texto";
    public static final String COLUNA_FAVORITADA = "favoritada";
    public static final String COLUNA_ENVIADA = "enviada";
    public static final String COLUNA_AUTOR = "autor";

    public static final int ORDEM_AVALIACAO = 1;
    public static final int ORDEM_FAVORITOS = 2;
    public static final int ORDEM_DATA = 3;
    public static final int ORDEM_ENVIADAS = 4;
    public static final int ORDEM_NAO_ENVIADAS = 5;

    private static long QUANTIDADE_TOTAL=0;
    public static int QUANTIDADE_POR_PAGINA=20;

    private SQLiteDatabase dataBase = null;


    private static MensagemDAO instance;


    public static MensagemDAO getInstance(Context context) {
        if(instance == null)
            instance = new MensagemDAO(context);
        return instance;
    }

    private MensagemDAO(Context context) {
        DataBaseHelper persistenceHelper = DataBaseHelper.getInstance(context);
        dataBase = persistenceHelper.getWritableDatabase();
    }

    public void salvar(Mensagem mensagem) {
        ContentValues values = gerarContentValeuesMenssagem(mensagem);
        dataBase.insert(NOME_TABELA, null, values);
    }

    public Mensagem getMensagem(int id){

        Cursor cursor = dataBase.query(NOME_TABELA, new String[] { COLUNA_ID,
                COLUNA_TEXTO, COLUNA_ENVIADA, COLUNA_FAVORITADA, COLUNA_AUTOR }, COLUNA_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        List<Mensagem> mensagem = converterCursorEmMensagens(cursor);
        // return contact
        if(!mensagem.isEmpty()){
            return mensagem.get(0);
        }
        return null;
    }

    public List<Mensagem> recuperarTodos() {
        String queryReturnAll = "SELECT * FROM " + NOME_TABELA;
        Cursor cursor = dataBase.rawQuery(queryReturnAll, null);

        return converterCursorEmMensagens(cursor);
    }

    public List<Mensagem> getMensagens(int pagina, int ordem) {
        String parametros = parametrize(pagina);
        String ordem_clausula = ordering(ordem);
        String queryReturnPaginate = "SELECT * FROM " + NOME_TABELA +ordem_clausula+parametros;
        Log.w("Droido","Executando SQL: "+queryReturnPaginate);
        Cursor cursor = dataBase.rawQuery(queryReturnPaginate, null);
        return converterCursorEmMensagens(cursor);
    }

    public void deletar(Mensagem mensagem) {
        String[] valoresParaSubstituir = {
                String.valueOf(mensagem.getId())
        };
        dataBase.delete(NOME_TABELA, COLUNA_ID + " =  ?", valoresParaSubstituir);
    }

    public void atualizar(Mensagem mensagem) {
        ContentValues valores = gerarContentValeuesMenssagem(mensagem);

        String[] valoresParaSubstituir = {
                String.valueOf(mensagem.getId())
        };
        Log.d("Droido","Executando SQL: "+NOME_TABELA+ valores+ COLUNA_ID + " = ?"+ valoresParaSubstituir[0]);
        dataBase.update(NOME_TABELA, valores, COLUNA_ID + " = ?", valoresParaSubstituir);
        //String sql = "UPDATE \"mensagens\" SET \"favoritada\" = \""+mensagem.getFavoritada()+"\" WHERE  \"_id\" = "+mensagem.getId()+" ;";
        //Log.d("Droido","Executando SQL: "+sql);
        //dataBase.execSQL(sql);
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


    private List<Mensagem> converterCursorEmMensagens(Cursor cursor) {
        List<Mensagem> mensagens = new ArrayList<Mensagem>();
        if(cursor == null)
            return mensagens;

        try {

            if (cursor.moveToFirst()) {
                do {

                    int indexID = cursor.getColumnIndex(COLUNA_ID);
                    int indexTexto = cursor.getColumnIndex(COLUNA_TEXTO);
                    int indexFavoritada = cursor.getColumnIndex(COLUNA_FAVORITADA);
                    int indexEnviada = cursor.getColumnIndex(COLUNA_ENVIADA);
                    int indexAutor = cursor.getColumnIndex(COLUNA_AUTOR);

                    int id = cursor.getInt(indexID);
                    String texto = cursor.getString(indexTexto);
                    boolean favoritada = cursor.getString(indexFavoritada).contentEquals("true");

                    boolean enviada = cursor.getString(indexEnviada).contentEquals("true");
                    String autor = cursor.getString(indexAutor);
                    Mensagem mensagem = new Mensagem(id,texto, favoritada, enviada, autor);

                    mensagens.add(mensagem);

                } while (cursor.moveToNext());
            }

        } finally {
            cursor.close();
        }
        return mensagens;
    }

    private ContentValues gerarContentValeuesMenssagem(Mensagem mensagem) {
        ContentValues values = new ContentValues();
        values.put(COLUNA_TEXTO, mensagem.getTexto());
        values.put(COLUNA_FAVORITADA, String.valueOf(mensagem.getFavoritada()));
        values.put(COLUNA_ENVIADA, String.valueOf(mensagem.getEnviada()));
        values.put(COLUNA_AUTOR, String.valueOf(mensagem.getAutor()));

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
