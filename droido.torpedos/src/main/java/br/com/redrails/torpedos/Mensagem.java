package br.com.redrails.torpedos;

public class Mensagem {

    private int id;
    private String texto;
    private String slug;
    private Boolean favoritada;
    private Boolean enviada;

    public Mensagem() {

    }

    public Mensagem(int id, String texto, boolean favoritada, boolean enviada) {
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

    public Boolean toggleFavorite(){
        if(this.favoritada){
            this.favoritada=false;
        }else{
            this.favoritada=true;
        }
        return this.favoritada;
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