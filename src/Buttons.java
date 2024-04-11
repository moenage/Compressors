import java.awt.FlowLayout;

import java.awt.event.ActionEvent;    
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Buttons implements ActionListener {

  private enum Actions {
    AUDIO_IMPORT,
    IMAGE_IMPORT,
    EXIT_IMPORT
  }

  	JFrame frame = new JFrame("Test");
  	int imageTrue = 0;
  	int audioTrue = 0;
  	int exitTrue = 0;
  	String fileName = "";
  	
  public void Buttons_Implement () {

	
	Buttons instance = new Buttons();

    
    frame.setLayout(new FlowLayout());
    frame.setSize(300, 300);

    JButton audioImport = new JButton("Audio Import");
    audioImport.setActionCommand(Actions.AUDIO_IMPORT.name());
    audioImport.addActionListener(instance);
    frame.add(audioImport);

    JButton imageImport = new JButton("Image Import");
    imageImport.setActionCommand(Actions.IMAGE_IMPORT.name());
    imageImport.addActionListener(instance);
    frame.add(imageImport);
    
    JButton exitImport = new JButton("Exit Import");
    exitImport.setActionCommand(Actions.EXIT_IMPORT.name());
    exitImport.addActionListener(instance);
    exitImport.addActionListener(e -> {
        frame.dispose();
     });
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.add(exitImport);

    frame.setVisible(true);
  }
  
  public String returnType() {
	  if(imageTrue == 1) {
		  return "Image";
	  }
	  else if( audioTrue == 1){
		  return "Audio";
	  }
	  else {
		  return "Exit";
	  }
  }
  
  public void exitFrame() {
	  frame.dispose();
  }

  public void actionPerformed(ActionEvent evt) {
    if (evt.getActionCommand() == Actions.AUDIO_IMPORT.name()) {
//      JOptionPane.showMessageDialog();
      fileName = JOptionPane.showInputDialog(null, "File Name");
      audioTrue = 1;
      imageTrue = 0;
      File audioFile = new File(fileName);
      try {
		ReadAudio var1 = new ReadAudio(audioFile, "out" + String.valueOf(Global.audioCount) + ".wav", fileName);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
      Global.audioCount++;

        
    } 
    
    else if (evt.getActionCommand() == Actions.IMAGE_IMPORT.name()) {
      fileName = JOptionPane.showInputDialog(null, "File Name");
      audioTrue = 0;
      imageTrue = 1;
      File imageFile = new File(fileName);
      try {
		ReadImage var3 = new ReadImage(imageFile, fileName);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    }
    
    else if (evt.getActionCommand() == Actions.EXIT_IMPORT.name()) {
        System.out.println("Exited");
        exitTrue = 1;
        frame.dispose();
      }
  }
}