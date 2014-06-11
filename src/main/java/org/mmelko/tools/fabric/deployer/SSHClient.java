package org.mmelko.tools.fabric.deployer;

import com.jcraft.jsch.*;

import java.io.InputStream;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: mmelko
 * Date: 2/21/14
 * Time: 12:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class SSHClient {

        public static final int TIMEOUT = 120;


        private String hostname = "localhost";
        private String localHostName;
        private int port = -1;

        private String username ;
        private String password ;

        private Session session;
        private Channel channel;

        JSch ssh = new JSch();



        public SSHClient() {
            // default values are used
        }

        public SSHClient(String localHostName) {
            this.localHostName = localHostName;
         //   SSHClient();
        }

        public SSHClient(String hostname, int port) {
            this.hostname = hostname;
            this.port = port;
        }

        public String getHostname() {
            return hostname;
        }

        public void setHostname(String hostname) {
            this.hostname = hostname;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }


        public String executeCommand(String command) throws Exception {
            //
            String returnString = "";

          //  ssh.setKnownHosts("/home/mmelko/.ssh/known_hosts");
        //    System.out.println(ssh.getHostKeyRepository().getHostKey().toString());

            System.out.println("LOG INFO: Command: " + command);

            channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);

            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);

            InputStream in = channel.getInputStream();

            channel.connect();

            returnString = convertStreamToString(in);

            System.out.println("LOG INFO: Response: " + returnString);



            return returnString;
        }




        private boolean waitForString(String command, String expectedString, int iterationSeconds, boolean shouldContain) throws Exception {
            int tryCount = 0;
            while (tryCount < iterationSeconds) {

                String commandResult = executeCommand(command);

                if (commandResult.contains(expectedString) == shouldContain) {
                    return true;
                }
                tryCount++;
                Thread.sleep(1500);
            }
            throw new RuntimeException("LOG ERROR: Command \"" + command + "\" didn't contain \"" + expectedString + "\" string.");
        }

        public boolean waitCommandContainsString(String command, String expectedString, int iterationSeconds) throws Exception {
            return waitForString(command, expectedString, iterationSeconds, true);
        }

        public boolean waitCommandNotContainsString(String command, String expectedString, int iterationSeconds) throws Exception {
            return waitForString(command, expectedString, iterationSeconds, false);
        }



        private String convertStreamToString(java.io.InputStream is) {
            Scanner s = new Scanner(is).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }


       public void init() throws JSchException {

           if(port==-1)
               session = ssh.getSession(username, hostname);
           else
               session = ssh.getSession(username,hostname,port);
           session.setConfig("StrictHostKeyChecking", "no");
           session.setPassword(password);
           //  session.setUserInfo();


           System.out.println("Connection establishing...");
           session.connect();
           System.out.println("Connection established.");

       }


        public void disconnect(){

            if (channel != null) {
                channel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }



