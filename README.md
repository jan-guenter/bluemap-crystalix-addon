# BlueMap Crystalix Add-on

Speed-first prototype for the exact All the Mons 1.2.0 Crystalix rendering
tuple. It renders the three native Crystalix glass blocks with their persisted
RGB color, invisible state, ordinary/transparent texture choice, internal-face
culling, and the Fusion FULL connected sheets used by the matching client.

## Exact inputs

- Crystalix `3.0.0`: 817,004 bytes, SHA-256
  `42f97cf776cff8261bf671e64a333bbec65a8bf28e519d39cd958e0af9848e6c`.
- Fusion `1.3.12`: 923,270 bytes, SHA-256
  `17f5215648a98bcde4134577b013200dbf363273ae282449c51408ae8346f2fa`.
- BlueMap `5.22` Java-21 backport at workspace commit `9be321df`.

The add-on reads operator-installed resources and redistributes none of them.
Unknown state, malformed/missing `color` NBT, missing resources, or a changed
artifact tuple uses BlueMap's stock renderer.

## Scope

Owned IDs:

- `crystalix:crystalix_glass`
- `crystalix:clear_crystalix_glass`
- `crystalix:bordered_crystalix_glass`

The stable exterior does not change with reinforcement, conductor, redstone,
ghost collision, or waterloggability. Real/fake light modes are represented by
their stable face lighting; inventory/UI behavior is irrelevant. Crystalix
material installed inside FramedBlocks remains an explicit later integration
slice because this bridge must not collide with the accepted FramedBlocks
renderer over `framedblocks:*` IDs.

## Installation

Install the matching Crystalix and Fusion versions, then place the add-on JAR
from the GitHub release in the server's `mods` directory alongside BlueMap.
Clients do not need the add-on.

## Fast local check

```bash
gradle --no-daemon clean check build
```
