package ca.elixa.iris;

@FunctionalInterface
public interface LogHandler<I> {
    void handle(I identifier, Log log);
}
