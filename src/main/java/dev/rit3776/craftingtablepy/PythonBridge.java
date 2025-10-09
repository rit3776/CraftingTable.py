/*
 * Copyright (c) 2025 rit3776.
 * Licensed under the MIT License.
 */
package dev.rit3776.craftingtablepy;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.Map;
import com.google.gson.Gson;

import net.minecraft.server.MinecraftServer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class PythonBridge {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Process pythonProcess;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private MinecraftServer server;

    public PythonBridge() {
        ServerLifecycleEvents.SERVER_STARTED.register(s -> {
            this.server = s;
            CraftingTablepy.LOGGER.info("[CraftingTable.py] MinecraftServer registered in PythonBridge.");
        });
    }


    public void start() {
        startPythonProcess();
        executor.submit(() -> {
            try {
                serverSocket = new ServerSocket(52577, 0, InetAddress.getByName("127.0.0.1")); // Only localhost can connect
                CraftingTablepy.LOGGER.info("[CraftingTable.py] Waiting for Python connection...");
                clientSocket = serverSocket.accept();
                CraftingTablepy.LOGGER.info("[CraftingTable.py] Python connected.");

                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // Read loop
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.startsWith("CMD:")) {
                        String cmd = line.substring(4);
                        CraftingTablepy.LOGGER.info("[Python->MC CMD] " + cmd);
                        executeMinecraftCommand(cmd);
                    } else if (line.startsWith("LOG:")) {
                        String msg = line.substring(4);
                        CraftingTablepy.LOGGER.info("[Python->MC LOG] " + msg);
                    } else {
                        CraftingTablepy.LOGGER.warn("[Python->MC Unknown] " + line);
                    }
                }
            } catch (IOException e) {
                CraftingTablepy.LOGGER.error("Error in PythonBridge: ", e);
            }
        });
    }

    private void startPythonProcess() {
        try {
            File scriptFile = new File("config/craftingtablepy/craftingtable.py");

            if (!scriptFile.exists()) {
                CraftingTablepy.LOGGER.error("[CraftingTable.py] Python script not found: " + scriptFile.getAbsolutePath());
                return;
            }

            // Launch Python process
            ProcessBuilder pb = new ProcessBuilder("python", scriptFile.getAbsolutePath());
            pb.redirectErrorStream(true);
            pythonProcess = pb.start();

            // Send Python process output to log
            new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(pythonProcess.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        CraftingTablepy.LOGGER.info("[Python] " + line);
                    }
                } catch (IOException ignored) {}
            }).start();

            CraftingTablepy.LOGGER.info("[CraftingTable.py] Python process started: " + scriptFile.getAbsolutePath());
        } catch (IOException e) {
            CraftingTablepy.LOGGER.error("[CraftingTable.py] Failed to start Python process", e);
        }
    }

    private void executeMinecraftCommand(String command) {
        if (server == null) {
            CraftingTablepy.LOGGER.warn("[CraftingTable.py] Command skipped: server not ready yet -> " + command);
            return;
        }

        server.execute(() -> {
            server.getCommandManager().executeWithPrefix(
                server.getCommandSource().withLevel(4),
                command
            );
        });
    }

    // Send event to Python
    public void sendEvent(String eventName, Map<String, Object> data) {
        if (out != null) {
            Map<String, Object> payload = Map.of(
                "type", "event",
                "name", eventName,
                "data", data
            );
            String json = new Gson().toJson(payload);
            out.println(json);
        }
    }
}
