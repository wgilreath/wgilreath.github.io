#!/bin/sh
#
# gitit.sh (Get it?)
# July 27, 2017
# Create index.html from files and update wgilreath.github.io auto-magically.
#
if [ -z "$1" ]
then
    echo "\nError: Github Commit Comment Required!\n"
    exit 
fi

echo "\nCreating index of directory for wgilreath.github.io.\n"

#create html index
java -jar MkIndex.jar
mv index-byname.html index.html

echo "\nUpdating wgilreath.github.io with files.\n"
	
#update wgilreath.github.io with directory
git add --all
git commit -m $1
git push -u origin master

echo "\nAll done!\n"
exit
