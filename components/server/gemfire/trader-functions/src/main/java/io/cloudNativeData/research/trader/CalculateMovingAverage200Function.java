package io.cloudNativeData.research.trader;

import nyla.solutions.core.patterns.conversion.Converter;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.partition.PartitionRegionHelper;
import org.apache.geode.pdx.PdxInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class CalculateMovingAverage200Function implements Function<Object[]> {

    private static final int MOVING_AVERAGE_PERIOD = 200;

    private final Converter<Region, Region> toLocalRegion;
    private final Logger logger = LogManager.getLogger(CalculateMovingAverage200Function.class);

    public CalculateMovingAverage200Function()
    {
        this(region -> PartitionRegionHelper.getLocalData(region));
    }
    public CalculateMovingAverage200Function(Converter<Region, Region> toLocalRegion) {
        this.toLocalRegion = toLocalRegion;
    }

    @Override
    public void execute(FunctionContext<Object[]> functionContext) {


        // Ensure the function is executed on a Region
        if (!(functionContext instanceof RegionFunctionContext)) {
            throw new FunctionException("Only region functions are supported");
        }

        RegionFunctionContext<Object[]> rfc = (RegionFunctionContext) functionContext;
        Region<String, PdxInstance> region = toLocalRegion.convert(rfc.getDataSet());

        // Expecting the ticker string passed as an argument

        if (rfc.getArguments() == null || rfc.getArguments().length == 0) {
            throw new FunctionException("Target ticker is null or empty");
        }

        String targetTicker = String.valueOf(rfc.getArguments()[0]);

        logger.info("Calculating Moving Average Price for ticker " + targetTicker);

        //.sorted(Comparator.comparing(StockDailyPrice::getPriceDate).reversed())

        // Stream local data entries to filter by Ticker using PDX fields
        List<PdxInstance> prices = region.values().stream()
                .map(value -> {
                    if (targetTicker.equalsIgnoreCase(String.valueOf(value.getField("ticker")))) {
                        return value;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                // Sort by date descending to get the most recent records
                .sorted(Comparator.comparing(pdx -> (LocalDate)((PdxInstance)pdx).getField("priceDate") ).reversed())
                .limit(MOVING_AVERAGE_PERIOD)
                .toList();

        logger.info("Search ticker: {}, prices: {} ",targetTicker,prices);
        // If we don't have enough data points, we can either return what we have or handle it
        if (prices.isEmpty()) {
            rfc.getResultSender().lastResult(BigDecimal.ZERO);
            return;
        }

        // Calculate the average
        var sum = prices.stream()
                .map(pdx -> (BigDecimal)pdx.getField("closePrice"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        logger.info("Search ticker: {}, sum: {}", targetTicker,sum);

        var average = sum.divide(BigDecimal.valueOf(prices.size()), 4, RoundingMode.HALF_UP);
        logger.info("Search ticker: {}, sum: {}", targetTicker,sum);

        // Send result back to the client
        rfc.getResultSender().lastResult(average);

    }

    @Override
    public String getId() {
        return "calculateMovingAverage200";
    }
}
