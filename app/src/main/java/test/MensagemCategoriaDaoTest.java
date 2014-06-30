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
import br.com.redrails.torpedos.models.MensagemCategoria;

/**
 * Criado por luiz em 06/04/14.
 * Todos os direitos reservados para RedRails
 */
public class MensagemCategoriaDaoTest extends AndroidTestCase {
    CategoriaDAO categoriaDao;
    MensagemDAO mensagemDao;
    MensagemCategoriaDAO mensagemCategoriaDAO;
    private static final String TEST_FILE_PREFIX = "test_";


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        RenamingDelegatingContext context
                = new RenamingDelegatingContext(getContext(), TEST_FILE_PREFIX);
        categoriaDao = CategoriaDAO.getInstance(context);
        mensagemDao = MensagemDAO.getInstance(context);
        mensagemCategoriaDAO = MensagemCategoriaDAO.getInstance(context);
    }


    public void testUpdateRelationship(){
        mensagemCategoriaDAO.deletarTudo();
        categoriaDao.deletarTudo();
        Categoria categoria1 = new Categoria(1,"categoria 1","categoria1");
        Categoria categoria2 = new Categoria(2,"categoria 2","categoria2");
        Categoria categoria3 = new Categoria(3,"categoria 3","categoria3");

        categoriaDao.salvar(categoria1);
        categoriaDao.salvar(categoria2);
        categoriaDao.salvar(categoria3);

        Categoria categoriaDb1 = categoriaDao.getCategoria(categoria1.getSlug());
        Categoria categoriaDb2 = categoriaDao.getCategoria(categoria2.getSlug());
        Categoria categoriaDb3 = categoriaDao.getCategoria(categoria3.getSlug());

        assertEquals(categoria1.getSlug(), categoriaDb1.getSlug());

        Mensagem mensagem1 = new Mensagem(1,"texto",false, false, "autor","1.droid",1,1f);
        mensagemDao.salvar(mensagem1);
        Mensagem mensagem = mensagemDao.getMensagemBySlug(mensagem1.getSlug());
        assertEquals(mensagem1.getSlug(), mensagem.getSlug());

        mensagem.setCategoria(categoriaDb1);
        mensagem.setCategoria(categoriaDb2);
        mensagem.setCategoria(categoriaDb3);
        assertEquals(3, mensagem.getCategorias().size());


        mensagemCategoriaDAO.atualizarRelacionamento(mensagem);
        assertEquals(3,mensagemCategoriaDAO.getQuantidadeTotal());

        List<Categoria> mensagemCategorias = categoriaDao.getCategoriasFromMensagem(mensagem.getId());
        assertEquals(3, mensagemCategorias.size());
        assertEquals(categoriaDb1.getSlug(),mensagemCategorias.get(0).getSlug());
        assertEquals(categoriaDb2.getSlug(),mensagemCategorias.get(1).getSlug());

    }


    public void testMensagemCategoriaCRUD() {

        MensagemCategoria mensagemCategoria = new MensagemCategoria(0,1,1);

        mensagemCategoriaDAO.deletarTudo();

        mensagemCategoriaDAO.salvar(mensagemCategoria);

        List<MensagemCategoria> mensagemCategoriasNaBase = mensagemCategoriaDAO.recuperarTodas();
        assertFalse(mensagemCategoriasNaBase.isEmpty());

        MensagemCategoria mensagemCategoriaRecuperado = mensagemCategoriasNaBase.get(0);
        mensagemCategoriaRecuperado.setMensagemId(3);

        mensagemCategoriaDAO.atualizar(mensagemCategoriaRecuperado);

        MensagemCategoria mensagemCategoriaEditado = mensagemCategoriaDAO.recuperarTodas().get(0);

        assertSame(mensagemCategoriaRecuperado.getId(), mensagemCategoriaEditado.getId());
        assertNotSame(mensagemCategoria.getMensagemId(), mensagemCategoriaEditado.getMensagemId());

        mensagemCategoriaDAO.deletar(mensagemCategoriaEditado);

        assertTrue(mensagemCategoriaDAO.recuperarTodas().isEmpty());

        mensagemCategoriaDAO.fecharConexao();

    }







    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
