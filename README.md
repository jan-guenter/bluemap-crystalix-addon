# BlueMap Crystalix Add-on

BlueMap 5.23 feature-backport add-on for the exact All the Mons 1.2.0 Crystalix
rendering tuple. It renders the three native Crystalix glass blocks with their persisted
RGB color, invisible state, ordinary/transparent texture choice, internal-face
culling, and the Fusion FULL connected sheets used by the matching client.

## Exact inputs

- Crystalix `3.0.0`: 817,004 bytes, SHA-256
  `42f97cf776cff8261bf671e64a333bbec65a8bf28e519d39cd958e0af9848e6c`.
- Fusion `1.3.12`: 923,270 bytes, SHA-256
  `17f5215648a98bcde4134577b013200dbf363273ae282449c51408ae8346f2fa`.
- BlueMap feature backport
  `5.22-feature.backport-5.23-stateless-java-web-server-46` at commit
  `7e07f4e74ec1e92a6ead9aa1e66054af3e133aac`, API commit
  `285c9a60eff3ac2b0cab308ce1058d1565be0971`.

Version `0.1.0-alpha.2` is an unpublished migration candidate. It compiles the
four Adapter API `0.1.0-alpha.2` sources and preserves the accepted renderer.
The candidate production JAR is 50,655 bytes with SHA-256
`62bbadf2f70d5335785001d26058a216892e6cf9db193db132ec4d93f862975b`.

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
in `config/bluemap/packs` and restart BlueMap. Do not place it in `mods`.

## Fast local check

Clone with `--recurse-submodules`, or initialize both exact support modules:

```bash
git submodule update --init --recursive -- \
  tooling/bluemap-addon-toolkit modules/bluemap-addon-adapter-api
```

The settings preflight rejects an uninitialized, changed, dirty, or incorrectly
pinned support checkout.

```bash
gradle --no-daemon \
  -PbluemapSourcePath=/path/to/exact/bluemap-backport \
  -PcrystalixJar=/path/to/crystalix-3.0.0.jar \
  -PfusionJar=/path/to/fusion-1.3.12-neoforge-mc1.21.1.jar \
  clean prototypeCheck build generatePomFileForAddonPublication \
  generateMetadataFileForAddonPublication
```
