package com.fxplay.models;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MonitorComida {
    private final ConcurrentLinkedQueue<Orden> ordenesPendientes = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Comida> platosPreparados = new ConcurrentLinkedQueue<>();
    private static final int CAPACIDAD_MAXIMA_PLATOS = 10;

    private final Lock lock = new ReentrantLock(true); 
    private final Condition cocineroCondition = lock.newCondition();

    private boolean meseroEnCocina = false;
    private int cocineroEsperando = 0;
    public synchronized void insertarPlato(Comida comida) {
        lock.lock();
        try {
            while (platosPreparados.size() >= CAPACIDAD_MAXIMA_PLATOS || meseroEnCocina) {
                cocineroEsperando++;
                try {
                    cocineroCondition.await();
                } finally {
                    cocineroEsperando--;
                }
            }

            platosPreparados.offer(comida);
            System.out.println("Plato listo para servir. Platos en cocina: " + platosPreparados.size());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Interrupción al insertar plato.");
        } finally {
            lock.unlock();
        }
    }

    public synchronized List<Orden> recogerPlatos() {
        List<Orden> ordenesEntregadas = new ArrayList<>();
        lock.lock();
        try {
            meseroEnCocina = true;
            System.out.println("Mesero recogiendo platos...");
            System.out.println("total: " +ordenesPendientes.size());
            while (!ordenesPendientes.isEmpty()) {
                //System.out.println("jalando: " + ordenesPendientes.size());
                //Comida plato = platosPreparados.poll();
                    Orden ordenLista = ordenesPendientes.poll() ;
                            //System.out.println(ordenLista.getNumeroOrden());
                           // System.out.println(ordenLista + " sss");
                    if (ordenLista != null) {
                        ordenLista.cambiarEstado(Orden.Estado.ENTREGADA);
                        ordenesEntregadas.add(ordenLista);
                        System.out.println("Orden entregada: " + ordenLista.getNumeroOrden());
                    }
            }

            if (ordenesEntregadas.isEmpty()) {
                System.out.println("No hay platos u órdenes para recoger.");
            }
        } finally {
            meseroEnCocina = false;
            cocineroCondition.signalAll();
            lock.unlock();
        }
        return ordenesEntregadas;
    }

    public synchronized void insertarOrden(Orden nuevaOrden) {
        lock.lock();
        try {
            if (nuevaOrden != null) {
                ordenesPendientes.offer(nuevaOrden);
                System.out.println("Orden agregada a la cola: " + nuevaOrden.getNumeroOrden());
            }
        } finally {
            lock.unlock();
        }
    }

    public synchronized boolean hayPlatosDisponibles() {
        lock.lock();
        try {
            return !platosPreparados.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    public synchronized int totalPendientes() {
        lock.lock();
        try {
            return ordenesPendientes.size();
        } finally {
            lock.unlock();
        }
    }
}