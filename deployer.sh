#!/bin/bash

 usage()
{
cat << EOF
usage: $0 options


This script run fabric-cluster-deployer project, which can deploy fabric cluster to ssh containers from scratch.

OPTIONS:

   --all            Creating fabric from scratch. Downloading and running fuse etc.
   --fuse           prepare fuse, that means download it, unzip and run
   --fabric         create fabric
   --ensemble       initialize ensemble from containers.conf file
   --containers     initialize containers from containers.conf file
   --clean          kill all containers and remove resources
EOF
}


if [[ $# -eq 0 ]] ; then
    usage
    exit 0
fi

#while getopts “-help:h:u:p:b” OPTION
#do
#     case $OPTION in
#         -help)
#             usage
#             exit 1
#             ;;
#         h)
#             HOST="-Dssh.url="$OPTARG
#             ;;
#         u)
#             USER="-Dssh.user="$OPTARG
#             ;;
#         p)
#             PASSWD="-Dssh.password="$OPTARG
#             ;;
#
#         b)  BUILD="-Dssh.password="$OPTARG
#                       ;;
#
#
#     esac
#done

for var in "$@"
do
    if [ $var == "--all" ]
    then
      all="--all"


    elif [ $var == "--fuse" ]
        then
          fuse="--fuse"


    elif [ $var == "--fabric" ]
        then
          fabric="--fabric"


    elif [ $var == "--ensemble" ]
        then
          ensemble="--ensemble"


    elif [ $var == "--containers" ]
        then
          containers="--containers"


    elif [ $var == "--clean" ]
        then
          clean="--clean"

     elif [ $var == "--clone" ]
            then
              clone="--clone"

     elif [ $var == "--add-container" ]
                 then
                   add="--add-container "$2
    else
        usage

    fi




done

mvn exec:java -Dexec.args="$all $clean $fuse $fabric $ensemble $containers $clone $add"