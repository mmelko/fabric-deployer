package org.mmelko.tools.fabric.deployer;

/**
 * Created with IntelliJ IDEA.
 * User: mmelko
 * Date: 2/21/14
 * Time: 5:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class Container {
    private boolean child=true;
    private boolean ens=false;
    private String name;
    private boolean isMQ=true;
    private boolean assignProfileAfterCreate=false;

    private String profile="default";
    private String host;
    private String sshUsername="";
    private String sshPas;

    private String networks;
    private String networkUsername;
    private String networkPassword;
    private String brokerName;
    private String brokerGroup;


    public Container(){
        this.child=true;
        this.ens=false;
        this.name="";
        this.isMQ=false;
        this.assignProfileAfterCreate=false;

        this.profile="default";
        this.host="";
        this.sshUsername="";
        this.sshPas="";
        this.networks="";
        this.networkUsername="";
        this.networkPassword="";
        this.brokerName="";
        this.brokerGroup="";
    }


    public String getCreateCommand(){
        String cmd="container-create-";
        if(child)
              cmd+="child root";
        else
            cmd+="ssh --host "+host+" --user "+sshUsername+" --password "+ sshPas;


      if(!assignProfileAfterCreate && !profile.equals("default")) {
            cmd += "--profile "+ profile;
      }

      cmd+=" "+name;

      return cmd;
    }

    public String assignProfileCommand(){

        return "";
    }

    public String createBroker() throws Exception {
        if(!isMQ)
            throw  new Exception("Container is not ment to have broker");
        String res= "mq-create --assign-container "+name+"  ";

        if (!profile.equals("default") && profile.equals(""))
            res += "--parent-profile "+ profile;
        if(!brokerGroup.equals(""))
            res += " --group "+ brokerGroup;

        if(!networks.equals("") && !networkUsername.equals("") && !networkPassword.equals("")){

          res += " --networks " + networks + " --networks-username " + networkUsername + " --networks-password "+networkPassword;

        }
       // else throw new

        if (brokerName.equals(""))
            res += " "+name;
        else res += " "+brokerName;
        return res;
    }


    //-------------------------------------------------------
    //       GETTERS AND SETTERS
    //------------------------------------------------------

    public String getSshUsername() {
        return sshUsername;
    }

    public void setSshUsername(String sshUsername) {
        this.sshUsername = sshUsername;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getSshPas() {
        return sshPas;
    }

    public void setSshPas(String sshPas) {
        this.sshPas = sshPas;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEns() {
        return ens;
    }

    public void setEns(boolean ens) {
        this.ens = ens;
    }

    public boolean isChild() {
        return child;
    }

    public void setChild(boolean child) {
        this.child = child;
    }

    public String getNetworks() {
        return networks;
    }

    public void setNetwork(String network) {
        this.networks = network;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }


    public Boolean getMQ() {
        return isMQ;
    }

    public void setMQ(Boolean MQ) {
        isMQ = MQ;
    }

    public Boolean getAssignProfileAfterCreate() {
        return assignProfileAfterCreate;
    }

    public void setAssignProfileAfterCreate(Boolean assignProfile) {
        this.assignProfileAfterCreate = assignProfile;
    }

    public void setNetworks(String networks) {
        this.networks = networks;
    }

    public String getNetworkPassword() {
        return networkPassword;
    }

    public void setNetworkPassword(String networkPassword) {
        this.networkPassword = networkPassword;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }
    public String getNetworkUsername() {
        return networkUsername;
    }

    public void setNetworkUsername(String networkUsername) {
        this.networkUsername = networkUsername;
    }

    public String getBrokerGroup() {
        return brokerGroup;
    }

    public void setBrokerGroup(String brokerGroup) {
        this.brokerGroup = brokerGroup;
    }

}
