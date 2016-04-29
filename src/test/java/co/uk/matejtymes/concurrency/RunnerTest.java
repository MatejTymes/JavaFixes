package co.uk.matejtymes.concurrency;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class RunnerTest {

    @Test
    public void shouldWaitTillAllTasksAreFinished() {

        // define number of threads
        Runner runner = Runner.runner(10);

        // submit tasks
        runner.runTaskIn(5, TimeUnit.SECONDS, () -> System.out.println("!"));
        runner.runRunnableIn(3, TimeUnit.SECONDS, () -> System.out.println("World"));
        runner.runCallable(() -> { System.out.println("Hello"); return 0; });

        // wait till they finish
        runner.waitTillDone();

        System.out.println("First set of tasks finished");


        // and then REUSE the class again


        runner.runTaskIn(2, TimeUnit.SECONDS, () -> System.out.println("This is awesome"));

        runner.waitTillDone();

        System.out.println("Second set of tasks finished");


        // or just kill it - it's up to you

        runner.shutdown();
    }

}