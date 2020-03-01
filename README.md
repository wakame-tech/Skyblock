# SkyBlock(仮称)(WIP)
## 📚 導入プラグイン
- [WorldEdit v7.1](https://www.curseforge.com/minecraft/bukkit-plugins/worldedit/files)
- [Skript 2.5pre](https://github.com/SkriptLang/Skript/releases)
- [VoxelSniper 5.171.0](https://dev.bukkit.org/projects/voxelsniper)
- [WorldEditSelectionVisualizer](https://www.spigotmc.org/resources/worldeditselectionvisualizer-1-7-1-15.17311/)
- [Netherboard v1.1.3](https://github.com/MinusKube/Netherboard/releases)
- [Iris ToolBox v1.1(Skript)](https://forum.civa.jp/viewtopic.php?f=15&t=63)
- [AutoSaveWorld](https://dev.bukkit.org/projects/autosaveworld)
- [Skyblock v0.1(自作プラグイン)](https://github.com/wakame-tech/Skyblock)

## ✨ プラグインの機能
- 島ごとにschematicで管理
- Advancements DSL API

## 🐳 start spigot server
```bash
docker-compose up -d
```

## 📚 attach logs
```bash
docker-compose exec spigot-server mc_log
```

## 🔥 Build
require [WorldEdit](https://www.curseforge.com/minecraft/bukkit-plugins/worldedit/files) in `plugins` Folder.

```bash
cd Skyblock
./gradlew build
```
