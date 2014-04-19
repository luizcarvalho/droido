package test;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.test.suitebuilder.annotation.SmallTest;

import br.com.redrails.torpedos.daos.CategoriaDAO;
import br.com.redrails.torpedos.daos.MensagemCategoriaDAO;
import br.com.redrails.torpedos.daos.MensagemDAO;
import br.com.redrails.torpedos.models.Categoria;
import br.com.redrails.torpedos.models.Mensagem;

/**
 * Criado por luiz em 06/04/14.
 * Todos os direitos reservados para RedRails
 */
public class DaosTest extends AndroidTestCase {
    CategoriaDAO categoriaDao;
    MensagemDAO mensagemDao;
    MensagemCategoriaDAO mensagemCategoriaDao;
    private static final String TEST_FILE_PREFIX = "test_";


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        RenamingDelegatingContext context
                = new RenamingDelegatingContext(getContext(), TEST_FILE_PREFIX);
        categoriaDao = CategoriaDAO.getInstance(context);
        //categoriaDao.deletarTudo();
        mensagemDao = MensagemDAO.getInstance(context);
        mensagemCategoriaDao = MensagemCategoriaDAO.getInstance(context);
    }

    @SmallTest
    public void testGetCategoriaWithSlug(){
        Categoria categoria = new Categoria();
        categoria.setNome("Nova Categoria");
        categoria.setSlug("nova-categoria");
        categoriaDao.salvar(categoria);
        assertEquals(categoriaDao.getQuantidadeTotal(),1);
        Categoria categoria1 = categoriaDao.getCategoria("nova-categoria");
        assertEquals(categoria1.getNome(),categoria.getNome());

    }
    @SmallTest
    public void testSetCategoriasStringInMensagem(){
        Mensagem mensagem = new Mensagem();
        mensagem.setCategoriasString("categoria1,categoria2,categoria3");
        assertEquals(mensagem.getCategorias().size(),3);
        assertEquals(mensagem.getCategorias().get(2),"categoria3");
    }

    @SmallTest
    public void testSetSlugInMensagem(){
        mensagemDao.deletarTudo();
        Mensagem mensagem = new Mensagem();
        mensagem.setSlug("mySlug");
        mensagem.setId(1);
        assertEquals("mySlug", mensagem.getSlug());
        mensagemDao.salvar(mensagem);
        mensagem = mensagemDao.getMensagem(1);
        assertEquals("mySlug", mensagem.getSlug());

    }

    @SmallTest
    public void testGetMensagem(){
        mensagemDao.deletarTudo();
        Mensagem mensagem = new Mensagem();
        mensagem.setSlug("slug");
        mensagem.setId(1);
        mensagemDao.salvar(mensagem);
        assertEquals(mensagem.getSlug(),mensagemDao.getMensagem(1).getSlug());
    }

    @SmallTest
    public void testMensagemAsFirst(){

        mensagemDao.deletarTudo();
        Mensagem mensagemFirst = new Mensagem();
        mensagemFirst.setSlug("first");
        mensagemDao.salvar(mensagemFirst);
        assertEquals(1,mensagemDao.getQuantidadeTotal());

        Mensagem mensagemSecond = new Mensagem();
        mensagemSecond.setSlug("second");
        mensagemDao.salvar(mensagemSecond);
        assertEquals(2, mensagemDao.getQuantidadeTotal());

        Mensagem mensagem = mensagemDao.first();
        assertEquals("first",mensagem.getSlug());
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
