# Deobfuscate Minecraft Server
cd `dirname $0`
DEOBFUSCATOR=./MC-Remapper-master/build/install/MC-Remapper/bin/MC-Remapper
# mapping url exists "server_mappings.url" in .minecraft/versions/<version>/<version>.json
# 1.15.2
# SERVER_MAPPPING_URL=https://launcher.mojang.com/v1/objects/59c55ae6c2a7c28c8ec449824d9194ff21dc7ff1/server.txt
# SERVER_JAR=../Skyblock/libs/minecraft_server_1.15.2.jar
# OUTPUT=minecraft_server_unmapped_1.15.2.jar
# 1.15.1R1
SERVER_MAPPPING_URL=https://launcher.mojang.com/v1/objects/47f8a03f5492223753f5f2b531d4938813903684/server.txt
SERVER_JAR=1.15.1R1.jar
OUTPUT=server_1.15.1R1.jar

$DEOBFUSCATOR --mapping $SERVER_MAPPPING_URL \
 --input $SERVER_JAR \
 --output $OUTPUT \
 --fixlocalvar=delete