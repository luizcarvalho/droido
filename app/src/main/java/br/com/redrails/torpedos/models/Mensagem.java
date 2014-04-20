package br.com.redrails.torpedos.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Mensagem {

    private int id;
    private String texto;
    private String slug;
    private String autor;
    private Integer data;
    private Integer avaliacao;
    private Boolean favoritada;
    private Boolean enviada;
    private List<Categoria> categorias = new ArrayList<Categoria>();

    public Mensagem() {

    }

    public Mensagem(int id, String texto, boolean favoritada, boolean enviada, String autor, String slug, Integer data, Integer avaliacao) {
        super();
        this.id = id;
        this.texto = texto;
        this.favoritada = favoritada;
        this.enviada = enviada;
        this.slug = slug;
        this.autor = autor;
        this.data = data;
        this.avaliacao = avaliacao;
    }



    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Boolean getFavoritada() {
        return favoritada;
    }

    public void setFavoritada(Boolean favoritada) {
        this.favoritada = favoritada;
    }

    public Integer getData() {
        return data;
    }

    public void setData(Integer data) {
        this.data = data;
    }

    public Integer getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(Integer avaliacao) {
        this.avaliacao = avaliacao;
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

    public void setCategoriasString(String categorias){
        for(String categoriaSlug : categorias.split(",")){
            Categoria categoria = new Categoria(0,categoriaSlug,categoriaSlug);
            this.categorias.add(categoria);
        }
    }

    public void setCategoria(Categoria categoria) {
        this.categorias.add(categoria);
    }

    public List<Categoria> getCategorias(){
        return categorias;
    }

    @Override
    public String toString() {
        return "#< id: "+id+" texto: "+texto+" slug: "+slug+" favoritada: "+favoritada+" enviada: "+enviada+
                " autor: "+autor+" data: "+data+" avaliacao: "+avaliacao+" >";
    }


}