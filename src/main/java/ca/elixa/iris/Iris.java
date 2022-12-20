package ca.elixa.iris;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A tracing Logger
 */
public class Iris<I> {

    private final Supplier<I> getFunction;
    private final Function<I, List<Log>> getLogsFunction;
    private final BiConsumer<I, List<Log>> storeLogsFunction;
    private final Consumer<List<Log>> outputFunction;
    //singleton logic
    private Iris (Supplier<I> getFunction,
                  Function<I, List<Log>> getLogsFunction,
                  BiConsumer<I, List<Log>> storeLogsFunction,
                  Consumer<List<Log>> outputFunction){

        this.getFunction = getFunction;
        this.getLogsFunction = getLogsFunction;
        this.storeLogsFunction = storeLogsFunction;
        this.outputFunction = outputFunction;
    }

    private static Iris instance = null;

    /**
     * Initialize Iris
     * @param getFunction - function that returns T
     * @param getLogsFunction - function that takes T and a list of logs
     * @param storeLogsFunction - function that T and a log list, and stores the list.
     * @param outputFunction - function that gets called on commit()
     * @return
     * @param <T> - The type that is storing the logs
     */
    public static <T> Iris init(Supplier<T> getFunction,
                                Function<T, List<Log>> getLogsFunction,
                                BiConsumer<T, List<Log>> storeLogsFunction,
                                Consumer<List<Log>> outputFunction){
        if(instance == null)
            instance = new Iris(getFunction, getLogsFunction, storeLogsFunction, outputFunction);

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

    public static void error(String message){
        log(message, Level.ERROR);
    }
    public static void warning(String message){
        log(message, Level.WARNING);
    }
    public static void info(String message){
        log(message, Level.INFO);
    }
    public static void debug(String message){
        log(message, Level.DEBUG);
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
        List<Log> currentLogs = iris.getLogsFunction.apply(identifier);

        if(currentLogs == null)
            currentLogs = new ArrayList<>();

        currentLogs.add(log);

        iris.storeLogsFunction.accept(identifier, currentLogs);
    }

    /**
     * Pull the logs out of iris and to wherever you want!
     * @param <T>
     */
    public static <T> void commit(){
        Iris<T> iris = getInstance();
        T identifier = iris.getFunction.get();

        List<Log> logs = iris.getLogsFunction.apply(identifier);

        if(logs != null)
            iris.outputFunction.accept(logs);
    }
}
