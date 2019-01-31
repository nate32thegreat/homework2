package com.cybr406.post.problems;

import com.cybr406.post.PostApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Profile;

@Profile("setup")
@SpringBootApplication(exclude = {
    JpaRepositoriesAutoConfiguration.class,
    WebMvcAutoConfiguration.class,
    LiquibaseAutoConfiguration.class,
    JdbcRepositoriesAutoConfiguration.class,
    DataSourceAutoConfiguration.class
})
public class LiquibaseSetupApplication {

  public static void main(String[] args) {
    SpringApplication.run(PostApplication.class, args);
  }
  
}
