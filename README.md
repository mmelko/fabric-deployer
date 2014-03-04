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
        
Root container repesents main node, which with deployer is using for downloading fuse and creating fabric environment.


#### Containers configuration

Configuration of containers is specified in containers.conf file which has to have following format. There are two types of separators. Separator `|` for separing groups of properties:


` | container  properties   |   container name       |      profile properties      | broker properties`
and separator `;` for separating fields of property group.

 __Container group: __
 
 Here is specified type of the container with additional configurations.
 
        [child,ssh,ens];host;user;pass| ... |
        
 If is container specified as `child`, others fields are automaticly ignored.
 For adding container to ensemble we should specify it as `ens`
 
 
 __Profile properties:__

        
 |     child         |   name  of container   |  profile-name; assignafter   |  brokername;group;networks;networkusername;network-password|
 |ssh;host;user;pass |                        | profile-name;  false/true    |                                                      |
 |ssh;host;user;pass |                                                                                               

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



