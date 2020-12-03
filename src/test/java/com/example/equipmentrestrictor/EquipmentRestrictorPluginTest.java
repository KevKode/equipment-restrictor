package com.example.equipmentrestrictor;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class EquipmentRestrictorPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(EquipmentRestrictorPlugin.class);
		RuneLite.main(args);
	}
}