#!/bin/sh
# 
#
if [ -z "$1" ]
then
    echo "\nError: Github Commit Comment Required!\n"
    exit 
fi

#create html index
echo "\nCreating index of directory for github.io\n"
	
java -jar MkIndex.jar
mv index-byname.html html

echo "\nUpdating github.io with files\n"
	
#update github.io with directory
git add --all
git commit -m $1
git push -u origin master

echo "\nAll done!\n"
	
