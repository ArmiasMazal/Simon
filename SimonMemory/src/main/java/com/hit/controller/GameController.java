package main.java.com.hit.controller;

import main.java.com.hit.server.Request;
import main.java.com.hit.server.Response;
import main.java.com.hit.service.MemoryGameService;

import java.util.List;
import java.util.Map;

public class GameController {
    private MemoryGameService gameService;

    public GameController() {
        this.gameService = new MemoryGameService();
    }

    public Response<?> handle(Request<?> request) {
        String action = request.getHeaders().get("action");

        switch (action) {
            case "game/check":
                List<String> sequence = (List<String>) request.getBody();
                boolean result = gameService.checkSequence(sequence);
                return new Response<>(Map.of("status", "ok"), result);

            case "game/start":
                gameService.startNewGame();
                return new Response<>(Map.of("status", "ok"), "Game started");

            case "game/score":
                Integer score = ((Double) request.getBody()).intValue();
                gameService.saveScore(score);
                return new Response<>(Map.of("status", "ok"), "Score saved: " + score);

            case "game/history":
                List<String> history = gameService.getScoreHistory();
                return new Response<>(Map.of("status", "ok"), history);

            default:
                return new Response<>(Map.of("status", "error"), "Unknown action: " + action);
        }
    }
}