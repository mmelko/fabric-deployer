package org.mmelko.tools.fabric.deployer.tests;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mmelko.tools.fabric.deployer.CloningStrategy;
import org.mmelko.tools.fabric.deployer.Container;
import org.mmelko.tools.fabric.deployer.ContainerParser;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mmelko
 * Date: 6/11/14
 * Time: 10:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class ContainerTest {

    private static List<Container> containers;
    private static List<Container> ens;
    private static List<List<Container>> clones;


    @BeforeClass
    public static void readContainers() throws Exception {

        ContainerParser.setNumberOfClusterClones(3);
        ContainerParser.CLONING_STRATEGY= CloningStrategy.GROUP_ADDING;
        ContainerParser parser = new ContainerParser("src/test/resources/test.conf");
        containers = parser.getContainers();
        clones = parser.getClonedContainers();
        ens = parser.getEnslemble();
    }

    @Test
    public void testParser() {

        Assert.assertEquals(3, containers.size());
        Assert.assertEquals(3, clones.size());

        Assert.assertEquals(3, clones.get(0).size());
        Assert.assertEquals(3, clones.get(1).size());
        Assert.assertEquals(3, clones.get(2).size());

    }

    @Test
    public void commandTests() throws Exception {
        for (Container c : ens) {
            System.out.println(c.getCreateCommand());
            Assert.assertTrue(c.isEns());
        }

        for (Container c : containers) {
            System.out.println(c.getCreateCommand());
            if (c.getMQ())
                System.out.println(c.createBroker());
        }

        for (List<Container> cl : clones)
            for (Container c : cl)  {
                System.out.println(c.getCreateCommand());
        if (c.getMQ())
            System.out.println(c.createBroker());
            }

        Assert.assertEquals("example21", clones.get(0).get(1).getName());
        Assert.assertEquals("example23", clones.get(2).get(1).getName());

        Assert.assertTrue( clones.get(2).get(2).getCreateCommand().contains("--profile profile"));
    }

    @Test public void cloningStrategiesTest() throws Exception {
        ContainerParser.setNumberOfClusterClones(3);
        ContainerParser.CLONING_STRATEGY= CloningStrategy.GROUP_ADDING;

        ContainerParser parser = new ContainerParser("src/test/resources/test.conf");

        clones = parser.getClonedContainers();

        for(List<Container> cl:parser.getClonedContainers()){
            Assert.assertEquals("brokergroup",cl.get(2).getBrokerGroup());
        }

        ContainerParser.CLONING_STRATEGY= CloningStrategy.GROUP_CLONING;

        parser = new ContainerParser("src/test/resources/test.conf");
        for(int i = 1;i<=3 ;i++){
            Assert.assertEquals("brokergroup"+i,parser.getClonedContainers().get(i-1).get(2).getBrokerGroup());
        }

    }
}
