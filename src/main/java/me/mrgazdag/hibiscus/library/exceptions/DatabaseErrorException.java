package me.mrgazdag.hibiscus.library.exceptions;

public class DatabaseErrorException extends Exception {
    public DatabaseErrorException(String cause) {
        super(cause);
    }
    public DatabaseErrorException(Throwable cause) {
        super("Falied to connect to the database: " + cause.getLocalizedMessage(), cause);
    }
}
