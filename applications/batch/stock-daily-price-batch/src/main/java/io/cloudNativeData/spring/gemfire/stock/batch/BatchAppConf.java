package io.cloudNativeData.spring.gemfire.stock.batch;

import io.cloudNativeData.trading.StockDailyPrice;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.infrastructure.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.infrastructure.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@EnableTask
@EnableTransactionManagement
@EnableBatchProcessing
        //(tablePrefix = "${batch.job.repository.schema.prefix:}BOOT3_BATCH_")
@EnableAutoConfiguration
public class BatchAppConf {

    @Value("classpath:csv/stock-demo-csv.csv")
    private Resource sourceStockDailyPrice;



    @Value("${batch.read.chunk.size:100}")
    private int chunkSize;


    @Value("${batch.jdbc.username:}")
    private String batchUsername;


    @Value("${batch.jdbc.password:''}")
    private String batchPassword;

    @Bean
    Job job(JobRepository jobRepository, Step step) {
        return new JobBuilder("stock-daily-price-job", jobRepository)
                .flow(step)
                .end()
                .build();
    }

    @Bean
    public FlatFileItemReader<StockDailyPrice> csvReader() {

        // 3. Configure the LineMapper
        DefaultLineMapper<StockDailyPrice> lineMapper = new DefaultLineMapper<>();

        // Tokenizer splits the line into fields
        var tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(",");
        tokenizer.setNames("ticker", "priceDate", "closePrice"); // Match CSV columns

        // FieldSetMapper maps fields to the POJO
        BeanWrapperFieldSetMapper<StockDailyPrice> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(StockDailyPrice.class);

        // FIX: Define a custom editor to handle the specific date-time format
        PropertyEditorSupport localDateEditor = new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                if (text != null && !text.trim().isEmpty()) {
                    // 'M/d/yyyy H:mm:ss' safely handles single/double digit months, days, and 24hr time
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy H:mm:ss");
                    setValue(LocalDate.parse(text, formatter));
                } else {
                    setValue(null);
                }
            }
        };

        // FIX: Register the custom editor to the mapper for LocalDate fields
        Map<Class<?>, java.beans.PropertyEditor> customEditors = new HashMap<>();
        customEditors.put(LocalDate.class, localDateEditor);
        fieldSetMapper.setCustomEditors(customEditors);

        // Assemble the line mapper
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        // (Assuming sourceStockDailyPrice is defined at the class level or passed in)
        var reader = new FlatFileItemReader<StockDailyPrice>(sourceStockDailyPrice, lineMapper);

        // 2. Skip the header row of the CSV
        reader.setLinesToSkip(1);
        return reader;
    }

    @Bean
    ItemWriter<StockDailyPrice> writer(GemfireTemplate gemFireTemplate){

        ItemWriter<StockDailyPrice> itemWriter = c ->
            gemFireTemplate.putAll(convertToMap(c));

        return itemWriter;
    }

    protected BinaryOperator<StockDailyPrice> mergeFunction() {
        return (oldValue, newValue) -> {
            System.out.println(String.format("Duplicate key %s", oldValue));
            return newValue;
        };
    }

    private Map<?,?> convertToMap(Chunk<? extends StockDailyPrice> chunk) {

        Function<StockDailyPrice,String> toKeyFunction =  stockDailyPrice -> {
          return stockDailyPrice.getTicker()+"|"+stockDailyPrice.getPriceDate();
        };

        return  chunk.getItems().parallelStream().collect(
                Collectors.toMap(toKeyFunction, i -> i, mergeFunction()));


    }

    @Bean
    public Step loadStep(JobRepository jobRepository,
                           PlatformTransactionManager transactionManager,
                           ItemReader<StockDailyPrice> itemReader,
                           ItemWriter<StockDailyPrice> itemWriter) {
        return new StepBuilder("load-step", jobRepository)
                .<StockDailyPrice, StockDailyPrice>chunk(chunkSize)
                .reader(itemReader)
                .writer(itemWriter)
                .build();
    }
}
