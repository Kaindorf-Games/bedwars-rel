JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64 mvn clean package
cp ./plugin/target/BedwarsRel-*.jar pathToMinecraftServer/plugins/BedwarsRel.jar
docker restart docker-minecraft-server
docker logs docker-minecraft-server -f --tail 10