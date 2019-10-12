
public class StockInfo {
	public final String ticker;
	public final double price;

	public StockInfo(final String symbol, final double thePrice) {
		ticker = symbol;
		price = thePrice;
	}


	public String toString() {
		return ticker + " " + price + ", ";
	}
}


// Copyright Ivana Vukasinovic, 2019