import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.text.DecimalFormat;

public class ReadImage {
	
	public ReadImage(File file, String fileName) throws IOException{
		
		BufferedImage bimg = ImageIO.read(new File(fileName));
		int width = bimg.getWidth();
		int height = bimg.getHeight();
//		System.out.println("Width: " + width + "Height: " +  height);
		
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		
		try{
			  image = ImageIO.read(file);
		    }
		
		catch(IOException e){
		      System.out.println("Error Reading Image");
		}
		
		DisplayImages displayed = new DisplayImages();
		
		BufferedImage DCT_transformed_quantized_inverseImage = DCT_transformed_quantized_inverse(image, width, height);
		
		displayed.displayImages(bimg, DCT_transformed_quantized_inverseImage);
		
	}
	
	public double[][] applyDCT(double[][] eightBlock) {
		
		double sum = 0.0;
		double C1 = 0.0;
		double C2 = 0.0;
		
		double[][] returnMe = new double[8][8];
		
//		System.out.println("DCT:");
		
		for(int x = 0; x < 8; x++) {
			for(int y = 0; y < 8; y++) {
				
				sum = 0;
				
				for(int i = 0; i < 8; i++) {
					
					for(int j = 0; j < 8; j++) {
						sum = sum + eightBlock[i][j] * Math.cos(((2.0*i+1) * x * Math.PI)/16.0) * Math.cos(((2.0*j+1) * y * Math.PI)/16.0);
					}
				}
					
				if(x == 0) {
					C1 = 1/Math.sqrt(2);
				}
				else {
					C1 = 1;
				}
				
				if(y == 0) {
					C2 = 1/Math.sqrt(2);
				}
				else {
					C2 = 1;
				}
				
				returnMe[x][y] = 1/4.0 * C1 * C2 * sum;
//				System.out.printf(String.format("%.2f",returnMe[x][y]) + " ");		
				
				
			}
//			System.out.println();
		}
		
		return returnMe;
	}
	
	public int[][] applyQuant(double[][] eightBlock) {
		
//		System.out.println("Quant:");
		
		double[][]	quantTable=	{
					{1, 1, 2, 4, 8, 16, 32, 64},
					{1, 1, 2, 4, 8, 16, 32, 64},
					{2, 2, 2, 4, 8, 16, 32, 64},
					{4, 4, 4, 4, 8, 16, 32, 64},
					{8, 8, 8, 8, 8, 16, 32, 64},
					{16, 16, 16, 16, 16, 16, 32, 64},
					{32, 32, 32, 32 , 32, 32, 32, 64},
					{64, 64, 64, 64, 64, 64, 64, 64}};
		
//		double[][]	quantTable=	{
//				{5,3,4,4,4,3,5,4},
//				{4,4,5,5,5,6,7,12},
//				{8,7,7,7,7,15,11,11},
//				{9,12,13,15,18,18,17,15},
//				{20,20,20,20,20,20,20,20},
//				{20,20,20,20,20,20,20,20},
//				{20,20,20,20,20,20,20,20},
//				{20,20,20,20,20,20,20,20}};
		
		
		int[][] returnMe = new int[8][8];
		
		for(int x = 0; x < 8; x++) {
			for(int y = 0; y < 8; y++) {
				returnMe[x][y] = (int) (eightBlock[x][y] / quantTable[x][y]);
				
//				System.out.printf(returnMe[x][y] + " ");
			}
//			System.out.println();
		}
		
		
		return returnMe;
	}
	
	public double[][] applyIDCT(int[][] eightBlock) {
		
//		System.out.println("IDCT:");
		
		double sum = 0.0;
		double C1 = 0.0;
		double C2 = 0.0;
		
		double[][] returnMe = new double[8][8];
		
		for(int x = 0; x < 8; x++) {
			for(int y = 0; y < 8; y++) {
				
				sum = 0;
				
				for(int i = 0; i < 8; i++) {
					
					for(int j = 0; j < 8; j++) {
						
						if(i == 0) {
							C1 = 1/Math.sqrt(2);
						}
						else {
							C1 = 1;
						}
						
						if(j == 0) {
							C2 = 1/Math.sqrt(2);
						}
						else {
							C2 = 1;
						}
						
						sum = sum + eightBlock[i][j] * Math.cos(((2.0*x+1) * i * Math.PI)/16.0) * Math.cos(((2.0*y+1) * j * Math.PI)/16.0) * C1 * C2;
					}
					
				}
				
				returnMe[x][y] = 1/4.0 * sum;
				
//				System.out.printf(String.format("%.2f",returnMe[x][y]) + " ");
				
				
			}
//			System.out.println();
		}
		
		return returnMe;
	}
	
	public int checkRGB(int val) {
		if(val > 255) {
			return 255;
		}
		else if(val < 0) {
			return 0;
		}
		return val;
	}
	
	
	
