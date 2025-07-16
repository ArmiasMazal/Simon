package main.java.com.hit.algorithm;
import java.util.List;
import java.util.Stack;
public class DFSIterative implements IAlgoDFS {
    @Override
    public boolean searchPath(List<String> sequence) {
        Stack<Integer> stack = new Stack<>();
        stack.push(0);

        while (!stack.isEmpty()) {
            int index = stack.pop();
            if (index == sequence.size()) return true;
            stack.push(index + 1);
        }
        return false;
    }
}

