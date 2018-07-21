import java.rmi.*;

public class RMIClient {
    static final String server = "127.0.0.1";

    public static void main(String args[]) {
        try {
            String host = "rmi://" + server + "/RMICounterObject";

            RemoteCounter counterServer = (RemoteCounter)Naming.lookup(host);
            System.out.println(counterServer.inc());
        }
        catch (Exception e) {
            System.err.println(e);
        }
    }
}