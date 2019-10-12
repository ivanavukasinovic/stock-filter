
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class MakeMeRich {
	public static final List<String> symbols = Arrays.asList("AMD", "HPQ", "IBM", "TXN", "VMW", "XRX", "AAPL", "ADBE",
			"AMZN", "CRAY", "CSCO", "SNE", "GOOG", "INTC", "INTU", "MSFT", "ORCL", "TIBX", "VRSN", "YHOO");

	final static String API_KEY = "M5UXS34WKHM5UUDJ";

	private static Double getPriceLive(String symbol) throws IOException {

		String rootURL = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE";
		rootURL += "&symbol=" + URLEncoder.encode(symbol, "UTF-8");
		rootURL += "&apikey=" + API_KEY;
		
		URL request = new URL(rootURL);
		String response = IOUtils.toString(request.openStream());

		JSONObject root = new JSONObject(response);

		JSONObject globalQuote;
		try {
			globalQuote = (JSONObject) root.get("Global Quote");
		} catch (JSONException e) {
			System.out.println("Wrong symbol entered: " + symbol);
			return 0.0;
		}

		String priceAsString = (String) globalQuote.get("05. price");
		double priceAsDouble = Double.parseDouble(priceAsString);

		return priceAsDouble;
	}

	
	public static void main(String[] args) throws IOException {

		// Inserting a Copyright notice
		List<File> javaFilesInFolder = Files.walk(Paths.get("src"))
				.filter(Files::isRegularFile)
				.map(Path::toFile)
				.filter(x -> x.getName().endsWith(".java"))
				.collect(Collectors.toList());

		for (File javaFile : javaFilesInFolder) {
			List<String> allLines = FileUtils.readLines(javaFile);
			if (!allLines.get(allLines.size()-1).contains("// Copyright"))
				FileUtils.write(javaFile, "\n// Copyright Ivana Vukasinovic, 2019", true);
			else
				System.out.println("File " + javaFile.getName() + " already has a copyright.");
		}
		
		System.out.println("==================================================================");

		// 1. Print these symbols using a Java 8 for-each and lambdas
		System.out.println("\nSymbols printed: ");
		symbols.stream().forEach(System.out::println);
		System.out.println("==================================================================");

		// 2. Use the StockUtil class to print the price of Bitcoin
		System.out.println("The price of Bitcoin from StockUtil class: " + StockUtil.getPrice("BTC-USD"));
		System.out.println("The realtime price of Bitcoin is: " + getPriceLive("BTC-USD"));
		System.out.println("==================================================================");

		// 3. Create a new List of StockInfo that includes the stock price
		List<StockInfo> listStockInfo = StockUtil.prices.entrySet()
				.stream()
				.map(stock -> new StockInfo(stock.getKey(), stock.getValue()))
				.collect(Collectors.toList());
		System.out.println("List of stocks that includes the stock price from StockUtil class: ");
		listStockInfo.forEach(System.out::println);
		System.out.println("==================================================================");

		// API requests limited up to 5 API requests per minute:
		// - 1 request already used for getting price of Bitcoin,
		// - 4 requests will be used for getting price of four randomly selected stocks.
		Map<String, Double> randomFourPrices = new HashMap<String, Double>();
		Object[] keys = StockUtil.prices.keySet().toArray();
		for (int i = 0; i < 4; i++) {
			Object key = keys[new Random().nextInt(keys.length)];
			randomFourPrices.put((String) key, StockUtil.prices.get(key));
		}

		List<StockInfo> listStockLive = randomFourPrices.entrySet()
				.stream()
				.map(stock -> {
					try {
						return new StockInfo(stock.getKey(), getPriceLive(stock.getKey()));
					} catch (IOException e) {
						System.out.println("Error!");
						return null;
						}
					})
				.collect(Collectors.toList());
		System.out.println("List of four randomly selected stocks that includes realtime stock price: ");
		listStockLive.forEach(System.out::println);
		System.out.println("==================================================================");

		// 4. Find the highest-priced stock under $500
		// * It was not applied to realtime prices due to limited access
		StockInfo highestPricedStock = listStockInfo
				.stream()
				.filter(StockUtil.isPriceLessThan(500))
				.reduce(StockUtil::pickHigh)
				.get();
		System.out.println("The highest-priced stock from StockUtil class under $500 is: " + highestPricedStock);

	}

}

// Copyright Ivana Vukasinovic, 2019