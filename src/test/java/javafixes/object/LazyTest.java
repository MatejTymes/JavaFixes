package javafixes.object;

import javafixes.concurrency.Runner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.UUID.randomUUID;
import static javafixes.common.CollectionUtil.newList;
import static javafixes.concurrency.Runner.runner;
import static javafixes.test.Random.pickRandomValue;
import static javafixes.test.Random.randomInt;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LazyTest {

    @Mock
    private Callable<UUID> valueInitializer;

    @Test
    public void shouldNotBeInitializedIfValueHasNotBeenCalled() {
        // When
        Lazy<UUID> lazy = new Lazy<>(valueInitializer);

        // Then
        assertThat(lazy.isInitialized(), is(false));

        verifyZeroInteractions(valueInitializer);
    }


    @Test
    public void shouldProvideLazyValue() throws Exception {
        UUID expectedValue = randomUUID();
        when(valueInitializer.call()).thenReturn(expectedValue);

        Lazy<UUID> lazy = new Lazy<>(valueInitializer);

        // When
        UUID actualValue = lazy.value();


        // Then
        assertThat(actualValue, equalTo(expectedValue));
        assertThat(lazy.isInitialized(), is(true));

        verify(valueInitializer, times(1)).call();
        verifyNoMoreInteractions(valueInitializer);
    }

    @Test
    public void shouldCallInitializationOnlyOnce() throws Exception {
        UUID expectedValue = randomUUID();
        when(valueInitializer.call()).thenReturn(expectedValue);

        Lazy<UUID> lazy = new Lazy<>(valueInitializer);

        // When
        lazy.value();
        UUID actualValue = lazy.value();


        // Then
        assertThat(actualValue, equalTo(expectedValue));
        assertThat(lazy.isInitialized(), is(true));

        verify(valueInitializer, times(1)).call();
        verifyNoMoreInteractions(valueInitializer);
    }

    @Test
    public void shouldCallInitializationOnlyOnceOnConcurrentAccess() throws Exception {
        UUID expectedValue = randomUUID();
        when(valueInitializer.call()).thenReturn(expectedValue);

        Lazy<UUID> lazy = new Lazy<>(valueInitializer);

        // When
        int numberOfThread = randomInt(3, 10);
        Runner runner = runner(numberOfThread);
        CountDownLatch starter = new CountDownLatch(numberOfThread);
        List<Future<UUID>> results = newList();
        for (int i = 0; i < numberOfThread; i++) {
            results.add(runner.run(() -> {
                starter.countDown();
                starter.await();

                return lazy.value();
            }));
        }
        runner.waitTillDone().shutdownNow();

        // Then
        assertThat(lazy.isInitialized(), is(true));
        for (Future<UUID> result : results) {
            assertThat(result.isDone(), is(true));
            assertThat(result.get(), equalTo(expectedValue));
        }

        verify(valueInitializer, times(1)).call();
        verifyNoMoreInteractions(valueInitializer);
    }

    @Test
    public void shouldNotBeInitializeIfInitializationFails() throws Exception {
        Exception expectedException = pickRandomValue(
                new Exception("I failed you"),
                new RuntimeException("And I failed you again")
        );
        when(valueInitializer.call()).thenThrow(expectedException);

        Lazy<UUID> lazy = new Lazy<>(valueInitializer);

        // When
        try {
            lazy.value();

            // Then
            fail("expected IllegalStateException");
        } catch (IllegalStateException actualException) {
            assertThat(actualException.getCause(), equalTo(expectedException));
            assertThat(lazy.isInitialized(), is(false));

            verify(valueInitializer, times(1)).call();
            verifyNoMoreInteractions(valueInitializer);
        }
    }

    @Test
    public void shouldAllowToCallInitializationUntilItSucceeds() throws Exception {
        int failureCount = randomInt(2, 5);
        UUID expectedValue = randomUUID();
        Exception expectedException = pickRandomValue(
                new Exception("I failed you"),
                new RuntimeException("And I failed you again")
        );

        AtomicInteger callCount = new AtomicInteger(0);
        when(valueInitializer.call()).then(invocation -> {
            if (callCount.incrementAndGet() <= failureCount) {
                throw expectedException;
            }
            return expectedValue;
        });

        Lazy<UUID> lazy = new Lazy<>(valueInitializer);

        for (int i = 0; i < failureCount; i++) {
            try {
                // When
                lazy.value();

                // Then
                fail("expected IllegalStateException");
            } catch (IllegalStateException actualException) {
                assertThat(actualException.getCause(), equalTo(expectedException));
                assertThat(lazy.isInitialized(), is(false));
            }
        }

        verify(valueInitializer, times(failureCount)).call();

        // When
        UUID actualValue = lazy.value();

        // Then
        assertThat(actualValue, equalTo(expectedValue));
        assertThat(lazy.isInitialized(), is(true));

        verify(valueInitializer, times(failureCount + 1)).call();

        verifyNoMoreInteractions(valueInitializer);

        // When
        actualValue = lazy.value();

        // Then
        assertThat(actualValue, equalTo(expectedValue));
        assertThat(lazy.isInitialized(), is(true));

        verify(valueInitializer, times(failureCount + 1)).call();

        verifyNoMoreInteractions(valueInitializer);
    }
}