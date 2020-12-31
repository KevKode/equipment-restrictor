package com.equipmentrestrictor;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Arrays;

import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;
import net.runelite.client.util.WildcardMatcher;
import net.runelite.http.api.item.ItemEquipmentStats;
import net.runelite.http.api.item.ItemStats;

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

	private static final List<String> EQUIP_MENU_OPTIONS = Arrays.asList("Wield", "Wear", "Equip", "Hold", "Ride", "Chill");

	private List<String> whitelistItems;
	private List<String> blacklistItems;
	private Map<Integer, Boolean> slotLocks;

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		String menuOption = event.getMenuOption();
		if (!EQUIP_MENU_OPTIONS.contains(menuOption))
		{
			return;
		}

		Integer itemId = findItemIdFromWidget(event.getWidgetId(), event.getActionParam());
		if (itemId == null)
		{
			return;
		}

		ItemStats itemStats = itemManager.getItemStats(itemId, true);
		if (itemStats == null || !itemStats.isEquipable())
		{
			return;
		}

		ItemEquipmentStats itemEquipmentStats = itemStats.getEquipment();
		if (itemEquipmentStats == null)
		{
			return;
		}

		String itemName = itemManager.getItemComposition(itemId).getName();
		int itemSlot = itemEquipmentStats.getSlot();
		boolean itemIsTwoHanded = itemEquipmentStats.isTwoHanded();

		if (!canPlayerEquipItem(itemName, itemSlot, itemIsTwoHanded))
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "You can't " + menuOption.toLowerCase() + " restricted item: <col=ff00000>" + itemName + "</col>", null);
			event.consume();
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals(CONFIG_GROUP))
		{
			return;
		}

		switch (event.getKey())
		{
			case WHITELIST:
				whitelistItems = new LinkedList<>(Text.fromCSV(config.getWhitelist()));
				break;
			case BLACKLIST:
				blacklistItems = new LinkedList<>(Text.fromCSV(config.getBlacklist()));
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

	private boolean canPlayerEquipItem(String itemName, int itemSlot, boolean itemIsTwoHanded)
	{
		// The player can equip the item if...
		return (
			isItemInList(itemName, whitelistItems)                           // the item is in the whitelist.
			|| (                                                             // 	(only proceed if the item isn't in the whitelist, it overrides everything)
				!isItemInList(itemName, blacklistItems)                      // the item is not in the black list...
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

	private boolean isItemInList(String itemName, List<String> itemList)
	{
		return itemList.stream().anyMatch(listItemName -> WildcardMatcher.matches(listItemName, itemName));
	}

	private Integer findItemIdFromWidget(int widgetId, int actionParam)
	{
		int widgetGroup = WidgetInfo.TO_GROUP(widgetId);
		int widgetChild = WidgetInfo.TO_CHILD(widgetId);
		Widget widget = client.getWidget(widgetGroup, widgetChild);

		if (widget == null)
		{
			return null;
		}

		if (widgetGroup == WidgetInfo.INVENTORY.getGroupId())
		{
			WidgetItem widgetItem = widget.getWidgetItem(actionParam);
			if (widgetItem != null)
			{
				return widgetItem.getId();
			}
		}
		else if (widgetGroup == WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER.getGroupId())
		{
			Widget widgetItem = widget.getChild(actionParam);
			if (widgetItem != null)
			{
				return widgetItem.getItemId();
			}
		}

		return null;
	}

	@Override
	protected void startUp() throws Exception
	{
		whitelistItems = new LinkedList<>(Text.fromCSV(config.getWhitelist()));
		blacklistItems = new LinkedList<>(Text.fromCSV(config.getBlacklist()));

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
