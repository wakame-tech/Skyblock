cd `dirname $0`
source ./env.sh
scp -r ../Skyblock/build/libs/Skyblock-0.0.1.jar $ADDR:$DATA_DIR/plugins