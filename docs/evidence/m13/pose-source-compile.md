# Pose Source Compile Evidence

Compiler: `mwccarm 2004/b56`

The checked-in pose fragments compile successfully with the pinned compiler.
The source is an intentional replacement, so it does not byte-match the
original functions.

| Function | Original size | Compiled size | Result |
|---|---:|---:|---|
| `Player_AdvanceAnims` at `0x020BEDD4` | `0xF8` | `0x100` | Compiles; replacement |
| `_ZN9ModelAnim11UpdateVertsEv` at `0x0201686C` | `0x30` | `0x50` | Compiles; replacement |
| `_ZN14BlendModelAnim11UpdateVertsEv` at `0x02016578` | `0x4C` | `0x108` | Compiles; replacement |

The matching tool reported no byte match for the three replacements. This is
not a failure of source compilation. It is a required warning that the
replacement functions need a relocation-aware payload builder, exact hook
guards, and range/continuation verification before runtime use.

No Action Replay payload was generated or enabled from this compile.

