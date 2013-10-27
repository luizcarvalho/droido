package br.com.redrails.torpedos;

public class Menssagem {

    private int id;
    private String texto;
    private String slug;
    private Boolean favoritada;
    private Boolean enviada;

    public Menssagem() {

    }

    public Menssagem(int id, String texto) {
        super();
        this.id = id;
        this.texto = texto;
        this.favoritada = false;
        this.enviada = false;
        this.slug = "slug"+id;
    }



    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Boolean getFavoritada() {
        return favoritada;
    }

    public void setFavoritada(Boolean favoritada) {
        this.favoritada = favoritada;
    }

    public Boolean getEnviada() {
        return enviada;
    }

    public void setEnviada(boolean enviada) {
        this.enviada = enviada;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return texto;
    }
} 