// ComparadorCriterioA.java
import java.util.Comparator;

/**
 * Critério A - Valor Final do Pedido (crescente).
 * Desempate 1: Volume Total de Itens.
 * Desempate 2: Código Identificador do primeiro item do pedido.
 */
public class ComparadorCriterioA implements Comparator<Pedido> {

    @Override
    public int compare(Pedido o1, Pedido o2) {
        int cmp = Double.compare(o1.valorFinal(), o2.valorFinal());

        if (cmp == 0) {
            cmp = Integer.compare(o1.getTotalItens(), o2.getTotalItens());
        }

        if (cmp == 0) {
            cmp = Integer.compare(o1.getIdPrimeiroProduto(), o2.getIdPrimeiroProduto());
        }

        return cmp;
    }
}