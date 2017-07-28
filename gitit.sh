#!/bin/sh
# 
#
if [ -z "$1" ]
then
    echo "\nError: Github Commit Comment Required!\n"
    exit 
fi

#create html index
java -jar MkIndex.jar
mv index-byname.html html

#update github.io with directory
git add --all
git commit -m $1
git push -u origin master


