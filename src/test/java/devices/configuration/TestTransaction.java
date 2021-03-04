package devices.configuration;

import java.util.function.Supplier;

public class TestTransaction {

    public static void transactional(Runnable body) {
        if (!org.springframework.test.context.transaction.TestTransaction.isActive()) {
            org.springframework.test.context.transaction.TestTransaction.start();
        }
        body.run();
        org.springframework.test.context.transaction.TestTransaction.flagForCommit();
        org.springframework.test.context.transaction.TestTransaction.end();
    }

    public static <T> T transactional(Supplier<T> body) {
        if (!org.springframework.test.context.transaction.TestTransaction.isActive()) {
            org.springframework.test.context.transaction.TestTransaction.start();
        }
        T result = body.get();
        org.springframework.test.context.transaction.TestTransaction.flagForCommit();
        org.springframework.test.context.transaction.TestTransaction.end();
        return result;
    }
}
