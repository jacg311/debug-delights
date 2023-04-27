package io.github.jacg311;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
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
	public static final Logger LOGGER = LoggerFactory.getLogger("debug-delights");
	public static final Gson GSON = new GsonBuilder()
			.setPrettyPrinting()
			.setLenient()
			.create();

	public static Config CONFIG;


	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTED.register(DebugDelights::dumpRegistriesToFile);

		Path configFile = FabricLoader.getInstance().getConfigDir().resolve("debug-delights.json");

		try {
			if (Files.notExists(configFile)) {
				CONFIG = new Config();
				GSON.toJson(CONFIG, Files.newBufferedWriter(configFile));
				Files.writeString(configFile, GSON.toJson(CONFIG));
			} else {
				CONFIG = GSON.fromJson(Files.newBufferedReader(configFile), Config.class);
			}
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	private static void dumpRegistriesToFile(MinecraftServer server) {
		if (!CONFIG.shouldDumpRegistries) return;

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