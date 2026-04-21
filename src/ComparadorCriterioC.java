// ComparadorCriterioC.java
import java.util.Comparator;

/**
 * Critério C - Ticket Médio por Variedade de Produtos.
 * valorFinal / quantidade de posições ocupadas no carrinho.
 * Desempate 1: Valor Final do Pedido.
 * Desempate 2: Código Identificador.
 */
public class ComparadorCriterioC implements Comparator<Pedido> {

    @Override
    public int compare(Pedido o1, Pedido o2) {
        double ticket1 = o1.getQuantosProdutos() == 0 ? 0.0 : o1.valorFinal() / o1.getQuantosProdutos();
        double ticket2 = o2.getQuantosProdutos() == 0 ? 0.0 : o2.valorFinal() / o2.getQuantosProdutos();

        int cmp = Double.compare(ticket1, ticket2);

        if (cmp == 0) {
            cmp = Double.compare(o1.valorFinal(), o2.valorFinal());
        }

        if (cmp == 0) {
            cmp = Integer.compare(o1.getIdPedido(), o2.getIdPedido());
        }

        return cmp;
    }
}