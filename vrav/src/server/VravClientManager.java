/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package src.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.SQLException;
import java.util.List;

import src.common.VravCommunicationUtil;
import src.common.VravCryptedCommunicator;
import src.common.VravHeader;
import src.common.VravRequest;
import src.common.VravTextTransport;
import src.server.rdg.Activity;
import src.server.rdg.ActivityDAO;
import src.server.rdg.ActivityType;
import src.server.rdg.Actor;
import src.server.rdg.ActorDAO;
import src.server.rdg.UserLevel;

public class VravClientManager implements Runnable, VravCryptedCommunicator
{
	private VravServer server;
	private int descriptor;
	private String name;
	private String password;
    private DataInputStream rd;
    private DataOutputStream wr;

    public VravClientManager(int descriptor, String name, Socket socket, VravServer server) {
    	this.server = server;
    	this.descriptor = descriptor;
    	this.name = name;
        try {
			rd = new DataInputStream(socket.getInputStream());
	        wr = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            System.out.println("Error obtaining input/output streams from socket.");
        }
        new Thread(this).start();
    }
    
    @Override
    public void run() {
        while (true) {
        	try {
            	receiveRequest();
            } catch (Exception e) {
            	System.out.println("Communication with client " + getDescriptor() + " terminated.");
            	server.sendLogoffToAllOthers(getDescriptor());
                break;
            }
        }
    }
    
    public void sendLogon(int descriptor, String name) {
    	sendResponse(descriptor,VravHeader.HEADER_LOGON,name);
    }
    
    public void sendLogonResponse(int descriptor, String name, Boolean passed) {
    	sendResponse(descriptor,VravHeader.HEADER_LOGON_RESP, passed.toString());
    }
    
    public void sendGetActivities(int descriptor, String message) {
    	sendResponse(descriptor,VravHeader.HEADER_GET_ACTIVITIES, message);
    }
    
    public void sendLogoff(int descriptor) {
    	sendResponse(descriptor,VravHeader.HEADER_LOGOFF,"");
    }
    
    public void sendTextModification(int descriptor, VravTextTransport transport) {
    	sendResponse(descriptor,transport.getHeader(),VravCommunicationUtil.createTextTransportRaw(transport));
    }
    
    public void sendMessage(int descriptor, String message) {
    	sendResponse(descriptor,VravHeader.HEADER_MESSAGE,message);
    }
    
