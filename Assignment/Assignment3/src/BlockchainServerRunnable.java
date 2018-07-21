import java.io.*;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

public class BlockchainServerRunnable implements Runnable{

    private Socket clientSocket;
    private Blockchain blockchain;
    private Block headblock;
    private Block previousblock;
    private Block currblock;
    private HashMap<ServerInfo, Date> serverStatus;
    private String localIP = "";
    private String remoteIP = "";
    private int remotePort = 0;
    private int localPort = 0;
    private int blockchain_size;

    public BlockchainServerRunnable(Socket clientSocket, Blockchain blockchain, HashMap<ServerInfo, Date> serverStatus) {
        this.clientSocket = clientSocket;
        this.blockchain = blockchain;
        this.serverStatus = serverStatus;
        this.blockchain_size = this.blockchain.getLength();
        this.headblock = this.blockchain.getHead();
        this.previousblock = new Block();
        this.currblock = new Block();
    }

    public void run() {
        try {
            serverHandler(clientSocket.getInputStream(), clientSocket.getOutputStream());
            clientSocket.close();
        } catch (IOException e) {
        }
    }

    public int compares(byte[] hashcode1, byte[] hashcode2){
        if(hashcode1.length == hashcode2.length){
            for (int i = 0; i < hashcode1.length; i++){
                if(hashcode1[i] < hashcode2[i]){
                    return 1;
                }
                else if(hashcode1[i] < hashcode2[i]){
                    return -1;
                }
                else{
                    return 0;        
                }
            }
        }
        return 0;
    }

