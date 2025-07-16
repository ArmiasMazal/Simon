package main.java.com.hit.client;

import com.google.gson.Gson;
import main.java.com.hit.server.Request;
import main.java.com.hit.server.Response;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Client {
    public static void main(String[] args) {
        try (
                Socket socket = new Socket("localhost", 34567);
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            List<String> sequence = Arrays.asList("🔴", "🟢", "🔵");
            Request<List<String>> request = new Request<>(
                    Map.of("action", "game/check"),
                    sequence
            );

            Gson gson = new Gson();
            String jsonRequest = gson.toJson(request);
            writer.println(jsonRequest);

            String jsonResponse = reader.readLine();
            Response<?> response = gson.fromJson(jsonResponse, Response.class);

            System.out.println("Response from server: " + jsonResponse);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
