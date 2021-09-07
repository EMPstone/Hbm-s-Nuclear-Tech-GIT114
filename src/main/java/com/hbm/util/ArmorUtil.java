package com.hbm.util;

import java.util.ArrayList;
import java.util.List;

import com.hbm.handler.ArmorModHandler;
import com.hbm.handler.HazmatRegistry;
import com.hbm.items.ModItems;
import com.hbm.lib.Library;
import com.hbm.potion.HbmPotion;
import com.hbm.util.ArmorRegistry.HazardClass;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.ForgeEventFactory;

public class ArmorUtil {
	
	/*
	 * The less horrifying part
	 */
	
	public static void register() {
		ArmorRegistry.registerHazard(ModItems.gas_mask_filter_mono, HazardClass.PARTICLE_COARSE, HazardClass.GAS_MONOXIDE);
		ArmorRegistry.registerHazard(ModItems.gas_mask_filter, HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE, HazardClass.GAS_CHLORINE, HazardClass.BACTERIA);
	}
	
	public static boolean checkArmor(EntityPlayer player, Item... armor) {
		
		for(int i = 0; i < 4; i++) {
			if(!checkArmorPiece(player, armor[i], 3 - i))
				return false;
		}
		
		return true;
	}
	
	public static boolean checkArmorPiece(EntityPlayer player, Item armor, int slot) {
		return !checkArmorNull(player, slot) && player.inventory.armorInventory[slot].getItem() == armor;
	}
	
	public static boolean checkArmorNull(EntityPlayer player, int slot) {
		return player.inventory.armorInventory[slot] == null;
	}
	
	public static void damageSuit(EntityPlayer player, int slot, int amount) {
		
		if(player.inventory.armorInventory[slot] == null)
			return;
		
		int j = player.inventory.armorInventory[slot].getItemDamage();
		player.inventory.armorInventory[slot].setItemDamage(j += amount);

		if(player.inventory.armorInventory[slot].getItemDamage() > player.inventory.armorInventory[slot].getMaxDamage())
			player.inventory.armorInventory[slot] = null;
	}
	
	public static void resetFlightTime(EntityPlayer player) {
		
		if(player instanceof EntityPlayerMP) {
			EntityPlayerMP mp = (EntityPlayerMP) player;
			ReflectionHelper.setPrivateValue(NetHandlerPlayServer.class, mp.playerNetServerHandler, 0, "floatingTickCount", "field_147365_f");
		}
	}
	
	/*
	 * The more horrifying part
	 */
	public static boolean checkForHazmat(EntityPlayer player) {
		
		if(checkArmor(player, ModItems.hazmat_helmet, ModItems.hazmat_plate, ModItems.hazmat_legs, ModItems.hazmat_boots) || 
				checkArmor(player, ModItems.hazmat_helmet_red, ModItems.hazmat_plate_red, ModItems.hazmat_legs_red, ModItems.hazmat_boots_red) || 
				checkArmor(player, ModItems.hazmat_helmet_grey, ModItems.hazmat_plate_grey, ModItems.hazmat_legs_grey, ModItems.hazmat_boots_grey) || 
				checkArmor(player, ModItems.t45_helmet, ModItems.t45_plate, ModItems.t45_legs, ModItems.t45_boots) || 
				checkArmor(player, ModItems.schrabidium_helmet, ModItems.schrabidium_plate, ModItems.schrabidium_legs, ModItems.schrabidium_boots) || 
				checkForHaz2(player)) {
			
			return true;
		}
		
		if(player.isPotionActive(HbmPotion.mutation))
			return true;
		
		return false;
	}
	
	public static boolean checkForHaz2(EntityPlayer player) {
		
		if(checkArmor(player, ModItems.hazmat_paa_helmet, ModItems.hazmat_paa_plate, ModItems.hazmat_paa_legs, ModItems.hazmat_paa_boots) || 
				checkArmor(player, ModItems.liquidator_helmet, ModItems.liquidator_plate, ModItems.liquidator_legs, ModItems.liquidator_boots) || 
				checkArmor(player, ModItems.euphemium_helmet, ModItems.euphemium_plate, ModItems.euphemium_legs, ModItems.euphemium_boots))
		{
			return true;
		}
		
		return false;
	}
	
	public static boolean checkForAsbestos(EntityPlayer player) {
		
		if(checkArmor(player, ModItems.asbestos_helmet, ModItems.asbestos_plate, ModItems.asbestos_legs, ModItems.asbestos_boots))
			return true;

		return false;
	}
	
	public static boolean checkForDigamma(EntityPlayer player) {
		
		if(checkArmor(player, ModItems.fau_helmet, ModItems.fau_plate, ModItems.fau_legs, ModItems.fau_boots))
			return true;
		
		if(checkArmor(player, ModItems.dns_helmet, ModItems.dns_plate, ModItems.dns_legs, ModItems.dns_boots))
			return true;
		
		if(player.isPotionActive(HbmPotion.stability.id))
			return true; 
		
		return false;
	}
	
