package gad.hiai.chat.hiaichat;

public class ListModel {

    private  String DeviceName="";
    private  String StatusDevice="";

    public  ListModel (String DeviceName, String StatusDevice) {
        this.DeviceName = DeviceName;
        this.StatusDevice = StatusDevice;
    }

    /*********** Set Methods ******************/

    public void setDeviceName(String DeviceName)
    {
        this.DeviceName = DeviceName;
    }

    /*********** Get Methods ****************/

    public String getDeviceName()
    {
        return this.DeviceName;
    }

    /*********** Set Methods ******************/

    public void setStatusDevice(String StatusDevice)
    {
        this.StatusDevice = StatusDevice;
    }

    /*********** Get Methods ****************/

    public String getStatusDevice()
    {
        return this.StatusDevice;
    }

}
