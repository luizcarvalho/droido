package br.com.redrails.torpedos.parse;

import android.content.Context;

import com.parse.ParseObject;

import java.util.List;

import br.com.redrails.torpedos.daos.CategoriaDAO;
import br.com.redrails.torpedos.daos.MensagemCategoriaDAO;
import br.com.redrails.torpedos.daos.MensagemDAO;
import br.com.redrails.torpedos.models.Categoria;
import br.com.redrails.torpedos.models.Mensagem;

/**
 * Criado por luiz em 19/04/14.
 * Todos os direitos reservados para RedRails
 */
public class ParseHelper {
    Context context;
    boolean needUpdate = false;
    public static final String KEY_UPDATED_AT = "updatedAt";

    public ParseHelper(Context contexto){
        context = contexto;
    }

    public void updateMensagens(List<ParseObject> mensagemList){
        MensagemDAO mensagemDao = MensagemDAO.getInstance(context);
        MensagemCategoriaDAO mensagemCategoriaDAO = MensagemCategoriaDAO.getInstance(context);

        for(ParseObject mensagemParse: mensagemList){
            Mensagem mensagem = MensagemParse.toMensagem(mensagemParse);
            mensagem.setId((int) mensagemDao.atualizarOuSalvar(mensagem));
            mensagemCategoriaDAO.atualizarRelacionamento(mensagem);
        }
    }


    public void updateCategorias(List<ParseObject> categoriaList){
        CategoriaDAO categoriaDao = CategoriaDAO.getInstance(context);

        for(ParseObject categoriaParse: categoriaList){
            Categoria categoria = CategoriaParse.toCategoria(categoriaParse);
            categoriaDao.atualizarOuSalvar(categoria);

        }
    }


}
