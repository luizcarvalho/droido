package br.com.redrails.torpedos.parse;

import com.parse.ParseObject;

import br.com.redrails.torpedos.models.Categoria;
import br.com.redrails.torpedos.models.Mensagem;

/**
 * Criado por luiz em 02/04/14.
 * Todos os direitos reservados para RedRails
 */


public class CategoriaParse{

    public static final String CLASSE = "CategoriaParse";
    public static final String KEY_NOME = "nome";
    public static final String KEY_SLUG = "slug";



    public static Categoria toCategoria(ParseObject categoriaParse){
        Categoria categoria = new Categoria();
        categoria.setNome(categoriaParse.getString(KEY_NOME));
        categoria.setSlug(categoriaParse.getString(KEY_SLUG));

        return categoria;
    }

}
