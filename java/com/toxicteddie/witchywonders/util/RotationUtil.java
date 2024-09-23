package com.toxicteddie.witchywonders.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

public class RotationUtil {
    public static void rotatePoseStack(PoseStack poseStack, float x, float y, float z) {
        poseStack.mulPose(Axis.XP.rotationDegrees(x));
        poseStack.mulPose(Axis.YP.rotationDegrees(y));
        poseStack.mulPose(Axis.ZP.rotationDegrees(z));
    }
}
