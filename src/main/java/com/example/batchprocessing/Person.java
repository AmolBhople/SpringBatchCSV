package com.example.batchprocessing;


import lombok.*;
import org.springframework.stereotype.Component;

import java.util.Date;
import org.springframework.context.annotation.Bean;

@Component
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@ToString
public class Person {

  private String id;
  private String name;
  private Long salary;
  private Date start_date;
  private String dept;

  public Person(String name, String dept){
    this.name = name;
    this.dept = dept;
  }
}