    public void sendResponse(int descriptor, VravHeader header, String message) {
    	
    	String response = VravCommunicationUtil.createServiceResponse(descriptor,header,message);
		try {
			String signature = signMessage(response);
			String signedMessage = VravCommunicationUtil.appendSignature(signature, response);
			for (String chunk : VravCommunicationUtil.chunkize(signedMessage)) {
				wr.writeUTF(encrypt(chunk));
			}
			wr.writeUTF(VravCommunicationUtil.END_MESSAGE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
    private void receiveRequest() throws IOException {
    	StringBuilder chunksDecrypted = new StringBuilder();
    	while (true) {
    		String msgChunk = rd.readUTF();
    		if (VravCommunicationUtil.END_MESSAGE.equals(msgChunk)) {
    			break;
    		}
    		chunksDecrypted.append(decrypt(msgChunk));
    	}
    	String msgDecrypted = chunksDecrypted.toString();
    	
    	VravCommunicationUtil.log("Manager: text read = \"" + msgDecrypted + "\"");
    	
    	String signature = VravCommunicationUtil.parseSignature(msgDecrypted);
    	String message = VravCommunicationUtil.parseMessage(msgDecrypted);

    	if (!evaluateSignature(message,signature)) {
    		throw new IllegalStateException("Signature evalution failed!");
    	}
		
    	VravRequest request = VravCommunicationUtil.parseServiceRequest(message);
		processRequest(request);
    }
    
    private void processRequest(VravRequest request) {
    	if (VravHeader.HEADER_MESSAGE == request.getHeader()) {
    		receiveMessage(request);
    		return;
    	}
    	if (VravHeader.HEADER_ADD_TEXT == request.getHeader() || VravHeader.HEADER_REMOVE_TEXT == request.getHeader() || VravHeader.HEADER_REPLACE_TEXT == request.getHeader()) {
    		receiveTextModification(request);
    		return;
    	}
    	if (VravHeader.HEADER_LOGON == request.getHeader()) {
    		receiveLogon(request);
    		return;
    	}
    	if (VravHeader.HEADER_GET_ACTIVITIES == request.getHeader()) {
    		receiveGetActivities(request);
    		return;
    	}
    }
    
	private void receiveMessage(VravRequest request) {
		String message = request.getMessage();
		
		server.sendMessageToAllOthers(getDescriptor(), message);
		VravDbClient.storeActivity(name, ActivityType.SEND_MESSAGE, true);
	}
	
	private void receiveLogon(VravRequest request) {
		String[] messageSplit = request.getMessage().split(";");
		String name = messageSplit[0];
		String password = messageSplit[1];
		
		this.name = name;
		this.password = password;
		
		if (validateActorOnLogon()) {
			sendLogonResponse(descriptor, name, Boolean.TRUE);
			VravDbClient.storeActivity(name,ActivityType.LOGON,true);
			server.sendLogonToAllOthers(getDescriptor());
		} else {
			sendLogonResponse(descriptor, name, Boolean.FALSE);
			VravDbClient.storeActivity(name,ActivityType.LOGON,false);
		}
    }
	
	private void receiveGetActivities(VravRequest request) {
		
		try {
			List<Activity> activities = ActivityDAO.getAllActivities();
			StringBuilder response = new StringBuilder("(actorName, timestamp, activityType, success)\n\n");
			for (Activity activity : activities) {
				response.append("(");
				response.append(activity.getActorName()+", ");
				response.append(activity.getTimestamp()+", ");
				response.append(activity.getActivityType()+", ");
				response.append(activity.getSuccess());
				response.append(")\n");
			}
			
			sendGetActivities(descriptor, response.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
	
	private void receiveTextModification(VravRequest request) {
		VravTextTransport transport = VravCommunicationUtil.parseTextTransportRaw(request.getHeader(), request.getMessage());
		server.sendTextModificationToAllOthers(getDescriptor(), transport);
	}

	public int getDescriptor() {
		return descriptor;
	}

	public String getName() {
		return name;
	}
	
	public PublicKey getPublicKey() {
        ObjectInputStream in = null;
        PublicKey publicKey = null;
		
        try {
			in = new ObjectInputStream(new FileInputStream(VravServer.CLIENT_MSG_PUBLIC_KEY_PATH));
	        publicKey = (PublicKey) in.readObject(); 

	        in.close(); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return publicKey;
	}
	
	public PrivateKey getPrivateKey() {
        ObjectInputStream in = null;
        PrivateKey publicKey = null;
		
        try {
			in = new ObjectInputStream(new FileInputStream(VravServer.SERVER_MSG_PRIVATE_KEY_PATH));
	        publicKey = (PrivateKey) in.readObject(); 

	        in.close(); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return publicKey;
	}
	
	private boolean validateActorOnLogon() {
		try {
			Actor foundActor = ActorDAO.getActorByName(name);
		
			if (foundActor == null) {
				System.out.println("Actor " + name + "not found. Creating entry.");
				Actor a = new Actor();
				a.setUsername(name);
				a.setUserLevel(UserLevel.BASIC);
				a.store();
			} else {
				// check password
				if (password == null || !password.equals(foundActor.getPassword())) {
					System.out.println("Wrong password for actor " + name + ". Rejecting logon.");
					return false;
				}
			}
		} catch (Exception e) {
			System.out.println("Error occured on querying actor");
			e.printStackTrace();
			return false;
		}
		System.out.println("Logon for actor " + name + " accepted.");
		return true;
	}
}
