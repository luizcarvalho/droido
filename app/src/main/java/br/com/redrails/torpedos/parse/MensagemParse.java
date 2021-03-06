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
    public static final String KEY_SLUG = "slug";
    public static final String KEY_DATA = "data";
    public static final String KEY_TEXTO = "texto";
    public static final String KEY_CATEGORIAS = "categorias";



    public static Mensagem toMensagem(com.parse.ParseObject mensagemParse){
        Mensagem mensagem = new Mensagem();
        mensagem.setTexto(mensagemParse.getString(KEY_TEXTO));
        mensagem.setSlug(mensagemParse.getString(KEY_SLUG));
        mensagem.setAutor(mensagemParse.getString(KEY_AUTOR));
        mensagem.setData(mensagemParse.getInt(KEY_DATA));
        mensagem.setAvaliacao(convertAvaliacao(mensagemParse.getInt(KEY_AVALIACAO)));
        mensagem.setCategoriasString(mensagemParse.getString(KEY_CATEGORIAS));

        return mensagem;
    }

    private static Float convertAvaliacao(Integer avaliacao){
        Float delta = 0.5f;
        Float avaliacaoConvertida = avaliacao/20.0f;
        return Math.round(avaliacaoConvertida / delta) * delta;
    }

}
