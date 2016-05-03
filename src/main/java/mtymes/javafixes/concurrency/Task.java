package mtymes.javafixes.concurrency;

/**
 * @author mtymes
 * @since 10/22/14 11:07 PM
 */
public interface Task {

    // same as runnable, but can throw exception
    void run() throws Exception;
}
