package gui;

import java.awt.Dimension;
import java.awt.Image;

import javax.swing.ImageIcon;

import Images.ImagesReferance;



class ImageAndText{
	protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = ImagesReferance.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
                return null;
        }
	}
		
	private ImageIcon img;
	private String text;
	
	public ImageAndText() {
		img=null;
		setText("");	
	}
	
	public ImageAndText(String path,String text) {
		setImg(path);
		setText(text);
	}
	
	void scaleImg(Dimension d) {
		scaleImg(d.width,d.height);
	}

	
	void scaleImg(int width,int height) {
		if (img!=null) {
		Image image = this.img.getImage(); // transform it 
		Image newimg = image.getScaledInstance(width, height,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
		this.img = new ImageIcon(newimg);  // transform it back
		}
	}
	
	public void setImg(String path) {this.img=createImageIcon(path);}
	public void setText(String s) {this.text=s;}
	public ImageIcon getImage() {return img;}
	public String toString() {return text;}
	public String getText() {return toString();}
}