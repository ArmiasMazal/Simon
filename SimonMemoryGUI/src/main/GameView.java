package main;

import com.google.gson.Gson;
import javafx.animation.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.Glow;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import main.java.com.hit.server.Request;
import main.java.com.hit.server.Response;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.*;

public class GameView implements Initializable {

    @FXML private Button redBtn, greenBtn, blueBtn, yellowBtn;
    @FXML private Button startBtn, historyBtn;
    @FXML private Label resultLabel;

    private final Map<String, Button> colorButtons = new HashMap<>();
    private final List<String> colorSequence = new ArrayList<>();
    private final List<String> userSequence = new ArrayList<>();
    private final Random random = new Random();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colorButtons.put("🔴", redBtn);
        colorButtons.put("🟢", greenBtn);
        colorButtons.put("🔵", blueBtn);
        colorButtons.put("🟡", yellowBtn);

        redBtn.setOnAction(e -> handleUserInput("🔴"));
        greenBtn.setOnAction(e -> handleUserInput("🟢"));
        blueBtn.setOnAction(e -> handleUserInput("🔵"));
        yellowBtn.setOnAction(e -> handleUserInput("🟡"));

        disableColorButtons(true);
        startBtn.setOnAction(e -> startNewGame());
        historyBtn.setOnAction(e -> showHistory());
    }

    private void startNewGame() {
        stopFloatingLabel();
        colorSequence.clear();
        userSequence.clear();
        resultLabel.setText("");
        addColorToSequence();
        playSequence();
    }

    private void addColorToSequence() {
        List<String> colors = List.of("🔴", "🟢", "🔵", "🟡");
        colorSequence.add(colors.get(random.nextInt(colors.size())));
    }

    private void playSequence() {
        resultLabel.setText("");
        setButtonState("showing");
        disableColorButtons(true);
        Timeline timeline = new Timeline();
        Duration delay = Duration.seconds(1);

        for (int i = 0; i < colorSequence.size(); i++) {
            String color = colorSequence.get(i);
            Button btn = colorButtons.get(color);
            KeyFrame frame = new KeyFrame(delay.multiply(i + 1), e -> {
                flashButton(btn);
                playSoundForColor(color);
            });
            timeline.getKeyFrames().add(frame);
        }

        timeline.setOnFinished(e -> {
            userSequence.clear();
            colorButtons.values().forEach(btn -> btn.getStyleClass().remove("showing"));
            setButtonState("choosing");
            disableColorButtons(false);
        });

        timeline.play();
    }

    private void playWrongSound() {
        try {
            Media sound = new Media(getClass().getResource("/sounds/wrong.mp3").toExternalForm());
            MediaPlayer player = new MediaPlayer(sound);
            player.play();
        } catch (Exception e) {
            System.out.println("⚠️ שגיאה בהשמעת צליל טעות");
        }
    }

    private void flashButton(Button btn) {
        btn.getStyleClass().add("highlight");

        Glow glow = new Glow(0.8);
        btn.setEffect(glow);

        ScaleTransition scale = new ScaleTransition(Duration.millis(200), btn);
        scale.setFromX(1.0);
        scale.setToX(1.3);
        scale.setFromY(1.0);
        scale.setToY(1.3);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);

        scale.setOnFinished(e -> {
            btn.setEffect(null);
            btn.getStyleClass().remove("highlight");
        });

        scale.play();
    }

    private void handleUserInput(String color) {
        playSoundForColor(color); // ✅ צליל בעת לחיצה
        Button btn = colorButtons.get(color);
        flashButton(btn);

        userSequence.add(color);
        int index = userSequence.size() - 1;

        if (!userSequence.get(index).equals(colorSequence.get(index))) {
            PauseTransition soundDelay = new PauseTransition(Duration.seconds(0.8));
            soundDelay.setOnFinished(ev -> playWrongSound());
            soundDelay.play();
            animateResultLabel("❌ טעות! נסו שוב");
            startFloatingLabel();
            sendScoreToServer(colorSequence.size() - 1);
            disableColorButtons(true);
            return;
        }

        if (userSequence.size() == colorSequence.size()) {
            animateResultLabel("✅ נכון! לשלב הבא...");
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(e -> {
                addColorToSequence();
                playSequence();
            });
            pause.play();
        }
    }

    private void disableColorButtons(boolean disable) {
        colorButtons.values().forEach(btn -> btn.setDisable(disable));
    }

    private void setButtonState(String styleClass) {
        colorButtons.values().forEach(btn -> {
            btn.getStyleClass().removeAll("showing", "choosing", "highlight");
            btn.getStyleClass().add(styleClass);
        });
    }

    private void animateResultLabel(String message) {
        resultLabel.setText(message);
        resultLabel.setStyle("-fx-text-fill: " + "#ffffff" + ";");
        ScaleTransition st = new ScaleTransition(Duration.millis(250), resultLabel);
        st.setFromX(1);
        st.setToX(1.2);
        st.setCycleCount(2);
        st.setAutoReverse(true);
        st.play();
    }

    private void playSoundForColor(String colorKey) {
        String filename = switch (colorKey) {
            case "🔴" -> "red.mp3";
            case "🟢" -> "green.mp3";
            case "🔵" -> "blue.mp3";
            case "🟡" -> "yellow.mp3";
            default -> null;
        };

        if (filename == null) return;

        try {
            Media sound = new Media(getClass().getResource("/sounds/" + filename).toExternalForm());
            MediaPlayer player = new MediaPlayer(sound);
            player.play();
        } catch (Exception e) {
            System.out.println("⚠️ שגיאה בהשמעת הצליל: " + filename);
        }
    }

    private void sendScoreToServer(int score) {
        try (
                Socket socket = new Socket("localhost", 34567);
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            Request<Integer> request = new Request<>(Map.of("action", "game/score"), score);
            Gson gson = new Gson();
            writer.println(gson.toJson(request));
            String jsonResponse = reader.readLine();
            System.out.println("📤 נשלח לשרת: " + jsonResponse);
        } catch (IOException e) {
            showError("שגיאה בשליחת ניקוד לשרת", e.getMessage());
        }
    }

    private Timeline floatingTimeline;

    private void startFloatingLabel() {
        if (floatingTimeline != null) {
            floatingTimeline.stop();
        }

        floatingTimeline = new Timeline();
        floatingTimeline.setCycleCount(Timeline.INDEFINITE);

        Random rnd = new Random();

        KeyFrame moveFrame = new KeyFrame(Duration.millis(800), e -> {
            double x = rnd.nextDouble() * 300 - 150;
            double y = rnd.nextDouble() * 150 - 75;

            resultLabel.setTranslateX(x);
            resultLabel.setTranslateY(y);
        });

        floatingTimeline.getKeyFrames().add(moveFrame);
        floatingTimeline.play();
    }

    private void stopFloatingLabel() {
        if (floatingTimeline != null) {
            floatingTimeline.stop();
            floatingTimeline = null;
        }
        resultLabel.setTranslateX(0);
        resultLabel.setTranslateY(0);
    }

    private void showHistory() {
        try (
                Socket socket = new Socket("localhost", 34567);
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            Request<String> request = new Request<>(Map.of("action", "game/history"), null);
            Gson gson = new Gson();
            writer.println(gson.toJson(request));
            String jsonResponse = reader.readLine();
            Response<?> response = gson.fromJson(jsonResponse, Response.class);

            if ("ok".equals(response.getHeaders().get("status"))) {
                List<String> history = (List<String>) response.getBody();
                showHistoryDialog(history);
            }
        } catch (IOException e) {
            showError("שגיאה בקבלת היסטוריה מהשרת", e.getMessage());
        }
    }

    private void showHistoryDialog(List<String> historyLines) {
        stopFloatingLabel();
        Collections.reverse(historyLines);
        TableView<Map<String, String>> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(220);

        TableColumn<Map<String, String>, String> scoreCol = new TableColumn<>("Score");
        TableColumn<Map<String, String>, String> dateCol = new TableColumn<>("Date");

        scoreCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("score")));
        dateCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("date")));

        table.getColumns().addAll(scoreCol, dateCol);

        ObservableList<Map<String, String>> data = FXCollections.observableArrayList();

        for (String line : historyLines) {
            String[] parts = line.split("\\|");
            if (parts.length == 2) {
                Map<String, String> row = new HashMap<>();
                row.put("score", parts[0].replace("Score:", "").trim());
                row.put("date", parts[1].replace("Date:", "").trim());
                data.add(row);
            }
        }

        table.setItems(data);

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("היסטוריית משחקים");
        dialog.setHeaderText("טבלת ניקודים");
        dialog.getDialogPane().setContent(table);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        dialog.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("❌ " + title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
