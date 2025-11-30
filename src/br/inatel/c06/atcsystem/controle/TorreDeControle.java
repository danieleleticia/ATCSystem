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

        /**
         * Processa os voos aguardando:
         * - ordena por prioridade (maior primeiro)
         * - para cada voo tenta alocar aeronave e pista
         * - dispara thread do voo
         */
        public void processarVoos() {
            // cria cópia para evitar concorrência durante iteração
            List<Voo> copia;
            synchronized (voosAguardando) {
                copia = new ArrayList<>(voosAguardando);
            }

            // ordena por prioridade (aeronave)
            copia.sort(Comparator.comparingInt(Voo::getPrioridade).reversed());

            for (Voo voo : copia) {
                // já atribuído? pula
                if (voo.getAeronave() != null) continue;

                Aeronave a = selecionarAeronaveMaisAdequada();
                if (a == null) {
                    System.out.println("ATC: nenhuma aeronave disponível para voo " + voo.getCodigo());
                    continue;
                }

                Pista pistaLivre = buscarPistaDisponivel();
                if (pistaLivre == null) {
                    System.out.println("ATC: nenhuma pista disponível para voo " + voo.getCodigo());
                    continue;
                }

                // Marca aeronave em operação (removendo das disponíveis)
                sincronizadoRemoverAeronaveDisponivel(a);
                synchronized (aeronavesEmOperacao) {
                    aeronavesEmOperacao.add(a);
                }

                // Ocupa a pista (síncrono)
                boolean ocupou = pistaLivre.ocupar();
                if (!ocupou) {
                    // caso raro: alguém ocupou entre busca e ocupar
                    sincronizadoAddAeronaveDisponivel(a);
                    synchronized (aeronavesEmOperacao) { aeronavesEmOperacao.remove(a); }
                    continue;
                }

                // Atribui recursos ao voo
                voo.setAeronave(a);
                voo.setPista(pistaLivre);

                // Inicia o voo em uma nova thread
                Thread t = new Thread(voo);
                t.setName("Voo-" + voo.getCodigo());
                t.start();

                LogSistema.registrarAlocacao(a, voo);

                // Remove voo da lista aguardando (será notificado quando finalizar)
                synchronized (voosAguardando) {
                    voosAguardando.remove(voo);
                }

                System.out.println("ATC: Disparado voo " + voo.getCodigo()
                        + " com aeronave " + a.getMatricula()
                        + " na pista " + pistaLivre.getId());
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

        private Pista buscarPistaDisponivel() {
            synchronized (pistas) {
                for (Pista p : pistas) {
                    if (!p.estaOcupada()) return p;
                }
                return null;
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

    public void desligar() {
        sistemaAtivo = false;
    }


}


