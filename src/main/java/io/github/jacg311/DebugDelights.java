package io.github.jacg311;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class DebugDelights implements ModInitializer {
	public static final String MOD_ID = "debug_delights";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Config CONFIG;

	public static final Identifier CLEAR_CHUNKS = new Identifier(MOD_ID, "clear_chunks");

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTED.register(DebugDelights::dumpRegistriesToFile);

		Path configFile = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID + ".json");
		try {
			if (Files.notExists(configFile)) {
				CONFIG = new Config();
				JsonElement conf = Config.CODEC.encodeStart(JsonOps.INSTANCE, CONFIG)
						.getOrThrow(false, LOGGER::error);
				Files.writeString(configFile, conf.toString());
			} else {
				CONFIG = Config.CODEC.parse(
						JsonOps.INSTANCE,
						JsonHelper.deserialize(Files.newBufferedReader(configFile), true)
				).getOrThrow(false, LOGGER::error);
			}
		}
		catch (IOException e) {
			CONFIG = new Config();
		}
	}

	private static void dumpRegistriesToFile(MinecraftServer server) {
		if (!CONFIG.shouldDumpRegistries()) return;

		Map<Identifier, TreeSet<String>> treeMap = new HashMap<>();
		for (Identifier regId : Registries.REGISTRIES.getIds()) {
			TreeSet<String> set = new TreeSet<>();
			treeMap.put(regId, set);
			for (Identifier id : Registries.REGISTRIES.get(regId).getIds()) {
				set.add(id.toString());
			}
		}

		Path registryDir = FabricLoader.getInstance().getGameDir().resolve("registries");
		for (var entry : treeMap.entrySet()) {
			try {
				Path file = registryDir.resolve(entry.getKey().toString().replace(":", "/") + ".txt");
				Files.createDirectories(file.getParent());
				Files.createFile(file);

				BufferedWriter writer = Files.newBufferedWriter(file);

				for (String str : entry.getValue()) {
					writer.write(str);
					writer.newLine();
				}
				writer.close();
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}