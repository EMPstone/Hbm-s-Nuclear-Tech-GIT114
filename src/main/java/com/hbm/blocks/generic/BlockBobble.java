package com.hbm.blocks.generic;

import java.util.List;
import java.util.Random;

import com.hbm.items.ModItems;
import com.hbm.main.MainRegistry;

import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockBobble extends BlockContainer {

	public BlockBobble() {
		super(Material.iron);
	}

	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public Item getItemDropped(int i, Random rand, int j) {
		return null;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		
		if(!world.isRemote) {
			TileEntityBobble entity = (TileEntityBobble) world.getTileEntity(x, y, z);
			if(entity != null) {
				EntityItem item = new EntityItem(world, x + 0.5, y, z + 0.5, new ItemStack(this, 1, entity.type.ordinal()));
				item.motionX = 0;
				item.motionY = 0;
				item.motionZ = 0;
				world.spawnEntityInWorld(item);
			}
		}
		
		super.breakBlock(world, x, y, z, block, meta);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		
		if(world.isRemote) {
			FMLNetworkHandler.openGui(player, MainRegistry.instance, ModItems.guiID_item_bobble, world, x, y, z);
			return true;
			
		} else {
			return false;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		
		for(int i = 0; i < BobbleType.values().length; i++)
			list.add(new ItemStack(item, 1, i));
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
		int meta = MathHelper.floor_double((double)((player.rotationYaw + 180.0F) * 16.0F / 360.0F) + 0.5D) & 15;
		world.setBlockMetadataWithNotify(x, y, z, meta, 2);
		
		TileEntityBobble bobble = (TileEntityBobble) world.getTileEntity(x, y, z);
		bobble.type = BobbleType.values()[Math.abs(stack.getItemDamage()) % BobbleType.values().length];
		bobble.markDirty();
	}
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		float f = 0.0625F;
		this.setBlockBounds(5.5F * f, 0.0F, 5.5F * f, 1.0F - 5.5F * f, 0.625F, 1.0F - 5.5F * f);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		this.setBlockBoundsBasedOnState(world, x, y, z);
		return AxisAlignedBB.getBoundingBox(x + this.minX, y + this.minY, z + this.minZ, x + this.maxX, y + this.maxY, z + this.maxZ);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityBobble();
	}

	public static class TileEntityBobble extends TileEntity {
		
		public BobbleType type = BobbleType.NONE;

		@Override
		public boolean canUpdate() {
			return false;
		}

		@Override
		public Packet getDescriptionPacket() {
			NBTTagCompound nbt = new NBTTagCompound();
			this.writeToNBT(nbt);
			return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, nbt);
		}
		
		@Override
		public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
			this.readFromNBT(pkt.func_148857_g());
		}

		@Override
		public void readFromNBT(NBTTagCompound nbt) {
			super.readFromNBT(nbt);
			this.type = BobbleType.values()[Math.abs(nbt.getByte("type")) % BobbleType.values().length];
		}

		@Override
		public void writeToNBT(NBTTagCompound nbt) {
			super.writeToNBT(nbt);
			nbt.setByte("type", (byte) type.ordinal());
		}
	}
	
	public static enum BobbleType {
		
		NONE("null", "null", null, null, false),
		//vault tec bobbleheads
		STRENGTH("Strength", "Strength", null, "It's essential to give your arguments impact.", false),
		PERCEPTION("Perception", "Perception", null, "Only through observation will you perceive weakness.", false),
		ENDURANCE("Endurance", "Endurance", null, "Always be ready to take one for the team.", false),
		CHARISMA("Charisma", "Charisma", null, "Nothing says pizzaz like a winning smile.", false),
		INTELLIGENCE("Intelligence", "Intelligence", null, "It takes the smartest individuals to realize$there's always more to learn.", false),
		AGILITY("Agility", "Agility", null, "Never be afraid to dodge the sensitive issues.", false),
		LUCK("Luck", "Luck", null, "There's only one way to give 110%.", false),
		//contributor bobbles
		BOB("Robert \"The Bobcat\" Katzinsky", "HbMinecraft", "Hbm's Nuclear Tech Mod", "I know where you live, " + System.getProperty("user.name"), false),
		FRIZZLE("Frooz", "Frooz", "Weapon models", "BLOOD IS FUEL", true),
		PU238("Pu-238", "Pu-238", "Improved Tom impact mechanics", null, false),
		VT("VT-6/24", "VT-6/24", "Balefire warhead model and general texturework", "You cannot unfuck a horse.", true),
		DOC("The Doctor", "Doctor17PH", "Russian localization, lunar miner", "Perhaps the moon rocks were too expensive", true),
		//testing garbage. why is she so dumb?
		CIRNO("Cirno", "Cirno", "being a dumb ice fairy", "No brain. Head empty.", true);

		public String name;			//the title of the tooltip
		public String label;		//the name engraved in the socket
		public String contribution;	//what contributions this person has made, if applicable
		public String inscription;	//the flavor text
		public boolean skinLayers;
		
		private BobbleType(String name, String label, String contribution, String inscription, boolean layers) {
			this.name = name;
			this.label = label;
			this.contribution = contribution;
			this.inscription = inscription;
			this.skinLayers = layers;
		}
	}
}