/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;

public class VravClientManager implements Runnable, VravCryptedCommunicator
{
	private VravServer server;
	private int descriptor;
	private String name;
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
    }
    
	private void receiveMessage(VravRequest request) {
		String message = request.getMessage();
		server.sendMessageToAllOthers(getDescriptor(), message);
	}
	
	private void receiveLogon(VravRequest request) {
		String name = request.getMessage();
		this.name = name;
		server.sendLogonToAllOthers(getDescriptor());
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
}
