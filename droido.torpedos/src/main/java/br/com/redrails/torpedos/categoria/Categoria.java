package br.com.redrails.torpedos.categoria;

public class Categoria {

    private int id;
    private String nome;
    private String slug;

    public Categoria() {

    }

    public Categoria(int id, String nome, String slug) {
        super();
        this.id = id;
        this.nome = nome;
        this.slug = "slug"+id;
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