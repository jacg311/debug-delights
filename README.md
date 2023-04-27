# Debug Delights
A mod that adds several features that help you with mod development!

Features:
- Draw Slot Ids in all Inventories
- Dump all registries and their contents
- Dump all tags and their contents
- Warn when a tag has been replaced
- Disable world saving (not yet functional)

Config:
A file "debug-delights.json" will be created on startup.
Here are the default values:
```json
{
  "shouldDrawSlotIds": true,
  "worldSavingEnabledMap": {},
  "shouldLogReplacedTagContents": true,
  "shouldDumpTags": false,
  "shouldDumpRegistries": false
}
```