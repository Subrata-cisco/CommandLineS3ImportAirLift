#!/bin/bash
# Import script

echo "Script will attemp to get the files from linux machine..."

username="uuname"
password="ppname"
host="elc.amazon.com"
remoteDir=/home/supi/dump/*.*
localDir="/scratch/supi/subrata/hari/"

aws_key="AKIAJME********W7B75A"
aws_sec="DYE/s+HABC8l*************hdYAJWOWZwm"
aws_bucket="bancsv"
aws_bucket_region="us-west-2"


# TODO - Clean the local directory first..


java -jar import-cli.jar import copy-from-unix -u "$username" -p "$password" -h "$host" -rd "$remoteDir" -ld "$localDir"
if [ $? -eq 0 ]; then
    echo Fetching files from "$host" passed.....
else
    echo Fetching files from "$host" failed.....
	exit 1;
fi

echo "Script will attemp to put the files to S3 now.."

java -jar import-cli.jar import process-file-command -k "$aws_key" -s "$aws_sec" -b "$aws_bucket" -r "$aws_bucket_region" -ld "$localDir"
if [ $? -eq 0 ]; then
    echo Storing file to S3 from "$localDir" passed..
else
    echo Storing file to S3 from "$localDir" failed..
fi

echo "All done !!"

	
