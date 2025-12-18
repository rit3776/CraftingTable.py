# * Copyright (c) 2025 rit3776.
# * Licensed under the MIT License.
import asyncio
import importlib.util
import os
import sys
import json

# ========================================
# CraftingTable.py Core (α-0.0.2)
# ========================================

sys.modules["craftingtable"] = sys.modules[__name__]

HOST = "127.0.0.1"
PORT = 52577

_event_handlers = {}
_writer = None
_user_mod = None

# Event decorator
def event(name: str, *, priority: int = 0):
    def decorator(func):
        if not asyncio.iscoroutinefunction(func):
            print(f"[CraftingTable.py] '{func.__name__}' is not async, ignored.")
            return func

        handlers = _event_handlers.setdefault(name, [])
        handlers.append((priority, func))

        # sort handlers by priority
        handlers.sort(key=lambda x: x[0], reverse=True)

        return func
    return decorator

# Run command to Minecraft
def cmd(cmd: str):
    if _writer is None:
        print("[CraftingTable.py] Not connected to server yet.")
        return
    _writer.write(f"CMD:{cmd}\n".encode())

    asyncio.create_task(_writer.drain())

# Log message to Minecraft console
def log(message: str):
    if _writer is None:
        print("[CraftingTable.py] Not connected to server yet.")
        return
    _writer.write(f"LOG:{message}\n".encode())

    asyncio.create_task(_writer.drain())

# Trigger reload of mod.py
def reload_mod():
    _event_handlers.clear()
    _load_user_module()
    log("mod.py reloaded")

# Read mod.py and register event handlers
def _load_user_module(path: str = "config/craftingtablepy/mod.py"):
    if not os.path.exists(path):
        log("mod.py not found.")
        sys.exit(1)

    global _user_mod
    spec = importlib.util.spec_from_file_location("user_mod", path)
    mod = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(mod)

    _user_mod = mod


# Event dispatcher
async def _dispatch_event(name: str, **data):
    handlers = _event_handlers.get(name)
    if not handlers:
        print(f"[CraftingTable.py] (ignored event) {name}")
        log(f"(ignored event) {name}")
        return

    for priority, func in handlers:
        try:
            await func(**data)
        except Exception as e:
            print(f"[CraftingTable.py] Error in {name}: {e}")
            log(f"Error in event '{name}': {e}")

# Main listening loop
async def _listen_to_server(reader):
    while True:
        line = await reader.readline()
        if not line:
            break

        msg = line.decode().strip()
        try:
            payload = json.loads(msg)
        except json.JSONDecodeError:
            print(f"[Minecraft] {msg}")
            continue

        if payload.get("type") == "event":
            await _dispatch_event(payload["name"], **payload.get("data", {}))
        elif payload.get("type") == "control":
            if payload.get("action") == "reload":
                reload_mod()
        else:
            print(f"[Minecraft] {msg}")


# Connect to Minecraft server
async def _connect_and_listen():
    global _writer
    print(f"[CraftingTable.py] Connecting to Minecraft ({HOST}:{PORT})...")
    reader, writer = await asyncio.open_connection(HOST, PORT)
    _writer = writer
    print("[CraftingTable.py] Connected to Minecraft.")
    await _listen_to_server(reader)


# Entry point
def start():
    _load_user_module()
    try:
        asyncio.run(_connect_and_listen())
    except KeyboardInterrupt:
        print("\n[CraftingTable.py] Stopped by user.")

if __name__ == "__main__":
    start()
