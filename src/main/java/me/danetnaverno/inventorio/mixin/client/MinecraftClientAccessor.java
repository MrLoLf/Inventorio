package me.danetnaverno.inventorio.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MinecraftClient.class)
@Environment(EnvType.CLIENT)
public interface MinecraftClientAccessor
{
    @Invoker("doItemUse")
    void invokeDoItemUse();
}
