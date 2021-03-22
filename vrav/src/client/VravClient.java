package src.client;

import java.applet.Applet;
import java.awt.Button;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import src.common.VravCommunicationUtil;
import src.common.VravCryptedCommunicator;
import src.common.VravHeader;
import src.common.VravResponse;
import src.common.VravTextDiffUtil;
import src.common.VravTextTransport;
import src.server.VravServer;

public class VravClient extends Applet implements Runnable, VravCryptedCommunicator
{
	private static final long serialVersionUID = -4297335882692216363L;

	// communication
	private Socket socket;
	private DataInputStream rd;
	private DataOutputStream wr;
	
	// client maps
	private Map<Integer,String> allClientNames = new HashMap<Integer, String>();
	private Map<Integer,TextArea> textAreas = new HashMap<Integer, TextArea>();
	
	// name setting components
	private String clientName;
	private String clientPassword;
	private TextArea nameArea;
	private TextArea passwordArea;
	private Button nameButton;
	private boolean nameSet = false;
	
	private Button getActivitiesButton;
	
	private int logonPassed = 0;
	private static final int LOGON_NOT_PASSED = 0;
	private static final int LOGON_WAIT = 1;
	private static final int LOGON_PASSED = 2;
	
	// keep record of last text state, to compute difference with newest state
	private String myAreaText = null;
	
	public void init()
	{
		refreshCanvas();
		createConnection();
		new Thread(this).start();
	}
	
