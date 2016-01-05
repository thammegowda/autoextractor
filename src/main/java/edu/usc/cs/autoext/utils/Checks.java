package edu.usc.cs.autoext.utils;

/**
 * Created by tg on 1/4/16.
 */
public class Checks {

    /**
     * A custom {@link RuntimeException} to indicate that a check has failed
     */
    public static class CheckFailedException extends RuntimeException{
        /**
         * creates an exception
         * @param message message to describe why this exception was raised.
         */
        public CheckFailedException(String message) {
            super(message);
        }
    }

    /**
     * Checks boolean condition, on failure raises {@link CheckFailedException}
     * @param condition predicate
     * @param message error message to assist debug task when the condition fails
     */
    public static void check(boolean condition, String message){
        if (!condition) {
            throw new CheckFailedException(message);
        }
    }
}
