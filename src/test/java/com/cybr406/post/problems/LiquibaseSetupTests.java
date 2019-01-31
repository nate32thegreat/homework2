package com.cybr406.post.problems;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("setup")
@SpringBootTest(classes = LiquibaseSetupApplication.class)
public class LiquibaseSetupTests {

  @Autowired
  Environment env;
  
  @Test
  public void testLiquibaseDependency() {
    try {
      Class.forName("liquibase.Liquibase");
    } catch (ClassNotFoundException e) {
      Assert.fail("You need to add the Liquibase dependency to your build.gradle file.");
    }
  }
  
  @Test
  public void testDataSqlRemoved() {
    ClassPathResource dataSql = new ClassPathResource("data.sql");
    assertFalse("Delete data.sql", dataSql.exists());
    ClassPathResource dataPostgresSql = new ClassPathResource("data-postgres.sql");
    assertFalse("Delete data-postgres.sql", dataPostgresSql.exists());
  }
  
  @Test
  public void testLiquibaseChangeLogLocation() {
    String location = env.getProperty("spring.liquibase.change-log");
    assertNotNull("Change log location must be set.", location);
    assertEquals("classpath:db/changelog/db.changelog-master.xml", location);
  }
  
  @Test
  public void testLiquibaseChangeLogExists() {
    ClassPathResource resource = new ClassPathResource("db/changelog/db.changelog-master.xml");
    assertTrue("Add db.changelog-master.xml to your project.", resource.exists());
  }
  
}
