package test;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import java.util.List;

import br.com.redrails.torpedos.daos.CategoriaDAO;
import br.com.redrails.torpedos.daos.MensagemCategoriaDAO;
import br.com.redrails.torpedos.daos.MensagemDAO;
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
