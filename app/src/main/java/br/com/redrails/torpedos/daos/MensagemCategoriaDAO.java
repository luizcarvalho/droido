package br.com.redrails.torpedos.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import br.com.redrails.torpedos.models.Categoria;
import br.com.redrails.torpedos.models.MensagemCategoria;
import br.com.redrails.torpedos.models.Mensagem;


public class MensagemCategoriaDAO extends BaseDAO{


    public static final String NOME_TABELA = "mensagem_categorias";
    public static final String COLUNA_ID = "_id";
    public static final String COLUNA_MENSAGEM_ID = "mensagem_id";
    public static final String COLUNA_CATEGORIA_ID = "categoria_id";


    public static String SQL_CREATION = "CREATE TABLE "+NOME_TABELA+" (\n" +
            "  "+COLUNA_ID+" INTEGER PRIMARY KEY,\n" +
            "  "+ COLUNA_MENSAGEM_ID +" INTEGER ,\n" +
            "  "+ COLUNA_CATEGORIA_ID +" INTEGER\n" +
            ");";

    private int QUANTIDADE_TOTAL=0;


    private static MensagemCategoriaDAO instance;
    private static Context myContext;
    private CategoriaDAO categoriaDAO;
    private MensagemDAO mensagemDAO;


    public static MensagemCategoriaDAO getInstance(Context context) {
        if(instance == null)
            instance = new MensagemCategoriaDAO(context);
        return instance;
    }

    private MensagemCategoriaDAO(Context context) {
        myContext=context;
        DataBaseHelper persistenceHelper = DataBaseHelper.getInstance(context);
        dataBase = persistenceHelper.getWritableDatabase();
        categoriaDAO = CategoriaDAO.getInstance(context);
        mensagemDAO = MensagemDAO.getInstance(context);
    }

    public void salvar(MensagemCategoria mensagemCategoria) {
        ContentValues values = gerarContentValeuesMensagemCategoria(mensagemCategoria);
        dataBase.insert(NOME_TABELA, null, values);
        reloadQuantidadeTotal();
    }

    public MensagemCategoria getMensagemCategoria(int id){

        Cursor cursor = dataBase.query(NOME_TABELA, new String[] { COLUNA_ID,
                        COLUNA_MENSAGEM_ID, COLUNA_CATEGORIA_ID}, COLUNA_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null);
        List<MensagemCategoria> mensagemCategoria = converterCursorEmMensagemCategorias(cursor);
        // return contact
        if(!mensagemCategoria.isEmpty()){
            return mensagemCategoria.get(0);
        }
        return null;
    }

    public void deletar(MensagemCategoria mensagemCategoria) {
        String[] valoresParaSubstituir = {
                String.valueOf(mensagemCategoria.getId())
        };
        dataBase.delete(NOME_TABELA, COLUNA_ID + " =  ?", valoresParaSubstituir);
        reloadQuantidadeTotal();
    }

    public void deletarAllOf(int mensagemId) {
        String[] valoresParaSubstituir = {
                String.valueOf(mensagemId)
        };
        dataBase.delete(NOME_TABELA, COLUNA_MENSAGEM_ID + " =  ?", valoresParaSubstituir);
        reloadQuantidadeTotal();
    }

    public void deletarTudo(){
        dataBase.execSQL("DELETE FROM " + NOME_TABELA);
        reloadQuantidadeTotal();
    }

    public void atualizar(MensagemCategoria mensagemCategoria) {
        ContentValues valores = gerarContentValeuesMensagemCategoria(mensagemCategoria);

        String[] valoresParaSubstituir = {
                String.valueOf(mensagemCategoria.getId())
        };
        dataBase.update(NOME_TABELA, valores, COLUNA_ID + " = ?", valoresParaSubstituir);
        recuperarTodas();

    }

    public int atualizarRelacionamento(Mensagem mensagem){
        int count = 0;
        if(!mensagem.getCategorias().isEmpty()){

            List<Categoria> categorias = categoriaDAO.findBySlugs(mensagem.getCategorias());
            mensagem = mensagemDAO.getMensagemBySlug(mensagem.getSlug());
            MensagemCategoria mensagemCategoria;

            deletarAllOf(mensagem.getId());
            for(Categoria categoria : categorias){
                count+=1;
                mensagemCategoria = new MensagemCategoria(0,categoria.getId(), mensagem.getId());
                salvar(mensagemCategoria);
            }

        }
        return  count;
    }


    public List<MensagemCategoria> recuperarTodas() {
        String queryReturnAll = "SELECT * FROM " + NOME_TABELA;
        Cursor cursor = dataBase.rawQuery(queryReturnAll, null);

        return converterCursorEmMensagemCategorias(cursor);
    }


    public void reloadQuantidadeTotal(){
        QUANTIDADE_TOTAL = dataBase.rawQuery("SELECT * FROM "+NOME_TABELA, null).getCount();
    }

    public long getQuantidadeTotal() {
        if(QUANTIDADE_TOTAL == 0){
            reloadQuantidadeTotal();
        }
        return QUANTIDADE_TOTAL;
    }


    private List<MensagemCategoria> converterCursorEmMensagemCategorias(Cursor cursor) {
        List<MensagemCategoria> mensagemCategorias = new ArrayList<MensagemCategoria>();
        if(cursor == null)
            return mensagemCategorias;

        try {

            if (cursor.moveToFirst()) {
                do {

                    int indexID = cursor.getColumnIndex(COLUNA_ID);
                    int indexCategoriaId = cursor.getColumnIndex(COLUNA_CATEGORIA_ID);
                    int indexMensagemId = cursor.getColumnIndex(COLUNA_MENSAGEM_ID);


                    int id = cursor.getInt(indexID);
                    Integer categoriaId = cursor.getInt(indexCategoriaId);
                    Integer mensagemId = cursor.getInt(indexMensagemId);

                    MensagemCategoria mensagemCategoria = new MensagemCategoria(id,categoriaId, mensagemId);

                    mensagemCategorias.add(mensagemCategoria);

                } while (cursor.moveToNext());
            }

        } finally {
            cursor.close();
        }
        return mensagemCategorias;
    }

    private ContentValues gerarContentValeuesMensagemCategoria(MensagemCategoria mensagemCategoria) {
        ContentValues values = new ContentValues();
        CategoriaDAO categoriaDao = CategoriaDAO.getInstance(myContext);

        values.put(COLUNA_CATEGORIA_ID, mensagemCategoria.getCategoriaId());
        values.put(COLUNA_MENSAGEM_ID, String.valueOf(mensagemCategoria.getMensagemId()));

        return values;
    }

}

