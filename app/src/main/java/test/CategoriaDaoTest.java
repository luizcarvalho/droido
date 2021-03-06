package test;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import java.util.ArrayList;
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
public class CategoriaDaoTest extends AndroidTestCase {
    CategoriaDAO categoriaDao;
    MensagemDAO mensagemDao;
    MensagemCategoriaDAO mensagemCategoriaDao;
    private static final String TEST_FILE_PREFIX = "test_";
    int quantidadeCategoriasEstaticas = 0;


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        RenamingDelegatingContext context
                = new RenamingDelegatingContext(getContext(), TEST_FILE_PREFIX);
        categoriaDao = CategoriaDAO.getInstance(context);
        //categoriaDao.deletarTudo();
        mensagemDao = MensagemDAO.getInstance(context);
        mensagemCategoriaDao = MensagemCategoriaDAO.getInstance(context);
        quantidadeCategoriasEstaticas = categoriaDao.categoriasFixas().size();
    }


    public void testCategoriaCRUD() {

        Categoria categoria = new Categoria(1,"Engracada","engracada");
        categoriaDao.deletarTudo();

        categoriaDao.salvar(categoria);

        List<Categoria> categoriasNaBase = categoriaDao.recuperarTodas();
        assertFalse(categoriasNaBase.isEmpty());

        Categoria categoriaRecuperado = categoriasNaBase.get(0);
        categoriaRecuperado.setSlug("funny");

        categoriaDao.atualizar(categoriaRecuperado);

        Categoria categoriaEditado = categoriaDao.recuperarTodas().get(0);

        assertSame(categoriaRecuperado.getId(), categoriaEditado.getId());
        assertNotSame(categoria.getSlug(), categoriaEditado.getSlug());

        categoriaDao.deletar(categoriaEditado);

        assertEquals(categoriaDao.recuperarTodas().size(), categoriaDao.categoriasFixas().size());

    }


    @SmallTest
    public void testGetCategoriaWithSlug(){

        Categoria categoria = new Categoria();
        categoria.setNome("Nova Categoria");
        categoria.setSlug("nova-categoria");
        categoriaDao.salvar(categoria);
        assertEquals(categoriaDao.getQuantidadeTotal(),1+quantidadeCategoriasEstaticas);
        Categoria categoria1 = categoriaDao.getCategoria("nova-categoria");
        assertEquals(categoria1.getNome(),categoria.getNome());

    }
    @SmallTest
    public void testSetCategoriasStringInMensagem(){
        Mensagem mensagem = new Mensagem();
        mensagem.setCategoriasString("categoria1,categoria2,categoria3");
        assertEquals(3,mensagem.getCategorias().size());
        assertEquals("categoria3", mensagem.getCategorias().get(2).getSlug());
    }

    @MediumTest
    public void testGetCategoriasBySlug(){

        List<Categoria> categorias = createListOfCategories();
        assertEquals(3+quantidadeCategoriasEstaticas,categoriaDao.getQuantidadeTotal());

        List<Categoria> categoriasRecuperadas = categoriaDao.findBySlugs(categorias);
        assertEquals(3,categoriasRecuperadas.size());

        assertEquals(categoriasRecuperadas.get(0).getSlug(),"categoria1");
        assertEquals(categoriasRecuperadas.get(1).getSlug(),"categoria2");
        assertEquals(categoriasRecuperadas.get(2).getSlug(),"categoria3");

    }




    private List<Categoria> createListOfCategories(){
        List<Categoria> result = new ArrayList<Categoria> ();

        categoriaDao.deletarTudo();
        Categoria categoria1 = new Categoria(1,"categoria 1","categoria1");
        result.add(categoria1);
        Categoria categoria2 = new Categoria(2,"categoria 2","categoria2");
        result.add(categoria2);
        Categoria categoria3 = new Categoria(3,"categoria 3","categoria3");
        result.add(categoria3);

        categoriaDao.salvar(categoria1);
        categoriaDao.salvar(categoria2);
        categoriaDao.salvar(categoria3);
        return result;
    }


    protected void tearDown() throws Exception {
        super.tearDown();
        categoriaDao.fecharConexao();
        mensagemDao.fecharConexao();
        mensagemCategoriaDao.fecharConexao();
    }

}
