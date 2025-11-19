package com.gruelbox.transactionoutbox.spring.test;

import com.gruelbox.transactionoutbox.AlreadyScheduledException;
import com.gruelbox.transactionoutbox.TransactionOutbox;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AlreadyScheduledTest {

  @Autowired
  private TransactionOutbox outbox;

  @Autowired
  private PlatformTransactionManager transactionManager;

  @Test
  public void aa() {
    TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

    transactionTemplate.execute(status -> {
      outbox.with().uniqueRequestId("my-unique-request").schedule(MyRemoteService.class).execute();
      return null;
    });
    transactionTemplate.execute(status -> {
      // Make sure we can't repeat the same work
      Assertions.assertThrows(
          AlreadyScheduledException.class,
          () -> outbox.with().uniqueRequestId("my-unique-request").schedule(MyRemoteService.class).execute());
      return null;
    });
  }
}
