package main.test.com.hit.algorithm;

import main.java.com.hit.algorithm.DFSIterative;
import main.java.com.hit.algorithm.DFSRecursive;
import main.java.com.hit.algorithm.IAlgoDFS;
import org.junit.jupiter.api.Assertions;
import org.junit.Test;

import java.util.Arrays;

public class IAlgoDFSTest {
    @Test
    public void testDFSRecursive() {
        IAlgoDFS algo = new DFSRecursive();
        Assertions.assertTrue(algo.searchPath(Arrays.asList("Red", "Green", "Blue")));
    }

    @Test
    public void testDFSIterative() {
        IAlgoDFS algo = new DFSIterative();
        Assertions.assertTrue(algo.searchPath(Arrays.asList("Red", "Green", "Blue")));
    }
}

