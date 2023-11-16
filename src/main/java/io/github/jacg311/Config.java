package io.github.jacg311;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record Config(boolean shouldDrawSlotIds, boolean shouldLogReplacedTagContents, boolean shouldDumpTags, boolean shouldDumpRegistries, Set<Block> randomTickBlocks) {
    public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("should_draw_slot_ids").forGetter(Config::shouldDrawSlotIds),
            Codec.BOOL.fieldOf("should_log_replaced_tag_contents").forGetter(Config::shouldLogReplacedTagContents),
            Codec.BOOL.fieldOf("should_dump_tags").forGetter(Config::shouldDumpTags),
            Codec.BOOL.fieldOf("should_dump_registries").forGetter(Config::shouldDumpRegistries),
            Registries.BLOCK.getCodec().listOf().xmap(Set::copyOf, List::copyOf).fieldOf("visualize_random_tick_block_whitelist").forGetter(Config::randomTickBlocks)
    ).apply(instance, Config::new));

    public Config() {
        this(true, false, false, false, new HashSet<>());
    }
}