	private void refreshCanvas() {
		removeAll();
		if (logonPassed != LOGON_PASSED) {
			nameArea = new TextArea(2,50);
			passwordArea = new TextArea(2,50);
			nameButton = new Button("Submit");
			nameButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String name = nameArea.getText();
					if (name != null && name.trim().length() > 0) {
						clientName = name;
						allClientNames.put(0, name);
						nameSet = true;
					}
					
					String password = passwordArea.getText();
					if (password != null && password.trim().length() > 0) {
						clientPassword = password;
						nameSet = true;
					}
					
					sendLogon();
				}
			});
			
			add(new Label("Zadaj meno:"));
			add(nameArea);
			add(new Label("Zadaj heslo:"));
			add(passwordArea);
			add(nameButton);
		} else {
			if (VravCommunicationUtil.ADMIN_USERNAME.equals(clientName)) {
				getActivitiesButton = new Button("Get Activities");
				getActivitiesButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						sendGetActivities();
					}
				});
				add(getActivitiesButton);
			}
			textAreas.put(0, new TextArea());
			for (int client : textAreas.keySet()) {
				add(new Label(allClientNames.get(client)));
				TextArea ta = textAreas.get(client);
				add(ta);
				ta.setEditable(client == 0);
			}
			setupTextListeners();
		}
		revalidate();
	}
	
	private void createConnection() {
		try {
		    try {
			    socket = new Socket("localhost", VravServer.PORT);
			    System.out.println("Client socket created.");
		    } catch (ConnectException e) {
		    	System.out.println("Client: unable to establish connection with the server.");
			}
		    wr = new DataOutputStream(socket.getOutputStream());
		    rd = new DataInputStream(socket.getInputStream());
		    
		    
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setupTextListeners() {
		textAreas.get(0).addTextListener(new TextListener(){
			@Override
			public void textValueChanged(TextEvent arg0) {
				String newText = textAreas.get(0).getText();
				String oldText = myAreaText;
				
				if (newText == null) {
					newText = "";
				}
				if (oldText == null) {
					oldText = "";
				}
				if (VravCommunicationUtil.EFFECTIVE_TALK_FlAG) {
					if (!oldText.equals(newText)) {
						try {
							VravTextTransport textTransport = VravTextDiffUtil.prepareTextTransport(oldText, newText);
							sendTextModification(textTransport);
						} catch (StringIndexOutOfBoundsException e) {
						}
					}
				} else {
					sendMessage(newText);
				}
				
				myAreaText = newText;
			}			
		});
	}	

	public void run() {
		// wait for user to fill name
		while (logonPassed != LOGON_PASSED) {
			while(logonPassed == LOGON_NOT_PASSED) {
				try {
					if (nameSet) {
						refreshCanvas();
						nameSet = false;
						logonPassed = LOGON_WAIT;
						break;
					}
					Thread.sleep(100);
				} catch (InterruptedException e) {
					break;
				}
			}
			while (logonPassed == LOGON_WAIT) {
				receiveResponse();
			}
		}
		
		while(receiveResponse()) {
		}
		System.out.println("Koniec rozhovoru.");
		sendLogoff();
		try {
  		  socket.close();
		} catch (Exception e) { 
			e.printStackTrace();
		}
	}
	
	private void sendMessage(String message) {
		sendRequest(VravHeader.HEADER_MESSAGE, message);
	}
	
	private void sendTextModification(VravTextTransport textTransport) {
		sendRequest(textTransport.getHeader(), VravCommunicationUtil.createTextTransportRaw(textTransport));
	}
	
	private void receiveMessage(VravResponse response) {
		int client = response.getClientDescriptor();
		String message = response.getMessage();
		
		TextArea ta = textAreas.get(client);
		if (ta != null) {
			ta.setText(message);
		}
	}
	
	private void sendLogon() {
		String name = clientName != null ? clientName : "";
		String pass = clientPassword != null ? clientPassword : "";
		
		sendRequest(VravHeader.HEADER_LOGON, name + ";" + pass);
	}
	
	private void sendGetActivities() {
		sendRequest(VravHeader.HEADER_GET_ACTIVITIES,"");
	}
	
	private void receiveLogon(VravResponse response) {
		int client = response.getClientDescriptor();
		String name = response.getMessage();
		
		textAreas.put(client,new TextArea());
		allClientNames.put(client, name);
		
		refreshCanvas();
	}
	
	private void receiveLogonResp(VravResponse response) {
		Boolean passed = Boolean.valueOf(response.getMessage());
		
		if (Boolean.TRUE.equals(passed)) {
			logonPassed = LOGON_PASSED;
		} else {
			System.out.println("Logon rejected, password does not match actor's stored password.");
			logonPassed = LOGON_NOT_PASSED;
		}
		refreshCanvas();
	}
	
	private void sendLogoff() {
		sendRequest(VravHeader.HEADER_LOGOFF, "");
	}
	
	private void receiveLogoff(VravResponse response) {
		int client = response.getClientDescriptor();
		
		textAreas.remove(client);
		allClientNames.remove(client);
		
		refreshCanvas();
	}
	
	private void receiveGetActivities(VravResponse response) {
		System.out.println("*** Vrav activities ***");
		System.out.println(response.getMessage());
	}
	
	private void receiveTextModification(VravResponse response) {
		try {
			VravTextTransport transport = VravCommunicationUtil.parseTextTransportRaw(response.getHeader(), response.getMessage());
			
			int client = response.getClientDescriptor();
			int from = transport.getFrom();
			int to = transport.getTo();
			String text = transport.getText();
			
			TextArea ta = textAreas.get(client);
			String currentText = ta.getText();
			if (ta != null) {
				if (VravHeader.HEADER_ADD_TEXT == transport.getHeader()) {
					StringBuilder sb = new StringBuilder(currentText);
					sb.insert(from, text);
					ta.setText(sb.toString());
					return;
				} else if (VravHeader.HEADER_REMOVE_TEXT == transport.getHeader()) {
					StringBuilder sb = new StringBuilder(currentText);
					sb.replace(from, to, "");
					ta.setText(sb.toString());
					return;
				} else if (VravHeader.HEADER_REPLACE_TEXT == transport.getHeader()) {
					StringBuilder sb = new StringBuilder(currentText);
					sb.replace(from, to, text);
					ta.setText(sb.toString());
					return;
				}
			}
		} catch (StringIndexOutOfBoundsException e) {
			return;
		}
	}
	
	private void sendRequest(VravHeader header, String request) {
		try {
			String message = VravCommunicationUtil.createServiceRequest(header, request);
			String signature = signMessage(message);
			String signedMessage = VravCommunicationUtil.appendSignature(signature, message);
			for (String chunk : VravCommunicationUtil.chunkize(signedMessage)) {
				wr.writeUTF(encrypt(chunk));
			}
			wr.writeUTF(VravCommunicationUtil.END_MESSAGE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean receiveResponse() {
		try {
			StringBuilder chunksDecrypted = new StringBuilder();
	    	while (true) {
	    		String msgChunk = rd.readUTF();
	    		if (VravCommunicationUtil.END_MESSAGE.equals(msgChunk)) {
	    			break;
	    		}
	    		chunksDecrypted.append(decrypt(msgChunk));
	    	}
	    	String text = chunksDecrypted.toString();
	    	
			VravCommunicationUtil.log("Client: text read = \"" + text + "\"");
			
			String signature = VravCommunicationUtil.parseSignature(text);
	    	String message = VravCommunicationUtil.parseMessage(text);
			
			if (!evaluateSignature(message,signature)) {
	    		throw new IllegalStateException("Signature evalution failed!");
	    	}
			
			VravResponse request = VravCommunicationUtil.parseServiceResponse(message);
			
			processResponse(request);
		} catch (IOException e1) {
			return false;
		} 
		return true;
	}
	
	private void processResponse(VravResponse response) {
		if (VravHeader.HEADER_MESSAGE == response.getHeader()) {
			receiveMessage(response);
			return;
		}
		if (VravHeader.HEADER_LOGON == response.getHeader()) {
			receiveLogon(response);
			return;
		}
		if (VravHeader.HEADER_LOGON_RESP == response.getHeader()) {
			receiveLogonResp(response);
			return;
		}
		if (VravHeader.HEADER_LOGOFF == response.getHeader()) {
			receiveLogoff(response);
			return;
		}
		if (VravHeader.HEADER_ADD_TEXT == response.getHeader() || VravHeader.HEADER_REMOVE_TEXT == response.getHeader() || VravHeader.HEADER_REPLACE_TEXT == response.getHeader()) {
    		receiveTextModification(response);
    		return;
    	}
		if (VravHeader.HEADER_GET_ACTIVITIES == response.getHeader()) {
			receiveGetActivities(response);
			return;
		}
	}
	
	public PublicKey getPublicKey() {
        ObjectInputStream in = null;
        PublicKey publicKey = null;
		
        try {
			in = new ObjectInputStream(new FileInputStream(VravServer.SERVER_MSG_PUBLIC_KEY_PATH));
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
			in = new ObjectInputStream(new FileInputStream(VravServer.CLIENT_MSG_PRIVATE_KEY_PATH));
	        publicKey = (PrivateKey) in.readObject(); 

	        in.close(); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return publicKey;
	}
}
