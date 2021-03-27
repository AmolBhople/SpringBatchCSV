package com.example.batchprocessing;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

  @Autowired
  private DataSource dataSource;

  @Autowired
  public JobBuilderFactory jobBuilderFactory;

  @Autowired
  public StepBuilderFactory stepBuilderFactory;

  @Bean
  public FlatFileItemReader<Person> reader() {
    return new FlatFileItemReaderBuilder<Person>()
            .name("personItemReader")
            .resource(new ClassPathResource("E://Codebase//CSVGenrator//output1.csv"))
            .delimited()
            .names(new String[]{"id", "name", "salary", "start_date", "dept"})
            .fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
              setTargetType(Person.class);
            }})
            .build();
  }

  @Bean
  public FlatFileItemReader<Person> reader1() {
    FlatFileItemReader<Person> personFlatFileItemReader = new FlatFileItemReader<>();
    ClassPathResource classPathResource = new ClassPathResource("E://Codebase//CSVGenrator//output1.csv");
    personFlatFileItemReader.setResource(classPathResource);
    personFlatFileItemReader.setName("personItemReader1");
    personFlatFileItemReader.setLinesToSkip(1);
    personFlatFileItemReader.setLineMapper(lineMapper());


    return personFlatFileItemReader;
  }

  private LineMapper<Person> lineMapper() {
    DefaultLineMapper<Person> personDefaultLineMapper =  new DefaultLineMapper<>();
    DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
    delimitedLineTokenizer.setDelimiter(",");
    delimitedLineTokenizer.setStrict(false);
    delimitedLineTokenizer.setNames(new String[]{"id", "name", "salary", "start_date", "dept"});

    BeanWrapperFieldSetMapper<Person> personBeanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
    personBeanWrapperFieldSetMapper.setTargetType(Person.class);

    personDefaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
    personDefaultLineMapper.setFieldSetMapper(personBeanWrapperFieldSetMapper);

    return personDefaultLineMapper;

  }

  @Bean
  public PersonItemProcessor processor() {
    return new PersonItemProcessor();
  }

  @Bean
  public JdbcBatchItemWriter<Person> writer() {
    return new JdbcBatchItemWriterBuilder<Person>()
            .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
            .sql("INSERT INTO people (id, name, salary, start_date, dept) VALUES (:id, :name, :salary, :start_date, :dept)")
            .dataSource(dataSource)
            .build();
  }

  @Bean
  public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
    return jobBuilderFactory.get("importUserJob")
            .incrementer(new RunIdIncrementer())
            .listener(listener)
            .flow(step1)
            .end()
            .build();
  }

  @Bean
  public Step step1(JdbcBatchItemWriter<Person> writer) {
    return stepBuilderFactory.get("step1")
            .<Person, Person> chunk(10)
            .reader(reader())
            .processor(processor())
            .writer(writer)
            .build();
  }

}