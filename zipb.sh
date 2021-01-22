o ====================================================
cd /mnt/jstocklocal/jstock-master/dist/stockslog
echo start to backup: `date "+%Y-%m-%d %H:%M:%S"`
pyty="python"
#pytystart="/mnt/jstocklocal/baiduyunclient/bypy/bypy.py"
pytystart="bypy"
updatefile=""
pytycommand_syncup="syncup"
pytycommand_upload="upload"
pytycommand_meta="meta"
for file in `ls $1`
do
  echo $1/$file
  if expr index $file '2' = 12
  then
    echo "["
    echo "    " processing file: $file
    file_folder="$file"_folder
    bypy mkdir $file_folder
    bypy meta /$file_folder
    if [ $? -eq 0 ]
    then
        mkdir /home/lisdoo/$file_folder
        tar -cjf - $1/$file | split -b 20m - /home/lisdoo/$file_folder/$file.xz 
        if [ $? -eq 0 ]
        then
            for innerFile in `ls /home/lisdoo/$file_folder`
            do
	        echo "        " created file: /home/lisdoo/$file_folder/$innerFile
	      #echo "        " pyty updating file: /home/lisdoo/$file_folder/$innerFile
	      #updatefile=/home/lisdoo/"$file_folder"/"$innerFile"
	      #updatedfile="$file_folder"/"$innereFile"
	      #echo "        " $updatefile
	      #echo "        " $updatedfile
	      ##$pyty $pytystart $pytycommand_upload $updatefile
	      ##$pyth $pytystart $pytycommand_meta $updatedfile
	      #echo "        " $pytystart $pytycommand_upload $updatefile
	      #bypy $pytycommand_upload $updatefile $file_folder
	      #exitCode=$?
	      #if [ $exitCode -ne 0 ]
	      #then
	      #  echo "        " update file failed [ $exitCode ] : $updatefile 
	      #else
	      #  echo "        " $pytystart $pytycommand_meta $updatedfile $file_folder
	      #  bypy $pytycommand_meta $updatedfile $file_folder
	      #  if [ $? -eq 0 ] 
	      #  then 	      
	      #    rm -v $updatefile
	      #  fi
	      #fi
	      #echo "         "
            done
        fi
        echo "        " bypy uploading file /home/lisdoo/$file_folder to folder /$file_folder
        bypy $pytycommand_upload /home/lisdoo/$file_folder /jstocklog/$file_folder
        exitCode=$?
        if [ $exitCode -ne 0]
        then
            echo "        " upload folder failed [ $exitCode ] : /home/lisdoo/$file_folder
        else
            echo "        " upload folder OK /home/lisdoo/$file_folder
            rm -v $1/$file
            rm -rvf /home/lisdoo/$file_folder
        fi
        echo "]"
    fi
  fi
done
echo ended: `date "+%Y-%m-%d %H:%M:%S"`
echo ""
echo ""


