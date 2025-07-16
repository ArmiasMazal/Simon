package main.java.com.hit.service;

import main.java.com.hit.algorithm.IAlgoDFS;
import main.java.com.hit.algorithm.DFSRecursive;
import main.java.com.hit.dao.DaoFileImpl;
import main.java.com.hit.dao.IDao;
import main.java.com.hit.dm.ColorSequence;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MemoryGameService {
    private final IAlgoDFS algo;
    private final IDao dao;

    public MemoryGameService() {
        this.algo = new DFSRecursive();
        this.dao = new DaoFileImpl("src/main/resources/datasource.txt");
    }

    public MemoryGameService(IAlgoDFS algo, IDao dao) {
        this.algo = algo;
        this.dao = dao;
    }

    public boolean checkSequence(List<String> sequence) {
        return algo.searchPath(sequence);
    }

    public void startNewGame() {
        System.out.println("New game started (placeholder)");
    }

    public void saveScore(int score) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        String line = "Score: " + score + " | Date: " + timestamp;
        dao.saveSequence(List.of(line));
        System.out.println("✅ Score saved: " + line);
    }

    public List<String> getScoreHistory() {
        List<String> allLines = dao.loadSequence();

        Set<String> uniqueLines = new LinkedHashSet<>(allLines);

        return new ArrayList<>(uniqueLines);
    }
}