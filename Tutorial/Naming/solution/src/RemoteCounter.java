import java.rmi.*;

public interface RemoteCounter extends Remote {
    public int inc() throws RemoteException;
    public int getValue() throws RemoteException;
}