package br.com.redrails.torpedos.models;


public class Categoria {
    static public int TIPO_FIXA = 1;
    static public int TIPO_DINAMICA = 2;

    static public int TODAS = 1;
    static public int FAVORITAS = 2;



    private int id;
    private String nome;
    private String slug;
    private int tipo;

    public Categoria() {

    }

    public Categoria(int id, String nome, String slug) {
        super();
        this.id = id;
        this.nome = nome;
        this.slug = "slug"+id;
        this.tipo = TIPO_DINAMICA;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipoFixa() {
        this.tipo = TIPO_FIXA;
    }

    public void setTipoDinamica() {
        this.tipo = TIPO_DINAMICA;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return nome;
    }


} 