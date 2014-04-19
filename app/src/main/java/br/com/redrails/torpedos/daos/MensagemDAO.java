package br.com.redrails.torpedos.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.com.redrails.torpedos.DataBaseHelper;
import br.com.redrails.torpedos.models.Categoria;
import br.com.redrails.torpedos.models.Mensagem;
import br.com.redrails.torpedos.models.MensagemCategoria;

/**
 * Criado por luiz em 10/26/13.
 * Todos os direitos reservados para RedRails
 */
public class MensagemDAO extends BaseDAO{


    public static final String NOME_TABELA = "mensagens";
    public static final String COLUNA_ID = "_id";
    public static final String COLUNA_TEXTO = "texto";
    public static final String COLUNA_SLUG = "slug";
    public static final String COLUNA_FAVORITADA = "favoritada";
    public static final String COLUNA_ENVIADA = "enviada";
    public static final String COLUNA_AUTOR = "autor";
    public static final String COLUNA_AVALIACAO = "avaliacao";
    public static final String COLUNA_DATA = "data";

    public static final int ORDEM_AVALIACAO = 1;
    public static final int ORDEM_FAVORITOS = 2;
    public static final int ORDEM_DATA = 3;
    public static final int ORDEM_ENVIADAS = 4;
    public static final int ORDEM_NAO_ENVIADAS = 5;
    public static final int ORDEM_ALEATORIA = 6;

    public static long QUANTIDADE_TOTAL=0;
    public static int QUANTIDADE_POR_PAGINA=20;

    public Filtro filtro;


    public static String SQL_CREATION = "CREATE TABLE mensagens \n("+
            " "+COLUNA_ID+" INTEGER PRIMARY KEY ,\n"+
            " "+COLUNA_TEXTO+" TEXT,\n"+
            " "+COLUNA_SLUG+" TEXT(16),\n"+
            " "+COLUNA_AVALIACAO+" REAL DEFAULT (2.5) ,\n"+
            " "+COLUNA_ENVIADA+" TEXT(8) DEFAULT 'false' ,\n"+
            " "+COLUNA_FAVORITADA+" TEXT(8) DEFAULT 'false' ,\n"+
            " "+COLUNA_DATA+" INTEGER DEFAULT (0) ,\n"+
            " "+COLUNA_AUTOR+" TEXT(64) DEFAULT ('Luiz Carvalho')\n"+
            ");";


    private static MensagemDAO instance;
    private CategoriaDAO categoriaDao;

    public static MensagemDAO getInstance(Context context) {
        if(instance == null)
            Log.w("RedRails","NOVA INSTANCIA");
            instance = new MensagemDAO(context);
        return instance;
    }


    private MensagemDAO(Context context) {
        DataBaseHelper persistenceHelper = DataBaseHelper.getInstance(context);
        filtro = new Filtro();
        dataBase = persistenceHelper.getWritableDatabase();
    }




    public static MensagemDAO getInstance(Context context, int type) {
        if(instance == null)
            instance = new MensagemDAO(context, type);
        return instance;
    }


    private MensagemDAO(Context context, int type) {
        DataBaseHelper persistenceHelper = DataBaseHelper.getInstance(context);
        filtro = new Filtro();
        dataBase = persistenceHelper.getWritableDatabase();
    }

    public void salvar(Mensagem mensagem) {
        ContentValues values = gerarContentValeuesMenssagem(mensagem);
        dataBase.insert(NOME_TABELA, null, values);
        reloadQuantidadeTotal();
    }

    public void atualizarOuSalvar(Mensagem mensagem){
        if(getMensagemBySlug(mensagem.getSlug())==null){
            salvar(mensagem);
            Log.d("Droido","Salvando: "+mensagem.getSlug());
        }else{
            Log.d("Droido","Atualizando: "+mensagem.getSlug());
            atualizar(mensagem);
        }
    }

    public Mensagem getMensagemBySlug(String slug){
        Cursor cursor = dataBase.rawQuery("SELECT * FROM mensagens WHERE slug=? LIMIT 1", new String[]{String.valueOf(slug)});
        List<Mensagem> mensagem = converterCursorEmMensagens(cursor);
        // return contact
        if(!mensagem.isEmpty()){
            return mensagem.get(0);
        }
        return null;
    }

