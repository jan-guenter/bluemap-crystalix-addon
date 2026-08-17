# Agent guide

This is the speed-first Crystalix 3.0.0/Fusion 1.3.12 BlueMap prototype for
All the Mons 1.2.0. Preserve the exact artifact tuple and stock fallback.

- Own only the three native `crystalix:*` glass IDs.
- Render persisted RGB, invisibility, ordinary/transparent material choice,
  and exact same-block Fusion FULL connectivity.
- Reinforcement, ghost collision, redstone behavior and waterloggability do
  not change the static exterior.
- Do not bundle Crystalix/Fusion resources or implementation classes.
- Crystalix material inside `framedblocks:*` is an explicit integration gap;
  do not create overlapping FramedBlocks renderer registrations casually.
- Use a small compile/package sanity pass, then disposable staging and owner
  comparison. Do not add an exhaustive matrix before a real failure asks for it.
