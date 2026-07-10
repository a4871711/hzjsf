#!/bin/bash
# 后端打包脚本：自动切到 JDK 8 后用 Maven 打 WAR
# 用法：./build.sh          正常打包
#      ./build.sh -o       离线模式打包（依赖已在本地仓库时更快）
set -e

# 脚本所在目录（即 Backend/），保证在任意路径下调用都能正确定位
cd "$(dirname "$0")"

# 强制使用 JDK 8（本机默认 JDK 可能是 17/25，会导致 aspectjweaver 报 ZipException 假错）
JAVA8_HOME=$(/usr/libexec/java_home -v 1.8 2>/dev/null)
if [ -z "$JAVA8_HOME" ]; then
  echo "❌ 未找到 JDK 8，请先安装（如 Corretto 8）。当前已装版本："
  /usr/libexec/java_home -V
  exit 1
fi
export JAVA_HOME="$JAVA8_HOME"
echo "✅ 使用 JDK: $JAVA_HOME"
java -version

echo "----------------------------------------"
echo "开始打包（跳过测试）..."
mvn clean package "$@"

echo "----------------------------------------"
WAR="target/hzjsf.war"
if [ -f "$WAR" ]; then
  echo "✅ 打包成功：$(pwd)/$WAR"
  ls -lh "$WAR"
else
  echo "❌ 打包结束但未找到 $WAR，请检查上面的 Maven 输出"
  exit 1
fi
