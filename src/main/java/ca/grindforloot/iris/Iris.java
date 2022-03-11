package ca.grindforloot.iris;

import io.vertx.core.Context;
import io.vertx.core.Vertx;

import java.lang.ref.Cleaner;

/**
 * A smart logger that traces a request given a request-local map.
 *
 * @param
 */
public class Iris {
    //singleton logic
    private Iris(){};
    private static Iris instance = null;
    public static Iris getInstance(){
        if(instance == null)
            instance = new Iris();

        return instance;
    }

    public Logger getLogger(){
        Context ctx = Vertx.currentContext();
        if(ctx == null)
            throw new RuntimeException("Null context");

        /**
         * What we're doing here is keeping a single logger for the length of the request's life.
         * When the context dies, the reference to the logger will become unreachable, and it will then commit.
         *
         * We are using a dummy object to trigger this cleanup, to avoid a reference loop.
         */
        Logger logger = ctx.getLocal("logger");
        if(logger == null){

            logger = new Logger(instance);
            ctx.putLocal("logger", logger);

            Object tracer = new Object();

            Cleaner cleaner = Cleaner.create();

            Logger finalLogger = logger;
            cleaner.register(tracer, () -> {
                finalLogger.commit();

            });
        }

        return logger;

    }

}
