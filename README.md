FABRIC-CLUSTER-DEPLOY-TOOL
============================


This project allows deploying new fabric cluster to remote machines from scratch. Project can download fuse installation, prepare it for running, create fabric, expanding ensemble and create ssh and child containers with specified profiles.

###Requierements 
ssh server, wget, unzip  installed is required. Also Java, curl and telnet is required for running Jboss-fuse and fabric.


###Configuration

Containers properties must be specified in  `containers.conf` file. Default location is `src/main/resources` but it's also possible to specify own location. 
Beside this you have to specify root container options in pom.xml:

        <ssh.url>${ssh.url}</ssh.url>
        <ssh.password>${ssh.password}</ssh.password>
        <ssh.user>${ssh.user}</ssh.user>
        
Root container represents main node, which with project is using for downloading fuse and creating fabric environment.


#### Containers configuration

Configuration of containers is specified in containers.conf file which has to have following format. There are two types of separators. Separator `|` for separing groups of properties:


` | container  properties   |   container name       |      profile properties      | broker properties`
and separator `;` for separating fields of property group.

 __Container group:__
 
 Here is specified type of the container with additional configurations.
 
        [child,ssh,ens];host;user;pass| ... |
        
 If is container specified as `child`, others fields are automaticly ignored.
 For adding container to ensemble we should specify it as `ens`
 
 __Container name:__
 Name of container.
 
 __Profile properties:__
 
 If you want to assign for your container particular profile just place here the profile name. It's also possible  to specify if the container will be created with the profile or the profile will be assigned to container after creating.
 
        |..|...|mq-create;[true,false]| ...

__Broker properties__

Here are specified properties of the broker, shutch as broker name, broker group and connection to network.

        ..|..|..|brokername;group;networks;networks-username;networks-password

Example:

        child|childcontainer|;false|broker

This will create child-container with name 'childcontainer'  and create broker for this container using 'mq-create'       


###Usage

You have 2 options for using this tool. Using bash script (linux only) or maven.

For install project type `mvn clean install`.

For running use: 

`mvn exec:java -Dexec.args=arguments` or use attached script `./deployer.sh`.


Possible arguments


` --all` - creating fabric from scratch. Downloading and running fuse etc.
    
`--fuse` - prepare fuse, that means download it, unzip and run
     
`--fabric` - create fabric
     
`--ensemble` - initialize ensemble from containers.conf file
    
`--containers` initialize containers from containers.conf file
     
`--clean` all containers
    

###Examples:
If you want create fabric-cluster with 3 ensemble-servers and BrokerMesh with 3 brokers with group 'brokerGroup' and with network connector to 'brokerGroup' from scratch:

1.Specify containers.conf:
       
        # this is comment --
        #first server is root container specified in pom.xml
        ens;1.1.1.2;user;pass|server2
        ens;1.1.1.3;user;pass|server3
        
        ssh;1.1.1.4;user;pass|container1|;true|broker1;brokergroup;brokergroup;admin;admin
        ssh;1.1.1.5;user;pass|container2|;true|broker2;brokergroup;brokergroup;admin;admin
        ssh;1.1.1.6;user;pass|container3|;true|broker3;brokergroup;brokergroup;admin;admin
        
2.use this command:

`mvn exec:java -Dexec.args="--all"` or `./deployer.sh --all`


If you want to just download fuse and create fabric use:

`mvn exec:java -Dexec.args="--fuse --fabric"` or `./deployer.sh --fuse --fabric`

If you want to kill fabric and all containers use :

`mvn exec:java -Dexec.args="--clean"` or `./deployer.sh --clean`

etc.



