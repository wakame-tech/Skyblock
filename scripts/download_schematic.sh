cd `dirname $0`
source ./env.sh
scp -r $ADDR:$DATA_DIR/plugins/WorldEdit/schematics/* ../tmp
scp -r $ADDR:$DATA_DIR/plugins/Skyblock/* ../tmp