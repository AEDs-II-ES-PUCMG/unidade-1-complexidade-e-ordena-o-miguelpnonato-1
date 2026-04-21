// Pedido.java
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Pedido implements Comparable<Pedido>{

	private static int ultimoID = 1;

	private int idPedido;

	/** Quantidade máxima de itens diferentes de um pedido */
	private static final int MAX_ITENS = 10;

	/** Porcentagem de desconto para pagamentos à vista */
	private static final double DESCONTO_PG_A_VISTA = 0.15;

	/** Vetor para armazenar os itens do pedido */
	private ItemDePedido[] itens;

	/** Data de criação do pedido */
	private LocalDate dataPedido;

	/** Indica a quantidade de itens no pedido até o momento */
	private int quantItens = 0;

	/** Indica a forma de pagamento do pedido sendo: 1, pagamento à vista; 2, pagamento parcelado */
	private int formaDePagamento;

	public Pedido(LocalDate dataPedido, int formaDePagamento) {
		idPedido = ultimoID++;
		itens = new ItemDePedido[MAX_ITENS];
		quantItens = 0;
		this.dataPedido = dataPedido;
		this.formaDePagamento = formaDePagamento;
	}

	public boolean incluirProduto(Produto novo) {
		return incluirProduto(novo, 1);
	}

	public boolean incluirProduto(Produto novo, int quantidade) {
		if (quantItens < MAX_ITENS) {
			itens[quantItens++] = new ItemDePedido(novo, quantidade);
			return true;
		}
		return false;
	}

	public double valorFinal() {
		double valorPedido = 0;
		BigDecimal valorPedidoBD;

		for (int i = 0; i < quantItens; i++) {
			valorPedido += itens[i].getPrecoVenda() * itens[i].getQuantidade();
		}

		if (formaDePagamento == 1) {
			valorPedido *= (1.0 - DESCONTO_PG_A_VISTA);
		}

		valorPedidoBD = BigDecimal.valueOf(valorPedido).setScale(2, RoundingMode.HALF_UP);
		return valorPedidoBD.doubleValue();
	}

    public double valorCatalogoAtual() {
        double soma = 0;

        for (int i = 0; i < quantItens; i++) {
            soma += itens[i].getProduto().valorDeVenda() * itens[i].getQuantidade();
        }

        return BigDecimal.valueOf(soma).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public double indiceEconomia() {
        double economia = valorCatalogoAtual() - valorFinal();
        return BigDecimal.valueOf(economia).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

	@Override
	public String toString() {
		StringBuilder stringPedido = new StringBuilder();

		stringPedido.append(String.format("Número do pedido: %02d\n", idPedido));

		DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		stringPedido.append("Data do pedido: " + formatoData.format(dataPedido) + "\n");

		stringPedido.append("Pedido com " + quantItens + " produto(s).\n");
		stringPedido.append("Produtos no pedido:\n");
		for (int i = 0; i < quantItens; i++) {
			stringPedido.append(itens[i].toString() + "\n");
		}

		stringPedido.append("Pedido pago ");
		if (formaDePagamento == 1) {
			stringPedido.append("à vista. Percentual de desconto: " + String.format("%.2f", DESCONTO_PG_A_VISTA * 100) + "%\n");
		} else {
			stringPedido.append("parcelado.\n");
		}

		stringPedido.append("Valor total do pedido: R$ " + String.format("%.2f", valorFinal()));

		return stringPedido.toString();
	}

    @Override
    public int compareTo(Pedido outro) {
    	if (this.idPedido == outro.idPedido) {
    		return 0;
    	} else if (this.idPedido < outro.idPedido) {
    		return -1;
    	} else {
    		return 1;
    	}
    }

    public LocalDate getDataPedido() {
    	return dataPedido;
    }

    public int getIdPedido() {
    	return idPedido;
    }

    public int getFormaDePagamento() {
    	return formaDePagamento;
    }

    /** Retorna a quantidade de itens (entradas distintas) no pedido. */
    public int getQuantosProdutos() {
    	return quantItens;
    }

    /**
     * Retorna o total de unidades físicas encomendadas, somando as quantidades de todos os itens.
     */
    public int getTotalItens() {
        int total = 0;
        for (int i = 0; i < quantItens; i++) {
            total += itens[i].getQuantidade();
        }
        return total;
    }

    /**
     * Retorna o código identificador do primeiro produto do pedido.
     */
    public int getIdPrimeiroProduto() {
        if (quantItens > 0 && itens[0] != null) {
            return itens[0].getProduto().hashCode();
        }
        return 0;
    }
}