package dk.aau.ida8.util;

/**
 * Defines an immutable pair of values. To be used as the key to a map
 * created for grouping participants.
 */
public class Tuple<T, U> {
    private final T fst;
    private final U snd;

    public Tuple(T fst, U snd) {
        this.fst = fst;
        this.snd = snd;
    }

    public T getFst() {
        return fst;
    }

    public U getSnd() {
        return snd;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Tuple && equals((Tuple) o);
    }

    public boolean equals(Tuple o) {
        return this.getFst().equals(o.getFst()) &&
                this.getSnd().equals(o.getSnd());
    }

    @Override
    public int hashCode() {
        return getFst().hashCode() + getSnd().hashCode();
    }
}