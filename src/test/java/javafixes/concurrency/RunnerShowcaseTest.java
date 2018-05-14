package javafixes.concurrency;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.Callable;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

@Ignore
public class RunnerShowcaseTest {

    private Runner runner;

    @Before
    public void setUp() throws Exception {
        // create runner with 10 threads
        runner = Runner.runner(10);
    }

    @After
    public void tearDown() throws Exception {
        // shutdown runner
        runner.shutdownAndAwaitTermination();
    }

    @Test
    public void showcaseTest() {
        Runnable runnable = () -> System.out.println("This is awesome");

        Callable<Integer> callable = () -> 5;


        runner.runIn(2, SECONDS, runnable);
        runner.run(runnable);


        runner.waitTillDone(); // blocks until all tasks are finished (or failed)


        // and reuse it

        runner.runIn(500, MILLISECONDS, callable);

        runner.waitTillDone();
    }


    @Test
    public void shouldWaitTillAllTasksAreFinished() {


        Runnable runnable = () -> System.out.println("This is awesome");


        // submit tasks - Runnable, Callable or Task (custom Runnable that can throw Exceptions) - each call will return you a Future
        runner.runIn(2, SECONDS, runnable); // this will run in 2 seconds
        runner.run(runnable); // this will run immediately


        runner.waitTillDone(); // blocks after all tasks are finishined (or failed)
        // after this you can REUSE the class again


        // or submit tasks java8 style

        runner.runTaskIn(5, SECONDS, () -> System.out.println("!"));
        runner.runRunnableIn(3, SECONDS, () -> System.out.println("World"));
        runner.runCallable(() -> {
            System.out.println("Hello");
            return 0;
        });


        runner.waitTillDone();
    }
}