package br.inatel.c06.atcsystem.controle;

import br.inatel.c06.atcsystem.arquivos.LogSistema;
import br.inatel.c06.atcsystem.voo.Voo;
import br.inatel.c06.atcsystem.aeronave.Aeronave;
import br.inatel.c06.atcsystem.pista.Pista;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TorreDeControle implements Runnable {
    private List<Aeronave> aeronavesDisponiveis = new ArrayList<>();
    private List<Aeronave> aeronavesEmOperacao = new ArrayList<>();
    private List<Pista> pistas = new ArrayList<>();
    private List<Voo> voosAguardando = new ArrayList<>();

    private volatile boolean sistemaAtivo = true;

    // cadastro
    public void adicionarAeronave(Aeronave a) {
        synchronized (aeronavesDisponiveis) {
            aeronavesDisponiveis.add(a);
        }
    }

    public void adicionarPista(Pista p) {
        synchronized (pistas) {
            pistas.add(p);
        }
    }

    public void adicionarVoo(Voo v) {
        v.setControle(this);
        synchronized (voosAguardando) {
            voosAguardando.add(v);
        }
    }

    public void processarVoos() {
        List<Voo> copia;
        synchronized (voosAguardando) {
            copia = new ArrayList<>(voosAguardando);
        }

        // --- PARTE 1: CONSUMO DE COMBUSTÍVEL NA ESPERA ---
        for (Voo voo : copia) {
            // Se o voo é de POUSO, ele está gastando combustível dando voltas
            if (voo.getTipo() == Voo.TipoOperacao.POUSO) {
                voo.getAeronave().consumirCombustivel(100.0);

                // Recalcula prioridade (Se baixar de 10%, vira Emergência 5 automaticamente)
                voo.getAeronave().atualizaPrioridade();
            }
        }

        // --- PARTE 2: REORGANIZA A FILA PELA PRIORIDADE ---
        // Quem ficou sem combustível (Prio 5) pula para o começo da lista agora!
        copia.sort(Comparator.comparingInt(Voo::getPrioridade).reversed());

        // --- PARTE 3: TENTA ALOCAR PISTA ---
        for (Voo voo : copia) {

            Aeronave aeronave = voo.getAeronave();
            if (aeronave == null) {
                continue; // segurança
            }

            // Agora buscamos pista compatível com o TAMANHO da aeronave
            Pista pistaLivre = buscarPistaDisponivel(aeronave);

            if (pistaLivre == null) {
                // Nenhuma pista livre que suporte o tamanho dessa aeronave
                continue;
            }

            // --- ACHOU PISTA! VAMOS LANÇAR O VOO ---

            // Remove da espera
            synchronized (voosAguardando) {
                voosAguardando.remove(voo);
            }

            // Adiciona na lista visual de "Em Operação"
            synchronized (aeronavesEmOperacao) {
                aeronavesEmOperacao.add(aeronave);
            }

            // Tenta ocupar a pista fisicamente, já validando tamanho
            if (!pistaLivre.ocupar(aeronave)) {
                // Se por algum motivo falhar (concorrência), volta pra fila na próxima iteração
                continue;
            }

            voo.setPista(pistaLivre);

            // Inicia a thread do voo
            Thread t = new Thread(voo);
            t.setName("Voo-" + voo.getCodigo());
            t.start();

            LogSistema.registrarAlocacao(voo.getAeronave(), voo);

            System.out.println("ATC: Autorizado " + voo.getTipo() + " voo " + voo.getCodigo()
                    + " [Avião: " + voo.getAeronave().getMatricula() + "]"
                    + " [Prioridade: " + voo.getPrioridade() + "]"
                    + " na " + pistaLivre.getId());
        }
    }
    public List<Aeronave> obterCopiaFilaPrioridade() {
        synchronized (aeronavesDisponiveis) {
            List<Aeronave> copia = new ArrayList<>(aeronavesDisponiveis);
            // ordena da MAIOR prioridade de pouso para a menor
            copia.sort(Comparator.comparingInt(Aeronave::getPrioridadePouso).reversed());
            return copia;
        }
    }

    // Seleciona aeronave "mais adequada" — neste exemplo: maior prioridade
    private Aeronave selecionarAeronaveMaisAdequada() {
        synchronized (aeronavesDisponiveis) {
            Aeronave melhor = null;
            for (Aeronave a : aeronavesDisponiveis) {
                if (melhor == null || a.getPrioridadePouso() > melhor.getPrioridadePouso()) {
                    melhor = a;
                }
            }
            return melhor;
        }
    }

    // Agora escolhe pista com base no tamanho da aeronave
    private Pista buscarPistaDisponivel(Aeronave aeronave) {
        synchronized (pistas) {
            Pista melhor = null;

            for (Pista p : pistas) {
                if (p.estaOcupada()) {
                    continue;
                }

                // A pista precisa suportar o tamanho da aeronave
                if (!p.suportaAeronave(aeronave)) {
                    continue;
                }

                // Regra: escolhe a MENOR pista que ainda caiba na aeronave
                if (melhor == null || p.getTamanhoMaximoPista() < melhor.getTamanhoMaximoPista()) {
                    melhor = p;
                }
            }

            return melhor;
        }
    }

    // wrappers sincronizados
    private void sincronizadoRemoverAeronaveDisponivel(Aeronave a) {
        synchronized (aeronavesDisponiveis) {
            aeronavesDisponiveis.remove(a);
        }
    }
    private void sincronizadoAddAeronaveDisponivel(Aeronave a) {
        synchronized (aeronavesDisponiveis) {
            aeronavesDisponiveis.add(a);
        }
    }
    public void notificarVooFinalizado(Voo voo) {
        Aeronave a = voo.getAeronave();
        if (a != null) {
            // remove de emOperacao e volta para disponiveis
            synchronized (aeronavesEmOperacao) {
                aeronavesEmOperacao.remove(a);
            }
            sincronizadoAddAeronaveDisponivel(a);
            System.out.println("ATC: Aeronave " + a.getMatricula() + " retornou para disponibilidade.");
        }

        // Pista já foi liberada pelo próprio Voo -> nada a fazer aqui
        System.out.println("ATC: Voo " + voo.getCodigo() + " finalizado e processado.");
    }

    @Override
    public void run() {
        System.out.println("ATC: Thread da torre iniciada.");
        while (sistemaAtivo) {

            processarVoos();

            try {
                // Pausa pequena para não ficar 100% do tempo ocupando CPU
                Thread.sleep(500); // 0,5 segundo
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("ATC: Thread da torre interrompida.");
                break;
            }
        }
        System.out.println("ATC: Por hoje é só");
    }

    public List<Voo> getVoosAguardandoCopia() {
        synchronized (voosAguardando) {
            return new ArrayList<>(voosAguardando);
        }
    }

    public void desligar() {
        sistemaAtivo = false;
    }
}