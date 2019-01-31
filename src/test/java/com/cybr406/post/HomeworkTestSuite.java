package com.cybr406.post;

import com.cybr406.post.problems.DatabaseTests;
import com.cybr406.post.problems.LiquibaseChangeLogTests;
import com.cybr406.post.problems.LiquibaseSetupTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    LiquibaseSetupTests.class,
    LiquibaseChangeLogTests.class,
    DatabaseTests.class
})
public class HomeworkTestSuite {
}
