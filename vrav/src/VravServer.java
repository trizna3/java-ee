package src;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;


public class VravServer {

    final static int PORT = 2005;

    private Map<Integer,VravClientManager> clientManagers = new HashMap<Integer, VravClientManager>();
    
    public static void main(String[] args) {
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
}
