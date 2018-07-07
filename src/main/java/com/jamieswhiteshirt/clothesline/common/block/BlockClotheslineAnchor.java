package com.jamieswhiteshirt.clothesline.common.block;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.api.AbsoluteNetworkState;
import com.jamieswhiteshirt.clothesline.api.INetworkManager;
import com.jamieswhiteshirt.clothesline.api.INetworkNode;
import com.jamieswhiteshirt.clothesline.api.Measurements;
import com.jamieswhiteshirt.clothesline.common.ClotheslineItems;
import com.jamieswhiteshirt.clothesline.common.ClotheslineSoundEvents;
import com.jamieswhiteshirt.clothesline.common.tileentity.TileEntityClotheslineAnchor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockClotheslineAnchor extends BlockDirectional {
    private static final AxisAlignedBB AABB_DOWN  = new AxisAlignedBB(6.0D / 16.0D,         0.0D, 6.0D / 16.0D, 10.0D / 16.0D, 12.0D / 16.0D, 10.0D / 16.0D);
    private static final AxisAlignedBB AABB_UP    = new AxisAlignedBB(6.0D / 16.0D, 4.0D / 16.0D, 6.0D / 16.0D, 10.0D / 16.0D,          1.0D, 10.0D / 16.0D);
    private static final AxisAlignedBB AABB_NORTH = new AxisAlignedBB(6.0D / 16.0D,         0.0D, 6.0D / 16.0D, 10.0D / 16.0D, 12.0D / 16.0D,          1.0D);
    private static final AxisAlignedBB AABB_SOUTH = new AxisAlignedBB(6.0D / 16.0D,         0.0D,         0.0D, 10.0D / 16.0D, 12.0D / 16.0D, 10.0D / 16.0D);
    private static final AxisAlignedBB AABB_WEST  = new AxisAlignedBB(6.0D / 16.0D,         0.0D, 6.0D / 16.0D,          1.0D, 12.0D / 16.0D, 10.0D / 16.0D);
    private static final AxisAlignedBB AABB_EAST  = new AxisAlignedBB(        0.0D,         0.0D, 6.0D / 16.0D, 10.0D / 16.0D, 12.0D / 16.0D, 10.0D / 16.0D);
    private static final AxisAlignedBB[] AABB_BY_FACING = new AxisAlignedBB[] { AABB_UP, AABB_DOWN, AABB_NORTH, AABB_SOUTH, AABB_WEST, AABB_EAST };

    public BlockClotheslineAnchor() {
        super(Material.WOOD);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.DOWN));
    }

    @SuppressWarnings("deprecation")
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return AABB_BY_FACING[state.getValue(FACING).getIndex()];
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

        return !isExceptionBlockForAttaching(block) && state.getBlockFaceShape(world, otherPos, facing) == BlockFaceShape.SOLID || facing == EnumFacing.UP && state.getBlock().canPlaceTorchOnTop(state, world, otherPos);
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
                tileEntity.crank(6);
                //world.playSound(player, pos, ClotheslineSoundEvents.PULLEY, SoundCategory.BLOCKS, 0.5F, 0.9F + world.rand.nextFloat() * 0.2F);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        INetworkManager manager = world.getCapability(Clothesline.NETWORK_MANAGER_CAPABILITY, null);
        if (manager != null) {
            manager.destroy(pos);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityClotheslineAnchor) {
            INetworkNode networkNode = ((TileEntityClotheslineAnchor) tileEntity).getNetworkNode();
            if (networkNode != null) {
                AbsoluteNetworkState networkState = networkNode.getNetwork().getState();
                int momentum = networkState.getMomentum();
                float pitch = 0.2F + 0.6F * ((float)momentum / AbsoluteNetworkState.MAX_MOMENTUM) + rand.nextFloat() * 0.1F;
                if (rand.nextInt(12 * AbsoluteNetworkState.MAX_MOMENTUM) < momentum) {
                    world.playSound(Minecraft.getMinecraft().player, pos, ClotheslineSoundEvents.BLOCK_CLOTHESLINE_ANCHOR_SQUEAK, SoundCategory.BLOCKS, 0.1F, pitch);
                }
            }
        }
    }
}
