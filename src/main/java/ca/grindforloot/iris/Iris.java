package ca.grindforloot.iris;

import java.lang.ref.Cleaner;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A tracing Logger
 */
public class Iris<I> {

    private final Supplier<I> getFunction;
    private final LogHandler<I> logFunction;
    private final Consumer<I> outputFunction;
    //singleton logic
    private Iris (Supplier<I> getFunction, LogHandler<I> logFunction, Consumer<I> outputFunction){
        this.getFunction = getFunction;
        this.logFunction = logFunction;
        this.outputFunction = outputFunction;
    }

    private static Iris instance = null;

    /**
     * Initialize Iris
     * @param getFunction - function that returns T
     * @param logFunction - function that takes T and a log, and stores the log in local memory
     * @param outputFunction - function that gets called on commit()
     * @return
     * @param <T> - The type that is storing the logs
     */
    public static <T> Iris init(Supplier<T> getFunction, LogHandler<T> logFunction, Consumer<T> outputFunction){
        if(instance == null)
            instance = new Iris(getFunction, logFunction, outputFunction);

        return instance;
    }

    /**
     * Get the Iris instance
     * @return
     */
    private static Iris getInstance(){
        if(instance == null)
            throw new IllegalStateException("bruh");
        return instance;
    }

    /**
     * Log a message at Level.INFO
     * @param message
     * @param <T>
     */
    public static <T> void log(String message){
        log(message, Level.INFO);
    }

    /**
     * Log a message at the specified level
     * @param message
     * @param level
     * @param <T>
     */
    public static <T> void log(String message, Level level){
        //create the log
        Log log = new Log(message, level);

        //get the identifier
        Iris<T> iris = getInstance();
        T identifier = iris.getFunction.get();

        //store the logs
        iris.logFunction.handle(identifier, log);
    }

    /**
     * Pull the logs out of iris and to wherever you want!
     * @param <T>
     */
    public static <T> void commit(){
        Iris<T> iris = getInstance();
        T identifier = iris.getFunction.get();

        iris.outputFunction.accept(identifier);
    }
}
