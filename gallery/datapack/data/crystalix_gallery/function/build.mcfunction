function crystalix_gallery:clear
fill 192 99 204 250 99 220 smooth_stone

# 1: Stock translucent and opaque controls.
setblock 196 100 207 minecraft:white_stained_glass
setblock 200 100 207 minecraft:red_stained_glass
setblock 204 100 207 minecraft:amethyst_block

# 2: Isolated native forms with their transparent texture branch.
setblock 196 100 212 crystalix:crystalix_glass[transparent=true]
setblock 200 100 212 crystalix:clear_crystalix_glass[transparent=true]
setblock 204 100 212 crystalix:bordered_crystalix_glass[transparent=true]

# 3: Persisted red, green, and blue tint on the colored texture branch.
setblock 196 100 217 crystalix:crystalix_glass[transparent=false]
data merge block 196 100 217 {color:16711680}
setblock 200 100 217 crystalix:clear_crystalix_glass[transparent=false]
data merge block 200 100 217 {color:65280}
setblock 204 100 217 crystalix:bordered_crystalix_glass[transparent=false]
data merge block 204 100 217 {color:255}

# 4: Clear FULL sheet with edge, corner, center, and diagonal selections.
fill 210 100 206 212 100 208 crystalix:clear_crystalix_glass[transparent=true]
setblock 210 100 211 crystalix:clear_crystalix_glass[transparent=true]
setblock 211 100 212 crystalix:clear_crystalix_glass[transparent=true]
setblock 212 100 211 crystalix:clear_crystalix_glass[transparent=true]

# 5: Bordered FULL sheet in a colored connected patch.
fill 216 100 206 218 100 208 crystalix:bordered_crystalix_glass[transparent=false]
data merge block 216 100 206 {color:16763904}
data merge block 217 100 206 {color:16763904}
data merge block 218 100 206 {color:16763904}
data merge block 216 100 207 {color:16763904}
data merge block 217 100 207 {color:16763904}
data merge block 218 100 207 {color:16763904}
data merge block 216 100 208 {color:16763904}
data merge block 217 100 208 {color:16763904}
data merge block 218 100 208 {color:16763904}

# 6: Connection boundaries: transparent mismatch and an invisible center.
setblock 224 100 207 crystalix:clear_crystalix_glass[transparent=true]
setblock 225 100 207 crystalix:clear_crystalix_glass[transparent=false]
setblock 228 100 207 crystalix:bordered_crystalix_glass[invisible=false,transparent=true]
setblock 229 100 207 crystalix:bordered_crystalix_glass[invisible=true,transparent=true]
setblock 230 100 207 crystalix:bordered_crystalix_glass[invisible=false,transparent=true]
data merge block 228 100 207 {color:65535}
data merge block 229 100 207 {color:65535}
data merge block 230 100 207 {color:65535}

# 7: Stable state variants; shadeless is geometry-identical, fake light is bright.
setblock 236 100 207 crystalix:crystalix_glass[shadeless=false,light=none]
setblock 238 100 207 crystalix:crystalix_glass[shadeless=true,light=none]
setblock 240 100 207 crystalix:crystalix_glass[shadeless=false,light=light]
setblock 242 100 207 crystalix:crystalix_glass[shadeless=false,light=fake_light]
setblock 244 100 207 crystalix:crystalix_glass[shadeless=false,light=dark]

scoreboard players set #ready crystalix_gallery 1
tellraw @a [{"text":"Crystalix gallery ready: ","color":"aqua"},{"text":"/function crystalix_gallery:pose","color":"yellow"}]
