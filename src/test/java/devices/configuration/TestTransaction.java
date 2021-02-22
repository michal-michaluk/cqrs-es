package devices.configuration;

public class TestTransaction {

    public static void transactional(Runnable body) {
        if (!org.springframework.test.context.transaction.TestTransaction.isActive()) {
            org.springframework.test.context.transaction.TestTransaction.start();
        }
        body.run();
        org.springframework.test.context.transaction.TestTransaction.flagForCommit();
        org.springframework.test.context.transaction.TestTransaction.end();
    }
}
