package org.mmelko.tools.fabric.deployer;

import com.jcraft.jsch.JSchException;

import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mmelko
 * Date: 2/21/14
 * Time: 12:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeployFabric {

    static final String SSH_ADRESS = System.getProperty("ssh.url");
    static final String SSH_USER = System.getProperty("ssh.user");
    static final String SSH_PASSWORD = System.getProperty("ssh.password");
    static final String FUSE_FOLDER = System.getProperty("fuse.foldername");
    static final String FUSE_URL = System.getProperty("fuse.url");
    static final String FUSE_ZIP = System.getProperty("fuse.zip");
    static final String FUSE_BUILD = System.getProperty("fuse.build");
    static final String CONFIG_PATH = System.getProperty("fuse.containers.configpath");
    static int cloned = 1;
    static SSHClient sshClient;
    static List<Container> ensembe;
    static List<Container> containers;
    static List<Container> allContainers;
    static List<List<Container>> clonedContainer;
    //static List<String> servers;

    public static void main(String[] arg) throws Exception {

        readContainersFromFile(0);

        if (arg.length > 0) {
            List<String> args = Arrays.asList(arg);

            for (String s : args) {
                System.out.println("Argument: " + s);
            }

            if (arg[0].equals("--all")) {
                //if(arg.length>1)

                cleanContainers("");
                prepareFuse();
                initFabric();
                initEnsemble();
                initContainers();
                cloneContainers();


            } else {
                if (args.contains("--clean"))
                    cleanContainers("");

                if (args.contains("--fuse"))
                    prepareFuse();

                if (args.contains("--fabric"))
                    initFabric();

                if (args.contains("--ensemble"))
                    initEnsemble();

                if (args.contains("--containers"))
                    initContainers();

                if (args.contains("--clone"))
                    cloneContainers();

                if (args.contains("--add-container"))
                    initSingleContainer(arg[1]);
            }
        }

        System.out.println("FINISH");


    }

    private static void cloneContainers() throws Exception {
        sshToRootContainer();
        for (List<Container> clonedC : clonedContainer)
            for (Container c : clonedC) {
                System.out.println(sshClient.executeCommand(c.getCreateCommand()));

                //    Thread.sleep(10000);
                waitForProvision(c.getName());
                if (c.getMQ())
                    System.out.println(sshClient.executeCommand(c.createBroker()));

                waitForProvision(c.getName());

                //     System.out.println(sshClient.executeCommand("container-list"));
            }

        sshClient.disconnect();

    }

    public static void initSsh(boolean connect) throws JSchException {
        System.out.println("USED CREDENTIALS: " + SSH_ADRESS + " " + SSH_USER + " " + SSH_PASSWORD);


        sshClient = new SSHClient();
        sshClient.setUsername(SSH_USER);
        sshClient.setPassword(SSH_PASSWORD);
        sshClient.setHostname(SSH_ADRESS);
        if (connect)
            sshClient.init();
    }

    public static void prepareFuse() throws Exception {
        initSsh(true);
        System.out.println("FUSE " + FUSE_FOLDER + " " + FUSE_URL + "");

        System.out.println(sshClient.executeCommand("wget " + FUSE_URL));
        System.out.println(sshClient.executeCommand("unzip " + FUSE_ZIP));
        System.out.println(sshClient.executeCommand("printf \" \nadmin=admin,admin\" >> " + FUSE_FOLDER + "/etc/users.properties"));

        System.out.println(sshClient.executeCommand(FUSE_FOLDER + "/bin/start"));
        sshClient.disconnect();
        System.out.println("Starting FUSE");
        Thread.sleep(80000);
    }

    public static void initFabric() throws Exception {
        sshToRootContainer();

        System.out.println(sshClient.executeCommand("fabric:create --global-resolver localip --wait-for-provisioning "));
        //  Thread.sleep(10000);

        System.out.println(sshClient.executeCommand("container-list"));
        sshClient.disconnect();
    }

    public static void initEnsemble() throws Exception {
        if (!ensembe.isEmpty()) {
            sshToRootContainer();
            // readContainersFromFile();
            String contlist = "";

            for (Container c : ensembe) {
                System.out.println(sshClient.executeCommand(c.getCreateCommand()));

                //  Thread.sleep(10000);
                waitForProvision(c.getName());
                contlist += " " + c.getName();
                //  System.out.println(sshClient.executeCommand("container-list"));
            }

            System.out.println(sshClient.executeCommand("ensemble-add --force" + contlist));
            waitForProvision("root");


            System.out.println(sshClient.executeCommand("container-list"));

            sshClient.disconnect();
        }
    }

    public static void readContainersFromFile(String path, int clones) throws Exception {

        ContainerParser parser = new ContainerParser(path);
        ContainerParser.setNumberOfClusterClones(clones);
        cloned = clones;
        containers = parser.getContainers();
        ensembe = parser.getEnslemble();
        allContainers = parser.getAllContainers();
        clonedContainer = parser.getClonedContainers();

    }

    public static void readContainersFromFile(int clones) throws Exception {
        ContainerParser.setNumberOfClusterClones(clones);
        readContainersFromFile("", clones);
    }

    public static boolean initSingleContainer(String containerName) throws Exception {

        //  readContainersFromFile(0);
        Container single = null;

        for (Container c : containers) {
            if (c.getName().equals(containerName)) {
                single = c;
                break;
            }
        }

        if (single == null) {
            System.out.println("Couldn't find container with name " + containerName);
            return false;
        } else {
            sshToRootContainer();
            System.out.println(sshClient.executeCommand(single.getCreateCommand()));

            //    Thread.sleep(10000);
            waitForProvision(single.getName());
            if (single.getMQ()) {
                System.out.println(sshClient.executeCommand(single.createBroker()));

            }

            waitForProvision(single.getName());
            if (single.getMQ()) {
                System.out.println(sshClient.executeCommand("profile-edit --pid org.fusesource.mq.fabric.server-" + single.getBrokerName() + "/network.consumerTTL=1 mq-broker-" + single.getBrokerGroup() + "." + single.getBrokerName()));
                System.out.println(sshClient.executeCommand("profile-edit --pid org.fusesource.mq.fabric.server-" + single.getBrokerName() + "/network.messageTTL=1000 mq-broker-" + single.getBrokerGroup() + "." + single.getBrokerName()));
                //   System.out.println(sshClient.executeCommand("profile-edit --pid org.fusesource.mq.fabric.server-"+c.getBrokerName()+"/network.decreaseNetworkConsumerPriority=true mq-broker-"+c.getBrokerGroup()+"."+c.getBrokerName()));

            }
            sshClient.disconnect();
        }

        return true;
    }

    public static void initContainers() throws Exception {
        //  sshToRootContainer();

        for (Container c : containers) {
            sshToRootContainer();
            System.out.println(sshClient.executeCommand(c.getCreateCommand()));

            //    Thread.sleep(10000);
            waitForProvision(c.getName());
            if (c.getMQ()) {
                System.out.println(sshClient.executeCommand(c.createBroker()));

            }

            waitForProvision(c.getName());
            if (c.getMQ()) {
                System.out.println(sshClient.executeCommand("profile-edit --pid org.fusesource.mq.fabric.server-" + c.getBrokerName() + "/network.consumerTTL=1 mq-broker-" + c.getBrokerGroup() + "." + c.getBrokerName()));
                System.out.println(sshClient.executeCommand("profile-edit --pid org.fusesource.mq.fabric.server-" + c.getBrokerName() + "/network.messageTTL=1000 mq-broker-" + c.getBrokerGroup() + "." + c.getBrokerName()));
                //   System.out.println(sshClient.executeCommand("profile-edit --pid org.fusesource.mq.fabric.server-"+c.getBrokerName()+"/network.decreaseNetworkConsumerPriority=true mq-broker-"+c.getBrokerGroup()+"."+c.getBrokerName()));

            }
            sshClient.disconnect();
            //     System.out.println(sshClient.executeCommand("container-list"));
        }


    }

    public static void sshToRootContainer() throws JSchException {
        sshClient = new SSHClient();
        sshClient.setPort(8101);
        sshClient.setUsername("admin");
        sshClient.setPassword("admin");
        sshClient.setHostname(SSH_ADRESS);
        sshClient.init();
    }

    public static void cleanContainers(String path) throws Exception {
        readContainersFromFile(path, 0);
        initSsh(true);


        // clean root

        sshClient.executeCommand("pkill -9 -f karaf");
        sshClient.executeCommand("pkill -9 -f java");
        sshClient.executeCommand("rm -rf jboss-fuse*");
        sshClient.disconnect();

        for (Container c : allContainers) {
            if (!c.isChild()) {
                System.out.println("Connecting to .." + c.getHost());
                sshClient.setHostname(c.getHost());
                sshClient.init();

                sshClient.executeCommand("pkill -9 -f karaf");
                sshClient.executeCommand("pkill -9 -f java");
                sshClient.executeCommand("rm -rf containers");
                sshClient.disconnect();
                //pkill -9 -f karaf

            }
        }
    }


    public static boolean waitForProvision(String name) throws Exception {

        boolean result = false;
        int timeout = 120;

        String buffer = "";
        int time = 0;

        while (result == false) {

            String res = sshClient.executeCommand("fabric:container-list | grep " + name);
            if (!res.equals(buffer)) {
                System.out.println(res);
                buffer = String.copyValueOf(res.toCharArray());
            }
            if (res.contains("success"))
                result = true;
            else {
                Thread.sleep(1000);
                time++;

            }

            if (time >= 120) {
                System.out.println("ERROR with container " + name);
                return false;
            }


        }

        return result;

    }

    /**
     * init fabric cluster according configuration file
     *
     * @param scenarioName
     * @param clones
     * @throws Exception
     */
    public static void initFabricForScenario(String scenarioName, int clones) throws Exception {

        readContainersFromFile("src/main/resources/" + scenarioName, clones);

        cleanContainers("src/main/resources/" + scenarioName);
        prepareFuse();
        initFabric();
        initEnsemble();
        initContainers();

        if (clones > 0) {

            cloneContainers();
        }
    }

    public static void initJustEnsemble(String scenarioName, int clones) throws Exception {
        readContainersFromFile("src/main/resources/" + scenarioName, clones);

        cleanContainers("src/main/resources/" + scenarioName);
        prepareFuse();
        initFabric();
        initEnsemble();
    }

    private static void commandForEach(String command) throws Exception {
        for (Container c : containers) {
            sshClient = new SSHClient();
            sshClient.setUsername(c.getSshUsername());
            sshClient.setPassword(c.getSshPas());
            sshClient.setHostname(c.getHost());
            sshClient.init();

            // sshClient.executeCommand("containers/"+c.getName()+"/fabric8-karaf-1.0.0.redhat-"+FUSE_BUILD+"bin/client -u admin -p admin "+command);
            System.out.println("containers/" + c.getName() + "/fabric8-karaf-1.0.0.redhat-" + FUSE_BUILD + "/bin/client -u admin -p admin " + command);
            for (int i = 1; i <= cloned; i++) {
                System.out.println("containers/" + c.getName() + i + "/fabric8-karaf-1.0.0.redhat-" + FUSE_BUILD + "/bin/client -u admin -p admin " + command);
            }

            sshClient.disconnect();

        }
    }


}


