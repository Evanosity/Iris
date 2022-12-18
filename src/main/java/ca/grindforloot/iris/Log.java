package ca.grindforloot.iris;

public class Log {
    public final Level level;
    public final String message;

    public Log(String message, Level level){
        this.message = message;
        this.level = level;
    }
}
