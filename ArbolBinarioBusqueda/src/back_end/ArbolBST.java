/*
Salcedo Alvarez Hugo Emmanuel
Morales Escobar Juan Adrian
 */
package back_end;

import Nodos.Empleado;
import Nodos.NodoBST;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Árbol binario de búsqueda (BST) de empleados ordenado por id.
 *
 * Convenciones:
 * - insertar lanza IllegalArgumentException si el empleado es inválido o si el id ya existe.
 * - buscar devuelve el Empleado o null si no existe (o si el árbol está vacío).
 * - eliminar devuelve el Empleado eliminado o null si no existía.
 * - Los recorridos devuelven una lista (vacía si el árbol está vacío).
 * - profundidad se mide en NIVELES: árbol vacío = 0, un solo nodo = 1.
 *
 * @author ASUS
 */
public class ArbolBST {

    private NodoBST raiz;
    private int tamanio;

    public ArbolBST() {
        this.raiz = null;
        this.tamanio = 0;
    }

    public NodoBST getRaiz() {
        return raiz;
    }

    /** @return cantidad total de empleados en el árbol. */
    public int getTamanio() {
        return tamanio;
    }

    public boolean estaVacio() {
        return raiz == null;
    }

    // ------------------------------------------------------------------
    // Insertar
    // ------------------------------------------------------------------
    public void insertar(Empleado empleado) {
        validarEmpleado(empleado);
        Integer id = empleado.getId();

        if (raiz == null) {
            raiz = new NodoBST(empleado);
            tamanio++;
            return;
        }
        NodoBST actual = raiz;
        while (true) {
            int cmp = id.compareTo(actual.getDato().getId());
            if (cmp == 0) {
                throw new IllegalArgumentException("Ya existe un empleado con el ID " + id + ".");
            }
            if (cmp < 0) {
                if (actual.getIzquierdo() == null) {
                    actual.setIzquierdo(new NodoBST(empleado));
                    break;
                }
                actual = actual.getIzquierdo();
            } else {
                if (actual.getDerecho() == null) {
                    actual.setDerecho(new NodoBST(empleado));
                    break;
                }
                actual = actual.getDerecho();
            }
        }
        tamanio++;
    }

    /** Valida el empleado antes de insertarlo (la lógica no depende de la GUI). */
    private void validarEmpleado(Empleado e) {
        if (e == null) {
            throw new IllegalArgumentException("El empleado no puede ser nulo.");
        }
        if (e.getId() == null) {
            throw new IllegalArgumentException("El ID del empleado es obligatorio.");
        }
        Validaciones.textoRequerido("Nombre", e.getNombre());
        Validaciones.textoRequerido("Puesto", e.getPuesto());
        Validaciones.textoRequerido("Departamento", e.getDepartamento());
        Validaciones.validarSalario(e.getSalario());
    }