	public static boolean checkForDigamma2(EntityPlayer player) {
		
		if(!checkArmor(player, ModItems.robes_helmet, ModItems.robes_plate, ModItems.robes_legs, ModItems.robes_boots))
			return false;
		
		if(player.isPotionActive(HbmPotion.stability.id))
			return true;
		
		for(int i = 0; i < 4; i++) {
			
			ItemStack armor = player.getCurrentArmor(i);
			
			if(armor != null && ArmorModHandler.hasMods(armor)) {
				
				ItemStack mods[] = ArmorModHandler.pryMods(armor);
				
				if(!(mods[ArmorModHandler.cladding] != null && mods[ArmorModHandler.cladding].getItem() == ModItems.cladding_iron))
					return false;
			}
		}
		
		return player.getMaxHealth() < 3;
	}
	
	public static boolean checkForFaraday(EntityPlayer player) {
		
		ItemStack[] armor = player.inventory.armorInventory;
		
		if(armor[0] == null || armor[1] == null || armor[2] == null || armor[3] == null) return false;
		
		if(isFaradayArmor(armor[0]) && isFaradayArmor(armor[1]) && isFaradayArmor(armor[2]) && isFaradayArmor(armor[3]))
			return true;
		
		return false;
	}
	
	public static final String[] metals = new String[] {
			"chainmail",
			"iron",
			"silver",
			"gold",
			"platinum",
			"tin",
			"lead",
			"liquidator",
			"schrabidium",
			"euphemium",
			"steel",
			"cmb",
			"titanium",
			"alloy",
			"copper",
			"bronze",
			"electrum",
			"t45",
			"bj",
			"starmetal",
			"hazmat", //also count because rubber is insulating
			"rubber",
			"hev",
			"ajr",
			"spacesuit"
	};
	
	public static boolean isFaradayArmor(ItemStack item) {
		
		String name = item.getUnlocalizedName();
		
		for(String metal : metals) {
			
			if(name.toLowerCase().contains(metal))
				return true;
		}
		
		if(HazmatRegistry.getCladding(item) > 0)
			return true;
		
		return false;
	}
	
	@Deprecated
	public static boolean checkForGasMask(EntityPlayer player) {

		if(checkArmorPiece(player, ModItems.hazmat_helmet, 3)) {
			return true;
		}
		if(checkArmorPiece(player, ModItems.hazmat_helmet_red, 3)) {
			return true;
		}
		if(checkArmorPiece(player, ModItems.hazmat_helmet_grey, 3)) {
			return true;
		}
		if(checkArmorPiece(player, ModItems.hazmat_paa_helmet, 3)) {
			return true;
		}
		if(checkArmorPiece(player, ModItems.liquidator_helmet, 3)) {
			return true;
		}
		if(checkArmorPiece(player, ModItems.gas_mask, 3)) {
			return true;
		}
		if(checkArmorPiece(player, ModItems.gas_mask_m65, 3)) {
			return true;
		}
		if(checkArmorPiece(player, ModItems.t45_helmet, 3)) {
			return true;
		}
		if(checkArmorPiece(player, ModItems.ajr_helmet, 3)) {
			return true;
		}
		if(checkArmorPiece(player, ModItems.ajro_helmet, 3)) {
			return true;
		}
		if(checkArmorPiece(player, ModItems.hev_helmet, 3)) {
			return true;
		}
		if(checkArmorPiece(player, ModItems.fau_helmet, 3)) {
			return true;
		}
		if(checkArmorPiece(player, ModItems.dns_helmet, 3)) {
			return true;
		}
		if(checkArmorPiece(player, ModItems.schrabidium_helmet, 3)) {
			return true;
		}
		if(checkArmorPiece(player, ModItems.euphemium_helmet, 3)) {
			return true;
		}
		
		if(player.isPotionActive(HbmPotion.mutation))
			return true;
		
		ItemStack helmet = player.getCurrentArmor(3);
		if(helmet != null && ArmorModHandler.hasMods(helmet)) {
			
			ItemStack mods[] = ArmorModHandler.pryMods(helmet);
			
			if(mods[ArmorModHandler.helmet_only] != null && mods[ArmorModHandler.helmet_only].getItem() == ModItems.attachment_mask)
				return true;
		}
		
		return false;
	}
	
