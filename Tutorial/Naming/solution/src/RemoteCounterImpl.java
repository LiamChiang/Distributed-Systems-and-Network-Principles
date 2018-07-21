import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

public class RemoteCounterImpl extends UnicastRemoteObject implements
        RemoteCounter {

    int c = 0;

    public RemoteCounterImpl() throws RemoteException {}

    public int inc() throws RemoteException {
        return ++c;
    }

    public int getValue() throws RemoteException {
        return c;
    }

    public static void main(String args[]) {
        try {
            RemoteCounterImpl counterServer = new RemoteCounterImpl();
            // Bind this object instance to the name "RMICounterObject"
            Naming.rebind("RMICounterObject", counterServer);
        } catch (Exception e) { System.err.println(e); }
    }
}