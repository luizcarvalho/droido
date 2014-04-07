package test;

import android.test.AndroidTestCase;
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


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mContext = getContext();
        categoriaDao = CategoriaDAO.getInstance(mContext);
        categoriaDao.deletarTudo();
        mensagemDao = MensagemDAO.getInstance(mContext);
        mensagemCategoriaDao = MensagemCategoriaDAO.getInstance(mContext);
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


    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
