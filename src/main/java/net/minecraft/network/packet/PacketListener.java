// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

public abstract class PacketListener
{
    public abstract boolean isServerPacketListener();
    
    public void handleBlockRegionUpdate(final BlockRegionUpdatePacket packet) {}
    public void onUnhandledPacket(final Packet packet) {}
    public void onDisconnect(final String reason, final Object[] reasonObjects) {}
    public void handleDisconnect(final DisconnectPacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleLogin(final LoginPacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleMovePlayer(final MovePlayerPacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleChunkTilesUpdate(final ChunkTilesUpdatePacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handlePlayerAction(final PlayerActionPacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleTileUpdate(final TileUpdatePacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleChunkVisibility(final ChunkVisibilityPacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleAddPlayer(final AddPlayerPacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleMoveEntity(final MoveEntityPacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleTeleportEntity(final TeleportEntityPacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleUseItem(final UseItemPacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleSetCarriedItem(final SetCarriedItemPacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleRemoveEntity(final RemoveEntityPacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleAddItemEntity(final AddItemEntityPacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleTakeItemEntity(final TakeItemEntityPacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleChat(final ChatPacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleAddEntity(final AddEntityPacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleAnimate(final AnimatePacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handlePlayerCommand(final PlayerCommandPacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handlePreLogin(final PreLoginPacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleAddMob(final AddMobPacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleSetTime(final SetTimePacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleSetSpawn(final SetSpawnPositionPacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleSetEntityMotion(final SetEntityMotionPacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleSetEntityData(final SetEntityDataPacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleRidePacket(final SetRidingPacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleInteract(final InteractPacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleEntityEvent(final EntityEventPacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleSetHealth(final SetHealthPacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleRespawn(final RespawnPacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleExplosion(final ExplodePacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleContainerOpen(final ContainerOpenPacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleContainerClose(final ContainerClosePacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleContainerClick(final ContainerClickPacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleContainerSetSlot(final ContainerSetSlotPacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleContainerContent(final ContainerSetContentPacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleSignUpdate(final SignUpdatePacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleContainerSetData(final ContainerSetDataPacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleSetEquippedItem(final SetEquippedItemPacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleContainerAck(final ContainerAckPacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleAddPainting(final AddPaintingPacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleTileEvent(final TileEventPacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleAwardStat(final AwardStatPacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleEntityActionAtPosition(final EntityActionAtPositionPacket packet) {this.onUnhandledPacket(packet);}
    public void handlePlayerInput(final PlayerInputPacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleGameEvent(final GameEventPacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleAddGlobalEntity(final AddGlobalEntityPacketPacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleComplexItemData(final ComplexItemDataPacket packet) {
        this.onUnhandledPacket(packet);
    }
    public void handleLevelEvent(final LevelEventPacket packet) {
        this.onUnhandledPacket(packet);
    }
}
