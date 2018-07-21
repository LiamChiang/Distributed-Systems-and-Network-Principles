import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

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
            Registry registry = LocateRegistry.createRegistry(1099);
            // Bind this object instance to the name "RMICounterObject"
            registry.rebind("RMICounterObject", counterServer);
        } catch (Exception e) { System.err.println(e); }
    }
}