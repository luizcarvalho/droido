package br.com.redrails.torpedos.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.com.redrails.torpedos.DataBaseHelper;
import br.com.redrails.torpedos.models.Categoria;
import br.com.redrails.torpedos.models.Mensagem;

/**
 * Criado por luiz em 10/26/13.
 * Todos os direitos reservados para RedRails
 */
public class CategoriaDAO extends BaseDAO{


    public static final String NOME_TABELA = "categorias";
    public static final String COLUNA_ID = "_id";
    public static final String COLUNA_NOME = "nome";
    public static final String COLUNA_SLUG = "slug";


    public static String SQL_CREATION = "CREATE TABLE "+NOME_TABELA+" (\n" +
            "  "+COLUNA_ID+" INTEGER PRIMARY KEY,\n" +
            "  "+COLUNA_NOME+" TEXT(32),\n" +
            "  "+COLUNA_SLUG+" TEXT(16)\n" +
            ");";

    private static CategoriaDAO instance;
    protected int QUANTIDADE_TOTAL=0;



    public static CategoriaDAO getInstance(Context context) {
        if(instance == null)
            instance = new CategoriaDAO(context);
        return instance;
    }

    private CategoriaDAO(Context context) {
        DataBaseHelper persistenceHelper = DataBaseHelper.getInstance(context);
        dataBase = persistenceHelper.getWritableDatabase();
    }

    public void salvar(Categoria categoria) {
        Log.d("Droido","Salvando Categoria "+categoria.getSlug());
        ContentValues values = gerarContentValeuesCategoria(categoria);
        dataBase.insert(NOME_TABELA, null, values);
        reloadQuantidadeTotal();
    }

    public Categoria getCategoria(int id){


        Cursor cursor = dataBase.query(NOME_TABELA, new String[] { COLUNA_ID,
                COLUNA_NOME, COLUNA_SLUG }, COLUNA_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null);
        List<Categoria> categoria = converterCursorEmCategorias(cursor);
        // return contact
        if(!categoria.isEmpty()){
            return categoria.get(0);
        }
        return null;
    }

    public void deletarTudo(){
        dataBase.execSQL("DELETE FROM " + NOME_TABELA);
        reloadQuantidadeTotal();
    }

    public Categoria getCategoria(String slug){

        Cursor cursor = dataBase.query(NOME_TABELA, new String[] { COLUNA_ID,
                        COLUNA_NOME, COLUNA_SLUG }, COLUNA_SLUG + "=?",
                new String[] { String.valueOf(slug) }, null, null, null);
        List<Categoria> categoria = converterCursorEmCategorias(cursor);
        // return contact
        if(!categoria.isEmpty()){
            return categoria.get(0);
        }
        return null;
    }

    public List<Categoria> getCategoriasFromMensagem(int mensagemId){
        String query = "SELECT * FROM " + NOME_TABELA+
                " LEFT JOIN mensagem_categorias ON mensagem_categorias.categoria_id = categorias._id  " +
                " WHERE mensagem_categorias.mensagem_id="+mensagemId;

        List<Categoria> categorias = new ArrayList<Categoria>();
        Cursor cursor=null;
        try{
            cursor = dataBase.rawQuery(query, null);
            categorias = converterCursorEmCategorias(cursor);
        }catch (Exception e){
            Log.e("RedRails", "Erro: "+e.getMessage());

        }finally {
            if(cursor!=null){
                cursor.close();
            }
        }
        return categorias;
    }


    public List<Categoria> categoriasFixas(){

        List<Categoria> categoriasFixas = new ArrayList<Categoria>();
        Categoria categoriaTodas = new Categoria(Categoria.TODAS,"Todas", null);
        categoriaTodas.setTipoFixa();
        Categoria categoriaFavoritas = new Categoria(Categoria.FAVORITAS,"Favoritas", null);
        categoriaFavoritas.setTipoFixa();
        categoriasFixas.add(categoriaTodas);
        categoriasFixas.add(categoriaFavoritas);

        return categoriasFixas;
    }

    public List<Categoria> recuperarTodas() {
        String queryReturnAll = "SELECT * FROM " + NOME_TABELA;
        List<Categoria> categorias = categoriasFixas();
        Cursor cursor=null;
        try{
            cursor = dataBase.rawQuery(queryReturnAll, null);
            categorias.addAll(converterCursorEmCategorias(cursor));
        }catch (Exception e){
            Log.e("RedRails", "Erro: "+e.getMessage());

        }finally {
            if(cursor!=null){
                cursor.close();
            }
        }
        return categorias;
    }

