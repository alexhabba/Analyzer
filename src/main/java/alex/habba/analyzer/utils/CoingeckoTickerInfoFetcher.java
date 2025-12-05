package alex.habba.analyzer.utils;

import alex.habba.analyzer.dto.TickerInfoDto;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Получение информации о тикере coinId, marketCapUsd, circulatingSupply, platforms
 */
public class CoingeckoTickerInfoFetcher {

    private static String getJson(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        int code = conn.getResponseCode();
        if (code != 200) {
            throw new RuntimeException("HTTP " + code + " for " + urlStr);
        }
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }

    // 1) Найти coin id по тикеру (symbol)
    private static String findCoinIdByTicker(String ticker) throws Exception {
        String q = URLEncoder.encode(ticker, StandardCharsets.UTF_8);
        String url = "https://api.coingecko.com/api/v3/search?query=" + q;
        String json = getJson(url);
        JSONObject root = new JSONObject(json);

        // сначала пытаемся по symbol точным совпадением (case-insensitive)
        JSONArray coins = root.optJSONArray("coins");
        if (coins == null) return null;

        String tickerLower = ticker.toLowerCase();

        // 1) exact symbol match
        for (int i = 0; i < coins.length(); i++) {
            JSONObject c = coins.getJSONObject(i);
            String symbol = c.optString("symbol", "").toLowerCase();
            if (symbol.equals(tickerLower)) {
                return c.getString("id");
            }
        }

        // 2) иначе взять первый результат (best-effort)
        if (coins.length() > 0) return coins.getJSONObject(0).getString("id");
        return null;
    }

    // 2) Получить данные по id
    public static JSONObject getCoinData(String coinId) throws Exception {
        String url = "https://api.coingecko.com/api/v3/coins/" +
                URLEncoder.encode(coinId, StandardCharsets.UTF_8) +
                "?localization=false&tickers=false&community_data=false&developer_data=false&sparkline=false";
        String json = getJson(url);
        return new JSONObject(json);
    }

    // 3) Удобный метод получения market cap и circulating supply и platforms
    public static TickerInfoDto fetchByTicker(String ticker) throws Exception {
        String id = findCoinIdByTicker(ticker);
        if (id == null) throw new RuntimeException("Coin not found for ticker: " + ticker);

        JSONObject coin = getCoinData(id);

        JSONObject marketData = coin.optJSONObject("market_data");
        BigDecimal marketCapUsd = null;
        BigDecimal circulating = null;
        if (marketData != null) {
            JSONObject mc = marketData.optJSONObject("market_cap");
            if (mc != null && !mc.isNull("usd")) marketCapUsd = new BigDecimal(mc.getNumber("usd").toString());
            if (!marketData.isNull("circulating_supply")) circulating = new BigDecimal(marketData.getNumber("circulating_supply").toString());
        }

        JSONObject platforms = coin.optJSONObject("platforms"); // map chain -> contract address (or "")
        // build TickerInfo
        TickerInfoDto info = new TickerInfoDto();
        info.ticker = ticker;
        info.coinId = id;
        info.name = coin.optString("name", null);
        info.marketCapUsd = marketCapUsd;
        info.circulatingSupply = circulating;
        if (platforms != null) {
            for (String key : platforms.keySet()) {
                String addr = platforms.optString(key, "").trim();
                if (!addr.isEmpty()) info.platforms.put(key, addr);
            }
        }
        return info;
    }
}

