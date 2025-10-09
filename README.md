# CraftingTable.py

**CraftingTable.py** is a Fabric mod that allows you to control Minecraft using Python scripts in an event-driven way. Inspired by `discord.py`, it lets you handle server events asynchronously and execute commands directly from Python.

> ⚠️ **Note:** This is an early development version (v0.0.1). Many features will be added in the future.

---

## Requirements

To use CraftingTable.py, the following software is required:
* Fabric Loader: 0.17.2 or higher
* Fabric API: 0.97.3 or higher
* Java: 17 or higher
* Python: 3.11 recommended (other versions may also work)
Make sure these dependencies are installed before running the mod.

---

## Provided Event Functions

You can define the following asynchronous functions in `mod.py` to respond to in-game events:

| Function                                                     | Description                               | Parameters                                                                                                                                                                                                                                                    |
| ------------------------------------------------------------ | ----------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `on_player_join(player)`                                     | Triggered when a player joins the server  | `player` — player name (`str`)                                                                                                                                                                                                                                |
| `on_player_leave(player)`                                    | Triggered when a player leaves the server | `player` — player name (`str`)                                                                                                                                                                                                                                |
| `on_chat(message, type, player)`                             | Triggered when a chat message is sent     | `message` — message content (`str`) <br> `type` — message details (`str`) <br> `player` — player name (`str`)                                                                                                                                                 |
| `on_item_use(world, item_id, count, player, hand, nbt=None)` | Triggered when a player uses an item      | `world` — world type (`str`) <br> `item_id` — item ID (`str`) <br> `count` — item count (`int`) <br> `player` — player name (`str`) <br> `hand` — main hand or off-hand (`str`) <br> `nbt` — NBT data (`str`, optional, set to `None` if the item has no NBT) |

---

## Provided Functions and Variables

| Function / Variable    | Description                                                                                |
| ---------------------- | ------------------------------------------------------------------------------------------ |
| `mc.cmd(command: str)` | Executes a server command in real-time. Example: `mc.cmd("give player minecraft:apple 5")` |
| `mc.log(message: str)` | Prints a message to the server log. Example: `mc.log("Player joined")`                     |

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

async def on_player_join(player):
    mc.cmd(f"title {player} actionbar Welcome, {player}!")
    mc.cmd(f"give {player} minecraft:apple 5")

async def on_chat(message, type, player):
    if message.lower() == "hello":
        mc.cmd(f"say Hello, {player}!")
```

---

## Usage

1. Install the Fabric mod as usual.
2. Launch Minecraft to generate `mod.py` and `craftingtable.py`.
3. Edit `mod.py` to define your Python scripts.
4. Start the server and your scripts will automatically handle events and execute commands.

---

## Future Plans

* Add support for other versions.
* Add more advanced event types and APIs.
* Improve stability and performance for large-scale servers.

---

## License

MIT
