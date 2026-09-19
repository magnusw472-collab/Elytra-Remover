# Elytra Remover — Fabric mod for 1.21.11

A standalone Fabric mod that strips elytras out of item frames the instant
a chunk is generated for the first time in the End - before any player
could possibly have reached it. Runs entirely passively; no commands, no
config, nothing to toggle.

## Why this is safe for already-explored/played areas

It hooks `ServerChunkEvents.CHUNK_GENERATE`, which fires exactly once per
chunk - the moment it's generated for the very first time. It never fires
again for that chunk on later loads. That means:

- Anywhere already generated (your existing 2500x2500 area, or anywhere
  players have been) is completely untouched, forever.
- Any elytra a player deliberately places into a decorative item frame
  anywhere in an already-generated chunk is safe - this mod will never
  look at that chunk again.
- Only genuinely new territory, the moment it's created, gets checked.

## Setup (same process as before)

1. Create a **new, separate GitHub repository** for this mod (don't mix it
   into the Last Stand mod's repo).
2. Unzip this project on your computer.
3. On the new repo's page, use "uploading an existing file" and drag in the
   **contents** of the unzipped folder directly (not the folder itself) -
   you should end up with `build.gradle`, `settings.gradle`,
   `gradle.properties`, `README.md`, `src`, and `.github` all sitting at
   the repo's root, side by side.
4. Commit, then check the **Actions** tab - it should build automatically
   using the exact same Gradle/Fabric versions already proven to work for
   your Last Stand mod.
5. Once green, open the run, scroll to **Artifacts**, download
   `elytra-remover-jar`, unzip it once to get `elytra-remover-1.0.0.jar`.
6. Drop that jar into your server's `mods/` folder **alongside** (not
   instead of) your existing Last Stand mod jar and Fabric API - all three
   jars coexist fine in the same folder.
7. Restart the server.

There's no command or in-game feedback - it just runs silently in the
background whenever new chunks generate in the End.
