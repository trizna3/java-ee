package src;

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
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class VravClient extends Applet implements Runnable
{
	private static final long serialVersionUID = -4297335882692216363L;

	private Socket socket;
	
	/* Map<Descriptor,Name> */
	private Map<Integer,String> allClientNames = new HashMap<Integer, String>();
	private Map<Integer,TextArea> textAreas = new HashMap<Integer, TextArea>();
	
	private String clientName;
	private TextArea nameArea;
	private Button nameButton;

	//	private boolean zapisuje = false;
	private Thread th;
	private boolean nameSet = false;
	private DataInputStream rd;
	private DataOutputStream wr;
	
	public void init()
	{
		refreshCanvas();
		createConnection();
		th = new Thread(this);
		th.start();
	}
	
	private void refreshCanvas() {
		if (!nameSet) {
			nameArea = new TextArea(2,50);
			nameButton = new Button("Submit");
			nameButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String name = nameArea.getText();
					if (name != null && name.trim().length() > 1) {
						clientName = name;
						allClientNames.put(0, name);
						nameSet = true;
					}
				}
			});
			
			add(new Label("Zadaj meno:"));
			add(nameArea);
			add(nameButton);
			
		} else {
			removeAll();
			textAreas.put(0, new TextArea());
			for (int client : textAreas.keySet()) {
				add(new Label(allClientNames.get(client)));
				TextArea ta = textAreas.get(client);
				add(ta);
				ta.setEditable(client == 0);
			}
			listenery();
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
	
//	public void paint() {
//		
//	}

	public void listenery() {
		textAreas.get(0).addTextListener(new TextListener(){
			@Override
			public void textValueChanged(TextEvent arg0) {
				String messageOut = textAreas.get(0).getText();
				sendMessage(messageOut);
			}			
		});
	}	

	public void run() {
		// wait for user to fill name
		while(true) {
			try {
				if (nameSet) {
					refreshCanvas();
					break;
				}
				Thread.sleep(100);
			} catch (InterruptedException e) {
				break;
			}
		}
		
		// run applet after name was set
		sendLogon();
		

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
	
	private void receiveMessage(VravResponse response) {
		int client = response.getClientDescriptor();
		String message = response.getMessage();
		
		TextArea ta = textAreas.get(client);
		if (ta != null) {
			ta.setText(message);
		}
	}
	
	private void sendLogon() {
		sendRequest(VravHeader.HEADER_LOGON, clientName == null ? "" : clientName);
	}
	
	private void receiveLogon(VravResponse response) {
		int client = response.getClientDescriptor();
		String name = response.getMessage();
		
		textAreas.put(client,new TextArea());
		allClientNames.put(client, name);
		
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
	
	private void sendRequest(VravHeader header, String request) {
		try {
			wr.writeUTF(VravCommunicationUtil.createServiceRequest(header, request));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean receiveResponse() {
		try {
			String text = rd.readUTF();
			VravCommunicationUtil.log("Client: text read = \"" + text + "\"");
			
			VravResponse request = VravCommunicationUtil.parseServiceResponse(text);
			
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
		if (VravHeader.HEADER_LOGOFF == response.getHeader()) {
			receiveLogoff(response);
			return;
		}
	}
}