	public BufferedImage DCT_transformed_quantized_inverse(BufferedImage image, int width, int height) throws IOException {
		
		int pixel = 0;
		int alpha = 0;
		int red = 0;
		int green = 0;
		int blue = 0;
		
		double Yr = 0.0;
		double Ug = 0.0;
		double Vb = 0.0;
		
//		PrintWriter writerAlpha = new PrintWriter("Quant Alpha Result" + Global.imageCount + ".txt", "UTF-8");
//		PrintWriter writerRed = new PrintWriter("Quant Y Result" + Global.imageCount + ".txt", "UTF-8");
//		PrintWriter writerGreen = new PrintWriter("Quant U Result" + Global.imageCount + ".txt", "UTF-8");
//		PrintWriter writerBlue = new PrintWriter("Quant V Result" + Global.imageCount + ".txt", "UTF-8");
		
		
		
		for(int x = 0; x < width; x = x + 8) {
			for(int y = 0; y < height; y = y + 8) {
				
				double[][] eightBlock = new double[8][8];
				double[][] alphaEightBlock = new double[8][8];
				double[][] redEightBlock = new double[8][8];
				double[][] greenEightBlock = new double[8][8];
				double[][] blueEightBlock = new double[8][8];
				
				for(int i = x, xi = 0; i < (x+8); i++, xi++ ) {
					for(int j = y, yj = 0; j < (y+8); j++, yj++ ) {
						
						pixel = image.getRGB(i, j);
						
						alpha = (pixel>>24)&0xff;
				        red = (pixel>>16)&0xff;
				        green = (pixel>>8)&0xff;
				        blue = pixel&0xff;
						
				        Yr =  0.257 * (double)red + 0.504 * (double)green + 0.098 * (double)blue +  16;
				        Ug = -0.148 * (double)red - 0.291 * (double)green + 0.439 * (double)blue + 128;
				        Vb =  0.439 * (double)red - 0.368 * (double)green - 0.071 * (double)blue + 128;
				        
						eightBlock[xi][yj] = pixel;
						alphaEightBlock[xi][yj] = alpha;
						redEightBlock[xi][yj] = Yr;
						greenEightBlock[xi][yj] = Ug;
						blueEightBlock[xi][yj] = Vb;
						
					}
				}
				
				
				alphaEightBlock = applyDCT(alphaEightBlock);
				redEightBlock = applyDCT(redEightBlock);
				greenEightBlock = applyDCT(greenEightBlock);
				blueEightBlock = applyDCT(blueEightBlock);
				
				
				
				int[][] alphaIntEightBlock = applyQuant(alphaEightBlock);
				int[][] redIntEightBlock = applyQuant(redEightBlock);
				int[][] greenIntEightBlock = applyQuant(greenEightBlock);
				int[][] blueIntEightBlock = applyQuant(blueEightBlock);
				
				
//				for(int i = 0; i < 8; i++) {
//					for(int j = 0; j < 8; j++) {
//						writerAlpha.print(alphaIntEightBlock[i][j] + " ");
//						writerRed.print(redIntEightBlock[i][j] + " ");
//						writerGreen.print(greenIntEightBlock[i][j] + " ");
//						writerBlue.print(blueIntEightBlock[i][j] + " ");
//					}
//					writerAlpha.println();
//					writerRed.println();
//					writerGreen.println();
//					writerBlue.println();
//					
//				}
//				writerAlpha.println();
//				writerRed.println();
//				writerGreen.println();
//				writerBlue.println();
				
				
				alphaEightBlock = applyIDCT(alphaIntEightBlock);
				redEightBlock = applyIDCT(redIntEightBlock);
				greenEightBlock = applyIDCT(greenIntEightBlock);
				blueEightBlock = applyIDCT(blueIntEightBlock);
				
				for(int i = x, xi = 0; i < (x+8); i++, xi++ ) {
					for(int j = y, yj = 0; j < (y+8); j++, yj++ ) {
						
						pixel = image.getRGB(i, j);
						
						alpha = (int) alphaEightBlock[xi][yj];
				        Yr = redEightBlock[xi][yj] - 16;
				        Ug = greenEightBlock[xi][yj] - 128;
				        Vb = blueEightBlock[xi][yj] -128;
						
						alpha = (int) alphaEightBlock[xi][yj];
						red = (int) (1.164 * Yr + 1.596 * Vb);
						green = (int) (1.164 * Yr - 0.392 * Ug - 0.813 * Vb);
						blue = (int) (1.164 * Yr + 2.017 * Ug);
						
						alpha = checkRGB(alpha);
						red = checkRGB(red);
						green = checkRGB(green);
						blue = checkRGB(blue);     
				        
				        pixel = (alpha<<24) | (red<<16) | (green<<8) | blue;
				        
				        image.setRGB(i, j, pixel);
					}
				}
				
				
			}

			
		}
		
//		writerAlpha.close();
//		writerRed.close();
//		writerGreen.close();
//		writerBlue.close();
		
		String fileName = "Output" + Global.imageCount + ".bmp";
		Global.DCTImageName = fileName;
		File newFile = new File(fileName);
		Global.imageCount++;

		      
		ImageIO.write(image, "bmp", newFile);
		
		BufferedImage bimg = ImageIO.read(new File(fileName));

		
		return bimg;

	}

}