    public Mensagem getMensagem(int id){
        Cursor cursor = dataBase.rawQuery("SELECT * FROM mensagens WHERE _id=? LIMIT 1",new String[]{String.valueOf(id)});

        /*Cursor cursor = dataBase.query(NOME_TABELA, new String[] { COLUNA_ID,
                COLUNA_TEXTO, COLUNA_ENVIADA, COLUNA_FAVORITADA, COLUNA_AUTOR }, COLUNA_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
                */
        List<Mensagem> mensagem = converterCursorEmMensagens(cursor);
        // return contact
        if(!mensagem.isEmpty()){
            return mensagem.get(0);
        }
        return null;
    }

    public Mensagem getMensagemByOrder(String order){
        Cursor cursor = dataBase.rawQuery("SELECT * FROM mensagens ORDER BY _id "+order+" LIMIT 1", null);
        List<Mensagem> mensagem = converterCursorEmMensagens(cursor);
        // return contact
        if(!mensagem.isEmpty()){
            return mensagem.get(0);
        }
        return null;
    }

    public Mensagem first(){
            return getMensagemByOrder("ASC");
    }

    public Mensagem last(){
        return getMensagemByOrder("DESC");
    }


    public List<Mensagem> recuperarTodas() {
        String queryReturnAll = "SELECT * FROM " + NOME_TABELA;
        Cursor cursor = dataBase.rawQuery(queryReturnAll, null);

        return converterCursorEmMensagens(cursor);
    }

    private String parametrize(int tipo, Integer pagina){
        String retorno = " * ";
        String paginacao = paginate(pagina);
        if(tipo==BUSCA_COUNT){
            retorno = " COUNT(*) ";
        }

        String query = "SELECT "+retorno+" FROM " + NOME_TABELA +filtro.getClausula()+paginacao;
        Log.w("RedRails","Executando SQL: "+query);
        return query;
    }


    public List<Mensagem> getMensagens(int pagina) {
        Cursor cursor = dataBase.rawQuery(parametrize(BUSCA_SELECT,pagina), null);
        return converterCursorEmMensagens(cursor);
    }


    public void deletar(Mensagem mensagem) {
        String[] valoresParaSubstituir = {
                String.valueOf(mensagem.getId())
        };
        dataBase.delete(NOME_TABELA, COLUNA_ID + " =  ?", valoresParaSubstituir);
        reloadQuantidadeTotal();
    }

    public void deletarTudo(){
        dataBase.execSQL("DELETE FROM " + NOME_TABELA);
        QUANTIDADE_TOTAL=0;
    }

    public void atualizar(Mensagem mensagem) {
        ContentValues valores = gerarContentValeuesMenssagem(mensagem);

        String[] valoresParaSubstituir = {
                String.valueOf(mensagem.getId())
        };
        dataBase.update(NOME_TABELA, valores, COLUNA_ID + " = ?", valoresParaSubstituir);
    }

    public void atualizarMensagensCategoria(List<String> categoriasSlugs, Mensagem mensagem){
        if(!categoriasSlugs.isEmpty()){
            List<Categoria> categorias = categoriaDao.findBySlugs(categoriasSlugs);

            //MensagemCategoria.updateRelation()
        }
    }

    public void reloadQuantidadeTotal(){
        QUANTIDADE_TOTAL =DatabaseUtils.longForQuery(dataBase,parametrize(BUSCA_COUNT,null) , null);
    }

    public long getQuantidadeTotal() {
        if(QUANTIDADE_TOTAL == 0){
            reloadQuantidadeTotal();
        }
            return QUANTIDADE_TOTAL;
    }

