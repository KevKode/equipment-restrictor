# <img align="center" src="https://i.imgur.com/uw1mSGR.png" alt="Icon"> Equipment Restrictor 

Mainly made for Snowflake Ironmen, this plugin allows you to restrict the kinds of equipment your character can use. When clicking on an item that is blocked, you will receive a game message as shown below.

![GameMessage](https://i.imgur.com/qZPcyb2.png)

## Features

### Whitelist/Blacklist

![WhitelistBlacklist](https://i.imgur.com/g96bWkm.png)

Type a comma separated list of item names to allow/block them. Both lists accept the wildcard character `*`, so `*rune*` will affect any item with the string `rune` in its name.

**Note**: The Whitelist overrides the Blacklist, and both override all other locks. This means that anything in the whitelist will always be allowed, and anything in the blacklist will always be blocked, regardless of your other Slot Locks or Weapon Type Locks settings.

### Slot Locks

![SlotLocks](https://i.imgur.com/246thp9.png)

Check an equipment slot to lock it, blocking items from being equipped there.

**Note**: The Weapon Slot Lock overrides the Weapon Type Locks. This means that if it is locked, you will not be able to equip weapons regardless of your other Weapon Type Locks settings.

### Weapon Type Locks

![WeaponTypeLocks](https://i.imgur.com/HLx0Ol1.png)

Check a weapon type to lock it, blocking weapons of that type from being equipped.

## Example Setups

Salamander-Only, Monkman, and 2h-Only with a few 1h exceptions.

![SalamanderOnly](https://i.imgur.com/FicMJOv.png) ![Monkman](https://i.imgur.com/nGwgrrY.png) ![2hOnly](https://i.imgur.com/J5om5MR.png)

## Issues

 - **Newer items are not affected**: RuneLite gets the equipment stats of new items by scraping the Wiki's API weekly, they will be able to be restricted after that. The Item Stats plugin uses the same data, so if you don't see the item's stats on hover, you probably won't be able to restrict the item.

If you find an issue, feel free to create a GitHub Issue in the tab above or contact me on Discord at `Knetty#7451`.

## Planned Features

 - Tinting locked slots red in the equipment panel.
 - Allowing players to restrict by item stats like prayer bonus.
 - Special ironman symbols next to name.
 - Import/Export setups.
 - Swapping blocked item's menu entry.
 - Showing blocked status on hover.

## Changelog

### v1.0.1 [`aecb612`](https://github.com/KevKode/equipment-restrictor/tree/aecb6123b4af005d7fe95f805780db511c313182)
 - Updated message formatting to bring it in line with all other Runelite plugins, thanks to [`Arrafrost`](https://github.com/arrafrost) for this change. 
 
 ![GameMessage](https://i.imgur.com/vgKY2hZ.png)

### v1.0 [`da39382`](https://github.com/KevKode/equipment-restrictor/tree/da39382cceba7619c8ff3c04fb222d75b1290388)
 - Initial write of the plugin.
