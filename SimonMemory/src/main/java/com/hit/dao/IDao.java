package main.java.com.hit.dao;
import java.util.List;
public interface IDao {
    void saveSequence(List<String> sequence);
    List<String> loadSequence();
}

