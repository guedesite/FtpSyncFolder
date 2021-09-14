package fr.guedesite.ftpSyncFolder;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.filechooser.FileFilter;

public class GuiSelect {

	public JFrame Frame;
	public JPanel Panel;
	
	public JButton confirm, btnFile;
	public JTextField ip, user, pass;
	public JFileChooser file;
	
	public JTextField text;
	
	private File f;
	
	public GuiSelect() {
		Frame = new JFrame();
		
		Frame.setLocationRelativeTo(null);
    	Frame.setLayout(new BorderLayout());
		Frame.setResizable(false);
		Frame.setFocusable(true);
		Frame.setSize(300, 350);
	
		Frame.setTitle("Ftp Sync Folder - by guedesite");
		Frame.setUndecorated(false);
		Frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	
		Frame.setVisible(true);
		
		
		Panel = new JPanel();
		Panel.setBounds(0,0, 300, 350);
		Panel.setVisible(true);
		Panel.setFocusable(true);
		Panel.setLayout(null);
		Frame.add(Panel);
		
		
		text = new JTextField("Ftp Sync Folder");
		text.setBounds(0, 10, 300, 30);
		text.setEditable(false);
		text.setHorizontalAlignment(JTextField.CENTER);
		
		Panel.add(text);
		
		 ip = new JTextField();
		 ip.setBounds(22, 60, 240, 30);
		 ip.replaceSelection("Host Ip");
		 ip.setHorizontalAlignment(JTextField.CENTER);
		 Panel.add(ip);
		 
		 user = new JTextField();
		 user.setBounds(22, 110, 240, 30);
		 user.replaceSelection("User");
		 user.setHorizontalAlignment(JTextField.CENTER);
		 Panel.add(user);
		 
		 pass = new JTextField();
		 pass.setBounds(22, 160, 240, 30);
		 pass.replaceSelection("Password");
		 pass.setHorizontalAlignment(JTextField.CENTER);
		 Panel.add(pass);
		 
		 file = new JFileChooser();
		 file.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		 file.setAcceptAllFileFilterUsed(false);
		 
		 
		 btnFile = new JButton("Choose Folder");
		 btnFile.setBounds(22, 210, 240, 30);
		 btnFile.setHorizontalTextPosition(JButton.CENTER);
		 btnFile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (file.showOpenDialog(Frame) == JFileChooser.APPROVE_OPTION) { 

				   f= file.getSelectedFile();
				    
				}
				else {
					System.out.println("No Selection ");
				}
			}
		} );
		 
		 Panel.add(btnFile);
		 
		 confirm = new JButton("Start Sync");
		 confirm.setBounds(22, 260, 240, 30);
		 confirm.setHorizontalTextPosition(JButton.CENTER);
		 confirm.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(f != null && main.Instance.initConnection(ip.getText(), user.getText(), pass.getText()) && main.Instance.initFile(f)) {
					main.Instance.start();
					text.setText("Working");
				}else {
					text.setText("Error");
				}
			}
		} );
		 
		 Panel.add(confirm);
		 
		 reload();
		
		
	}
	
	public void setText(String q) {
		this.text.setText(q);
	}
	
	public void reload() {
		Frame.invalidate();
		Frame.validate();
		Frame.repaint();
	}
}
