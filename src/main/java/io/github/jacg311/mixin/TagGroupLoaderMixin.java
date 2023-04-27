package io.github.jacg311.mixin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.JsonOps;
import io.github.jacg311.DebugDelights;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.tag.TagFile;
import net.minecraft.registry.tag.TagGroupLoader;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Debug(export = true)
@Mixin(TagGroupLoader.class)
public class TagGroupLoaderMixin {
    @Inject(method = "loadTags", at = @At(value = "INVOKE", target = "Ljava/util/List;clear()V"))
    private void debugdelights$logTagReplacement(
            ResourceManager resourceManager,
            CallbackInfoReturnable<Map<Identifier, List<TagGroupLoader.TrackedEntry>>> cir,
            @Local(ordinal = 1) Identifier tagName,
            @Local Resource resource,
            @Local JsonElement jsonElement
            ) {
        if (DebugDelights.CONFIG.shouldLogReplacedTagContents) {
            DebugDelights.LOGGER.error("Tag {} has been replaced by data pack \"{}\"!", tagName, resource.getResourcePackName());
            DebugDelights.LOGGER.error("New contents of tag:\n{}", DebugDelights.GSON.toJson(jsonElement));
        }
    }

    @Inject(method = "loadTags", at = @At("RETURN"))
    private void debugdelights$dumpTagsToZip(ResourceManager resourceManager, CallbackInfoReturnable<Map<Identifier, List<TagGroupLoader.TrackedEntry>>> cir) {
        if (!DebugDelights.CONFIG.shouldDumpTags) return;

        Path path = FabricLoader.getInstance().getGameDir().resolve("dumped_tags.zip");
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(path))){
            for (Map.Entry<Identifier, List<TagGroupLoader.TrackedEntry>> entry : cir.getReturnValue().entrySet()) {
                String fileName = entry.getKey().getNamespace() + "/" + entry.getKey().getPath() + ".json";
                ZipEntry zipEntry = new ZipEntry(fileName);
                zipOutputStream.putNextEntry(zipEntry);

                TagFile tagFile = new TagFile(entry.getValue().stream().map(TagGroupLoader.TrackedEntry::entry).toList(), false);

                JsonElement result = TagFile.CODEC.encodeStart(JsonOps.INSTANCE, tagFile).result().orElse(new JsonObject());
                byte[] data = DebugDelights.GSON.toJson(result).getBytes(StandardCharsets.UTF_8);
                zipOutputStream.write(data);

            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
