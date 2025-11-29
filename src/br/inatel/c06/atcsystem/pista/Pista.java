package br.inatel.c06.atcsystem.pista;

public class Pista {
        private String id;
        private boolean ocupada;

        public Pista(String id) {
            this.id = id;
            this.ocupada = false;
        }

        public String getId() { return id; }
        public boolean estaOcupada() { return ocupada; }

        public synchronized boolean ocupar() {
            if (ocupada) return false;
            ocupada = true;
            return true;
        }

        public synchronized void liberar() {
            ocupada = false;
        }
    }
