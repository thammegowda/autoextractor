package edu.usc.cs.autoext.utils;

/**
 * A simple reusable timer utility for benchmarking the code snippet.
 *
 * @author Thamme Gowda
 */
public class Timer {

    private long start;
    private long end;

    /**
     * Creates a timer and also marks the start
     */
    public Timer() {
        this.start = System.currentTimeMillis();
    }

    /**
     * Starts the timer.
     * @see #reset() to reuse the timer
     */
    public void start(){
        this.start = System.currentTimeMillis();
    }

    /**
     * Resets the timer and returns the value before the reset
     * @return the previous value of the timer
     */
    public long reset(){
        long old = read();
        this.start = System.currentTimeMillis();
        return old;
    }

    /**
     * Stops the timer
     * @return the timer value at the stop
     * @see #read() to retrieve it later time
     */
    public long stop(){
        this.end = System.currentTimeMillis();
        return this.end - this.start;
    }

    /**
     * reads the timer value.
     * @return the timer value, computes the difference between the start and end when applicable
     */
    public long read(){
        return (this.end >= this.start ? this.end : System.currentTimeMillis()) - this.start;
    }

    /**
     * Gets the timestamp when this timer was started
     * @return start timestamp
     */
    public long getStart() {
        return start;
    }

    /**
     * Gets the timestamp when this timer was stopped.
     * @return stop timestamp. 0 if the timer was not stopped
     */
    public long getEnd() {
        return end;
    }
}
