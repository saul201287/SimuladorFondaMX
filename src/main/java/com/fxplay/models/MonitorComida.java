package com.fxplay.models;

import java.util.concurrent.ConcurrentLinkedQueue;

public class MonitorComida {
    private static MonitorComida instancia;
    private ConcurrentLinkedQueue<Comida> platosPreparados = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Orden> ordenesPendientes = new ConcurrentLinkedQueue<>();

    private static final int CAPACIDAD_MAXIMA = 5;

    public MonitorComida() {}

    public static synchronized MonitorComida getInstance() {
        if (instancia == null) {
            instancia = new MonitorComida();
        }
        return instancia;
    }

    // Métodos para manejar platos
    public synchronized void insertarPlato(Comida plato) throws InterruptedException {
        while (platosPreparados.size() >= CAPACIDAD_MAXIMA) {
            System.out.println("Cocina llena. Esperando...");
            wait();
        }

        platosPreparados.offer(plato);
        notifyAll();
    }

    public synchronized Comida retirarPlato() throws InterruptedException {
        while (platosPreparados.isEmpty()) {
            System.out.println("No hay platos. Esperando...");
            wait();
        }

        Comida plato = platosPreparados.poll();
        notifyAll();

        return plato;
    }

    // Métodos para manejar órdenes
    public synchronized void insertarOrden(Orden orden) {
        ordenesPendientes.offer(orden);
        System.out.println("Orden insertada para mesa " + orden.getMesa().getIdMesa());
    }

    public synchronized Orden retirarOrden() {
        return ordenesPendientes.poll();
    }

    // Métodos de utilidad
    public boolean hayPlatosDisponibles() {
        return !platosPreparados.isEmpty();
    }

    public boolean hayOrdenesPendientes() {
        return !ordenesPendientes.isEmpty();
    }

    public int platosEnCocina() {
        return platosPreparados.size();
    }

    public int ordenesPendientes() {
        return ordenesPendientes.size();
    }

    public synchronized Orden obtenerOrdenParaMesa(Mesa mesa) {
        return ordenesPendientes.stream()
                .filter(orden -> orden.getMesa().equals(mesa) && orden.getEstado() == Orden.Estado.EN_PROCESO)
                .findFirst()
                .orElse(null);
    }
}