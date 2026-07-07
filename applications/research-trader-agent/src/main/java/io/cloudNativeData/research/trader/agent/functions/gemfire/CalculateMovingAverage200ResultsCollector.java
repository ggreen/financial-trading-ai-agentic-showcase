package io.cloudNativeData.research.trader.agent.functions.gemfire;

import lombok.extern.slf4j.Slf4j;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.ResultCollector;
import org.apache.geode.distributed.DistributedMember;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class CalculateMovingAverage200ResultsCollector implements ResultCollector<Object, BigDecimal> {

    // Store the incoming results from executing nodes
    private final List<BigDecimal> nodeResults = new ArrayList<>();

    /**
     * Triggered every time a node finishes its local execution and sends back a result chunk.
     */
    @Override
    public synchronized void addResult(DistributedMember memberID, Object resultOfSingleExecution) {
        if (resultOfSingleExecution instanceof BigDecimal movingAverage) {

            log.info("MovingAverage 200 results {} from memberID: {} ", movingAverage,memberID);

            if(!BigDecimal.ZERO.equals(movingAverage))
                this.nodeResults.add(movingAverage);

        } else if (resultOfSingleExecution instanceof Throwable) {
            throw new FunctionException((Throwable) resultOfSingleExecution);
        }
    }

    /**
     * Reduces all gathered node responses into a single final BigDecimal result.
     */
    @Override
    public BigDecimal getResult() throws FunctionException {
        if (nodeResults.isEmpty()) {
            return BigDecimal.ZERO;
        }

        // Example reduction: Summing up the averages received and dividing by node count.
        // NOTE: Adjust this logic depending on whether your function returns raw sums/counts
        // or pre-calculated sub-averages.
        BigDecimal sum = BigDecimal.ZERO;
        for (BigDecimal val : nodeResults) {
            sum = sum.add(val);
        }

        return sum.divide(BigDecimal.valueOf(nodeResults.size()), 4, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getResult(long timeout, TimeUnit unit) throws FunctionException, InterruptedException {
        return getResult();
    }

    @Override
    public void endResults() {
        // No-op - signals all data chunks have been received
    }

    @Override
    public void clearResults() {
        this.nodeResults.clear();
    }

}
