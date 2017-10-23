package com.jamieswhiteshirt.clothesline.common.block;

import com.jamieswhiteshirt.clothesline.api.INetworkManager;
import com.jamieswhiteshirt.clothesline.common.ClotheslineItems;
import com.jamieswhiteshirt.clothesline.common.tileentity.TileEntityClotheslineAnchor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import javax.annotation.Nullable;

public class BlockClotheslineAnchor extends BlockDirectional {
    @CapabilityInject(INetworkManager.class)
    private static final Capability<INetworkManager> NETWORK_MANAGER_CAPABILITY = null;

    private static final AxisAlignedBB AABB_DOWN  = new AxisAlignedBB(7.0D / 16.0D,         0.0D, 7.0D / 16.0D, 9.0D / 16.0D, 11.0D / 16.0D, 9.0D / 16.0D);
    private static final AxisAlignedBB AABB_UP    = new AxisAlignedBB(7.0D / 16.0D, 5.0D / 16.0D, 7.0D / 16.0D, 9.0D / 16.0D,          1.0D, 9.0D / 16.0D);
    private static final AxisAlignedBB AABB_NORTH = new AxisAlignedBB(7.0D / 16.0D,         0.0D, 7.0D / 16.0D, 9.0D / 16.0D, 11.0D / 16.0D,         1.0D);
    private static final AxisAlignedBB AABB_SOUTH = new AxisAlignedBB(7.0D / 16.0D,         0.0D,         0.0D, 9.0D / 16.0D, 11.0D / 16.0D, 9.0D / 16.0D);
    private static final AxisAlignedBB AABB_WEST  = new AxisAlignedBB(7.0D / 16.0D,         0.0D, 7.0D / 16.0D,         1.0D, 11.0D / 16.0D, 9.0D / 16.0D);
    private static final AxisAlignedBB AABB_EAST  = new AxisAlignedBB(        0.0D,         0.0D, 7.0D / 16.0D, 9.0D / 16.0D, 11.0D / 16.0D, 9.0D / 16.0D);
    private static final AxisAlignedBB[] AABB_ARRAY = new AxisAlignedBB[] { AABB_UP, AABB_DOWN, AABB_NORTH, AABB_SOUTH, AABB_WEST, AABB_EAST };

    public BlockClotheslineAnchor() {
        super(Material.WOOD);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.DOWN));
    }

    @SuppressWarnings("deprecation")
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return AABB_ARRAY[state.getValue(FACING).getIndex()];
    }

    @SuppressWarnings("deprecation")
    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        for (EnumFacing enumfacing : FACING.getAllowedValues()) {
            if (canPlaceBlockOnSide(world, pos, enumfacing)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing facing) {
        BlockPos otherPos = pos.offset(facing.getOpposite());
        IBlockState state = world.getBlockState(otherPos);
        Block block = state.getBlock();

        return !isExceptionBlockForAttaching(block) && state.getBlockFaceShape(world, otherPos, facing) == BlockFaceShape.SOLID || facing == EnumFacing.UP && state.isSideSolid(world, pos, facing);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return canPlaceBlockOnSide(world, pos, facing) ? getDefaultState().withProperty(FACING, facing) : getDefaultState().withProperty(FACING, EnumFacing.DOWN);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        if (!canPlaceBlockOnSide(world, pos, state.getValue(FACING))) {
            dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.getFront(meta));
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState withRotation(IBlockState state, Rotation rotation) {
        return state.withProperty(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirror) {
        return state.withProperty(FACING, mirror.mirror(state.getValue(FACING)));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityClotheslineAnchor();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItem() != ClotheslineItems.CLOTHESLINE) {
            TileEntityClotheslineAnchor tileEntity = (TileEntityClotheslineAnchor)world.getTileEntity(pos);
            if (tileEntity != null) {
                tileEntity.crank(8);
            }
            return true;
        } else {
            return false;
        }
    }

    private void onDestroyed(World world, BlockPos pos) {
        //TODO: This does not handle all cases by far, we need a world event listener.
        if (!world.isRemote) {
            INetworkManager manager = world.getCapability(NETWORK_MANAGER_CAPABILITY, null);
            if (manager != null) {
                manager.destroy(pos);
            }
        }
    }

    @Override
    public void onBlockDestroyedByPlayer(World world, BlockPos pos, IBlockState state) {
        onDestroyed(world, pos);
    }

    @Override
    public void onBlockDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
        onDestroyed(world, pos);
    }
}
