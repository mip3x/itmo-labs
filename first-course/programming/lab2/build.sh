# pre-init
clear
# java -version
rm -rf build 2>>/dev/null

# creating directories
mkdir -p build/classes

# build classes
echo 'Building pokemons...'
javac -d ./build/classes -cp ./lib/Pokemon.jar ./src/Main.java $(find . | grep .java)

# create manifest
cd build/classes
echo -e 'Main-Class: Main\nClass-Path: lib/Pokemon.jar' >> Manifest.mf
cat Manifest.mf
cd ../..

# create jar-file
jar -cmf build/classes/Manifest.mf lab2.jar -C build/classes .
tree