    public void serverHandler(InputStream clientInputStream, OutputStream clientOutputStream) {

        localIP = (((InetSocketAddress) clientSocket.getLocalSocketAddress()).getAddress()).toString().replace("/", "");
        remoteIP = (((InetSocketAddress) clientSocket.getRemoteSocketAddress()).getAddress()).toString().replace("/", "");
        remotePort = ((InetSocketAddress) clientSocket.getRemoteSocketAddress()).getPort();
        localPort = ((InetSocketAddress) clientSocket.getLocalSocketAddress()).getPort();


        BufferedReader inputReader = new BufferedReader(
                new InputStreamReader(clientInputStream));
        PrintWriter outWriter = new PrintWriter(clientOutputStream, true);

        try {
            while (true) {
                String inputLine = inputReader.readLine();
                if (inputLine == null) {
                    break;
                }

                String[] tokens = inputLine.split("\\|");
                switch (tokens[0]) {
                    case "tx":
                        if (blockchain.addTransaction(inputLine))
                            outWriter.print("Accepted\n\n");
                        else
                            outWriter.print("Rejected\n\n");
                        outWriter.flush();
                        break;
                    case "pb":
                        outWriter.print(blockchain.toString() + "\n");
                        outWriter.flush();
                        break;
                    case "hb":
                        
                        serverStatus.put(new ServerInfo(remoteIP, Integer.parseInt(tokens[1])), new Date());
                        if (tokens[2].equals("0")) {

                            ArrayList<Thread> threadArrayList = new ArrayList<>();

                            for (ServerInfo ip : serverStatus.keySet()) {
                                if( ip.equals(new ServerInfo(remoteIP, Integer.parseInt(tokens[1]))) 
                                    || ip.equals(new ServerInfo(localIP, localPort)) ){
                                    continue;
                                }
                                
                                Thread thread = new Thread(new BlockchainClientRunnable(ip, "si|"+localPort+"|"+remoteIP+"|"+tokens[1]));

                                threadArrayList.add(thread);
                                thread.start();
                            }
                            for (Thread thread : threadArrayList) {    
                                thread.join();
                            }
                        }
                        break;

                    case "si":
                        if(!serverStatus.keySet().contains(new ServerInfo(tokens[2], Integer.parseInt(tokens[3])))){
                            serverStatus.put(new ServerInfo(tokens[2], Integer.parseInt(tokens[3])), new Date());

                            //relay
                            ArrayList<Thread> threadArrayList = new ArrayList<>();
                            
                            for (ServerInfo ip : serverStatus.keySet()) {
                                if(ip.equals(new ServerInfo(remoteIP, Integer.parseInt(tokens[1]))) 
                                    || ip.equals(new ServerInfo(localIP, localPort)) 
                                    || ip.equals(new ServerInfo(tokens[2], Integer.parseInt(tokens[3]))) ){
                                    continue;
                                }
                                Thread thread = new Thread(new BlockchainClientRunnable(ip, "si|"+localPort+"|"+tokens[2]+"|"+tokens[3]));
                                threadArrayList.add(thread);
                                thread.start();
                            }
                            for (Thread thread : threadArrayList) {
                                thread.join();
                            } 
                        }
                        break;
                    case "lb":
                        
                        Block block = new Block();
                        byte[] decodedHash = Base64.getDecoder().decode(tokens[3]);
                        // System.out.println(decodedHash.size());
                        try{
                            
                            if(Integer.parseInt(tokens[2]) > this.blockchain.getLength()){

                                //send the cu message when the peer has no blocks.
                                if(headblock == null){
                                    Socket lbserver = new Socket();
                                    lbserver.connect(new InetSocketAddress(remoteIP, Integer.parseInt(tokens[1])), 2000);
                                    PrintWriter writer = new PrintWriter(lbserver.getOutputStream(), true);
                                    //send the cu message for catching up the loss head block
                                    writer.println("cu");
                                    writer.flush();
                                    ObjectInputStream inputstream = new ObjectInputStream(lbserver.getInputStream());
                                    block = (Block) inputstream.readObject();
                                    
                                    this.blockchain.setHead(block);
                                    headblock = this.blockchain.getHead();
                                    headblock.setCurrentHash(block.calculateHash());
                                    previousblock = headblock.getPreviousBlock();
                                    blockchain_size++;
                                    this.blockchain.setLength(blockchain_size);
                                    currblock = headblock;
                                    
                                    inputstream.close();
                                    writer.close();
                                    lbserver.close();
                                }
                                
                                //if there is any previous block before the catched-up head block then we add in
                                while(block.getPreviousHash() != null){
                                    Socket lbserver = new Socket();
                                    lbserver.connect(new InetSocketAddress(remoteIP, Integer.parseInt(tokens[1])), 2000);
                                    PrintWriter writer = new PrintWriter(lbserver.getOutputStream(), true);

                                    //send the cu message for catching up the rest of the blocks except the head.
                                    writer.println("cu|" + Base64.getEncoder().encodeToString(block.getPreviousHash()));
                                    ObjectInputStream inputstream = new ObjectInputStream(lbserver.getInputStream());
                                    block = (Block) inputstream.readObject();
                                    currblock.setPreviousBlock(block);
                                    currblock = currblock.getPreviousBlock();
                                    blockchain_size++;
                                    this.blockchain.setLength(blockchain_size);
                                    inputstream.close();
                                    writer.close();
                                    lbserver.close();
                                }
                                
                                if(previousblock == null || previousblock.getPreviousBlock() == null){
                                    //send the cu message for catching up the new updated the block
                                    Socket lbserver = new Socket();
                                    lbserver.connect(new InetSocketAddress(remoteIP, Integer.parseInt(tokens[1])), 2000);
                                    PrintWriter writer = new PrintWriter(lbserver.getOutputStream(), true);
                                    writer.println("cu|" + tokens[3]);
                                    writer.flush();

                                    ObjectInputStream inputstream = new ObjectInputStream(lbserver.getInputStream());
                                    block = (Block) inputstream.readObject();
                                    Block oldhead = headblock;
                                    this.blockchain.setHead(block);
                                    this.blockchain.getHead().setPreviousBlock(oldhead);
                                    previousblock = previousblock.getPreviousBlock();
                                    blockchain_size++;
                                    this.blockchain.setLength(blockchain_size);
                                    inputstream.close();
                                    writer.close();
                                    lbserver.close();
                                } 
                            }
                            else if(Integer.parseInt(tokens[2]) == this.blockchain.getLength() && compares(decodedHash, headblock.calculateHash())==1){
                                //send the cu message when the peer has no blocks.
                                if(headblock == null){
                                    Socket lbserver = new Socket();
                                    lbserver.connect(new InetSocketAddress(remoteIP, Integer.parseInt(tokens[1])), 2000);
                                    PrintWriter writer = new PrintWriter(lbserver.getOutputStream(), true);
                                    //send the cu message for catching up the loss head block
                                    writer.println("cu");
                                    writer.flush();
                                    ObjectInputStream inputstream = new ObjectInputStream(lbserver.getInputStream());
                                    block = (Block) inputstream.readObject();
                                    
                                    this.blockchain.setHead(block);
                                    headblock = this.blockchain.getHead();
                                    headblock.setCurrentHash(block.calculateHash());
                                    previousblock = headblock.getPreviousBlock();
                                    blockchain_size++;
                                    this.blockchain.setLength(blockchain_size);
                                    currblock = headblock;
                                    inputstream.close();
                                    writer.close();
                                    lbserver.close();
                                }

                                //if there is any previous block before the catched-up head block then we add in
                                while(block.getPreviousHash() != null){
                                    Socket lbserver = new Socket();
                                    lbserver.connect(new InetSocketAddress(remoteIP, Integer.parseInt(tokens[1])), 2000);
                                    PrintWriter writer = new PrintWriter(lbserver.getOutputStream(), true);
                                    //send the cu message for catching up the rest of the blocks except the head.
                                    writer.println("cu|" + Base64.getEncoder().encodeToString(block.getPreviousHash()));
                                    ObjectInputStream inputstream = new ObjectInputStream(lbserver.getInputStream());
                                    block = (Block) inputstream.readObject();
                                    currblock.setPreviousBlock(block);
                                    currblock = currblock.getPreviousBlock();
                                    blockchain_size++;
                                    this.blockchain.setLength(blockchain_size);
                                    inputstream.close();
                                    writer.close();
                                    lbserver.close();
                                }
                                
                                if(previousblock == null || previousblock.getPreviousBlock() == null){
                                    //send the cu message for catching up the new updated the block
                                    Socket lbserver = new Socket();
                                    lbserver.connect(new InetSocketAddress(remoteIP, Integer.parseInt(tokens[1])), 2000);
                                    PrintWriter writer = new PrintWriter(lbserver.getOutputStream(), true);
                                    writer.println("cu|" + tokens[3]);
                                    writer.flush();

                                    ObjectInputStream inputstream = new ObjectInputStream(lbserver.getInputStream());
                                    block = (Block) inputstream.readObject();
                                    Block oldhead = headblock;
                                    this.blockchain.setHead(block);
                                    this.blockchain.getHead().setPreviousBlock(oldhead);
                                    previousblock = previousblock.getPreviousBlock();
                                    blockchain_size++;
                                    this.blockchain.setLength(blockchain_size);
                                    inputstream.close();
                                    writer.close();
                                    lbserver.close();
                                }
                            }
                        }
                        catch (ClassNotFoundException e){
                        }
                        catch (IOException e) {
                        }

                        break;
                    case "cu":
                        if(tokens.length == 1){
                            try{
                                
                                ObjectOutputStream outputstream = new ObjectOutputStream(clientSocket.getOutputStream());
                                if(this.blockchain.getHead() == null){
                                    outputstream.writeObject(this.blockchain.getHead());
                                    outputstream.flush();
                                    outputstream.close();
                                }
                                else{
                                    outputstream.writeObject(this.blockchain.getHead());
                                    outputstream.flush();
                                    outputstream.close();
                                    
                                }
                            }
                            catch(IOException e){
                            }
                        }
                        else if (tokens.length == 2){
                            try{
                                
                                decodedHash = Base64.getDecoder().decode(tokens[1]);
                                ObjectOutputStream outputstream = new ObjectOutputStream(clientSocket.getOutputStream());
                                if(Arrays.equals(decodedHash, this.blockchain.getHead().calculateHash()) == true){
                                    outputstream.writeObject(this.blockchain.getHead());
                                    outputstream.flush();
                                }

                                previousblock = this.blockchain.getHead().getPreviousBlock();
                                while(previousblock != null){
                                    if(Arrays.equals(previousblock.calculateHash(),decodedHash) == true){
                                        outputstream.writeObject(previousblock);
                                        outputstream.flush();
                                    }
                                    previousblock = previousblock.getPreviousBlock();
                                }
                                outputstream.close();
                            
                            }
                            catch(IOException e){
                            }
                        }
                        break;
                    case "cc":
                        return;
                    default:
                        outWriter.print("Error\n\n");
                        outWriter.flush();
                }
            }
        } catch (IOException e) {
        } 
        catch (InterruptedException e) {
            }
    }
}
