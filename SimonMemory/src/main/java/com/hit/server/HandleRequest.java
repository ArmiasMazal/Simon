package main.java.com.hit.server;

import com.google.gson.Gson;
import main.java.com.hit.controller.GameController;
import main.java.com.hit.server.Request;
import main.java.com.hit.server.Response;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class HandleRequest implements Runnable {
    private Socket clientSocket;
    private GameController controller;

    public HandleRequest(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.controller = new GameController();
    }

    @Override
    public void run() {
        try (
                Scanner reader = new Scanner(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true)
        ) {
            String jsonRequest = reader.nextLine();
            System.out.println("Received request: " + jsonRequest);

            Gson gson = new Gson();
            Request<?> request = gson.fromJson(jsonRequest, Request.class);

            Response<?> response = controller.handle(request);

            String jsonResponse = gson.toJson(response);
            writer.println(jsonResponse);
            System.out.println("Sent response: " + jsonResponse);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
