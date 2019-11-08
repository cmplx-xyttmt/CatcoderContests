#!/usr/bin/env bash
kotlinc SolarCity.kt -include-runtime -d Solar.jar
for i in 1 2 3 4 5
do
    java -jar Solar.jar < "level3_$i.in" > "output3_$i.out"
done
