package br.com.redrails.torpedos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.com.redrails.torpedos.categoria.Categoria;

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
    public static final int ORDEM_ALEATORIA = 6;

    public static long QUANTIDADE_TOTAL=0;
    public static int QUANTIDADE_POR_PAGINA=20;

    private int TIPO_BUSCA_SELECT = 1;
    private int TIPO_BUSCA_COUNT = 2;

    private SQLiteDatabase dataBase = null;


    private static MensagemDAO instance;
    public Filtro filtro;


    public static MensagemDAO getInstance(Context context) {
        if(instance == null)
            instance = new MensagemDAO(context);
        return instance;
    }

    private MensagemDAO(Context context) {
        DataBaseHelper persistenceHelper = DataBaseHelper.getInstance(context);
        filtro = new Filtro();
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

    private String parametrize(int tipo, Integer pagina){
        String retorno = " * ";
        if(tipo==TIPO_BUSCA_COUNT){
            retorno = " COUNT(*) ";
        }
        String paginacao = paginate(pagina);

        String query = "SELECT "+retorno+" FROM " + NOME_TABELA +filtro.getClausula()+paginacao;
        Log.w("Droido","Executando SQL: "+query);
        return query;
    }


    public List<Mensagem> getMensagens(int pagina) {
        Cursor cursor = dataBase.rawQuery(parametrize(TIPO_BUSCA_SELECT,pagina), null);
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
        dataBase.update(NOME_TABELA, valores, COLUNA_ID + " = ?", valoresParaSubstituir);

    }
    public void reloadQuantidadeTotal(){
        QUANTIDADE_TOTAL =DatabaseUtils.longForQuery(dataBase,parametrize(TIPO_BUSCA_COUNT,null) , null);
    }

    public long getQuantidadeTotal() {
        if(QUANTIDADE_TOTAL == 0){
            reloadQuantidadeTotal();
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
    private String paginate(Integer pagina){
        if(pagina!=null ){
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
        return "";

    }



    public Filtro getFiltro(){
        return instance.filtro;
    }

    public class Filtro{
        private String busca;
        private int ordem = ORDEM_ALEATORIA;
        private ArrayList<Integer> categorias = new ArrayList<Integer>();

        public Filtro(){
        }

        public void addCategoria(int categoriaID){
            this.categorias.add(categoriaID);
        }
        public void setCategoria(int categoriaID){
            ArrayList<Integer> array = new ArrayList<Integer>();
            array.add(categoriaID);
            this.categorias = array;
        }

        public ArrayList<Integer> getCategorias(){
            return categorias;
        }

        public void setBusca(String termo){
            this.busca = termo;
        }
        public void setOrdem(int ordem){
            this.ordem = ordem;
        }

        private String sqlForOrdem(){
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
                case ORDEM_AVALIACAO:
                    return ordemClausula+" avaliacao DESC ";
                case ORDEM_ALEATORIA:
                    return ordemClausula+" RANDOM()  ";
                default:
                    return ordemClausula+" RANDOM()  ";

            }

        }


        private String sqlForCategorias(){
            String sql = "";
            if(!categorias.isEmpty()){
                int categorias_size = categorias.size();
                if(!categorias.isEmpty()){
                    sql+=" LEFT JOIN mensagem_categorias ON mensagem_categorias.mensagem_id = mensagens._id  ";
                }
                sql+=" WHERE ";
                for(int i=0;i<categorias_size;i++){
                    sql+=" mensagem_categorias.categoria_id= '"+categorias.get(i)+"' ";
                    if((i+1)<categorias_size){
                        sql+=" and ";
                    }
                }

            }else{
                return "";
            }
            return sql;
        }

        private String sqlForBusca(){
            String sql = "";
            if(busca!=null){
                if(categorias.isEmpty()){
                    sql+=" WHERE "+sql;
                }else{
                    sql+=" and ";
                }
                sql+=" "+NOME_TABELA+"."+COLUNA_TEXTO+" like '%"+busca+"%' ";

            }

            return sql;
        }

        public String getClausula(){

            String categoriaSql = sqlForCategorias();
            String buscaSql = sqlForBusca();
            String ordemSql = sqlForOrdem();

            return categoriaSql+buscaSql+ordemSql;
        }

    }

}

