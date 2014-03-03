

FABRIC-CLUSTER-DEPLOYER
============================


This project allows deploying new fabric cluster to remote machines from scratch. Deployer can download fuse instalation, prepare it for running, create fabric, expanding ensemble and create ssh and child containers with specified profiles. 

###Requierements 
ssh server, wget, unzip  installed is required. Also Java, curl and telnet is required for running Jboss-fuse and fabric.


###Configuration

Container properties must be specified in  `containers.conf` file. Default location is `src/main/resources` but it's also possible to specify own location. 

###Usage

You have 2 options for using this tool. Using bash script (linux only) or maven.

For install program type `mvn clean install`.

For running: 

`mvn exec:java -Dexec.args=`arguments

Possible argumets
    
    ### --all - creating fabric from scratch. Downloading and running fuse etc.
    
    #### --fuse - prepare fuse, that means download it, unzip and run
    
    #### --fabric - create fabric
    #### --ensemble - initialize ensemble from containers.conf file
    #### --containers initialize containers from containers.conf file
    #### --clean all containers
    



