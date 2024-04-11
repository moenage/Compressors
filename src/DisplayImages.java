import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class DisplayImages extends JFrame implements ActionListener {

	public void displayImages(BufferedImage img1, BufferedImage img2) {
		
		displayGui(img1, img2, "Original Image, ", "DCT Image");

	}
	
	public void displayGui(BufferedImage img1, BufferedImage img2, String img1Name, String img2Name) {
		CombineImages combined = new CombineImages();
		BufferedImage imgCombined;
		String imageText = "";
		if(img2 != null) {
			imgCombined = CombineImages.combinedImages(img1, img2);
			imageText = "Image left is: " + img1Name + "Image right is: " + img2Name;
		}
		else {
			imgCombined = img1;
			imageText = "Image is:" + img1Name;
		}
		
		
        JOptionPane.showMessageDialog(
        	    null, 
        	    imageText, 
        	    " ",
        	    JOptionPane.INFORMATION_MESSAGE, 
        	    new ImageIcon(imgCombined));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
