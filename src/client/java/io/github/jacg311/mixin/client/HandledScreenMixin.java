package io.github.jacg311.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.jacg311.DebugDelights;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public class HandledScreenMixin extends Screen {
    protected HandledScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "drawSlot", at = @At("HEAD"))
    private void debugdelights$drawSlotId(MatrixStack matrices, Slot slot, CallbackInfo ci) {
        if (DebugDelights.CONFIG.shouldDrawSlotIds) {
            matrices.push();
            matrices.translate(0, 0, 500);
            this.textRenderer.draw(matrices, String.valueOf(slot.getIndex()), slot.x, slot.y, 0xFFFFFF);
            matrices.pop();
        }
    }

    /**
     * Make sure Tooltips are drawn above the slot id
    **/
    @WrapOperation(method = "drawMouseoverTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/item/ItemStack;II)V"))
    private void debugdelights$translateToolTipZCoordinate(HandledScreen<?> handledScreen, MatrixStack matrices, ItemStack stack, int x, int y, Operation<Void> original) {
        if (DebugDelights.CONFIG.shouldDrawSlotIds) {
            matrices.push();
            matrices.translate(0, 0, 550);
            original.call(handledScreen, matrices, stack, x, y);
            matrices.pop();
        }
    }
}
