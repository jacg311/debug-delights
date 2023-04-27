package io.github.jacg311;

import java.util.HashMap;
import java.util.Map;

public class Config {
    public boolean shouldDrawSlotIds = true;
    public Map<String, Boolean> worldSavingEnabledMap = new HashMap<>();
    public boolean shouldLogReplacedTagContents = true;
    public boolean shouldDumpTags = false;
    public boolean shouldDumpRegistries = false;
}
