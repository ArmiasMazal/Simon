package main.java.com.hit.algorithm;
import java.util.List;

public class DFSRecursive implements IAlgoDFS {
    @Override
    public boolean searchPath(List<String> sequence) {
        return dfsHelper(sequence, 0);
    }

    private boolean dfsHelper(List<String> sequence, int index) {
        if (index == sequence.size()) return true;
        return dfsHelper(sequence, index + 1);
    }
}
