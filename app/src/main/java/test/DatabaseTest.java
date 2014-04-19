package test;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import br.com.redrails.torpedos.daos.CategoriaDAO;
import br.com.redrails.torpedos.daos.MensagemCategoriaDAO;
import br.com.redrails.torpedos.daos.MensagemDAO;

/**
 * Criado por luiz em 07/04/14.
 * Todos os direitos reservados para RedRails
 */
public class DatabaseTest extends AndroidTestCase{
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @SmallTest
    public void testSQLCreationsIsNotNull(){
        assertNotNull(MensagemCategoriaDAO.SQL_CREATION);
        assertNotNull(MensagemDAO.SQL_CREATION);
        assertNotNull(CategoriaDAO.SQL_CREATION);
    }
}
