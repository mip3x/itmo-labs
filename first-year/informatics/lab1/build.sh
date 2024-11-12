#!/bin/bash

echo -n "Enter java main class > "
read filename 

javac $filename.java
jar -cfe $filename.jar $filename *.class
java -jar $filename.jar
