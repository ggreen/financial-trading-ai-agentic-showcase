package io.cloudNativeData.research.trader;

import io.cloudNativeData.trading.StockDailyPrice;
import nyla.solutions.core.patterns.conversion.Converter;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.partition.PartitionRegionHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class CalculateMovingAverage200Function implements Function<String[]> {

    private static final int MOVING_AVERAGE_PERIOD = 200;

    private final Converter<Region, Region> toLocalRegion;

    public CalculateMovingAverage200Function()
    {
        this(region -> PartitionRegionHelper.getLocalData(region));
    }
    public CalculateMovingAverage200Function(Converter<Region, Region> toLocalRegion) {
        this.toLocalRegion = toLocalRegion;
    }

    @Override
    public void execute(FunctionContext<String[]> functionContext) {


        // Ensure the function is executed on a Region
        if (!(functionContext instanceof RegionFunctionContext)) {
            throw new FunctionException("Only region functions are supported");
        }

        RegionFunctionContext<String[]> rfc = (RegionFunctionContext) functionContext;
        Region<String, StockDailyPrice> region = toLocalRegion.convert(rfc.getDataSet());

        // Expecting the ticker string passed as an argument

        if (rfc.getArguments() == null || rfc.getArguments().length == 0) {
            throw new FunctionException("Target ticker is null or empty");
        }

        String targetTicker = rfc.getArguments()[0];


        // Stream local data entries to filter by Ticker using PDX fields
        List<StockDailyPrice> prices = region.values().stream()
                .map(value -> {
                    if (targetTicker.equalsIgnoreCase(value.getTicker())) {
                        return value;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                // Sort by date descending to get the most recent records
                .sorted(Comparator.comparing(StockDailyPrice::getPriceDate).reversed())
                .limit(MOVING_AVERAGE_PERIOD)
                .toList();

        // If we don't have enough data points, we can either return what we have or handle it
        if (prices.isEmpty()) {
            rfc.getResultSender().lastResult(BigDecimal.ZERO);
            return;
        }

        // Calculate the average
        var sum = prices.stream()
                .map(StockDailyPrice::getClosePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var average = sum.divide(BigDecimal.valueOf(prices.size()), 4, RoundingMode.HALF_UP);

        // Send result back to the client
        rfc.getResultSender().lastResult(average);

    }

    @Override
    public String getId() {
        return "calculateMovingAverage200Function";
    }
}
