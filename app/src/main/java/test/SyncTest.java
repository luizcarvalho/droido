package test;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import android.test.suitebuilder.annotation.LargeTest;


import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import br.com.redrails.torpedos.daos.CategoriaDAO;
import br.com.redrails.torpedos.daos.MensagemCategoriaDAO;
import br.com.redrails.torpedos.daos.MensagemDAO;
import br.com.redrails.torpedos.models.Categoria;
import br.com.redrails.torpedos.models.Mensagem;
import br.com.redrails.torpedos.parse.ParseHelper;

/**
 * Criado por luiz em 06/04/14.
 * Todos os direitos reservados para RedRails
 */
public class SyncTest extends AndroidTestCase {
    CategoriaDAO categoriaDao;
    MensagemDAO mensagemDao;
    MensagemCategoriaDAO mensagemCategoriaDao;
    ParseHelper parseHelper;
    private static final String TEST_FILE_PREFIX = "test_";


    @Override
    protected void setUp() throws Exception {
        super.setUp();

        RenamingDelegatingContext context
                = new RenamingDelegatingContext(getContext(), TEST_FILE_PREFIX);
        Parse.initialize(context, "faW99J9Ipwv06eItZIhhXzTBhMYs9exZtlBW8ccf", "kEgPFQt1f143rUXrtlAgOwOoshbIfcahIqzE4eZe");
        categoriaDao = CategoriaDAO.getInstance(context);
        mensagemDao = MensagemDAO.getInstance(context);
        mensagemCategoriaDao = MensagemCategoriaDAO.getInstance(context);
        parseHelper = new ParseHelper(context);

    }
    @LargeTest
    public void testSaveParseData() throws ParseException {
        mensagemDao.deletarTudo();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("MensagemParse");
        query.setLimit(3);
        List<ParseObject> result = query.find();
        int totalRecebido = result.size();
        parseHelper.updateMensagens(result);
        assertTrue(mensagemDao.getQuantidadeTotal()>0);
        assertEquals(totalRecebido, mensagemDao.getQuantidadeTotal());
        Mensagem mensagem = mensagemDao.first();
        assertNotNull(mensagem.getSlug());

    }

    @LargeTest
    public void testUpdateParseData() throws ParseException {
        long total = mensagemDao.getQuantidadeTotal();
        assertTrue(total>0);
        Mensagem mensagem = mensagemDao.first();
        int id = mensagem.getId();
        mensagem.setEnviada(true);
        mensagem.setFavoritada(true);
        String textoAntigo = "texto_nao_padrao";
        mensagem.setTexto(textoAntigo);

        mensagemDao.atualizar(mensagem);
        assertEquals(total, mensagemDao.getQuantidadeTotal());
        mensagem = mensagemDao.getMensagem(id);


        assertTrue(mensagem.getFavoritada());
        assertTrue(mensagem.getEnviada());

        ParseQuery<ParseObject> query = ParseQuery.getQuery("MensagemParse");
        query.setLimit(3);
        List<ParseObject> result = query.find();
        assertTrue(result.size()>0);


        parseHelper.updateMensagens(result);

        assertEquals(total, mensagemDao.getQuantidadeTotal());
        mensagem = mensagemDao.getMensagem(id);

        
        assertTrue(!textoAntigo.equalsIgnoreCase(mensagem.getTexto()));
        assertTrue(mensagem.getFavoritada());
        assertTrue(mensagem.getEnviada());


    }


    @LargeTest
    public void testUpdateCategoriaParse() throws ParseException {

        long total = categoriaDao.getQuantidadeTotal();
        assertTrue(total>0);
        Categoria categoria = categoriaDao.first();
        int id = categoria.getId();
        String textoAntigo = "nome_nao_padrao";
        categoria.setNome(textoAntigo);

        categoriaDao.atualizar(categoria);
        assertEquals(total, categoriaDao.getQuantidadeTotal());

        ParseQuery<ParseObject> query = ParseQuery.getQuery("CategoriaParse");
        query.setLimit(3);
        List<ParseObject> result = query.find();

        assertTrue(result.size()>0);

        parseHelper.updateCategorias(result);

        assertEquals(total, categoriaDao.getQuantidadeTotal());
        categoria = categoriaDao.getCategoria(id);


        assertTrue(!textoAntigo.equalsIgnoreCase(categoria.getNome()));


    }



    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
