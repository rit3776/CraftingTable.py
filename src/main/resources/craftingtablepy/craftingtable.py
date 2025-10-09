# * Copyright (c) 2025 rit3776.
# * Licensed under the MIT License.
import asyncio
import importlib.util
import inspect
import os
import sys
import json

# ========================================
# CraftingTable.py Core (0.0.1)
# ========================================

HOST = "127.0.0.1"
PORT = 52577

_event_handlers = {}
_writer = None


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

# Register event handler
def _register_event(name, func):
    if asyncio.iscoroutinefunction(func):
        _event_handlers[name] = func
    else:
        print(f"[CraftingTable.py] '{name}' is not async, ignored.")


# Read mod.py and register event handlers
def _load_user_module(path: str = "config/craftingtablepy/mod.py"):
    if not os.path.exists(path):
        print("[CraftingTable.py] mod.py not found.")
        sys.exit(1)

    spec = importlib.util.spec_from_file_location("mod", path)
    mod = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(mod)

    # Register functions starting with "on_"
    for name, func in inspect.getmembers(mod, inspect.isfunction):
        if name.startswith("on_"):
            _register_event(name, func)


# Event dispatcher
async def _dispatch_event(name: str, **data):
    func = _event_handlers.get(name)
    if func:
        try:
            await func(**data)
        except Exception as e:
            print(f"[CraftingTable.py] Error in {name}: {e}")
    else:
        print(f"[CraftingTable.py] (ignored event) {name}")


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
            event_name = payload["name"]
            data = payload.get("data", {})
            await _dispatch_event(event_name, **data)
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

start()