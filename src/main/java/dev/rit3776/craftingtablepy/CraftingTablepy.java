/*
 * Copyright (c) 2025 rit3776.
 * Licensed under the MIT License.
 */
package dev.rit3776.craftingtablepy;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;


public class CraftingTablepy implements ModInitializer {
    public static final String MOD_ID = "craftingtablepy";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static PythonBridge pythonBridge;

    @Override
    public void onInitialize() {
        LOGGER.info("[CraftingTable.py] Checking Python files...");
        ensureUserScriptExists(); // Ensure config/craftingtablepy/mod.py and craftingtable.py exist
        LOGGER.info("[CraftingTable.py] Initializing...");

        pythonBridge = new PythonBridge();
        pythonBridge.start();

        List<String> detected = PythonEventScanner.scanEvents();
        EventRegistry.registerDetectedEvents(detected, pythonBridge);

        LOGGER.info("[CraftingTable.py] Initialized successfully!");
    }

    private static void ensureUserScriptExists() {
        Path configDir = Paths.get("config/craftingtablepy");
        Path coreFile = configDir.resolve("craftingtable.py");
        Path modFile = configDir.resolve("mod.py");

        try {
            if (!Files.exists(configDir)) {
                Files.createDirectories(configDir);
            }

            if (!Files.exists(coreFile)) {
                try (InputStream is = CraftingTablepy.class.getClassLoader()
                        .getResourceAsStream("craftingtablepy/craftingtable.py")) {
                    if (is == null) {
                        LOGGER.error("[CraftingTable.py] Default craftingtable.py not found in resources!");
                        return;
                    }
                    Files.copy(is, coreFile);
                    LOGGER.info("[CraftingTable.py] Generated default craftingtable.py in config directory.");
                }
            }
            if (!Files.exists(modFile)) {
                try (InputStream is = CraftingTablepy.class.getClassLoader()
                        .getResourceAsStream("craftingtablepy/mod.py")) {
                    if (is == null) {
                        LOGGER.error("[CraftingTable.py] Default mod.py not found in resources!");
                        return;
                    }
                    Files.copy(is, modFile);
                    LOGGER.info("[CraftingTable.py] Generated default mod.py in config directory.");
                }
            }
        } catch (IOException e) {
            LOGGER.error("[CraftingTable.py] Failed to copy default Python files.", e);
        }
    }

    public static PythonBridge getBridge() {
        return pythonBridge;
    }
}
