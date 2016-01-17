package edu.usc.irds.autoext.utils;

/**
 * A tuple to store pair of values
 */
public class Tuple2<F, S> {
    public final F pos0;
    public final S pos1;

    public Tuple2(F pos0, S pos1) {
        this.pos0 = pos0;
        this.pos1 = pos1;
    }

    public F getPos0() {
        return pos0;
    }

    public S getPos1() {
        return pos1;
    }

    @Override
    public String toString() {
        return "(" + pos0 + ", " + pos1 + ")";
    }
}
