// App.java
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class App {

	static String nomeArquivoDados;
    static Scanner teclado;
    static Produto[] produtosCadastrados;
    static int quantosProdutos = 0;
    static Pedido[] pedidosCadastrados;
    static Pedido[] pedidosOrdenadosPorData;
    static Pedido[] pedidosOrdenadosPorValor;
    static int quantPedidos = 0;
    static IOrdenator<Pedido> ordenador;
    
    static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    static void pausa() {
        System.out.println("Digite enter para continuar...");
        teclado.nextLine();
    }

    static void cabecalho() {
        System.out.println("AEDs II COMÉRCIO DE COISINHAS");
        System.out.println("=============================");
    }
    
    static <T extends Number> T lerOpcao(String mensagem, Class<T> classe) {
    	T valor;
        
    	System.out.println(mensagem);
    	try {
            valor = classe.getConstructor(String.class).newInstance(teclado.nextLine());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException 
        		| InvocationTargetException | NoSuchMethodException | SecurityException e) {
            return null;
        }
        return valor;
    }
    
    static Produto[] lerProdutos(String nomeArquivoDados) {
    	Scanner arquivo = null;
    	int numProdutos;
    	String linha;
    	Produto produto;
    	Produto[] produtosCadastrados;
    	
    	try {
    		arquivo = new Scanner(new File(nomeArquivoDados), Charset.forName("UTF-8"));
    		
    		numProdutos = Integer.parseInt(arquivo.nextLine());
    		produtosCadastrados = new Produto[numProdutos];
    		
    		for (int i = 0; i < numProdutos; i++) {
    			linha = arquivo.nextLine();
    			produto = Produto.criarDoTexto(linha);
    			produtosCadastrados[i] = produto;
    		}
    		quantosProdutos = numProdutos;
    		
    	} catch (IOException excecaoArquivo) {
    		produtosCadastrados = null;
    	} finally {
    		if (arquivo != null) arquivo.close();
    	}
    	
    	return produtosCadastrados;
    }
    
    static Pedido[] lerPedidos(String nomeArquivoDados) {
    	Pedido[] pedidosCadastrados;
    	Scanner arquivo = null;
    	int numPedidos;
    	String linha;
    	Pedido pedido;
    	
    	try {
    		arquivo = new Scanner(new File(nomeArquivoDados), Charset.forName("UTF-8"));
    		
    		numPedidos = Integer.parseInt(arquivo.nextLine());
    		pedidosCadastrados = new Pedido[numPedidos];
    		
    		for (int i = 0; i < numPedidos; i++) {
    			linha = arquivo.nextLine();
    			pedido = criarPedido(linha);
    			pedidosCadastrados[i] = pedido;
    		}
    		quantPedidos = numPedidos;
    		
    	} catch (IOException excecaoArquivo) {
    		pedidosCadastrados = null;
    	} finally {
    		if (arquivo != null) arquivo.close();
    	}
    	
    	return pedidosCadastrados;
    }
    
    private static Pedido criarPedido(String dados) {
    	String[] dadosPedido;
    	DateTimeFormatter formatoData;
    	LocalDate dataDoPedido;
    	int formaDePagamento;
    	Pedido pedido;
    	Produto produto;

    	dadosPedido = dados.split(";");

    	formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    	dataDoPedido = LocalDate.parse(dadosPedido[0], formatoData);

    	formaDePagamento = Integer.parseInt(dadosPedido[1]);

    	pedido = new Pedido(dataDoPedido, formaDePagamento);

    	for (int i = 2; i < dadosPedido.length; i++) {
    		String campo = dadosPedido[i];
    		String nomeProduto;
    		int quantidade;

    		int separador = campo.lastIndexOf(':');
    		if (separador >= 0) {
    			nomeProduto = campo.substring(0, separador);
    			try {
    				quantidade = Integer.parseInt(campo.substring(separador + 1));
    			} catch (NumberFormatException e) {
    				nomeProduto = campo;
    				quantidade = 1;
    			}
    		} else {
    			nomeProduto = campo;
    			quantidade = 1;
    		}

    		produto = pesquisarProduto(nomeProduto);
    		pedido.incluirProduto(produto, quantidade);
    	}
    	return pedido;
    }
    
    static Produto pesquisarProduto(String pesquisado) {
    	Produto produto = null;
    	Boolean localizado = false;
    	
    	for (int i = 0; (i < quantosProdutos && !localizado); i++) {
        	if (produtosCadastrados[i].descricao.equals(pesquisado)) {
        		produto = produtosCadastrados[i];
        		localizado = true;
        	}
        }
        
        if (!localizado) {
        	return null;
        } else {
        	return produto;
        }     
    }
    
    static int menu() {
        cabecalho();
        System.out.println("1 - Ordenar pedidos");
        System.out.println("2 - Embaralhar pedidos");
        System.out.println("3 - Listar todos os pedidos");
        System.out.println("4 - Localizar pedidos premium (por valor de corte)");
        System.out.println("0 - Finalizar");
        
        return lerOpcao("Digite sua opção: ", Integer.class);
    }
    
    static void localizarPedidosPremium() {
        cabecalho();
        Double valorCorte = lerOpcao("Informe o valor de corte (R$): ", Double.class);

        if (valorCorte == null) {
            System.out.println("Valor inválido!");
            return;
        }

        if (pedidosOrdenadosPorValor == null || pedidosOrdenadosPorValor.length == 0) {
            System.out.println("Não há pedidos cadastrados.");
            return;
        }

        int esq = 0;
        int dir = pedidosOrdenadosPorValor.length - 1;
        int primeiroIndice = -1;

        while (esq <= dir) {
            int meio = (esq + dir) / 2;
            double valorAtual = pedidosOrdenadosPorValor[meio].valorFinal();

            if (valorAtual >= valorCorte) {
                primeiroIndice = meio;
                dir = meio - 1;
            } else {
                esq = meio + 1;
            }
        }

        if (primeiroIndice == -1) {
            System.out.println("Nenhum pedido premium encontrado para o valor de corte informado.");
            return;
        }

        System.out.println("\nPedidos premium encontrados:\n");
        for (int i = primeiroIndice; i < pedidosOrdenadosPorValor.length; i++) {
            System.out.println(pedidosOrdenadosPorValor[i]);
            System.out.println();
        }
    }

    static int exibirMenuOrdenadores() {
        cabecalho();
        System.out.println("1 - Bolha");
        System.out.println("2 - Inserção"); 
        System.out.println("3 - Seleção"); 
        System.out.println("4 - Mergesort"); 
        System.out.println("5 - Heapsort"); 
        System.out.println("0 - Finalizar");
       
        return lerOpcao("Digite sua opção: ", Integer.class);
    }
    
    static int exibirMenuComparadores() {
        cabecalho();
        System.out.println("1 - Critério A: Valor Final do Pedido");
        System.out.println("2 - Critério B: Forma de Pagamento");
        System.out.println("3 - Critério C: Ticket Médio por Variedade");

        return lerOpcao("Digite sua opção: ", Integer.class);
    }
    
    static void ordenarPedidos(){
        int opcao = exibirMenuOrdenadores();
        switch (opcao) {
            case 1 -> ordenador = new Bubblesort<>();
            case 2 -> ordenador = new InsertionSort<>();
            case 3 -> ordenador = new SelectionSort<>();
            case 4 -> ordenador = new Mergesort<>();
            case 5 -> ordenador = new Heapsort<>();
        }

        if (ordenador != null) {
            opcao = exibirMenuComparadores();
            switch (opcao) {
                case 1:
                    ordenador.setComparador(new ComparadorCriterioA());
                    pedidosCadastrados = ordenador.ordenar(pedidosCadastrados);
                    break;
                case 2:
                    ordenador.setComparador(new ComparadorCriterioB());
                    pedidosCadastrados = ordenador.ordenar(pedidosCadastrados);
                    break;
                case 3:
                    ordenador.setComparador(new ComparadorCriterioC());
                    pedidosCadastrados = ordenador.ordenar(pedidosCadastrados);
                    break;
                default:
                    pedidosCadastrados = ordenador.ordenar(pedidosCadastrados);
            }

            System.out.println("Tempo gasto com a ordenação dos pedidos: " + ordenador.getTempoOrdenacao() + " ms.");
        }
        ordenador = null;
    }

    static void embaralharPedidos(){
        Collections.shuffle(Arrays.asList(pedidosCadastrados));
    }

    static void listarTodosOsPedidos() {
        cabecalho();
        System.out.println("\nPedidos cadastrados: ");
        for (int i = 0; i < quantPedidos; i++) {
        	System.out.println(String.format("%02d - %s\n", (i + 1), pedidosCadastrados[i].toString()));
        }
    }
    
    public static void main(String[] args) {
    	teclado = new Scanner(System.in, Charset.forName("UTF-8"));
        
    	nomeArquivoDados = "produtos.txt";
        produtosCadastrados = lerProdutos(nomeArquivoDados);
       
        String nomeArquivoPedidos = "pedidos.txt";
        pedidosCadastrados = lerPedidos(nomeArquivoPedidos);
        
        ComparadorPorData comparadorPorData = new ComparadorPorData();
        ordenador = new Heapsort<>();
        ordenador.setComparador(comparadorPorData);
        pedidosOrdenadosPorData = ordenador.ordenar(pedidosCadastrados);

        pedidosOrdenadosPorValor = Arrays.copyOf(pedidosCadastrados, quantPedidos);
        IOrdenator<Pedido> ordenadorValor = new Heapsort<>();
        ordenadorValor.setComparador(new ComparadorPorValor());
        pedidosOrdenadosPorValor = ordenadorValor.ordenar(pedidosOrdenadosPorValor);

        int opcao = -1;
      
        do{
        	opcao = menu();
            switch (opcao) {
                case 1 -> ordenarPedidos();
                case 2 -> embaralharPedidos();
                case 3 -> listarTodosOsPedidos();
                case 4 -> localizarPedidosPremium();
                case 0 -> System.out.println("FLW VLW OBG VLT SMP.");
            }
            pausa();
        } while (opcao != 0);       

        teclado.close();    
    }
}