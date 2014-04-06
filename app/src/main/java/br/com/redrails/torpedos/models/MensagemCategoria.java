package br.com.redrails.torpedos.models;


public class MensagemCategoria {
    private int id;
    private Integer categoria_id;
    private Integer mensagem_id;


    public MensagemCategoria() {

    }

    public MensagemCategoria(int id, Integer categoria_id, Integer mensagem_id) {
        super();
        this.id = id;
        this.categoria_id = categoria_id;
        this.mensagem_id = mensagem_id;

    }


    public Integer getCategoriaId() {
        return categoria_id;
    }

    public void setCategoriaId(Integer categoria_id) {
        this.categoria_id = categoria_id;
    }

    public Integer getMensagemId() {
        return mensagem_id;
    }

    public void setMensagemId(Integer mensagem_id) {
        this.mensagem_id = mensagem_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "mensagem_id: "+mensagem_id+" categoria_id: "+categoria_id;
    }


} 