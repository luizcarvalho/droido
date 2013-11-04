package br.com.redrails.torpedos;

public class Mensagem {

    private int id;
    private String texto;
    private String slug;
    private String autor;
    private Boolean favoritada;
    private Boolean enviada;

    public Mensagem() {

    }

    public Mensagem(int id, String texto, boolean favoritada, boolean enviada, String autor) {
        super();
        this.id = id;
        this.texto = texto;
        this.favoritada = favoritada;
        this.enviada = enviada;
        this.slug = "slug"+id;
        this.autor = autor;
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

    public Boolean toggleSended(){
        if(this.enviada){
            this.enviada=false;
        }else{
            this.enviada=true;
        }
        return this.enviada;
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

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    @Override
    public String toString() {
        return "#< id: "+id+"texto: "+texto+" favoritada: "+favoritada+" enviada: "+enviada+" autor: "+autor+" >";
    }


} 