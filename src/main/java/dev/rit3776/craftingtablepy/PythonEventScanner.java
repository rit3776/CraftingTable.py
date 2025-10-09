/*
 * Copyright (c) 2025 rit3776.
 * Licensed under the MIT License.
 */
package dev.rit3776.craftingtablepy;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.slf4j.Logger;

public class PythonEventScanner {
    private static final Logger LOGGER = CraftingTablepy.LOGGER;
    private static final String[] EVENTS = {
        "on_player_join", "on_player_leave", "on_chat", "on_item_use"
    };

    public static List<String> scanEvents() {
        List<String> detected = new ArrayList<>();
        Path path = Paths.get("config/craftingtablepy/mod.py");
        if (!Files.exists(path)) {
            LOGGER.warn("[CraftingTable.py] mod.py not found.");
            return detected;
        }

        try {
            String content = Files.readString(path);
            for (String ev : EVENTS) {
                if (content.contains("def " + ev)) {
                    detected.add(ev);
                    LOGGER.info("[CraftingTable.py] Detected event: " + ev);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to read mod.py", e);
        }
        return detected;
    }
}
