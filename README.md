# Skyblock
## 🐳 start spigot server
```bash
docker-compose up -d
```

## 📚 attach logs
```bash
docker-compose exec spigot-server mc_log
```

## 🔥 build plugin
require `WorldEdit` in `plugins` Folder.

```bash
cd Skyblock
./gradlew build
```

# 🏝 Islands
```
islands
  - name: リスポーン島
    id: main_island
    location:
      x: 8
      y: 22
      z: 9
  - name: 鼠島
    id: pika_island
    location:
      x: -46
      y: 38
      z: 44
  - name: 洋館島
    id: yokan_island
    location:
      x: 4
      y: 54
      z: 127
  - name: 地底島
    id: titei_island
    location:
      x: -232
      y: 142
      z: -204
  - name: 神社島
    id: jinja_island
    location:
      x: -166
      y: 116
      z: 44
  - name: 羊島
    id: hitsuji_island
    location:
      x: 39
      y: 14
      z: -3
```

### Links
Advancement Generator
<https://advancements.thedestruc7i0n.ca/>

CrazyAdvancementsAPI
<https://github.com/ZockerAxel/CrazyAdvancementsAPI>