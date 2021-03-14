package src;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;


public class VravServer {

    final static int PORT = 2005;
    
    public static final String SERVER_MSG_PUBLIC_KEY_PATH = "srvPublicKey";
    public static final String SERVER_MSG_PRIVATE_KEY_PATH = "srvPrivateKey";
    
    public static final String CLIENT_MSG_PUBLIC_KEY_PATH = "clientPublicKey";
    public static final String CLIENT_MSG_PRIVATE_KEY_PATH = "clientPrivateKey";

    private Map<Integer,VravClientManager> clientManagers = new HashMap<Integer, VravClientManager>();
    
    public static void main(String[] args) {
    	generateKeyPair();
        new VravServer().createConnection();
    }
    
    public synchronized void sendMessageToAllOthers(int fromDescriptor, String message) {
    	for (int id : clientManagers.keySet()) {
			if (id == fromDescriptor) {
				continue;
			}
			clientManagers.get(id).sendMessage(fromDescriptor, message);
		}
    }
    
    public synchronized void sendLogonToAllOthers(int fromDescriptor) {
    	for (int id : clientManagers.keySet()) {
			if (id == fromDescriptor) {
				continue;
			}
			clientManagers.get(id).sendLogon(fromDescriptor,clientManagers.get(fromDescriptor).getName());
		}
    }
    
    public synchronized void sendLogoffToAllOthers(int fromDescriptor) {
    	for (int id : clientManagers.keySet()) {
			if (id == fromDescriptor) {
				continue;
			}
			clientManagers.get(id).sendLogoff(fromDescriptor);
		}
    	
    	// remove clientManager of the client which logged off.
    	clientManagers.remove(fromDescriptor);
    }
    
    public synchronized void sendTextModificationToAllOthers(int fromDescriptor, VravTextTransport transport) {
    	for (int id : clientManagers.keySet()) {
			if (id == fromDescriptor) {
				continue;
			}
			clientManagers.get(id).sendTextModification(fromDescriptor,transport);
		}
    }

    private boolean createConnection() {
        Socket socket = null;	
        ServerSocket srv = null;
        try {
        	srv = new ServerSocket(PORT);
        	System.out.println("Server started. Listening for clients.");
            while (true) {
            	socket = srv.accept();
            	processNewClient(socket);
            }
        } catch (UnknownHostException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
        }
        
        return true;
    }
    
    private void processNewClient(Socket socket) {
    	int newId = getNewClientDescriptor();
        System.out.println("New client (id=" + newId + ") arrived.");
        clientManagers.put(newId,new VravClientManager(newId,VravCommunicationUtil.generateName(newId),socket,this));
        // New client will receive logonResponse for each other active client
        for (int id : clientManagers.keySet()) {
        	if (id == newId) {
        		continue;
        	}
        	VravClientManager actualMng = clientManagers.get(id);
        	clientManagers.get(newId).sendLogon(actualMng.getDescriptor(), actualMng.getName());
		}
    }
    
    private int getNewClientDescriptor() {
    	return clientManagers.size() + 1;
    }
    
    private static void generateKeyPair() {
    	generateKeyPairInternal(SERVER_MSG_PUBLIC_KEY_PATH, SERVER_MSG_PRIVATE_KEY_PATH);
    	generateKeyPairInternal(CLIENT_MSG_PUBLIC_KEY_PATH, CLIENT_MSG_PRIVATE_KEY_PATH);
    }
    
    private static void generateKeyPairInternal(String publicKeyPath, String privateKeyPath) {
    	try {
    		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
    		keyPairGen.initialize(2048);
    		KeyPair keyPair = keyPairGen.generateKeyPair();

    		ObjectOutputStream privateWriter = new ObjectOutputStream(new FileOutputStream(privateKeyPath));  
            privateWriter.writeObject(keyPair.getPrivate());
            privateWriter.close(); 
            
            ObjectOutputStream publicWriter = new ObjectOutputStream(new FileOutputStream(publicKeyPath));  
            publicWriter.writeObject(keyPair.getPublic());
            publicWriter.close();
		} catch (NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		}
    }
}
