package bo;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class StopIdPair {

    private final Set<String> set;
    private final String left;
    private final String right;

    public StopIdPair (String left, String right) {
        set = new HashSet<String>();
        set.add(left);
        set.add(right);
        this.left = left;
        this.right = right;
    }

    public String getLeft() {
        return left;
    }

    public String getRight() {
        return right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StopIdPair that = (StopIdPair) o;
        return Objects.equals(set, that.set);
    }

    @Override
    public int hashCode() {
        return set.hashCode();
    }
}