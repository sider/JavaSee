#!/bin/bash

echo "☕️ Building the binary..."

./gradlew shadowjar

if [[ -z "$VERSION" ]]; then
  OUTPUT=`java -jar build/libs/JavaSee-all.jar version`
  VERSION=${OUTPUT#* }
fi

set -euo pipefail

stage="JavaSee-$VERSION"
zip="JavaSee-bin-${VERSION}.zip"

echo "🕺 Staging in release/${stage}..."

rm -rf release/${stage}
rm -rf release/${zip}
mkdir -p release/${stage}
mkdir release/${stage}/lib
mkdir release/${stage}/bin

echo "🏦 Making an asset..."

cp -r LICENSE CREDITS README.md logo doc release/${stage}/
cp build/libs/JavaSee-all.jar release/${stage}/lib/JavaSee-all.jar
cp scripts/javasee release/${stage}/bin/javasee
cp scripts/javasee.bat release/${stage}/bin/javasee.bat

cd release
  zip -r ${zip} ${stage}
cd ..

echo "✍️ Preparing for a GitHub release..."
hub release create -d -a release/${zip} -m "JavaSee ${VERSION}" ${VERSION}

echo "🤖 It's done! 🎉"
echo "Visit https://github.com/sider/JavaSee/releases and publish the release!"
