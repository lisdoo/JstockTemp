bypy download /jstocklog/$1 .
cat ./${1:0:21}.* | tar xj
if [ $? -eq 0 ]; then 
  echo "tar xzf ok"
else
  rm -f ${1:0:21}
fi
rm -f ${1:0:21}.xz*
