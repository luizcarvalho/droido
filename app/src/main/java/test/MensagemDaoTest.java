package test;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import java.util.List;

import br.com.redrails.torpedos.daos.CategoriaDAO;
import br.com.redrails.torpedos.daos.MensagemCategoriaDAO;
import br.com.redrails.torpedos.daos.MensagemDAO;
import br.com.redrails.torpedos.models.Categoria;
import br.com.redrails.torpedos.models.Mensagem;

/**
 * Criado por luiz em 06/04/14.
 * Todos os direitos reservados para RedRails
 */
public class MensagemDaoTest extends AndroidTestCase {
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
        mensagemDao = MensagemDAO.getInstance(context);
        mensagemCategoriaDao = MensagemCategoriaDAO.getInstance(context);
    }

    @MediumTest
    public void testMensagemCRUD() {
        long mensagemId=0;

        Mensagem mensagem = new Mensagem(0,"texto test",false,false,"Autor","1.slug",1,1);
        MensagemDAO mensagemDAO =  MensagemDAO.getInstance(getContext());
        mensagemDAO.deletarTudo();

        mensagemId = mensagemDAO.salvar(mensagem);

        assertTrue(mensagemId!=0);

        List<Mensagem> mensagemsNaBase = mensagemDAO.recuperarTodas();
        assertFalse(mensagemsNaBase.isEmpty());

        Mensagem mensagemRecuperada = mensagemsNaBase.get(0);

        mensagemRecuperada.setSlug("2.slug");

        mensagemDAO.atualizar(mensagemRecuperada);

        Mensagem mensagemEditado = mensagemDAO.recuperarTodas().get(0);

        assertSame(mensagemRecuperada.getId(), mensagemEditado.getId());
        assertNotSame(mensagem.getSlug(), mensagemEditado.getSlug());

        mensagemDAO.deletar(mensagemEditado);

        assertTrue(mensagemDAO.recuperarTodas().isEmpty());

        mensagemDAO.fecharConexao();

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


    @MediumTest
    public void notestSaveMensagemWithCategorias(){
        categoriaDao.deletarTudo();
        mensagemDao.deletarTudo();
        Mensagem mensagem = new Mensagem(0,"ok",false,false,"me","1.slug",123,10);

        Categoria categoria1 = new Categoria(1,"categoria 1","categoria1");
        Categoria categoria2 = new Categoria(2,"categoria 2","categoria2");
        Categoria categoria3 = new Categoria(3,"categoria 3","categoria3");
        categoriaDao.salvar(categoria1);
        categoriaDao.salvar(categoria2);
        categoriaDao.salvar(categoria3);
        assertEquals(categoriaDao.getQuantidadeTotal(),3);

        mensagem.setCategoriasString(categoria1.getSlug()+","+categoria2.getSlug()+","+categoria3.getSlug());
        mensagemDao.salvar(mensagem);

    }



    

    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
