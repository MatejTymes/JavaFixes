package co.uk.matejtymes.concurrency;

import org.junit.Test;

import static java.util.concurrent.TimeUnit.SECONDS;

public class RunnerTest {

    @Test
    public void shouldWaitTillAllTasksAreFinished() {

        // create runner with 10 threads

        Runner runner = Runner.runner(10);


        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("This is awesome");
            }
        };

        // submit tasks - Runnable, Callable or Task (custom Runnable that can throw Exceptions) - each call will return you a Future
        runner.runIn(2, SECONDS, runnable); // this will run in 2 seconds
        runner.run(runnable); // this will run immediately


        runner.waitTillDone(); // blocks after all tasks are finishined (or failed)
        // after this you can REUSE the class again


        // or submit tasks java8 style

        runner.runTaskIn(5, SECONDS, () -> System.out.println("!"));
        runner.runRunnableIn(3, SECONDS, () -> System.out.println("World"));
        runner.runCallable(() -> { System.out.println("Hello"); return 0; });


        runner.waitTillDone();


        // or just kill it once you're done - it's up to you

        runner.shutdown();
    }

}