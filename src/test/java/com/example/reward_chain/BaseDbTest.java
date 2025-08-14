package com.example.reward_chain;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@ActiveProfiles("test")
@SpringBootTest
@Sql(scripts="/sql/reset_db.sql",
        executionPhase=Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public abstract class BaseDbTest {

}
