package me.danetnaverno.inventorio.mixin;

import me.danetnaverno.inventorio.MathStuffConstantsKt;
import me.danetnaverno.inventorio.duck.InventoryDuck;
import me.danetnaverno.inventorio.player.PlayerInventoryAddon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This class is genuinely painful to look at and breaks all sorts of OOP conventions,
 *  but hey, this is what you gotta do when you deal with mixins, APIs and mod compatibility.
 */
@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin implements InventoryDuck
{
    @Shadow @Final public PlayerEntity player;
    @Unique public PlayerInventoryAddon addon;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private <E> void createInventoryAddon(PlayerEntity player, CallbackInfo ci)
    {
        addon = new PlayerInventoryAddon((PlayerInventory)(Object)this);
        ((PlayerInventoryAccessor)this).setCombinedInventory(addon.getCombinedInventory());
    }

    @Overwrite
    public int size()
    {
        return addon.size();
    }

    @Overwrite
    public ItemStack getMainHandStack()
    {
        return addon.getMainHandStack();
    }

    @Inject(method = "getEmptySlot", at = @At(value = "RETURN"), cancellable = true)
    public void getEmptySlot(CallbackInfoReturnable<Integer> cir)
    {
        //If no free slot is found in the main inventory, look in the Deep Pockets' extension
        if (cir.getReturnValue() == -1)
            cir.setReturnValue(addon.getEmptyExtensionSlot());
    }

    @Inject(method = "getOccupiedSlotWithRoomForStack", at = @At(value = "RETURN"), cancellable = true)
    public void getOccupiedSlotWithRoomForStack(ItemStack stack, CallbackInfoReturnable<Integer> cir)
    {
        //If no fitting slot is found in the main inventory, look in the Deep Pockets' extension
        if (cir.getReturnValue() == 40 || cir.getReturnValue() == -1)
            cir.setReturnValue(addon.getOccupiedExtensionSlotWithRoomForStack(stack));
    }

    @Overwrite
    public float getBlockBreakingSpeed(BlockState block)
    {
        return addon.getBlockBreakingSpeed(block);
    }

    @Overwrite
    @Environment(EnvType.CLIENT)
    public void scrollInHotbar(double scrollAmount)
    {
        addon.scrollInHotbar(scrollAmount);
    }

    @Overwrite
    public static boolean isValidHotbarIndex(int slot)
    {
        return slot >= 0 && slot < MathStuffConstantsKt.inventorioRowLength;
    }

    @Overwrite
    public static int getHotbarSize()
    {
        return MathStuffConstantsKt.inventorioRowLength;
    }

    @Override
    public PlayerInventoryAddon getAddon()
    {
        return addon;
    }
}
