// Player-only temporal pose interpolation during a blend transition.
#include "BlendModelAnim.h"

extern "C" void func_0204531c(ModelComponents *data, s32 weight);

static volatile char **const kEnhancedPlayerModel =
    (volatile char **)(0x02004DF8);

void BlendModelAnim::UpdateVerts()
{
    if ((char *)this == *kEnhancedPlayerModel && blendWeight >= 0x1000) {
        s32 frame = currFrame;
        s32 base = frame >> 12;
        s32 alpha = frame & 0xfff;
        u32 count = numFramesAndFlags & 0x3fffffff;
        s32 next = base + 1;
        Matrix4x3 *primary = data.transforms;
        Matrix4x3 *alternate = (Matrix4x3 *)data.unk_10;

        if (count != 0 && (u32)next >= count) {
            next = (numFramesAndFlags & 0x80000000) ? 0 : (s32)count - 1;
            alpha = 0;
        }
        if (count != 0 && primary != 0 && alternate != 0 && alpha != 0) {
            data.UpdateBones(file, base);
            data.transforms = alternate;
            data.UpdateBones(file, next);
            data.transforms = primary;
            func_0204531c(&data, alpha);
            return;
        }
    }

    s32 frame = currFrame;
    data.UpdateBones(file, (u32)(frame << 4) >> 0x10);
    if (blendWeight < 0x1000) {
        func_0204531c(&data, blendWeight);
    } else {
        data.UpdateVertsUsingBones();
    }
}
