package main.test.com.hit.service;


import main.java.com.hit.algorithm.DFSIterative;
import main.java.com.hit.algorithm.DFSRecursive;
import main.java.com.hit.dao.DaoFileImpl;
import main.java.com.hit.dao.IDao;
import main.java.com.hit.service.MemoryGameService;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import java.util.Arrays;

public class MemoryGameServiceTest {
    @Test
    public void testGameWithDFSRecursive() {
        IDao dummyDao = new DaoFileImpl("src/test/resources/testdata.txt");
        MemoryGameService game = new MemoryGameService(new DFSRecursive(), dummyDao);
        Assertions.assertTrue(game.checkSequence(Arrays.asList("Red", "Blue", "Green")));
    }

    @Test
    public void testGameWithDFSIterative() {
        IDao testDao = new DaoFileImpl("src/test/resources/testdata.txt");
        MemoryGameService game = new MemoryGameService(new DFSIterative(), testDao);
        Assertions.assertTrue(game.checkSequence(Arrays.asList("Red", "Blue", "Green")));
    }
}


