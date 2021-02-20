/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class VravClientManager implements Runnable
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
			wr.writeUTF(response);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
    private void receiveRequest() throws IOException {
    	String textRead = rd.readUTF();
		
		VravCommunicationUtil.log("Manager: text read = \"" + textRead + "\"");
		VravRequest request = VravCommunicationUtil.parseServiceRequest(textRead);
		
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
}
