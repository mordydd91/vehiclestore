package gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

class ImageTextRenderer extends JLabel implements ListCellRenderer<ImageAndText> {
	
	public ImageTextRenderer() {
	       setOpaque(true);
           setVerticalAlignment(CENTER);
	}

	@Override
	public Component getListCellRendererComponent(JList list, ImageAndText item, int index, boolean isSelected,
			boolean cellHasFocus) {

		ImageIcon img = item.getImage();
		if (img != null) {
			setIcon(item.getImage());
		}
		setText(item.toString());


        if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		}
		else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
        
		setFont(list.getFont());
		
		return this;
	}

}