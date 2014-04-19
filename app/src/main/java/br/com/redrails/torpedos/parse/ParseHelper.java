package br.com.redrails.torpedos.parse;

import android.content.Context;

import com.parse.ParseObject;

import java.util.List;

import br.com.redrails.torpedos.daos.MensagemDAO;
import br.com.redrails.torpedos.models.Mensagem;

/**
 * Criado por luiz em 19/04/14.
 * Todos os direitos reservados para RedRails
 */
public class ParseHelper {
    Context context;
    public ParseHelper(Context contexto){
        context = contexto;
    }

    public void updateDatabase(List<ParseObject> mensagemList){
        MensagemDAO mensagemDao = MensagemDAO.getInstance(context);

        for(ParseObject mensagemParse: mensagemList){
            Mensagem mensagem = MensagemParse.toMensagem(mensagemParse);
            mensagemDao.atualizarOuSalvar(mensagem);
        }
    }
}
