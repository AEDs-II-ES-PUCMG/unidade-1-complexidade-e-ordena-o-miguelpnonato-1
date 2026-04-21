// ComparadorCriterioB.java
import java.util.Comparator;

/**
 * Critério B - Forma de Pagamento.
 * Primeiro pedidos à vista (1), depois parcelados (2).
 * Desempate 1: Valor Final do Pedido.
 * Desempate 2: Código Identificador.
 */
public class ComparadorCriterioB implements Comparator<Pedido> {

    @Override
    public int compare(Pedido o1, Pedido o2) {
        int cmp = Integer.compare(o1.getFormaDePagamento(), o2.getFormaDePagamento());

        if (cmp == 0) {
            cmp = Double.compare(o1.valorFinal(), o2.valorFinal());
        }

        if (cmp == 0) {
            cmp = Integer.compare(o1.getIdPedido(), o2.getIdPedido());
        }

        return cmp;
    }
}