    public List<Mensagem> converterCursorEmMensagens(Cursor cursor) {
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
                    int indexSlug = cursor.getColumnIndex(COLUNA_SLUG);
                    int indexAvaliacao = cursor.getColumnIndex(COLUNA_AVALIACAO);
                    int indexData = cursor.getColumnIndex(COLUNA_DATA);
                    int id = cursor.getInt(indexID);

                    String texto = cursor.getString(indexTexto);
                    boolean favoritada = cursor.getString(indexFavoritada).equalsIgnoreCase("true");

                    boolean enviada = cursor.getString(indexEnviada).equalsIgnoreCase("true");
                    String autor = cursor.getString(indexAutor);
                    String slug = cursor.getString(indexSlug);
                    Integer avalicao = cursor.getInt(indexAvaliacao);
                    Integer data = cursor.getInt(indexData);

                    Mensagem mensagem = new Mensagem(id,texto, favoritada, enviada, autor, slug, avalicao, data);

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
        values.put(COLUNA_SLUG, mensagem.getSlug());
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

    public void createOrUpdate(Mensagem mensagem, String categorias){
        String  query = "INSERT OR REPLACE INTO "+NOME_TABELA+
                " ("+COLUNA_ID+","+COLUNA_TEXTO+","+COLUNA_SLUG+", "+COLUNA_AUTOR+") \n" +
                "  VALUES ( " +
                "(SELECT "+COLUNA_ID+" FROM "+NOME_TABELA+" WHERE "+COLUNA_SLUG+"=\""+mensagem.getSlug()+"\") , \""+
                mensagem.getTexto()+"\", \""+
                mensagem.getSlug()+"\", \""+
                mensagem.getAutor()+
                "\"          ) ;";
        String  query2 = "INSERT INTO "+NOME_TABELA+
                " ("+COLUNA_TEXTO+","+COLUNA_SLUG+", "+COLUNA_AUTOR+") \n" +
                "  VALUES ( \"" +
                mensagem.getTexto()+"\", \""+
                mensagem.getSlug()+"\", \""+
                mensagem.getAutor()+
                "\"          ) ;";

        Log.w("RedRails",query);
        dataBase.execSQL(query);

    }



    public Filtro getFiltro(){
        return instance.filtro;
    }


    public class Filtro{
        private String busca;
        private int ordem = ORDEM_ALEATORIA;
        private ArrayList<Categoria> categorias = new ArrayList<Categoria>();
        private ArrayList<Categoria> staticCategorias = new ArrayList<Categoria>();
        boolean hasClausula=false;

        public Filtro(){
        }

        public void addCategoria(Categoria categoria){
            this.categorias.add(categoria);
            if(categoria.getTipo()==Categoria.TIPO_DINAMICA){
                this.categorias.add(categoria);
            }else{
                this.staticCategorias.add(categoria);
            }
        }
        public void setCategoria(Categoria categoria){
            ArrayList<Categoria> array = new ArrayList<Categoria>();
            ArrayList<Categoria> blankArray = new ArrayList<Categoria>();
            array.add(categoria);
            if(categoria.getTipo()==Categoria.TIPO_DINAMICA){
                this.categorias = array;
                this.staticCategorias = blankArray;//Seta apenas um das categorias por vez
            }else{
                this.staticCategorias = array;
                this.categorias = blankArray;//Seta apenas um das categorias por vez
            }
        }

        public ArrayList<Categoria> getCategorias(){
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


        private String sqlParaCategoriasDinamicas(){
            String sql = "";

            if(!categorias.isEmpty()){
                int categorias_size = categorias.size();
                hasClausula=true;

                for(int i=0;i<categorias_size;i++){
                    sql+=" mensagem_categorias.categoria_id= '"+categorias.get(i).getId()+"' ";
                    if((i+1)<categorias_size){
                        sql+=" and ";
                    }
                }

            }else{
                hasClausula=false;
                return "";
            }
            return sql;
        }

        private String sqlParaCategoriasEstaticas(){
            String sql = "";
            for(int i=0; i<staticCategorias.size();i++){
                if(staticCategorias.get(i).getId()==Categoria.TODAS){
                    hasClausula=false;
                    return "";

                }
                if(staticCategorias.get(i).getId()==Categoria.FAVORITAS){
                    sql+=" favoritada='true' ";
                    hasClausula=true;
                }
            }


            return sql;
        }


        /*
        * # se staticas vazias e dinamicas vazias
        *   => ""
        *
        * # se estaticas=1+ e dinamicas vazias
        *  =>  WHERE favoritada = 'true' and*
        *
        * # se estática 1+ e dinâmica 1+
        *  => LEFT JOIN mensagem_categorias ON mensagem_categorias.mensagem_id = mensagens._id   WHERE  mensagem_categorias.categoria_id= '1'
        *
        *
        * */



        private String sqlForCategorias(){

            String sql = "";
            String sqlDinamica = sqlParaCategoriasDinamicas();
            String sqlEstatica =  sqlParaCategoriasEstaticas();
            if(sqlDinamica!=""){
                sql+=" LEFT JOIN mensagem_categorias ON mensagem_categorias.mensagem_id = mensagens._id  ";
                hasClausula=true;
            }
            if(hasClausula){
                sql+=" WHERE ";
            }
            sql+=sqlDinamica+sqlEstatica;
            Log.w("RedRails","SQL GERADA: "+sql);
            return sql;
        }

        private String sqlForBusca(){
            String sql = "";
            if(busca!=null){
                if(!hasClausula){
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

