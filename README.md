# CraftingTable.py

**CraftingTable.py** is a Fabric mod that allows you to control Minecraft using Python scripts in an event-driven way. Inspired by `discord.py`, it lets you handle server events asynchronously and execute commands directly from Python.

> ⚠️ **Note:** This is an early development version (v0.0.2). Many features will be added in the future.
> 
> 🔄 **Upgrading from v0.0.1?** > Significant changes have been made to the event system and library files. Please read the **[Migration Guide (v0.0.1 → v0.0.2)](#migration-guide-v001--v002)** before updating to ensure your scripts continue to work.
---

## Requirements

To use CraftingTable.py, the following software is required:
* Fabric Loader: 0.17.2 or higher
* Fabric API: 0.97.3 or higher
* Java: 17 or higher
* Python: 3.11 recommended (other versions may also work)
Make sure these dependencies are installed before running the mod.

---

## Event System

Events are now registered using the `@mc.event` decorator. This replaces the old function-naming convention for better flexibility and control.

### Basic Syntax

```python
@mc.event("event_name", priority=1)
async def your_function_name(parameters):
    ...

```

* `event_name`: The name of the Minecraft event to listen for.
* `priority` (Optional): An integer defining the execution order.

### Available Events

| Event Name | Description | Parameters |
| --- | --- | --- |
| `"player_join"` | Triggered when a player joins | `player` — player name (`str`) |
| `"player_leave"` | Triggered when a player leaves | `player` — player name (`str`) |
| `"chat"` | Triggered when a chat message is sent | `message` — message content (`str`) <br> `type` — message details (`str`) <br> `player` — player name (`str`) |
| `"item_use"` | Triggered when a player uses an item | `world` — world type (`str`) <br> `item_id` — item ID (`str`) <br> `count` — item count (`int`) <br> `player` — player name (`str`) <br> `hand` — main hand or off-hand (`str`) <br> `nbt` — NBT data (`str`, optional, set to `None` if the item has no NBT) |

---

## Provided Functions and Variables

| Function / Variable    | Description                                                                                |
| ---------------------- | ------------------------------------------------------------------------------------------ |
| `mc.cmd(command: str)` | Executes a server command in real-time. Example: `mc.cmd("give player minecraft:apple 5")` |
| `mc.log(message: str)` | Prints a message to the server log. Example: `mc.log("Player joined")`                     |

---

## Administration Commands

* **/ctpy reload**: Reloads your `mod.py` and applies changes immediately.
* *Note: If you add a decorator for an event that was not present when the game started, a full game restart is required to register the listener on the Fabric side.*

---

## Getting Started

After first launching the mod, the following files are generated in your Minecraft game directory:

```
config/craftingtablepy/
├─ mod.py                  ← Write your Python event scripts here
├─ craftingtable.py        ← Python module for import, contains mc.cmd and mc.log
```

Edit `mod.py` to define your event handlers. For example:

```python
import craftingtable as mc

@mc.event("player_join")
async def welcome_player(player):
    mc.cmd(f"title {player} actionbar Welcome, {player}!")
    mc.cmd(f"give {player} minecraft:apple 5")

@mc.event("chat", priority=1)
async def greeting(message, type, player):
    if message.lower() == "hello":
        mc.cmd(f"say Hello, {player}!")
```

While in-game, run `/ctpy reload` to apply your changes!

---

## Usage

1. Install the Fabric mod as usual.
2. Launch Minecraft to generate `mod.py` and `craftingtable.py`.
3. Edit `mod.py` to define your Python scripts.
4. Start the server and your scripts will automatically handle events and execute commands.

---

## Technical Note on Reloading

To ensure all events can be reloaded dynamically, it is recommended to **include all decorators you plan to use in your `mod.py` at startup**. Even if the function body is empty (`pass`), having the decorator present allows the Fabric-side listener to initialize, enabling you to add logic later via `/ctpy reload`.

---

## Migration Guide (v0.0.1 → v0.0.2)

To upgrade to v0.0.2, please follow these steps carefully. **Simply replacing the jar file is not enough.**

### 1. Update the Mod File

Replace the old `.jar` file in your `mods` folder with the new **CraftingTable.py v0.0.2** version.

### 2. Force Regenerate the Library

The internal Python library has been updated. You **must delete** the following file to allow the mod to generate the latest version:

* **Path:** `/config/craftingtablepy/craftingtable.py`
* *Note: If you don't delete this, your scripts will fail to recognize the new decorator system.*

### 3. Update Your Scripts (`mod.py`)

The event system has changed from "naming convention" to "decorator-based". **Old functions without decorators will no longer be executed.**

**Example of changes required:**

| Version | Example Code in `mod.py` |
| --- | --- |
| **Old (v0.0.1)** | `async def on_chat(message, type, player):` |
| **New (v0.0.2)** | `@mc.event("chat")`<br>`async def on_chat(message, type, player):` |

---

## Event System

Events are now registered using the `@mc.event` decorator. This provides better control over execution and allows for custom function names.

### Basic Syntax

```python
@mc.event("event_name", priority=1)
async def your_custom_function_name(parameters):
    ...

```

---

## Future Plans

* Add support for other versions.
* Add more advanced event types and APIs.
* Improve stability and performance for large-scale servers.

---

## License

MIT