    private String parametrize(int tipo, Integer pagina){
        String retorno = " * ";
        if(tipo== BUSCA_COUNT){
            retorno = " COUNT(*) ";
        }

        String query = "SELECT "+retorno+" FROM " + NOME_TABELA;
        Log.w("RedRails","Executando SQL: "+query);
        return query;
    }


    public List<Categoria> getCategorias(int pagina) {
        Cursor cursor = dataBase.rawQuery(parametrize(BUSCA_SELECT,pagina), null);
        return converterCursorEmCategorias(cursor);
    }

    public void deletar(Categoria categoria) {
        String[] valoresParaSubstituir = {
                String.valueOf(categoria.getId())
        };
        dataBase.delete(NOME_TABELA, COLUNA_ID + " =  ?", valoresParaSubstituir);
        recuperarTodas();
    }

    public void atualizar(Categoria categoria) {
        ContentValues valores = gerarContentValeuesCategoria(categoria);

        String[] valoresParaSubstituir = {
                String.valueOf(categoria.getId())
        };
        dataBase.update(NOME_TABELA, valores, COLUNA_ID + " = ?", valoresParaSubstituir);
    }
    public void reloadQuantidadeTotal(){
        QUANTIDADE_TOTAL = (int) DatabaseUtils.longForQuery(dataBase,parametrize(BUSCA_COUNT,null) , null);
    }

    public long getQuantidadeTotal() {
        if(QUANTIDADE_TOTAL == 0){
            reloadQuantidadeTotal();
        }
        return QUANTIDADE_TOTAL;
    }

    public  List<Categoria> findBySlugs(List<Categoria> categorias) {
        String slugs = slugfyList(categorias);
        String sql = "SELECT * FROM categorias WHERE slug IN "+slugs;
        Log.d("Droido",sql);
        Cursor cursor = dataBase.rawQuery(sql,null);
        return converterCursorEmCategorias(cursor);
    }

    private String slugfyList(List<Categoria> categorias){

        StringBuilder slugs = new StringBuilder();
        slugs.append("(");
        for(int i = 0; i < categorias.size(); i++) {
            slugs.append("'");
            slugs.append(categorias.get(i).getSlug());
            slugs.append("'");

            if (i < categorias.size()- 1) {
                slugs.append(",");
            }
        }
        slugs.append(")");
        return slugs.toString();
    }


    private List<Categoria> converterCursorEmCategorias(Cursor cursor) {
        List<Categoria> categorias = new ArrayList<Categoria>();
        if(cursor == null)
            return categorias;

        try {

            if (cursor.moveToFirst()) {
                do {

                    int indexID = cursor.getColumnIndex(COLUNA_ID);
                    int indexTexto = cursor.getColumnIndex(COLUNA_NOME);
                    int indexSlug = cursor.getColumnIndex(COLUNA_SLUG);


                    int id = cursor.getInt(indexID);
                    String texto = cursor.getString(indexTexto);
                    String slug = cursor.getString(indexSlug);

                    Categoria categoria = new Categoria(id,texto, slug);

                    categorias.add(categoria);

                } while (cursor.moveToNext());
            }

        } finally {
            cursor.close();
        }
        return categorias;
    }

    private ContentValues gerarContentValeuesCategoria(Categoria categoria) {
        ContentValues values = new ContentValues();
        values.put(COLUNA_NOME, categoria.getNome());
        values.put(COLUNA_SLUG, String.valueOf(categoria.getSlug()));

        return values;
    }

    public void atualizarOuSalvar(Categoria categoria) {

            if(getCategoriaBySlug(categoria.getSlug())==null){
                salvar(categoria);
                Log.d("Droido","Salvando: "+categoria.getSlug());
            }else{
                Log.d("Droido","Atualizando: "+categoria.getSlug());
                atualizarPorSlug(categoria);
            }
    }

    public Categoria getCategoriaBySlug(String slug){
        Cursor cursor = dataBase.rawQuery("SELECT * FROM "+NOME_TABELA+" WHERE slug=? LIMIT 1", new String[]{String.valueOf(slug)});
        List<Categoria> categorias = converterCursorEmCategorias(cursor);
        // return contact
        if(!categorias.isEmpty()){
            return categorias.get(0);
        }
        return null;
    }

    public void atualizarPorSlug(Categoria categoria) {
        ContentValues valores = gerarContentValeuesCategoria(categoria);

        String[] slug = {
                categoria.getSlug()
        };
        dataBase.update(NOME_TABELA, valores, COLUNA_SLUG + " = ?", slug);
    }


}
