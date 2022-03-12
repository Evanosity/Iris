package ca.grindforloot.iris;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @param <I> the info of the request
 */
public class Logger<I> {
    public Logger(Iris source){

    }

    List<Log> logs = new ArrayList<>();

    protected void commit(){

    }
}
