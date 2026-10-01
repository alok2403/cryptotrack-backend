package com.cryptotrack.backend.service;

import com.cryptotrack.backend.dto.CoinSearchResult;
import com.cryptotrack.backend.dto.CryptoResponse;
import com.cryptotrack.backend.dto.PriceHistoryDto;

import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class CryptoService {

    private final RestClient restClient;

    // =====================================================
    // CACHE
    // =====================================================

    private final Map<String, CacheEntry<CryptoResponse>> priceCache =
            new ConcurrentHashMap<>();

    private final Map<String, CacheEntry<List<PriceHistoryDto>>> historyCache =
            new ConcurrentHashMap<>();

    private final Map<String, CacheEntry<List<CoinSearchResult>>> searchCache =
            new ConcurrentHashMap<>();

    private static final long CACHE_TTL_MILLIS = 60_000;

    private static final long STALE_CACHE_TTL_MILLIS =
            10 * 60_000;

    private static final List<Integer> ALLOWED_DAYS =
            List.of(1, 7, 30, 365);


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public CryptoService(RestClient.Builder builder) {

        HttpClient httpClient =
                HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(10))
                        .build();

        JdkClientHttpRequestFactory requestFactory =
                new JdkClientHttpRequestFactory(httpClient);

        requestFactory.setReadTimeout(
                Duration.ofSeconds(15)
        );

        this.restClient =
                builder
                        .requestFactory(requestFactory)
                        .baseUrl(
                                "https://api.coingecko.com/api/v3"
                        )
                        .build();
    }


    // =====================================================
    // GET MULTIPLE CRYPTOS
    // =====================================================

    public List<CryptoResponse> getMultipleCryptos(
            List<String> coinIds) {

        if (coinIds == null || coinIds.isEmpty()) {
            return List.of();
        }

        List<CryptoResponse> results =
                new ArrayList<>();

        for (String coinId : coinIds) {

            if (coinId == null || coinId.isBlank()) {
                continue;
            }

            try {

                results.add(
                        getCrypto(coinId)
                );

            } catch (Exception e) {

                System.err.println(
                        "Failed to fetch crypto "
                                + coinId
                                + ": "
                                + e.getMessage()
                );
            }
        }

        return results;
    }


    // =====================================================
    // GET CRYPTO
    // =====================================================

    public CryptoResponse getCrypto(
            String coinId) {

        String key = normalize(coinId);

        if (key.isEmpty()) {

            throw new RuntimeException(
                    "Cryptocurrency ID cannot be empty"
            );
        }

        // -------------------------------------------------
        // FRESH CACHE
        // -------------------------------------------------

        CacheEntry<CryptoResponse> cached =
                priceCache.get(key);

        if (cached != null &&
                !cached.isExpired()) {

            System.out.println(
                    "Returning fresh cached price for: "
                            + key
            );

            return cached.value;
        }

        // -------------------------------------------------
        // COINGECKO
        // -------------------------------------------------

        try {

            Map<String, Map<String, Object>> response =
                    executeWithRetry(() ->
                            restClient.get()
                                    .uri(
                                            uriBuilder ->
                                                    uriBuilder
                                                            .path(
                                                                    "/simple/price"
                                                            )
                                                            .queryParam(
                                                                    "ids",
                                                                    key
                                                            )
                                                            .queryParam(
                                                                    "vs_currencies",
                                                                    "usd"
                                                            )
                                                            .queryParam(
                                                                    "include_market_cap",
                                                                    "true"
                                                            )
                                                            .queryParam(
                                                                    "include_24hr_change",
                                                                    "true"
                                                            )
                                                            .build()
                                    )
                                    .retrieve()
                                    .body(Map.class)
                    );

            if (response == null ||
                    !response.containsKey(key)) {

                throw new RuntimeException(
                        "Cryptocurrency not found: "
                                + key
                );
            }

            Map<String, Object> crypto =
                    response.get(key);

            if (crypto == null) {

                throw new RuntimeException(
                        "Empty cryptocurrency response"
                );
            }

            Double price =
                    getDoubleOrNull(
                            crypto,
                            "usd"
                    );

            Double marketCap =
                    getDoubleOrNull(
                            crypto,
                            "usd_market_cap"
                    );

            Double change24h =
                    getDoubleOrNull(
                            crypto,
                            "usd_24h_change"
                    );

            if (price == null) {

                throw new RuntimeException(
                        "Price not available for "
                                + key
                );
            }

            String name =
                    getCryptoName(key);

            String symbol =
                    getCryptoSymbol(key);

            CryptoResponse result =
                    new CryptoResponse(
                            key,
                            name,
                            symbol,
                            price,
                            marketCap,
                            change24h
                    );

            priceCache.put(
                    key,
                    new CacheEntry<>(result)
            );

            System.out.println(
                    "Fresh crypto data received for: "
                            + key
                            + " = $"
                            + price
            );

            return result;

        } catch (Exception e) {

            System.err.println(
                    "CoinGecko failed for "
                            + key
                            + ": "
                            + e.getMessage()
            );

            if (cached != null &&
                    !cached.isTooOld()) {

                System.out.println(
                        "Using stale cached data for: "
                                + key
                );

                return cached.value;
            }

            throw new RuntimeException(
                    "Unable to fetch cryptocurrency data for "
                            + key,
                    e
            );
        }
    }


    // =====================================================
    // NORMAL CURRENT PRICES
    // =====================================================

    public Map<String, Double> getCurrentPrices(
            Set<String> cryptoIds) {

        if (cryptoIds == null ||
                cryptoIds.isEmpty()) {

            return Collections.emptyMap();
        }

        List<String> normalizedIds =
                cryptoIds.stream()
                        .filter(
                                id ->
                                        id != null &&
                                                !id.isBlank()
                        )
                        .map(this::normalize)
                        .filter(
                                id ->
                                        !id.isEmpty()
                        )
                        .distinct()
                        .toList();

        if (normalizedIds.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Double> prices =
                new HashMap<>();

        List<String> missingIds =
                new ArrayList<>();

        // -------------------------------------------------
        // USE NORMAL CACHE
        // -------------------------------------------------

        for (String id : normalizedIds) {

            CacheEntry<CryptoResponse> cached =
                    priceCache.get(id);

            if (cached != null &&
                    !cached.isExpired() &&
                    cached.value != null &&
                    cached.value.getPrice() != null) {

                prices.put(
                        id,
                        cached.value.getPrice()
                );

            } else {

                missingIds.add(id);
            }
        }

        if (missingIds.isEmpty()) {

            System.out.println(
                    "All prices served from normal cache."
            );

            return prices;
        }

        // -------------------------------------------------
        // FETCH MISSING
        // -------------------------------------------------

        Map<String, Double> fresh =
                fetchPricesFromCoinGecko(
                        missingIds
                );

        prices.putAll(fresh);

        return prices;
    }


    // =====================================================
    // PRICE ALERT PRICES
    // =====================================================

    /*
     * IMPORTANT:
     *
     * Price alerts MUST NOT use the normal price cache.
     *
     * The scheduler needs the latest price from CoinGecko.
     *
     * This method intentionally bypasses priceCache.
     */

    public Map<String, Double> getCurrentPricesForAlerts(
            Set<String> cryptoIds) {

        if (cryptoIds == null ||
                cryptoIds.isEmpty()) {

            return Collections.emptyMap();
        }

        List<String> normalizedIds =
                cryptoIds.stream()
                        .filter(
                                id ->
                                        id != null &&
                                                !id.isBlank()
                        )
                        .map(this::normalize)
                        .filter(
                                id ->
                                        !id.isEmpty()
                        )
                        .distinct()
                        .toList();

        if (normalizedIds.isEmpty()) {
            return Collections.emptyMap();
        }

        System.out.println(
                "=========================================="
        );

        System.out.println(
                "FETCHING LIVE PRICES FOR ALERTS"
        );

        System.out.println(
                "Coins: "
                        + normalizedIds
        );

        System.out.println(
                "=========================================="
        );

        try {

            Map<String, Double> prices =
                    fetchPricesFromCoinGecko(
                            normalizedIds
                    );

            if (prices.isEmpty()) {

                System.out.println(
                        "No live alert prices received."
                );

                return Collections.emptyMap();
            }

            for (Map.Entry<String, Double> entry :
                    prices.entrySet()) {

                System.out.println(
                        "LIVE PRICE: "
                                + entry.getKey()
                                + " = $"
                                + entry.getValue()
                );
            }

            return prices;

        } catch (Exception e) {

            System.err.println(
                    "Could not fetch live alert prices: "
                            + e.getMessage()
            );

            return Collections.emptyMap();
        }
    }


    // =====================================================
    // FETCH DIRECTLY FROM COINGECKO
    // =====================================================

    private Map<String, Double> fetchPricesFromCoinGecko(
            List<String> coinIds) {

        if (coinIds == null ||
                coinIds.isEmpty()) {

            return Collections.emptyMap();
        }

        String ids =
                coinIds.stream()
                        .collect(
                                Collectors.joining(",")
                        );

        try {

            Map<String, Map<String, Object>> response =
                    executeWithRetry(() ->
                            restClient.get()
                                    .uri(
                                            uriBuilder ->
                                                    uriBuilder
                                                            .path(
                                                                    "/simple/price"
                                                            )
                                                            .queryParam(
                                                                    "ids",
                                                                    ids
                                                            )
                                                            .queryParam(
                                                                    "vs_currencies",
                                                                    "usd"
                                                            )
                                                            .build()
                                    )
                                    .retrieve()
                                    .body(Map.class)
                    );

            if (response == null ||
                    response.isEmpty()) {

                return Collections.emptyMap();
            }

            Map<String, Double> prices =
                    new HashMap<>();

            for (Map.Entry<String, Map<String, Object>> entry :
                    response.entrySet()) {

                String coinId =
                        normalize(
                                entry.getKey()
                        );

                Map<String, Object> data =
                        entry.getValue();

                if (data == null) {
                    continue;
                }

                Double price =
                        getDoubleOrNull(
                                data,
                                "usd"
                        );

                if (price == null) {
                    continue;
                }

                prices.put(
                        coinId,
                        price
                );

                /*
                 * Update normal cache too.
                 *
                 * This is useful for the rest of the
                 * application, but the alert checker
                 * does NOT read this cache.
                 */

                CacheEntry<CryptoResponse> old =
                        priceCache.get(coinId);

                String name =
                        old != null &&
                                old.value != null &&
                                old.value.getName() != null
                                ? old.value.getName()
                                : getCryptoName(coinId);

                String symbol =
                        old != null &&
                                old.value != null &&
                                old.value.getSymbol() != null
                                ? old.value.getSymbol()
                                : getCryptoSymbol(coinId);

                Double marketCap =
                        old != null &&
                                old.value != null
                                ? old.value.getMarketCap()
                                : null;

                Double change24h =
                        old != null &&
                                old.value != null
                                ? old.value.getChange24h()
                                : null;

                CryptoResponse cryptoResponse =
                        new CryptoResponse(
                                coinId,
                                name,
                                symbol,
                                price,
                                marketCap,
                                change24h
                        );

                priceCache.put(
                        coinId,
                        new CacheEntry<>(
                                cryptoResponse
                        )
                );
            }

            System.out.println(
                    "CoinGecko returned "
                            + prices.size()
                            + " live prices."
            );

            return prices;

        } catch (Exception e) {

            System.err.println(
                    "Direct CoinGecko price request failed: "
                            + e.getMessage()
            );

            throw new RuntimeException(
                    "Unable to fetch live cryptocurrency prices",
                    e
            );
        }
    }


    // =====================================================
    // SAFE DOUBLE
    // =====================================================

    private Double getDoubleOrNull(
            Map<String, Object> map,
            String key) {

        if (map == null) {
            return null;
        }

        Object value =
                map.get(key);

        if (value == null) {
            return null;
        }

        if (value instanceof Number number) {
            return number.doubleValue();
        }

        try {

            return Double.parseDouble(
                    value.toString()
            );

        } catch (Exception e) {

            return null;
        }
    }


    // =====================================================
    // PRICE HISTORY
    // =====================================================

    public List<PriceHistoryDto> getPriceHistory(
            String coinId,
            int days) {

        int normalizedDays =
                ALLOWED_DAYS.contains(days)
                        ? days
                        : 7;

        String coinKey =
                normalize(coinId);

        if (coinKey.isEmpty()) {

            throw new RuntimeException(
                    "Cryptocurrency ID cannot be empty"
            );
        }

        String cacheKey =
                coinKey + ":" + normalizedDays;

        CacheEntry<List<PriceHistoryDto>> cached =
                historyCache.get(cacheKey);

        if (cached != null &&
                !cached.isExpired()) {

            return cached.value;
        }

        try {

            Map<String, Object> response =
                    executeWithRetry(() ->
                            restClient.get()
                                    .uri(
                                            uriBuilder ->
                                                    uriBuilder
                                                            .path(
                                                                    "/coins/{id}/market_chart"
                                                            )
                                                            .queryParam(
                                                                    "vs_currency",
                                                                    "usd"
                                                            )
                                                            .queryParam(
                                                                    "days",
                                                                    normalizedDays
                                                            )
                                                            .build(
                                                                    coinKey
                                                            )
                                    )
                                    .retrieve()
                                    .body(Map.class)
                    );

            if (response == null ||
                    !response.containsKey("prices")) {

                throw new RuntimeException(
                        "Price history not found"
                );
            }

            Object pricesObject =
                    response.get("prices");

            if (!(pricesObject instanceof List<?>)) {

                throw new RuntimeException(
                        "Invalid price history response"
                );
            }

            List<?> prices =
                    (List<?>) pricesObject;

            if (prices.isEmpty()) {

                throw new RuntimeException(
                        "No price history available"
                );
            }

            List<PriceHistoryDto> history =
                    new ArrayList<>();

            DateTimeFormatter formatter =
                    getFormatterForRange(
                            normalizedDays
                    );

            int step =
                    Math.max(
                            1,
                            prices.size() / 200
                    );

            for (int i = 0;
                 i < prices.size();
                 i++) {

                if (i % step != 0 &&
                        i != prices.size() - 1) {

                    continue;
                }

                Object pointObject =
                        prices.get(i);

                if (!(pointObject instanceof List<?>)) {
                    continue;
                }

                List<?> point =
                        (List<?>) pointObject;

                if (point.size() < 2) {
                    continue;
                }

                if (!(point.get(0) instanceof Number) ||
                        !(point.get(1) instanceof Number)) {

                    continue;
                }

                long timestamp =
                        ((Number) point.get(0))
                                .longValue();

                double price =
                        ((Number) point.get(1))
                                .doubleValue();

                String date =
                        formatter.format(
                                Instant.ofEpochMilli(
                                        timestamp
                                )
                        );

                history.add(
                        new PriceHistoryDto(
                                date,
                                price
                        )
                );
            }

            List<PriceHistoryDto> finalHistory =
                    Collections.unmodifiableList(
                            new ArrayList<>(history)
                    );

            historyCache.put(
                    cacheKey,
                    new CacheEntry<>(
                            finalHistory
                    )
            );

            return finalHistory;

        } catch (Exception e) {

            if (cached != null &&
                    !cached.isTooOld()) {

                return cached.value;
            }

            throw new RuntimeException(
                    "Unable to fetch price history for "
                            + coinKey,
                    e
            );
        }
    }


    // =====================================================
    // DATE FORMAT
    // =====================================================

    private DateTimeFormatter getFormatterForRange(
            int days) {

        ZoneId zone =
                ZoneId.systemDefault();

        if (days <= 1) {

            return DateTimeFormatter
                    .ofPattern("h:mm a")
                    .withZone(zone);
        }

        if (days <= 7) {

            return DateTimeFormatter
                    .ofPattern("EEE h a")
                    .withZone(zone);
        }

        if (days <= 30) {

            return DateTimeFormatter
                    .ofPattern("MMM dd")
                    .withZone(zone);
        }

        return DateTimeFormatter
                .ofPattern("MMM yyyy")
                .withZone(zone);
    }


    // =====================================================
    // SEARCH
    // =====================================================

    public List<CoinSearchResult> searchCoins(
            String query) {

        String key =
                normalize(query);

        if (key.isEmpty()) {
            return List.of();
        }

        CacheEntry<List<CoinSearchResult>> cached =
                searchCache.get(key);

        if (cached != null &&
                !cached.isExpired()) {

            return cached.value;
        }

        try {

            Map<String, Object> response =
                    executeWithRetry(() ->
                            restClient.get()
                                    .uri(
                                            uriBuilder ->
                                                    uriBuilder
                                                            .path("/search")
                                                            .queryParam(
                                                                    "query",
                                                                    key
                                                            )
                                                            .build()
                                    )
                                    .retrieve()
                                    .body(Map.class)
                    );

            if (response == null ||
                    !response.containsKey("coins")) {

                return List.of();
            }

            Object coinsObject =
                    response.get("coins");

            if (!(coinsObject instanceof List<?>)) {
                return List.of();
            }

            List<?> coins =
                    (List<?>) coinsObject;

            List<CoinSearchResult> results =
                    new ArrayList<>();

            int limit =
                    Math.min(
                            coins.size(),
                            30
                    );

            for (int i = 0;
                 i < limit;
                 i++) {

                Object coinObject =
                        coins.get(i);

                if (!(coinObject instanceof Map<?, ?>)) {
                    continue;
                }

                Map<?, ?> coin =
                        (Map<?, ?>) coinObject;

                String id =
                        getString(coin, "id");

                String name =
                        getString(coin, "name");

                String symbol =
                        getString(coin, "symbol");

                String thumb =
                        getString(coin, "thumb");

                if (id == null ||
                        name == null) {

                    continue;
                }

                results.add(
                        new CoinSearchResult(
                                id,
                                name,
                                symbol != null
                                        ? symbol
                                        : "",
                                thumb != null
                                        ? thumb
                                        : ""
                        )
                );
            }

            List<CoinSearchResult> finalResults =
                    Collections.unmodifiableList(
                            new ArrayList<>(results)
                    );

            searchCache.put(
                    key,
                    new CacheEntry<>(
                            finalResults
                    )
            );

            return finalResults;

        } catch (Exception e) {

            if (cached != null &&
                    !cached.isTooOld()) {

                return cached.value;
            }

            return List.of();
        }
    }


    // =====================================================
    // CURRENT PRICE
    // =====================================================

    public Double getCurrentPrice(
            String coinId) {

        return getCrypto(coinId)
                .getPrice();
    }


    // =====================================================
    // RETRY
    // =====================================================

    @FunctionalInterface
    private interface ApiCall<T> {
        T execute();
    }


    private <T> T executeWithRetry(
            ApiCall<T> apiCall) {

        int maxAttempts = 3;

        long[] delays = {
                500,
                1000,
                2000
        };

        Exception lastException = null;

        for (int attempt = 0;
             attempt < maxAttempts;
             attempt++) {

            try {

                return apiCall.execute();

            } catch (Exception e) {

                lastException = e;

                System.err.println(
                        "CoinGecko request failed. "
                                + "Attempt "
                                + (attempt + 1)
                                + "/"
                                + maxAttempts
                                + ": "
                                + e.getMessage()
                );

                if (attempt <
                        maxAttempts - 1) {

                    try {

                        Thread.sleep(
                                delays[attempt]
                        );

                    } catch (
                            InterruptedException interrupted) {

                        Thread.currentThread()
                                .interrupt();

                        throw new RuntimeException(
                                "Request interrupted",
                                interrupted
                        );
                    }
                }
            }
        }

        throw new RuntimeException(
                "CoinGecko request failed after "
                        + maxAttempts
                        + " attempts",
                lastException
        );
    }


    // =====================================================
    // STRING
    // =====================================================

    private String getString(
            Map<?, ?> map,
            String key) {

        Object value =
                map.get(key);

        return value == null
                ? null
                : value.toString();
    }


    // =====================================================
    // NORMALIZE
    // =====================================================

    private String normalize(
            String value) {

        if (value == null) {
            return "";
        }

        return value
                .trim()
                .toLowerCase();
    }


    // =====================================================
    // NAME
    // =====================================================

    private String getCryptoName(
            String coinId) {

        return switch (
                coinId.toLowerCase()) {

            case "bitcoin" ->
                    "Bitcoin";

            case "ethereum" ->
                    "Ethereum";

            case "solana" ->
                    "Solana";

            case "dogecoin" ->
                    "Dogecoin";

            case "cardano" ->
                    "Cardano";

            case "ripple" ->
                    "XRP";

            case "bitcoin-cash" ->
                    "Bitcoin Cash";

            case "litecoin" ->
                    "Litecoin";

            case "polkadot" ->
                    "Polkadot";

            case "chainlink" ->
                    "Chainlink";

            default ->
                    coinId;
        };
    }


    // =====================================================
    // SYMBOL
    // =====================================================

    private String getCryptoSymbol(
            String coinId) {

        return switch (
                coinId.toLowerCase()) {

            case "bitcoin" ->
                    "BTC";

            case "ethereum" ->
                    "ETH";

            case "solana" ->
                    "SOL";

            case "dogecoin" ->
                    "DOGE";

            case "cardano" ->
                    "ADA";

            case "ripple" ->
                    "XRP";

            case "bitcoin-cash" ->
                    "BCH";

            case "litecoin" ->
                    "LTC";

            case "polkadot" ->
                    "DOT";

            case "chainlink" ->
                    "LINK";

            default ->
                    coinId.toUpperCase();
        };
    }


    // =====================================================
    // CACHE ENTRY
    // =====================================================

    private static class CacheEntry<T> {

        final T value;

        final long fetchedAt;

        CacheEntry(T value) {

            this.value = value;

            this.fetchedAt =
                    System.currentTimeMillis();
        }

        boolean isExpired() {

            return System.currentTimeMillis()
                    - fetchedAt
                    > CACHE_TTL_MILLIS;
        }

        boolean isTooOld() {

            return System.currentTimeMillis()
                    - fetchedAt
                    > STALE_CACHE_TTL_MILLIS;
        }
    }
}