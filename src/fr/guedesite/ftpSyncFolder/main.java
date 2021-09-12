package fr.guedesite.ftpSyncFolder;

import java.io.File;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class main {

	public String[] ignore = {".settings",".buildpath",".project","bdd","license","readme",".git",".github"};
	public ftpConnection Connection;
	public GuiSelect Gui;
	
	private FileModif[] allFile;
	private Supplier<Stream<FileModif>> Stream;
	
	public static main Instance;
	
	public static void main(String[] args) {
		Instance = new main();
	}
	
	
	public main() {
		this.Gui = new GuiSelect();
	}

	
	public boolean initConnection(String ip, String user, String pass) {
		Connection = new ftpConnection(ip, user, pass);
		try  {
			
			Connection.Connect();
			return true;
		}  catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean initFile(File f) {
		if(f.isDirectory()) {
			int i = 0;
			i = loopInitFile(f,i);
			allFile = new FileModif[i];
			
			System.out.println("init "+ i +" files");
			
			loopInitFile(f,0);
			Stream = () -> Arrays.stream(allFile);
			
			return true;
		}
		return false;
	}
	
	public void start() {
		long time = System.currentTimeMillis();
		Connection.addFile(Stream.get().filter(x -> x.hasChange()).collect(Collectors.toList()));
		System.out.println((System.currentTimeMillis() - time) +"ms");
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		start();
	}
	private int loopInitFile(File f, int i) {
		for(File f1: f.listFiles()) {
			boolean flag = false;
			
			for(String ig : ignore) {
				if(f1.getName().toLowerCase().startsWith(ig)) {
					flag = true;
					break;
				}
			}
			if(flag) {
				continue;
			}
			
			
			if(allFile != null) {
				allFile[i]=new FileModif(f1);
			}
			i++;
			if(f1.isDirectory()) {
				i = loopInitFile(f1,i);
			}
		}
		return i;
	}
	
	public class FileModif {
		public File f;
		public long last;
		public FileModif(File f2) {
			this.f=f2;
			this.last = f2.lastModified();
		}
		
		public boolean hasChange() {
			if(f.lastModified() != last) {
				System.out.println("CHANGE "+f.getName());
				return true;
			} else {
				return false;
			}
		}
	}
}