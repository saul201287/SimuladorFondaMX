package com.fxplay.models;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.dsl.FXGL;

public class Comida {
    private Entity comidaEntity;
    private Orden orden;
    private double precioBase;
    private String descripcion;

    public Comida() {
    }

    public Comida(Orden orden) {
        this();
        this.orden = orden;
    }

    public Entity crearComida(double x, double y) {
        comidaEntity = FXGL.entityBuilder()
                .at(x, y)
                .viewWithBBox(FXGL.texture("comida.png"))
                .scale(.1, .1)
                .buildAndAttach();
        return comidaEntity;
    }

    public Entity getComidaEntity() {
        return comidaEntity;
    }

    public void setOrden(Orden orden) {
        this.orden = orden;
    }

    public Orden getOrden() {
        return this.orden;
    }

    

    @Override
    public String toString() {
        return "Comida{" +
                "orden=" + (orden != null ? orden.getNumeroOrden() : "Sin orden") +
                ", precioBase=" + precioBase +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}