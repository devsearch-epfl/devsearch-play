set -e

cd `dirname $0`/..
sbt stage
nohup ./target/universal/stage/bin/devsearch-play&

echo '*** Play application started ***'
