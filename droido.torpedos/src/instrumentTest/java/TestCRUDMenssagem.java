import android.test.AndroidTestCase;

import java.util.List;

import br.com.redrails.torpedos.Menssagem;
import br.com.redrails.torpedos.MenssagemDAO;

/**
 * Created by luiz on 10/27/13.
 */
class TestCRUDMenssagem extends AndroidTestCase {

    public void testCRUD() {

        Menssagem veiculo = new Menssagem(1, "PIADA HAHHAHAH");
        MenssagemDAO menssagemDAO =  MenssagemDAO.getInstance(getContext());

        menssagemDAO.salvar(veiculo);

        List<Menssagem> veiculosNaBase = menssagemDAO.recuperarTodos();
        assertFalse(veiculosNaBase.isEmpty());

        Menssagem veiculoRecuperado = veiculosNaBase.get(0);
        veiculoRecuperado.setTexto("NOVO TEXTO ROX");

        menssagemDAO.editar(veiculoRecuperado);

        Menssagem veiculoEditado = menssagemDAO.recuperarTodos().get(0);

        assertSame(veiculoRecuperado.getId(), veiculoEditado.getId());
        assertNotSame(veiculo.getTexto(), veiculoEditado.getTexto());

        menssagemDAO.deletar(veiculoEditado);

        assertTrue(menssagemDAO.recuperarTodos().isEmpty());

        menssagemDAO.fecharConexao();

    }

}