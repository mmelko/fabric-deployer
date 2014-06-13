package org.mmelko.tools.fabric.deployer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mmelko
 * Date: 2/23/14
 * Time: 4:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class ContainerParser {

    private List<Container> allContainers;
    private List<List<Container>> clones;


    private static int CLUSTER_CLONES = 0;
    private String path = "src/main/resources/containers.conf";
    public static CloningStrategy CLONING_STRATEGY = CloningStrategy.GROUP_CLONING;

    public ContainerParser(String p) throws Exception {
        //  System.out.println(System.getProperty("system.path"));
        allContainers = new ArrayList<Container>();


            clones = new ArrayList<List<Container>>();
        if (CLUSTER_CLONES > 0){
            for(int i=0;i<CLUSTER_CLONES;i++)
                clones.add(new ArrayList<Container>());
        }

        FileInputStream fis;
        if (p.equals(""))
            fis = new FileInputStream(path);
        else fis = new FileInputStream(p);
        InputStreamReader is = new InputStreamReader(fis);
        BufferedReader reader = new BufferedReader(is);
        String s = reader.readLine();

        while (s != null) {

            if (s.length() > 0 && s.toCharArray()[0] != '#') {
                Container c = parsteContainer(s);
                allContainers.add(parsteContainer(s));

                //if container doesn't belong to ensemble create clones
                if (!c.isEns())
                    for (int i = 1; i <= CLUSTER_CLONES; i++) {

                        Container next = parsteContainer(s);
                        next.setName(c.getName() + i);
                        next.setBrokerName(c.getBrokerName() + i);
                        if (CLONING_STRATEGY == CloningStrategy.GROUP_CLONING) {
                            next.setBrokerGroup(c.getBrokerGroup() + i);
                            next.setNetworks(c.getNetworks() + 1);
                        }
                        clones.get(i-1).add(next);
                    }
            }

            s = reader.readLine();
        }
        reader.close();


        //     OutputStreamWriter osw=new OutputStreamWriter(fis,"UTF-8");

        //    BufferedWriter writer = new BufferedWriter(osw);
        //     writer.newLine();
        //   writer.append(user+"="+password+","+rolename);
    }

    private Container parsteContainer(String input) throws Exception {
        Container result = new Container();

        String[] fields = input.split("\\|");

        // container properties
        String[] sshProperties = fields[0].split(";");
        // System.out.println("PARSING    " +fields[0]);

        if (sshProperties[0].equals("child"))
            result.setChild(true);

        else {
            result.setChild(false);

            if (sshProperties.length < 3)
                throw new Exception("Wrong format of container ");

            if (sshProperties[0].equals("ens"))
                result.setEns(true);

            else if (!sshProperties[0].equals("ssh"))
                throw new Exception("Bad format, type can be just child,ssh,ens and is " + sshProperties[0]);

            if (!sshProperties[1].equals(""))
                result.setHost(sshProperties[1]);
            else throw new Exception("Bad format: ssh_host can't be empty");

            if (!sshProperties[2].equals(""))
                result.setSshUsername(sshProperties[2]);

            else throw new Exception("Bad format: ssh_username can't be empty");


            if (!sshProperties[3].equals(""))
                result.setSshPas(sshProperties[3]);
            else throw new Exception("Bad format: ssh_password can't be empty");

        }


        //  CONTAINER NAME

        if (!fields[1].equals(""))
            result.setName(fields[1]);

        else throw new Exception("Bad format: name can't be empty");


        // PROFILE OPTIONS
        if (fields.length > 2) {

            String[] profile = fields[2].split(";");
            if (!profile[0].equals(""))
                result.setProfile(profile[0]);

            if (profile[1].equals("true")) {
                System.out.println("PROFILE" + profile[1]);
                result.setAssignProfileAfterCreate(true);
            }

            if (fields.length < 3)
                result.setMQ(false);

            if (fields.length > 3 && !fields[3].equals("")) {
                result.setMQ(true);
                String[] broker = fields[3].split(";");

                result.setBrokerName(broker[0]);

                if (broker.length > 1) {

                    if (!broker[1].equals(""))
                        result.setBrokerGroup(broker[1]);
                    if (broker.length > 2) {


                        if (!broker[2].equals(""))
                            result.setNetworks(broker[2]);
                        if (!broker[2].equals(""))
                            result.setNetworkUsername(broker[3]);
                        if (!broker[4].equals(""))
                            result.setNetworkPassword(broker[4]);
                    }

                }

            }

        }


        return result;
    }

    public List<Container> getAllContainers() {
        return allContainers;
    }

    public List<Container> getEnslemble() {
        List<Container> res = new ArrayList<Container>();
        for (Container c : allContainers)
            if (c.isEns())
                res.add(c);
        return res;
    }

    public List<Container> getContainers() {
        List<Container> res = new ArrayList<Container>();
        for (Container c : allContainers)
            if (!c.isEns())
                res.add(c);
        return res;
    }

    public List<List<Container>> getClonedContainers() {
        return clones;
    }

    public static void setNumberOfClusterClones(int clones) {
        CLUSTER_CLONES = clones;
    }

    public static int getNumberOfClusterClones() {
        return CLUSTER_CLONES;
    }


}
