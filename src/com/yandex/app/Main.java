package com.yandex.app;

import com.yandex.app.logic.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new InMemoryTaskManager();

        HttpTaskServer server;
        try {
            server = new HttpTaskServer(taskManager);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        server.start();
    }
}