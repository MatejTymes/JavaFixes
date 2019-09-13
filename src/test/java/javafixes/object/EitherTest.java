package javafixes.object;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.UUID.randomUUID;
import static javafixes.object.Either.left;
import static javafixes.object.Either.right;
import static javafixes.test.Random.randomInt;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class EitherTest {

    @Test
    public void shouldHandleLeft() throws Throwable {
        UUID value = randomUUID();
        Either<UUID, Integer> either = Either.left(value);

        assertThat(either.isLeft(), is(true));
        assertThat(either.isRight(), is(false));

        assertThat(either.getLeft(), equalTo(value));
        try {
            either.getRight();

            fail("expected NoSuchElementException");
        } catch (NoSuchElementException expectedException) {
            assertThat(expectedException.getMessage(), equalTo("Right value not defined"));
        }


        Throwable expectedThrowable = new Throwable("Failed with a Throwable");
        Exception expectedException = new Exception("Failed with another Exception");
        RuntimeException expectedRuntimeException = new RuntimeException("Failed with yet another Exception");


        assertThat(either.getLeftOrThrow(() -> expectedThrowable), equalTo(value));
        assertThat(either.getLeftOrThrow(() -> expectedException), equalTo(value));
        assertThat(either.getLeftOrThrow(() -> expectedRuntimeException), equalTo(value));

        try {
            either.getRightOrThrow(() -> expectedThrowable);
            fail("expected Throwable");
        } catch (Throwable actualThrowable) {
            assertThat(actualThrowable, equalTo(expectedThrowable));
        }

        try {
            either.getRightOrThrow(() -> expectedException);
            fail("expected Exception");
        } catch (Exception actualException) {
            assertThat(actualException, equalTo(expectedException));
        }

        try {
            either.getRightOrThrow(() -> expectedRuntimeException);
            fail("expected RuntimeException");
        } catch (RuntimeException actualRuntimeException) {
            assertThat(actualRuntimeException, equalTo(expectedRuntimeException));
        }


        AtomicInteger leftCallCount = new AtomicInteger(0);
        AtomicInteger rightCallCount = new AtomicInteger(0);


        leftCallCount.set(0);
        rightCallCount.set(0);
        Either<String, BigDecimal> mappedEither = either.map(
                leftValue -> {
                    leftCallCount.incrementAndGet();
                    return leftValue + "-" + leftValue;
                },
                rightValue -> {
                    rightCallCount.incrementAndGet();
                    return BigDecimal.valueOf(rightValue + 5);
                }
        );
        assertThat(mappedEither, equalTo(left(value + "-" + value)));
        assertThat(leftCallCount.get(), is(1));
        assertThat(rightCallCount.get(), is(0));


        leftCallCount.set(0);
        Either<String, Integer> leftMappedEither = either.mapLeft(leftValue -> {
            leftCallCount.incrementAndGet();
            return leftValue + "-" + leftValue;
        });
        assertThat(leftMappedEither, equalTo(left(value + "-" + value)));
        assertThat(leftCallCount.get(), is(1));

        rightCallCount.set(0);
        Either<UUID, BigDecimal> rightMappedEither = either.mapRight(rightValue -> {
            rightCallCount.incrementAndGet();
            return BigDecimal.valueOf(rightValue + 5);
        });
        assertThat(rightMappedEither, equalTo(left(value)));
        assertThat(rightCallCount.get(), is(0));

        leftCallCount.set(0);
        rightCallCount.set(0);
        int randomIncrement = randomInt(-100, 100);
        Integer foldedValue = either.fold(
                leftValue -> {
                    leftCallCount.incrementAndGet();
                    return leftValue.hashCode() + randomIncrement;
                },
                rightValue -> {
                    rightCallCount.incrementAndGet();
                    return rightValue + randomIncrement;
                }
        );
        assertThat(foldedValue, equalTo(value.hashCode() + randomIncrement));
        assertThat(leftCallCount.get(), is(1));
        assertThat(rightCallCount.get(), is(0));

        leftCallCount.set(0);
        assertThat(either.ifLeft(leftValue -> leftCallCount.incrementAndGet()), equalTo(left(value)));
        assertThat(leftCallCount.get(), is(1));

        rightCallCount.set(0);
        assertThat(either.ifRight(rightValue -> rightCallCount.incrementAndGet()), equalTo(left(value)));
        assertThat(rightCallCount.get(), is(0));

        assertThat(either.get(), equalTo(value));
    }

    @Test
    public void shouldHandleRight() throws Throwable {
        UUID value = randomUUID();
        Either<Integer, UUID> either = Either.right(value);

        assertThat(either.isLeft(), is(false));
        assertThat(either.isRight(), is(true));

        try {
            either.getLeft();

            fail("expected NoSuchElementException");
        } catch (NoSuchElementException expectedException) {
            assertThat(expectedException.getMessage(), equalTo("Left value not defined"));
        }
        assertThat(either.getRight(), equalTo(value));


        Throwable expectedThrowable = new Throwable("Failed with a Throwable");
        Exception expectedException = new Exception("Failed with another Exception");
        RuntimeException expectedRuntimeException = new RuntimeException("Failed with yet another Exception");


        try {
            either.getLeftOrThrow(() -> expectedThrowable);
            fail("expected Throwable");
        } catch (Throwable actualThrowable) {
            assertThat(actualThrowable, equalTo(expectedThrowable));
        }

        try {
            either.getLeftOrThrow(() -> expectedException);
            fail("expected Exception");
        } catch (Exception actualException) {
            assertThat(actualException, equalTo(expectedException));
        }

        try {
            either.getLeftOrThrow(() -> expectedRuntimeException);
            fail("expected RuntimeException");
        } catch (RuntimeException actualRuntimeException) {
            assertThat(actualRuntimeException, equalTo(expectedRuntimeException));
        }

        assertThat(either.getRightOrThrow(() -> expectedThrowable), equalTo(value));
        assertThat(either.getRightOrThrow(() -> expectedException), equalTo(value));
        assertThat(either.getRightOrThrow(() -> expectedRuntimeException), equalTo(value));


        AtomicInteger leftCallCount = new AtomicInteger(0);
        AtomicInteger rightCallCount = new AtomicInteger(0);


        leftCallCount.set(0);
        rightCallCount.set(0);
        Either<BigDecimal, String> mappedEither = either.map(
                leftValue -> {
                    leftCallCount.incrementAndGet();
                    return BigDecimal.valueOf(leftValue + 5);
                },
                rightValue -> {
                    rightCallCount.incrementAndGet();
                    return rightValue + "-" + rightValue;
                }
        );
        assertThat(mappedEither, equalTo(right(value + "-" + value)));
        assertThat(leftCallCount.get(), is(0));
        assertThat(rightCallCount.get(), is(1));


        leftCallCount.set(0);
        Either<BigDecimal, UUID> leftMappedEither = either.mapLeft(leftValue -> {
            leftCallCount.incrementAndGet();
            return BigDecimal.valueOf(leftValue + 5);
        });
        assertThat(leftMappedEither, equalTo(right(value)));
        assertThat(leftCallCount.get(), is(0));

        rightCallCount.set(0);
        Either<Integer, String> rightMappedEither = either.mapRight(rightValue -> {
            rightCallCount.incrementAndGet();
            return rightValue + "-" + rightValue;
        });
        assertThat(rightMappedEither, equalTo(right(value + "-" + value)));
        assertThat(rightCallCount.get(), is(1));

        leftCallCount.set(0);
        rightCallCount.set(0);
        int randomIncrement = randomInt(-100, 100);
        Integer foldedValue = either.fold(
                leftValue -> {
                    leftCallCount.incrementAndGet();
                    return leftValue + randomIncrement;
                },
                rightValue -> {
                    rightCallCount.incrementAndGet();
                    return rightValue.hashCode() + randomIncrement;
                }
        );
        assertThat(foldedValue, equalTo(value.hashCode() + randomIncrement));
        assertThat(leftCallCount.get(), is(0));
        assertThat(rightCallCount.get(), is(1));

        leftCallCount.set(0);
        assertThat(either.ifLeft(leftValue -> leftCallCount.incrementAndGet()), equalTo(right(value)));
        assertThat(leftCallCount.get(), is(0));

        rightCallCount.set(0);
        assertThat(either.ifRight(rightValue -> rightCallCount.incrementAndGet()), equalTo(right(value)));
        assertThat(rightCallCount.get(), is(1));

        assertThat(either.get(), equalTo(value));
    }
}