    // ------------------------------------------------------------------
    // Buscar
    // ------------------------------------------------------------------
    public Empleado buscar(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID a buscar no puede ser nulo.");
        }
        NodoBST actual = raiz;
        while (actual != null) {
            int cmp = id.compareTo(actual.getDato().getId());
            if (cmp == 0) {
                return actual.getDato();
            }
            actual = (cmp < 0) ? actual.getIzquierdo() : actual.getDerecho();
        }
        return null;
    }

    // ------------------------------------------------------------------
    // Eliminar
    // ------------------------------------------------------------------
    public Empleado eliminar(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID a eliminar no puede ser nulo.");
        }
        Empleado[] eliminado = new Empleado[1];
        raiz = eliminar(raiz, id, eliminado);
        if (eliminado[0] != null) {
            tamanio--;
        }
        return eliminado[0];
    }

    private NodoBST eliminar(NodoBST nodo, Integer id, Empleado[] eliminado) {
        if (nodo == null) {
            return null; // no existe
        }
        int cmp = id.compareTo(nodo.getDato().getId());
        if (cmp < 0) {
            nodo.setIzquierdo(eliminar(nodo.getIzquierdo(), id, eliminado));
            return nodo;
        }
        if (cmp > 0) {
            nodo.setDerecho(eliminar(nodo.getDerecho(), id, eliminado));
            return nodo;
        }
        // Nodo encontrado
        eliminado[0] = nodo.getDato();
        if (nodo.getIzquierdo() == null) {      // hoja o solo hijo derecho
            return nodo.getDerecho();
        }
        if (nodo.getDerecho() == null) {        // solo hijo izquierdo
            return nodo.getIzquierdo();
        }
        // Dos hijos: se reemplaza por el sucesor inorden (mínimo del subárbol derecho)
        NodoBST sucesor = nodo.getDerecho();
        while (sucesor.getIzquierdo() != null) {
            sucesor = sucesor.getIzquierdo();
        }
        nodo.setDato(sucesor.getDato());
        Empleado[] descarte = new Empleado[1];
        nodo.setDerecho(eliminar(nodo.getDerecho(), sucesor.getDato().getId(), descarte));
        return nodo;
    }

    // ------------------------------------------------------------------
    // Recorridos
    // ------------------------------------------------------------------
    /** Izquierdo - raíz - derecho (ids ascendentes). */
    public List<Empleado> mostrarInOrden() {
        List<Empleado> lista = new ArrayList<>();
        inOrden(raiz, lista);
        return lista;
    }

    private void inOrden(NodoBST n, List<Empleado> lista) {
        if (n == null) {
            return;
        }
        inOrden(n.getIzquierdo(), lista);
        lista.add(n.getDato());
        inOrden(n.getDerecho(), lista);
    }

    /** Raíz - izquierdo - derecho. */
    public List<Empleado> mostrarPreOrden() {
        List<Empleado> lista = new ArrayList<>();
        preOrden(raiz, lista);
        return lista;
    }

    private void preOrden(NodoBST n, List<Empleado> lista) {
        if (n == null) {
            return;
        }
        lista.add(n.getDato());
        preOrden(n.getIzquierdo(), lista);
        preOrden(n.getDerecho(), lista);
    }

    /** Izquierdo - derecho - raíz. */
    public List<Empleado> mostrarPostOrden() {
        List<Empleado> lista = new ArrayList<>();
        postOrden(raiz, lista);
        return lista;
    }

    private void postOrden(NodoBST n, List<Empleado> lista) {
        if (n == null) {
            return;
        }
        postOrden(n.getIzquierdo(), lista);
        postOrden(n.getDerecho(), lista);
        lista.add(n.getDato());
    }

    // ------------------------------------------------------------------
    // Amplitud y profundidad
    // ------------------------------------------------------------------
    /**
     * Máxima cantidad de nodos que hay en un mismo nivel (no el total de nodos).
     * Árbol vacío = 0.
     */
    public int amplitud() {
        if (raiz == null) {
            return 0;
        }
        int maximo = 0;
        Queue<NodoBST> cola = new ArrayDeque<>();
        cola.add(raiz);
        while (!cola.isEmpty()) {
            int enNivel = cola.size();
            maximo = Math.max(maximo, enNivel);
            for (int i = 0; i < enNivel; i++) {
                NodoBST n = cola.poll();
                if (n.getIzquierdo() != null) {
                    cola.add(n.getIzquierdo());
                }
                if (n.getDerecho() != null) {
                    cola.add(n.getDerecho());
                }
            }
        }
        return maximo;
    }

    /** Profundidad máxima medida en NIVELES: vacío = 0, un nodo = 1. */
    public int profundidad() {
        return profundidad(raiz);
    }

    private int profundidad(NodoBST n) {
        if (n == null) {
            return 0;
        }
        return 1 + Math.max(profundidad(n.getIzquierdo()), profundidad(n.getDerecho()));
    }

    // ------------------------------------------------------------------
    // Balancear y destruir
    // ------------------------------------------------------------------
    /**
     * Reorganiza los nodos para obtener un árbol equilibrado: toma los
     * empleados en orden (inorden) y reconstruye el árbol eligiendo siempre el
     * elemento central. No se pierde ni se modifica ningún empleado.
     */
    public void balancear() {
        if (raiz == null) {
            return;
        }
        List<Empleado> ordenados = mostrarInOrden();
        raiz = construirBalanceado(ordenados, 0, ordenados.size() - 1);
        // tamanio no cambia: se conservan los mismos empleados
    }

    private NodoBST construirBalanceado(List<Empleado> lista, int inicio, int fin) {
        if (inicio > fin) {
            return null;
        }
        int medio = (inicio + fin) / 2;
        NodoBST nodo = new NodoBST(lista.get(medio));
        nodo.setIzquierdo(construirBalanceado(lista, inicio, medio - 1));
        nodo.setDerecho(construirBalanceado(lista, medio + 1, fin));
        return nodo;
    }

    /** Elimina todos los nodos y deja el árbol vacío y listo para reutilizarse. */
    public void destruirArbol() {
        raiz = null;
        tamanio = 0;
    }
}
