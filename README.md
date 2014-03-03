
FABRIC-CLUSTER-DEPLOYER
============================


This project allows deploying new fabric cluster to remote machines from scratch. Deployer can download fuse instalation, prepare it for running, create fabric, expanding ensemble and create ssh and child containers with specified profiles. 

###Requierements 
ssh server, wget, unzip  installed is required. Also Java, curl and telnet is required for running Jboss-fuse and fabric.


###Configuration

Containers properties must be specified in  `containers.conf` file. Default location is `src/main/resources` but it's also possible to specify own location. 
Beside this you have to specify root container options in pom.xml:

        <ssh.url>${ssh.url}</ssh.url>
        <ssh.password>${ssh.password}</ssh.password>
        <ssh.user>${ssh.user}</ssh.user>

###Usage

You have 2 options for using this tool. Using bash script (linux only) or maven.

For install program type `mvn clean install`.

For running: 

`mvn exec:java -Dexec.args=arguments`


Possible arguments


` --all` - creating fabric from scratch. Downloading and running fuse etc.
    
`--fuse` - prepare fuse, that means download it, unzip and run
     
`--fabric` - create fabric
     
`--ensemble` - initialize ensemble from containers.conf file
    
`--containers` initialize containers from containers.conf file
     
`--clean` all containers
    

###Examples:
If you want create fabric-cluster from scratch use this command:

`mvn exec:java -Dexec.args="--all"`

If you want to just download fuse and create fabric use:

`mvn exec:java -Dexec.args="--fuse --fabric"`

If you want to kill fabric and all containers use :

`mvn exec:java -Dexec.args="--clean"`



