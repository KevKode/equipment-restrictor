package com.equipmentrestrictor;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(EquipmentRestrictorPlugin.CONFIG_GROUP)
public interface EquipmentRestrictorConfig extends Config
{
	@ConfigItem(
		keyName = EquipmentRestrictorPlugin.WHITELIST,
		name = "Whitelist",
		description = "Items that will always be equipped, overrides blacklist and all other locks",
		position = 1
	)
	default String getWhitelist()
	{
		return "";
	}

	@ConfigItem(
		keyName = EquipmentRestrictorPlugin.BLACKLIST,
		name = "Blacklist",
		description = "Items that will never be equipped, overrides all other locks",
		position = 2
	)
	default String getBlacklist()
	{
		return "";
	}

	@ConfigSection(
		name = "Equipment Slot Locks",
		description = "Locks an equipment slot, blocking items from being equipped there (✓ = locked)",
		position = 3
	)
	String equipmentSlotLocks = "equipmentSlotLocks";

	@ConfigItem(
		keyName = EquipmentRestrictorPlugin.WEAPON_SLOT,
		name = "Weapon Slot",
		description = "Locks the weapon slot",
		position = 1,
		section = equipmentSlotLocks
	)
	default boolean getWeaponSlotLock()
	{
		return false;
	}

	@ConfigItem(
		keyName = EquipmentRestrictorPlugin.SHIELD_SLOT,
		name = "Shield Slot",
		description = "Locks the shield slot",
		position = 2,
		section = equipmentSlotLocks
	)
	default boolean getShieldSlotLock()
	{
		return false;
	}

	@ConfigItem(
		keyName = EquipmentRestrictorPlugin.HEAD_SLOT,
		name = "Head Slot",
		description = "Locks the head slot",
		position = 3,
		section = equipmentSlotLocks
	)
	default boolean getHeadSlotLock()
	{
		return false;
	}

	@ConfigItem(
		keyName = EquipmentRestrictorPlugin.BODY_SLOT,
		name = "Body Slot",
		description = "Locks the body slot",
		position = 4,
		section = equipmentSlotLocks
	)
	default boolean getBodySlotLock()
	{
		return false;
	}

	@ConfigItem(
		keyName = EquipmentRestrictorPlugin.LEG_SLOT,
		name = "Leg Slot",
		description = "Locks the leg slot",
		position = 5,
		section = equipmentSlotLocks
	)
	default boolean getLegSlotLock()
	{
		return false;
	}

	@ConfigItem(
		keyName = EquipmentRestrictorPlugin.FEET_SLOT,
		name = "Feet Slot",
		description = "Locks the feet slot",
		position = 6,
		section = equipmentSlotLocks
	)
	default boolean getFeetSlotLock()
	{
		return false;
	}

	@ConfigItem(
		keyName = EquipmentRestrictorPlugin.HAND_SLOT,
		name = "Hand Slot",
		description = "Locks the hand slot",
		position = 7,
		section = equipmentSlotLocks
	)
	default boolean getHandSlotLock()
	{
		return false;
	}

	@ConfigItem(
		keyName = EquipmentRestrictorPlugin.NECK_SLOT,
		name = "Neck Slot",
		description = "Locks the neck slot",
		position = 8,
		section = equipmentSlotLocks
	)
	default boolean getNeckSlotLock()
	{
		return false;
	}

	@ConfigItem(
		keyName = EquipmentRestrictorPlugin.RING_SLOT,
		name = "Ring Slot",
		description = "Locks the ring slot",
		position = 9,
		section = equipmentSlotLocks
	)
	default boolean getRingSlotLock()
	{
		return false;
	}

	@ConfigItem(
		keyName = EquipmentRestrictorPlugin.CAPE_SLOT,
		name = "Cape Slot",
		description = "Locks the cape slot",
		position = 10,
		section = equipmentSlotLocks
	)
	default boolean getCapeSlotLock()
	{
		return false;
	}

	@ConfigItem(
		keyName = EquipmentRestrictorPlugin.AMMO_SLOT,
		name = "Ammo Slot",
		description = "Locks the ammo slot",
		position = 11,
		section = equipmentSlotLocks
	)
	default boolean getAmmoSlotLock()
	{
		return false;
	}

	@ConfigSection(
		name = "Weapon Type Locks",
		description = "Locks a weapon type, blocking weapons of that type from being equipped (✓ = locked)",
		position = 4
	)
	String weaponTypeSlotLocks = "weaponTypeSlotLocks";

	@ConfigItem(
		keyName = EquipmentRestrictorPlugin.ONE_HANDED,
		name = "One Handed",
		description = "Locks one handed weapons",
		position = 1,
		section = weaponTypeSlotLocks
	)
	default boolean getOneHandedLock()
	{
		return false;
	}

	@ConfigItem(
		keyName = EquipmentRestrictorPlugin.TWO_HANDED,
		name = "Two Handed",
		description = "Locks two handed weapons",
		position = 2,
		section = weaponTypeSlotLocks
	)
	default boolean getTwoHandedLock()
	{
		return false;
	}
}
