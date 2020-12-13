package com.equipmentrestrictor;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;
import net.runelite.client.util.WildcardMatcher;
import net.runelite.http.api.item.ItemEquipmentStats;

@Slf4j
@PluginDescriptor(
	name = "Equipment Restrictor",
	description = "Restrict the items the player can equip",
	tags = {"equipment", "items", "gear", "weapon", "offhand", "armor", "armour", "restrict", "ironman"}
)
public class EquipmentRestrictorPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private EquipmentRestrictorConfig config;

	@Inject
	private ItemManager itemManager;

	static final String CONFIG_GROUP = "equipmentrestrictor";
	static final String WHITELIST = "whitelist";
	static final String BLACKLIST = "blacklist";
	static final String WEAPON_SLOT = "weaponSlot";
	static final String SHIELD_SLOT = "shieldSlot";
	static final String HEAD_SLOT = "headSlot";
	static final String BODY_SLOT = "bodySlot";
	static final String LEG_SLOT = "legSlot";
	static final String FEET_SLOT = "feetSlot";
	static final String HAND_SLOT = "handSlot";
	static final String NECK_SLOT = "neckSlot";
	static final String RING_SLOT = "ringSlot";
	static final String CAPE_SLOT = "capeSlot";
	static final String AMMO_SLOT = "ammoSlot";
	static final String ONE_HANDED = "oneHanded";
	static final String TWO_HANDED = "twoHanded";

	private static final String WIELD = "Wield";
	private static final String WEAR = "Wear";

	private List<String> whitelistItems;
	private List<String> blacklistItems;
	private Map<Integer, Boolean> slotLocks;

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		final String menuOption = event.getMenuOption();

		if (menuOption.equals(WIELD) || menuOption.equals(WEAR))
		{
			try
			{
				final String itemName = itemManager.getItemComposition(event.getId()).getName();
				final ItemEquipmentStats itemEquipmentStats = Objects.requireNonNull(itemManager.getItemStats(event.getId(), true)).getEquipment();
				final int itemSlot = itemEquipmentStats.getSlot();
				final boolean itemIsTwoHanded = itemEquipmentStats.isTwoHanded();

				if (!canPlayerEquipItem(itemName, itemSlot, itemIsTwoHanded))
				{
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "You can't \"" + menuOption + "\" restricted item \"" + itemName + "\".", null);
					event.consume();
				}
			}
			catch (NullPointerException ex)
			{
				log.warn("unable to get item's equipment stats", ex);
			}
		}
	}

	private boolean canPlayerEquipItem(String itemName, int itemSlot, boolean itemIsTwoHanded)
	{
		// The player can equip the item if...
		return (
			itemInList(itemName, whitelistItems)                             // the item is in the whitelist.
			|| (                                                             // 	(only proceed if the item isn't in the whitelist, it overrides everything)
				!itemInList(itemName, blacklistItems)                        // the item is not in the black list...
				&&                                                           // 	and... (proceed to check if the item's slot is locked)
				!slotLocks.get(itemSlot)                                     // the items slot is not locked...
				&& (                                                         // 	and... (proceed to check if the item is a weapon, and if it's type is locked)
					(itemSlot != EquipmentInventorySlot.WEAPON.getSlotIdx()) // the item is not a weapon, it does not need to check handedness.
					|| (                                                     //		(only proceed if the item is a weapon, armor isn't subject to handedness)
						itemIsTwoHanded && !config.getTwoHandedLock()        // the item is 2h, and the 2h type is not locked.
					)
					|| (                                                     //		(only proceed if the item isn't 2h, need to check 1h lock)
						!itemIsTwoHanded && !config.getOneHandedLock()       // the item is 1h, and the 1h type is not locked.
					)
				)
			)
		);
	}

	private boolean itemInList(String itemName, List<String> itemList)
	{
		return itemList.stream().anyMatch(listItemName -> WildcardMatcher.matches(listItemName, itemName));
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals(CONFIG_GROUP))
		{
			switch (event.getKey())
			{
				case WHITELIST:
					whitelistItems = Text.fromCSV(config.getWhitelist());
					break;
				case BLACKLIST:
					blacklistItems = Text.fromCSV(config.getBlacklist());
					break;
				case HEAD_SLOT:
					slotLocks.put(EquipmentInventorySlot.HEAD.getSlotIdx(), config.getHeadSlotLock());
					break;
				case CAPE_SLOT:
					slotLocks.put(EquipmentInventorySlot.CAPE.getSlotIdx(), config.getCapeSlotLock());
					break;
				case NECK_SLOT:
					slotLocks.put(EquipmentInventorySlot.AMULET.getSlotIdx(), config.getNeckSlotLock());
					break;
				case WEAPON_SLOT:
					slotLocks.put(EquipmentInventorySlot.WEAPON.getSlotIdx(), config.getWeaponSlotLock());
					break;
				case BODY_SLOT:
					slotLocks.put(EquipmentInventorySlot.BODY.getSlotIdx(), config.getBodySlotLock());
					break;
				case SHIELD_SLOT:
					slotLocks.put(EquipmentInventorySlot.SHIELD.getSlotIdx(), config.getShieldSlotLock());
					break;
				case LEG_SLOT:
					slotLocks.put(EquipmentInventorySlot.LEGS.getSlotIdx(), config.getLegSlotLock());
					break;
				case HAND_SLOT:
					slotLocks.put(EquipmentInventorySlot.GLOVES.getSlotIdx(), config.getHandSlotLock());
					break;
				case FEET_SLOT:
					slotLocks.put(EquipmentInventorySlot.BOOTS.getSlotIdx(), config.getFeetSlotLock());
					break;
				case RING_SLOT:
					slotLocks.put(EquipmentInventorySlot.RING.getSlotIdx(), config.getRingSlotLock());
					break;
				case AMMO_SLOT:
					slotLocks.put(EquipmentInventorySlot.AMMO.getSlotIdx(), config.getAmmoSlotLock());
					break;
			}
		}
	}

	@Override
	protected void startUp() throws Exception
	{
		whitelistItems = Text.fromCSV(config.getWhitelist());
		blacklistItems = Text.fromCSV(config.getBlacklist());

		slotLocks = new HashMap<>();
		slotLocks.put(EquipmentInventorySlot.HEAD.getSlotIdx(), config.getHeadSlotLock());
		slotLocks.put(EquipmentInventorySlot.CAPE.getSlotIdx(), config.getCapeSlotLock());
		slotLocks.put(EquipmentInventorySlot.AMULET.getSlotIdx(), config.getNeckSlotLock());
		slotLocks.put(EquipmentInventorySlot.WEAPON.getSlotIdx(), config.getWeaponSlotLock());
		slotLocks.put(EquipmentInventorySlot.BODY.getSlotIdx(), config.getBodySlotLock());
		slotLocks.put(EquipmentInventorySlot.SHIELD.getSlotIdx(), config.getShieldSlotLock());
		slotLocks.put(EquipmentInventorySlot.LEGS.getSlotIdx(), config.getLegSlotLock());
		slotLocks.put(EquipmentInventorySlot.GLOVES.getSlotIdx(), config.getHandSlotLock());
		slotLocks.put(EquipmentInventorySlot.BOOTS.getSlotIdx(), config.getFeetSlotLock());
		slotLocks.put(EquipmentInventorySlot.RING.getSlotIdx(), config.getRingSlotLock());
		slotLocks.put(EquipmentInventorySlot.AMMO.getSlotIdx(), config.getAmmoSlotLock());
	}

	@Override
	protected void shutDown() throws Exception
	{
		whitelistItems.clear();
		blacklistItems.clear();
		slotLocks.clear();
	}

	@Provides
	EquipmentRestrictorConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(EquipmentRestrictorConfig.class);
	}
}
