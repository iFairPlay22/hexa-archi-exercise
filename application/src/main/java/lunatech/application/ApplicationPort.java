package lunatech.application;

/**
 * This port allows to manage services that needs to be run
 */
public interface ApplicationPort {

    /**
     * Start the service
     *
     * @param args Runnable arguments
     */
    void run(String[] args);

}
