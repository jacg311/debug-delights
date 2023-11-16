package io.github.jacg311.mixin.client;

import io.github.jacg311.DebugDelights;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin extends Screen {

    protected HandledScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "drawSlot", at = @At("HEAD"))
    private void debugdelights$drawSlotId(DrawContext context, Slot slot, CallbackInfo ci) {
        if (DebugDelights.CONFIG.shouldDrawSlotIds()) {
            MatrixStack matrices = context.getMatrices();
            matrices.push();
            matrices.translate(0, 0, 500);
            context.drawText(this.textRenderer, String.valueOf(slot.getIndex()), slot.x, slot.y, 0xFFFFFF, false);
            matrices.pop();
        }
    }

    @Inject(method = "drawMouseoverTooltip", at = @At("HEAD"))
    private void debugdelights$translateToolTipZCoordinate(DrawContext context, int x, int y, CallbackInfo ci) {
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(0, 0, 550);
    }

    @Inject(method = "drawMouseoverTooltip", at = @At("TAIL"))
    private void debugdelights$translateToolTipZCoordinate2(DrawContext context, int x, int y, CallbackInfo ci) {
        MatrixStack matrices = context.getMatrices();
        matrices.pop();
    }
}
