package com.dairymoose.tooslow.mixins;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {

	private static final Logger LOGGER = LogManager.getLogger();
	
	@Redirect(method="handleMovePlayer", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerPlayer;isChangingDimension()Z",
            ordinal = 0
            ))
	public boolean isChangingDim1(ServerPlayer player) {
		return true;
	}
	
	@Redirect(method="handleMovePlayer", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerPlayer;isChangingDimension()Z",
            ordinal = 1
            ))
	public boolean isChangingDim2(ServerPlayer player) {
		return true;
	}
	
	@Redirect(method="handleMoveVehicle", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;isSingleplayerOwner()Z",
            ordinal = 0
            ))
	public boolean isSingleplayerOwner(ServerGamePacketListenerImpl listener) {
		return true;
	}
	
	@Redirect(method="handleMoveVehicle", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;noCollision(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Z",
            ordinal = 0
            ))
	public boolean noCollision(ServerLevel serverLevel, Entity e, AABB aabb) {
		return false;
	}
	
}
