package ca.grindforloot.iris;

import java.lang.ref.Cleaner;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A threadsafe logger, that allows for custom implementations.
 */
public class Iris {
    //singleton logic
    private Iris(){};
    private static Iris instance = null;
    public static Iris getInstance(){
        if(instance == null) {
            instance = new Iris();
        }

        return instance;
    }
    
    private Map<Level, List<LogHandler>> handlers = new HashMap<>();
    private Supplier<Logger> getLoggerHandler = null;
    private Consumer<Logger> commitHandler = null;

    /**
     * Add a handler for a log level. A null level means it will be called for all logs.
     * @param level
     * @param handler
     */
    public void addHandler(Level level, LogHandler handler){
        handlers.get(level).add(handler);
    }

    /**
     * Log a message.
     * @param level
     * @param message
     * @param arguments
     */
    public void log(Level level, String message, Object... arguments){
        String formatted = String.format(message, arguments);

        //Load all of the handlers
        List<LogHandler> targetHandlers = handlers.get(null);
        if(level != null)
            targetHandlers.addAll(handlers.get(level));

        for(LogHandler handler : targetHandlers){
            handler.handle(null);
        }
    }
    
    public void setCommitHandler(Consumer<Logger> handler) {
        commitHandler = handler;
    }

    public void commit(){
        commitHandler.accept(getLogger());
    }


    /**
     * Set the handler
     * @param handler
     */
    public void setGetLoggerHandler(Supplier<Logger> handler){
        getLoggerHandler = handler;
    }

    public Logger getLogger(){
        return getLoggerHandler.get();
    }
}
