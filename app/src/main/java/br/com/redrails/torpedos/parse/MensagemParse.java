package br.com.redrails.torpedos.parse;

import com.parse.ParseObject;
import br.com.redrails.torpedos.models.Mensagem;

/**
 * Criado por luiz em 02/04/14.
 * Todos os direitos reservados para RedRails
 */


public class MensagemParse {
    public static final String KEY_AUTOR = "autor";
    public static final String KEY_AVALIACAO = "avaliacao";
    public static final String KEY_DATA = "data";
    public static final String KEY_TEXTO = "texto";


    public static Mensagem toMensagem(com.parse.ParseObject mensagemParse){
        Mensagem mensagem = new Mensagem();
        mensagem.setTexto(mensagemParse.getString(KEY_TEXTO));
        mensagem.setAutor(mensagemParse.getString(KEY_AUTOR));
        mensagem.setData(mensagemParse.getInt(KEY_DATA));
        mensagem.setAvaliacao(mensagemParse.getInt(KEY_AVALIACAO));

        return mensagem;
    }

}
