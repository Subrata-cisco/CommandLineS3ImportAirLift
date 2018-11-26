#!/bin/bash
# Script
echo "Open the yearmonth.txt file to find the starting Year and month to process ..."

maxMonth=12
maxYear=2018
maxRange=3

while IFS= read -r line;do
if [[ $line == year* ]]
then
    year=$(echo $line | cut -d'=' -f 2)
fi

if [[ $line == month* ]]
then
    month=$(echo $line | cut -d'=' -f 2)
fi

done < "yearmonth.config"

echo "Year is : $year and month is : $month"

if [[ "$month" -gt "$maxMonth" ]]
then
   echo "$month is not valid.."
   exit 0
fi

if [[ "$month" -eq "$maxMonth" ]]
then
  year=$((year + 1))
  month=$((month - maxMonth))
fi

if [[ "$year" -gt "$maxYear" ]]
then
   echo "$year is not valid.."
   exit 1
fi

start=$((month + 1)) 
end=$((month + maxRange))

if [[ "$end" -gt "$maxMonth" ]]
then
  end=$maxMonth
fi

echo Starting processing for Year : $year and Start Month :$start upto month:$end

for i in `seq  $start $end`
do
    echo "Process year $year and month $i"
    #./home/vagrant/test/second.sh $year $i
    ./second.sh $year $i
done

cat > yearmonth.config << EOF1
year=$year
month=$end
EOF1