	@Deprecated
	public static boolean checkForMonoMask(EntityPlayer player) {

		if(checkArmorPiece(player, ModItems.gas_mask_mono, 3))
			return true;
		
		if(checkArmorPiece(player, ModItems.liquidator_helmet, 3))
			return true;
		
		if(checkArmorPiece(player, ModItems.fau_helmet, 3))
			return true;
		
		if(checkArmorPiece(player, ModItems.dns_helmet, 3))
			return true;

		if(player.isPotionActive(HbmPotion.mutation))
			return true;
		
		ItemStack helmet = player.getCurrentArmor(3);
		if(helmet != null && ArmorModHandler.hasMods(helmet)) {
			
			ItemStack mods[] = ArmorModHandler.pryMods(helmet);
			
			if(mods[ArmorModHandler.helmet_only] != null && mods[ArmorModHandler.helmet_only].getItem() == ModItems.attachment_mask_mono)
				return true;
		}
		
		return false;
	}
	
	public static boolean checkForGoggles(EntityPlayer player) {

		if(checkArmorPiece(player, ModItems.goggles, 3)) {
			return true;
		}
		if(checkArmorPiece(player, ModItems.ashglasses, 3)) {
			return true;
		}
		if(checkArmorPiece(player, ModItems.hazmat_helmet_red, 3)) {
			return true;
		}
		if(checkArmorPiece(player, ModItems.hazmat_helmet_grey, 3)) {
			return true;
		}
		if(checkArmorPiece(player, ModItems.liquidator_helmet, 3)) {
			return true;
		}
		if(checkArmorPiece(player, ModItems.gas_mask, 3)) {
			return true;
		}
		if(checkArmorPiece(player, ModItems.t45_helmet, 3)) {
			return true;
		}
		if(checkArmorPiece(player, ModItems.ajr_helmet, 3)) {
			return true;
		}
		if(checkArmorPiece(player, ModItems.ajro_helmet, 3)) {
			return true;
		}
		if(checkArmorPiece(player, ModItems.bj_helmet, 3)) {
			return true;
		}
		if(checkArmorPiece(player, ModItems.hev_helmet, 3)) {
			return true;
		}
		if(checkArmorPiece(player, ModItems.fau_helmet, 3)) {
			return true;
		}
		if(checkArmorPiece(player, ModItems.hev_helmet, 3)) {
			return true;
		}
		
		return false;
	}
	
	public static boolean checkForFiend(EntityPlayer player) {
		
		return checkArmorPiece(player, ModItems.jackt, 2) && Library.checkForHeld(player, ModItems.shimmer_sledge);
	}
	
	public static boolean checkForFiend2(EntityPlayer player) {
		
		return checkArmorPiece(player, ModItems.jackt2, 2) && Library.checkForHeld(player, ModItems.shimmer_axe);
	}
	
	/*
	 * Default implementations for IGasMask items
	 */
	public static final String FILTERK_KEY = "hfrFilter";
	
	public static void installGasMaskFilter(ItemStack mask, ItemStack filter) {
		
		if(mask == null || filter == null)
			return;
		
		if(!mask.hasTagCompound())
			mask.stackTagCompound = new NBTTagCompound();
		
		NBTTagCompound attach = new NBTTagCompound();
		filter.writeToNBT(attach);
		
		mask.stackTagCompound.setTag(FILTERK_KEY, attach);
	}
	
	public static void removeFilter(ItemStack mask) {
		
		if(mask == null)
			return;
		
		if(!mask.hasTagCompound())
			return;
		
		mask.stackTagCompound.removeTag(FILTERK_KEY);
	}
	
	public static ItemStack getGasMaskFilter(ItemStack mask) {
		
		if(mask == null)
			return null;
		
		if(!mask.hasTagCompound())
			return null;
		
		NBTTagCompound attach = mask.stackTagCompound.getCompoundTag(FILTERK_KEY);
		ItemStack filter = ItemStack.loadItemStackFromNBT(attach);
		
		return filter;
	}
	
	public static void damageGasMaskFilter(ItemStack mask, int damage) {
		ItemStack filter = getGasMaskFilter(mask);
		
		if(filter.getMaxDamage() == 0)
			return;
		
		filter.setItemDamage(filter.getItemDamage() + damage);
		
		if(filter.getItemDamage() > filter.getMaxDamage())
			removeFilter(mask);
		else
			installGasMaskFilter(mask, filter);
	}
	
	public static void addGasMaskTooltip(ItemStack mask, EntityPlayer player, List list, boolean ext) {
		
		ItemStack filter = getGasMaskFilter(mask);
		
		if(filter == null)
			return;
		
		list.add(EnumChatFormatting.GOLD + "Installed filter:");
		
		List<String> lore = new ArrayList();
		filter.getItem().addInformation(filter, player, lore, ext);
		ForgeEventFactory.onItemTooltip(filter, player, lore, ext);
		lore.forEach(x -> list.add(EnumChatFormatting.YELLOW + "  " + x));
	}
}
