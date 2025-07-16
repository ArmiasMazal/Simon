package main.java.com.hit.dao;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DaoFileImpl implements IDao {
    private String pathFile;

    public DaoFileImpl(String pathFile) {
        this.pathFile = pathFile;
    }

    @Override
    public void saveSequence(List<String> sequence) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathFile, true))) {
            writer.write(sequence.get(0));
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    @Override
    public List<String> loadSequence() {
        List<String> sequence = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(pathFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sequence.add(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading from file: " + e.getMessage());
        }
        return sequence;
    }
}

