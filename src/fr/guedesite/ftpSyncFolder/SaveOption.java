package fr.guedesite.ftpSyncFolder;

import java.io.File;
import java.io.IOException;

import fr.guedesite.utils.nbt.io.NBTUtil;
import fr.guedesite.utils.nbt.io.NamedTag;
import fr.guedesite.utils.nbt.tag.CompoundTag;



public class SaveOption {

	public String ip = "Adresse Ip";
	public String User = "utilisateur";
	public String pass = "Mot de passe";
	public File folder =null;
	
	public SaveOption() {
		File f = new File("ftpSyncOption.bat");
		if(f.exists()) {
			try {
				NamedTag tag = NBTUtil.read(f);
				CompoundTag t = (CompoundTag) tag.getTag();
				ip = t.getString("ip");
				User = t.getString("user");
				pass = t.getString("pass");
				folder = new File(t.getString("file"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void save(String ip, String user, String pass, File f) {
		
		CompoundTag tag = new CompoundTag();
		tag.putString("ip", ip);
		tag.putString("user", user);
		tag.putString("pass", pass);
		tag.putString("file", f.getAbsolutePath());
		NamedTag n = new NamedTag("save", tag);
		
		try {
			NBTUtil.write(n, "ftpSyncOption.bat");
		} catch (IOException e) {
			try {
				NBTUtil.write(n, "ftpSyncOption.bat");
			} catch (IOException e2) {
				try {
					NBTUtil.write(n, "ftpSyncOption.bat");
				} catch (IOException e3) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
}
