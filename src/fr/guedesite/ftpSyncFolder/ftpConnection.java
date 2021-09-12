package fr.guedesite.ftpSyncFolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import fr.guedesite.ftpSyncFolder.main.FileModif;

public class ftpConnection {

	private String ServerIp = "";
	private int port = 21;
	private String User = "";
	private String Pass = "";
	
	private FTPClient Client;
	
	private ClientThread thread;
	
	public ftpConnection(String ip, String user, String pass) {
		this.ServerIp = ip;
		this.User = user;
		this.Pass = pass;
		this.Client = new FTPClient();
	}
	
	public void Connect() throws Exception {
		this.Client.setCharset(Charset.forName("UTF-8"));
		this.Client.setConnectTimeout(5000);
		this.Client.connect(this.ServerIp, this.port);
		this.Client.login(this.User, this.Pass);
		this.Client.setKeepAlive(true);
		this.Client.enterLocalPassiveMode();
		this.Client.setFileType(FTP.BINARY_FILE_TYPE);
		thread = new ClientThread(this.Client);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.run();
	}
	
	public FTPClient getClient() {
		return this.Client;
	}
	
	public void close() throws Exception {
		if(this.Client.isConnected()) {
			this.Client.logout();
			this.Client.disconnect();
		}
	}
	
	public void addFile(List<FileModif> p ) {
		if(!p.isEmpty()) {
			thread.addPending(p);
		}
	}
	
	class ClientThread extends Thread{
		
		public FTPClient client;
		public List<FileModif> pending = new ArrayList<FileModif>();
		
		public ClientThread(FTPClient c) {
			this.client = c;
		}
		
		public void addPending(List<FileModif> p ) {
			System.out.println("add");
			boolean flag = false;
			if(pending.isEmpty()) {
				flag = true;
			}
			this.pending.addAll(p);
			if(flag) {
				sendFile();
			}
			
		}
		
		@Override
		public void run() {
			
		}
		
		private void sendFile() {
			try {
				File firstLocalFile = pending.get(0).f;
				 
	            String firstRemoteFile = firstLocalFile.getName();
	            InputStream inputStream;
				inputStream = new FileInputStream(firstLocalFile);
	
	 
	            boolean done = this.client.storeFile(firstRemoteFile, inputStream);
	            inputStream.close();
	            if (done) {
	                System.out.println("The first file is uploaded successfully.");
	            } else {
	            	System.out.println("NOP�E");
	            }
	            pending.remove(0);
				if(!pending.isEmpty()) {
					sendFile();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}