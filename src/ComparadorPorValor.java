// ComparadorPorValor.java
import java.util.Comparator;

public class ComparadorPorValor implements Comparator<Pedido>{

	@Override
	public int compare(Pedido o1, Pedido o2) {
		int cmp = Double.compare(o1.valorFinal(), o2.valorFinal());

		if (cmp == 0) {
			cmp = Integer.compare(o1.getIdPedido(), o2.getIdPedido());
		}

		return cmp;
	}
}