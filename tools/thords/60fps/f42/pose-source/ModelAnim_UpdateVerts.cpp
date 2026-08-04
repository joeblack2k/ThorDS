// Player-only temporal pose interpolation for the Enhanced 60 Hz profile.
#include "ModelAnim.h"

extern "C" void func_0204531c(ModelComponents *data, s32 weight);

static volatile char **const kEnhancedPlayerModel =
    (volatile char **)(0x02004DF8);

static void update_temporal_pose(ModelAnim *model)
{
    s32 frame = model->currFrame;
    s32 base = frame >> 12;
    s32 alpha = frame & 0xfff;
    u32 count = model->numFramesAndFlags & 0x3fffffff;
    s32 next = base + 1;

    if (count == 0) {
        model->data.UpdateBones(model->file, base);
        model->data.UpdateVertsUsingBones();
        return;
    }
    if ((u32)next >= count) {
        next = (model->numFramesAndFlags & 0x80000000) ? 0 : (s32)count - 1;
        alpha = 0;
    }

    Matrix4x3 *primary = model->data.transforms;
    Matrix4x3 *alternate = (Matrix4x3 *)model->data.unk_10;
    if (primary == 0 || alternate == 0 || alpha == 0) {
        model->data.UpdateBones(model->file, base);
        model->data.UpdateVertsUsingBones();
        return;
    }

    model->data.UpdateBones(model->file, base);
    model->data.transforms = alternate;
    model->data.UpdateBones(model->file, next);
    model->data.transforms = primary;
    func_0204531c(&model->data, alpha);
}

void ModelAnim::UpdateVerts()
{
    if ((char *)this == *kEnhancedPlayerModel) {
        update_temporal_pose(this);
        return;
    }

    s32 frame = currFrame;
    data.UpdateBones(file, (u32)(frame << 4) >> 0x10);
    data.UpdateVertsUsingBones();
}
