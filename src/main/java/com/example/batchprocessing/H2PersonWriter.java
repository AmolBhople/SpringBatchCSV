package com.example.batchprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class H2PersonWriter implements ItemWriter<Person> {

    private static final Logger log = LoggerFactory.getLogger(H2PersonWriter.class);
//    @Autowired
//    private PersonRepository personRepository;

    @Override
    public void write(List<? extends Person> persons) throws Exception {
        log.info("Saving persons "+ persons);
        //personRepository.save(persons);
//        personRepository.saveAll(persons);
    }
}
