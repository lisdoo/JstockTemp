#bypy download /jstocklog/$1 .
cat ./${1:0:21}.* | tar xj
