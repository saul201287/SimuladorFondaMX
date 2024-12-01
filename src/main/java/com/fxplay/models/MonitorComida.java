package com.fxplay.models;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MonitorComida {
    private static MonitorComida instancia;

    private final ConcurrentLinkedQueue<Orden> ordenesPendientes = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Comida> platosPreparados = new ConcurrentLinkedQueue<>();

    private final Lock lock = new ReentrantLock();
    private final Condition ordenesDisponibles = lock.newCondition();
    private final Condition platosDisponibles = lock.newCondition();

    private static final int CAPACIDAD_MAXIMA_PLATOS = 5;
    private static final int CAPACIDAD_MAXIMA_ORDENES = 10;

    private boolean meseroTerminado = false;

    public MonitorComida() {
    }

    public static synchronized MonitorComida getInstance() {
        if (instancia == null) {
            instancia = new MonitorComida();
        }
        return instancia;
    }

    public void insertarOrden(Orden orden) {
        lock.lock();
        try {
            // Esperar si la cola de órdenes está llena
            while (ordenesPendientes.size() >= CAPACIDAD_MAXIMA_ORDENES) {
                System.out.println("Cola de órdenes llena. Esperando...");
                ordenesDisponibles.await();
            }

            ordenesPendientes.offer(orden);
            System.out.println("Orden insertada para mesa " + orden.getMesa().getIdMesa());

            ordenesDisponibles.signal();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    public Orden retirarOrdenParaCocinar() {
        lock.lock();
        try {
            while (ordenesPendientes.isEmpty() && !meseroTerminado) {
                System.out.println("No hay órdenes. Esperando...");
                ordenesDisponibles.await();
            }
            if (ordenesPendientes.isEmpty() && meseroTerminado) {
                return null;
            }

            Orden orden = ordenesPendientes.poll();
            ordenesDisponibles.signal();
            return orden;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } finally {
            lock.unlock();
        }
    }

    // Método para insertar plato preparado
    public void insertarPlato(Comida plato) {
        lock.lock();
        try {
            // Esperar si la cocina está llena
            while (platosPreparados.size() >= CAPACIDAD_MAXIMA_PLATOS) {
                System.out.println("Cocina llena. Esperando...");
                platosDisponibles.await();
            }

            platosPreparados.offer(plato);
            System.out.println("Plato añadido a la cocina");

            // Señalar que hay platos disponibles
            platosDisponibles.signal();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    // Método para retirar plato preparado
    public Comida retirarPlato() {
        lock.lock();
        try {
            // Esperar si no hay platos disponibles
            while (platosPreparados.isEmpty()) {
                System.out.println("No hay platos. Esperando...");
                platosDisponibles.await();
            }

            Comida plato = platosPreparados.poll();
            // Liberar espacio en la cocina
            platosDisponibles.signal();
            return plato;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } finally {
            lock.unlock();
        }
    }

    // Método para marcar que el mesero ha terminado de tomar órdenes
    public void marcarMeseroTerminado() {
        lock.lock();
        try {
            meseroTerminado = true;
            // Despertar cualquier hilo de cocinero que esté esperando
            ordenesDisponibles.signalAll();
        } finally {
            lock.unlock();
        }
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

    public Orden obtenerOrdenParaMesa(Mesa mesa) {
        return ordenesPendientes.stream()
                .filter(orden -> orden.getMesa().equals(mesa) && orden.getEstado() == Orden.Estado.EN_PROCESO)
                .findFirst()
                .orElse(null);
    }
}