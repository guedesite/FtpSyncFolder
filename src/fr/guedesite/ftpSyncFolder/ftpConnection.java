package fr.guedesite.ftpSyncFolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

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
		this.Client.connect(this.ServerIp, this.port);
		this.Client.login(this.User, this.Pass);
		this.Client.enterLocalPassiveMode();
		this.Client.setFileType(FTP.BINARY_FILE_TYPE);
		this.Client.setKeepAlive(true);
		System.out.println(this.Client.printWorkingDirectory());
		thread = new ClientThread(this.Client);

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
	
	class ClientThread {
		
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
		

		private void sendFile() {
			try {
				System.out.println(this.client.printWorkingDirectory());
				if(!this.client.changeWorkingDirectory("/")) {
					throw new Exception("Can't move in /");
				}
				
				File firstLocalFile = pending.get(0).f;

	            String firstRemoteFile = firstLocalFile.getName();
	            
	            
	            InputStream inputStream;
				inputStream = new FileInputStream(firstLocalFile);
				
				String current = firstLocalFile.getAbsolutePath().replace(main.Instance.getBase().getAbsolutePath(), "").replace(firstRemoteFile, "");
				System.out.println(current);
				while(current.indexOf("\\") != -1) {
					current = current.replace("\\", "/");
				}
				System.out.println(current);
				if(!current.equals("/")) {
					current = current.substring(1);
					followDir(Arrays.asList(current.split("/")), 0);
				}
				
	
				System.out.println(firstRemoteFile);
				System.out.println(firstLocalFile.getAbsolutePath());
	            boolean done = this.client.storeFile(firstRemoteFile, inputStream);
	            inputStream.close();
	            if (done) {
	                System.out.println("The first file is uploaded successfully.");
	                pending.get(0).last = pending.get(0).f.lastModified();
	                pending.remove(0);
	            } else {
	            	System.out.println("NOPE");
	            	 FileModif temp = new FileModif(firstLocalFile);
		                temp.last = 0;
		                pending.add(temp);
		                pending.remove(0);
	            }
	            for(String e:this.client.getReplyStrings()) {
	            	System.out.println(e);
	            }
				if(!pending.isEmpty()) {
					sendFile();
				}
			} catch (Exception e) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
				sendFile();
			}
		}
		
		
		private void followDir(List<String> ar, int index) throws Exception {
			if(index < ar.size()) {
				boolean flag = false;
				for(FTPFile a : this.client.listDirectories()) {
					System.out.println(a.getName());
					if(a.getName().equals(ar.get(index))) {
						flag = true;
						break;
					}
				}
				if(!flag) {
					if(!this.client.makeDirectory(ar.get(index))) {
						throw new Exception("Can't make directory "+ar.get(index));
					}
				}
				if(!this.client.changeWorkingDirectory(this.client.printWorkingDirectory()+"/"+ar.get(index))) {
					throw new Exception("Can't move in "+this.client.printWorkingDirectory()+"/"+ar.get(index));
				}
				System.out.println(Arrays.toString(ar.toArray()));
				index++;
				followDir(ar, index);
				
			}
		}
	}
	
}
