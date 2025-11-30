package br.inatel.c06.atcsystem.voo;

import br.inatel.c06.atcsystem.aeronave.Aeronave;
import br.inatel.c06.atcsystem.pista.Pista;
import br.inatel.c06.atcsystem.controle.TorreDeControle;
import br.inatel.c06.atcsystem.interfaces.Registravel;

public class Voo implements Runnable, Registravel {
    public enum TipoOperacao { DECOLAGEM, POUSO }
    public enum StatusVoo { AGUARDANDO, EM_OPERACAO, FINALIZADO }

    private final String codigo;
    private final String origem;
    private final String destino;
    private Aeronave aeronave; // atribuída pelo ATC
    private Pista pista;       // atribuída pelo ATC
    private final TipoOperacao tipo;
    private StatusVoo status;
    private TorreDeControle controle; // para callback na finalização

    // tempo em ms para simular operação (3 segundos para demonstração)
    private static final long TEMPO_OPERACAO_MS = 3000L;

    public Voo(String codigo, String origem, String destino, TipoOperacao tipo) {
        this.codigo = codigo;
        this.origem = origem;
        this.destino = destino;
        this.tipo = tipo;
        this.status = StatusVoo.AGUARDANDO;
    }

    // SETTERS usados pelo ATC
        public void setAeronave(Aeronave aeronave) {
            this.aeronave = aeronave;
        }

        public void setPista(Pista pista) {
            this.pista = pista;
        }

        public void setControle(TorreDeControle controle) {
            this.controle = controle;
        }

        public String getCodigo() { return codigo; }
        public Aeronave getAeronave() { return aeronave; }
        public Pista getPista() { return pista; }
        public TipoOperacao getTipo() { return tipo; }
        public String getOrigem() { return origem; }
        public String getDestino() { return destino; }

        // Retorna prioridade consultando a aeronave (se houver)
        public int getPrioridade() {
            return aeronave != null ? aeronave.getPrioridadePouso() : 0;
        }


        @Override
        public void run() {
            if (aeronave == null || pista == null) {
                registrarEvento("Erro: aeronave ou pista não atribuída. Abortando thread.");
                // garante que o ATC saiba que não houve operação
                if (controle != null) controle.notificarVooFinalizado(this);
                return;
            }

            iniciarOperacao();

            try {
                // Simula o tempo de operação (3 segundos)
                Thread.sleep(TEMPO_OPERACAO_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                registrarEvento("Thread interrompida: " + e.getMessage());
            }

            finalizar();
        }

        public void iniciarOperacao() {
            status = StatusVoo.EM_OPERACAO;
            registrarEvento("Iniciando " + tipo + ". Aeronave: " + aeronave.getMatricula()
                    + " Pista: " + pista.getId());
        }

        public void finalizar() {
            status = StatusVoo.FINALIZADO;
            // libera a pista (síncrono dentro da própria Pista)
            if (pista != null) {
                pista.liberar();
            }
            registrarEvento("FINALIZADO. Aeronave: " + (aeronave != null ? aeronave.getMatricula() : "N/A"));

            // AVISA o controle para ele reciclar a aeronave e atualizar estado
            if (controle != null) {
                controle.notificarVooFinalizado(this);
            }
        }

       @Override
        public void registrarEvento(String mensagem) {
            System.out.println("[VOO " + codigo + "] " + mensagem);
            // Opcional: também gravar em arquivo (requisito do professor) — posso adicionar se quiser
        }
    }
