# Agent guide

This is the Crystalix 3.0.0/Fusion 1.3.12 BlueMap add-on for All the Mons
1.2.0. Preserve the exact artifact tuple and stock fallback.

The only BlueMap target is feature-backport commit
`7e07f4e74ec1e92a6ead9aa1e66054af3e133aac`, API commit
`285c9a60eff3ac2b0cab308ce1058d1565be0971`. Compile the four Adapter API
`0.1.0-alpha.2` sources from gitlink commit
`e81f08bc4bfbf02d810ec8949a019130e2e61634`, source tree
`2f974c9bb2ba13888d69682f86f30f58922d30eb`. Never bundle its JAR.

- Own only the three native `crystalix:*` glass IDs.
- Render persisted RGB, invisibility, ordinary/transparent material choice,
  and exact same-block Fusion FULL connectivity.
- Reinforcement, ghost collision, redstone behavior and waterloggability do
  not change the static exterior.
- Do not bundle Crystalix/Fusion resources or implementation classes.
- Crystalix material inside `framedblocks:*` is an explicit integration gap;
  do not create overlapping FramedBlocks renderer registrations casually.
- Run `prototypeCheck` with the exact Crystalix and Fusion JAR properties,
  then the build and publication gates. Use disposable staging for owner comparison.
