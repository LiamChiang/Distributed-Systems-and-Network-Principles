import java.util.Scanner;
import java.util.ArrayList;
import java.io.*;
import java.net.*;

public class ClientDosattack {

    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("File unavailable");
            return;
        }
        String configFileName = args[0];

        ServerInfoList pl = new ServerInfoList();
        pl.initialiseFromFile(configFileName);

        Scanner sc = new Scanner(System.in);

        while (true) {
            String message = sc.nextLine();
            String[] data = message.split("\\|");
            BlockchainClient client = new BlockchainClient();
            if(data.length >= 4){
                if(data[0].matches("up")){
                    int index = Integer.parseInt(data[1]);
                    String host = data[2];
                    int port = Integer.parseInt(data[3]);
                    ServerInfo info = new ServerInfo(host, port);
                    info.setHost(host);
                    info.setPort(port);
                    try{
                        if(pl.updateServerInfo(index, info) == true){
                           System.out.print("Succeeded\n\n");  
                        }else{
                            System.out.print("Failed\n\n");
                        }
                    }
                    catch(IndexOutOfBoundsException e){
                        System.err.print("Update out of range\n\n");
                    }

                }
                else if(data[0].matches("pb")){
                    ArrayList<Integer> indices = new ArrayList<Integer>();
                    for(int index = 1; index < data.length; index++){
                        int i = Integer.parseInt(data[index]);
                        if(i >= 0){
                            indices.add(i);
                        }
                        else{
                            System.out.print("Unknown Command\n\n");    
                        }
                    }
                    client.multicast(pl, indices, data[0]);
                }
                else{
                    System.out.print("Unknown Command\n\n");
                }
            }
            if(data.length == 3){
                if(data[0].matches("ad")){
                    String host = data[1];
                    int port = 0;
                    try{
                        port = Integer.parseInt(data[2]);
                        ServerInfo info = new ServerInfo(host, port);
                        info.setHost(host);
                        info.setPort(port);
                        if(pl.addServerInfo(info) == true){
                            System.out.print("Succeeded\n\n");
                        }else{
                            System.out.print("Failed\n\n");
                        }
                    }catch(NumberFormatException e){
                        System.out.print("Failed\n\n");
                    }
                }
                else if(data[0].matches("tx")){
                    client.broadcast(pl, message);
                }
                else if(data[0].matches("pb")){
                    ArrayList<Integer> indices = new ArrayList<Integer>();
                    for(int index = 1; index < data.length; index++){
                        try{
                            int i = Integer.parseInt(data[index]);
                            if(i >= 0){
                                indices.add(i);
                            }
                            else{
                                System.out.print("Unknown Command\n\n");    
                            }
                        }
                        catch(NumberFormatException e){
                            System.out.print("Failed\n\n");
                        }
                    }
                    client.multicast(pl, indices, data[0]);
                }
                else{
                    System.out.print("Unknown Command\n\n");
                }
            }
            if(data.length == 2){
                if(data[0].matches("rm")){
                    int index = 0;
                    try{
                        index = Integer.parseInt(data[1]);
                        if(pl.removeServerInfo(index)==true){
                            System.out.print("Succeeded\n\n");
                        }else{
                            System.out.print("Failed\n\n");
                        }
                    }
                    catch(NumberFormatException e){
                        System.out.print("Failed\n\n");
                    }
                }
                else if(data[0].matches("pb")){
                    try{
                        int index = Integer.parseInt(data[1]);
                        if(index >= 0){
                            ServerInfo info = pl.getServerInfos().get(index);
                            client.unicast(index, info, data[0]);
                        }
                        else{
                            System.out.print("Unknown Command\n\n");
                        }
                    }
                    catch(NumberFormatException e){
                        System.out.print("Failed\n\n");
                    }   
                }   
                else{
                    System.out.print("Unknown Command\n\n");
                }
            }

            if(data.length == 1){
                if(data[0].matches("ls")){
                    System.out.print(pl.toString()+"\n");
                }
                else if(data[0].matches("pb")){
                    for(int i = 0; i < 1000000000; i++){
                        client.broadcast(pl, data[0]);
                    }   
                }
                else if(data[0].matches("sd")){
                    sc.close();
                    return;
                }
                else if(data[0].matches("cl")){
                    if(pl.clearServerInfo()){
                        System.out.print("Succeeded\n\n");
                    }else{
                        System.out.print("Failed\n\n");   
                    }
                }
                else{
                    System.out.print("Unknown Command\n\n");
                }
            }
        }
    }

    public void unicast (int serverNumber, ServerInfo p, String message) {
        Thread threads = new Thread();
        if(p != null){
            try{
                String host = p.getHost();
                int port = p.getPort();
                threads = new Thread(new BlockchainClientRunnable(serverNumber,host,port,message));
                threads.start();
                threads.join();
            }
            catch(InterruptedException e){
                System.out.println("Interrrupted Exception!");    
                return;
            }
            catch(NumberFormatException e){
                System.out.println("Unavailable number");
                return;
            }
        }
    }

    public void broadcast (ServerInfoList pl, String message) {
        ArrayList<ServerInfo> broadcastdata = pl.getServerInfos();
        Thread threads = new Thread();
        for(int i =0; i < broadcastdata.size(); i++){
            try{
                if(broadcastdata.get(i) != null){
                    String host = broadcastdata.get(i).getHost();
                    int port = broadcastdata.get(i).getPort();
                    threads = new Thread(new BlockchainClientRunnable(i,host,port,message));
                    threads.start();
                    threads.join();
                }
            }
            catch(InterruptedException e){
                System.out.println("Unavailable thread");
                continue;
            }
            catch(NumberFormatException e){
                System.out.println("Unavailable number");
                continue;
            }
        }
    }

    public void multicast (ServerInfoList serverInfoList, ArrayList<Integer> serverIndices, String message) {
        ArrayList<ServerInfo> multicastdata = serverInfoList.getServerInfos();
        Thread threads = new Thread();
        for(int i =0; i < serverIndices.size(); i++){
            try{
                if(multicastdata.get(serverIndices.get(i)) != null){
                    String host = multicastdata.get(serverIndices.get(i)).getHost();
                    int port = multicastdata.get(serverIndices.get(i)).getPort();
                    threads = new Thread(new BlockchainClientRunnable(serverIndices.get(i),host,port,message));
                    threads.start();
                    threads.join();
                }
            }
            catch(InterruptedException e){
                System.out.println("Interrrupted Exception!");
                continue;
            }
            catch(NumberFormatException e){
                System.out.println("Unavailable number");
                continue;
            }
        }
    }
    // implement any helper method here if you need any
}