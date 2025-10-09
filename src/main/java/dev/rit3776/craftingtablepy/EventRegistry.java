/*
 * Copyright (c) 2025 rit3776.
 * Licensed under the MIT License.
 */
package dev.rit3776.craftingtablepy;

import dev.rit3776.craftingtablepy.events.*;
import org.slf4j.Logger;
import java.util.List;

public class EventRegistry {
    private static final Logger LOGGER = CraftingTablepy.LOGGER;

    // Listen only to detected events
    public static void registerDetectedEvents(List<String> detectedEvents, PythonBridge bridge) {
        LOGGER.info("[CraftingTable.py] Registering Fabric events...");

        for (String event : detectedEvents) {
            switch (event) {
                case "on_player_join" -> PlayerJoinEvent.register(bridge);
                case "on_player_leave" -> PlayerLeaveEvent.register(bridge);
                case "on_chat" -> ChatEvent.register(bridge);
                case "on_item_use" -> ItemUseEvent.register(bridge);
                default -> LOGGER.warn("[CraftingTable.py] Unknown event: " + event);
            }
        }

        LOGGER.info("[CraftingTable.py] Event registration complete.");
    }
}
