package com.mcmoddev.ironagefurniture.api.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.List;

public class Seat extends Entity
{
    private BlockPos source;

    public Seat(World world)
    {
        super(Entities.SEAT, world);
        this.noClip = true;
    }

    private Seat(World world, BlockPos source, double yOffset)
    {
        this(world);
        this.source = source;
        this.setPosition(source.getX() + 0.5, source.getY() + yOffset, source.getZ() + 0.5);
    }

    @Override
    protected void registerData() {}

    @Override
    public void tick()
    {
        super.tick();
        if(source == null)
        {
            source = this.getPosition();
        }
        if(!this.world.isRemote)
        {
            if(this.getPassengers().isEmpty() || this.world.isAirBlock(source))
            {
                this.remove();
                world.updateComparatorOutputLevel(getPosition(), world.getBlockState(getPosition()).getBlock());
            }
        }
    }

    @Override
    protected void readAdditional(CompoundNBT compound)
    {

    }

    @Override
    protected void writeAdditional(CompoundNBT compound)
    {

    }

    @Override
    public double getMountedYOffset()
    {
        return 0.0;
    }

    public BlockPos getSource()
    {
        return source;
    }

    @Override
    protected boolean canBeRidden(Entity entity)
    {
        return true;
    }

    @Override
    public IPacket<?> createSpawnPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public static boolean create(World world, BlockPos pos, double yOffset, PlayerEntity player)
    {
        if(!world.isRemote)
        {
            List<Seat> seats = world.getEntitiesWithinAABB(Seat.class, new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1.0, pos.getY() + 1.0, pos.getZ() + 1.0));
            
            if(seats.isEmpty())
            {
                Seat seat = new Seat(world, pos, yOffset);
                world.addEntity(seat);
                player.startRiding(seat, false);
            }
        }
        
        return true;
    }
}