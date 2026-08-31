# Changelog

## 0.1.0-alpha.2 - 2026-08-31

- Target only BlueMap feature-backport commit
  `7e07f4e74ec1e92a6ead9aa1e66054af3e133aac` and API commit
  `285c9a60eff3ac2b0cab308ce1058d1565be0971`.
- Move the local adapter boundary from `bluemap522` to `bluemap523`.
- Compile the four pinned Adapter API sources and remove duplicate local helpers.
- Preserve persisted color, transparency, invisibility, and Fusion connectivity.

## 0.1.0-alpha.1 - 2026-08-17

- Render all three native Crystalix glass blocks with their persisted RGB
  color, invisibility, and ordinary or transparent material choice.
- Match Fusion FULL connected-glass face selection and cull internal faces.
- Fall back to BlueMap's stock renderer when the exact supported artifact
  tuple, resources, or saved block data are unavailable.
- Pass disposable full-pack staging and owner visual acceptance.
