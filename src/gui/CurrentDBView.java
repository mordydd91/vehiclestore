package gui;

import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;

import classes.Vehicle;

public class CurrentDBView extends JDialog{
	private static final long serialVersionUID = 1L;
	private DBConnect db = DBConnect.getConnection();
	private List<VehicleSelectButton> vehicleSelectButtons = new ArrayList<VehicleSelectButton>();
	static Dimension preferedImageSize = new Dimension(50, 50);
	public static void setPreferedImageSize(Dimension size) {
		DataPanel.preferedImageSize = size;
	}
	
	public void refresh() {
		for (VehicleSelectButton vS : vehicleSelectButtons) {
			remove(vS);
		}
		vehicleSelectButtons.clear();
		for (Vehicle v : db.getVehicles()) {
			VehicleSelectButton vS = new VehicleSelectButton(v, DataPanel.preferedImageSize);
			vS.setBorderPainted(false);
			vehicleSelectButtons.add(vS);
		}
		for (VehicleSelectButton vS : vehicleSelectButtons) {
			add(vS);
		}
		repaint();
		revalidate();
//		pack();
	}
	public CurrentDBView() {
		setTitle("Database");
		setLayout(new WrapLayout(WrapLayout.LEFT, 15, 10));
		addWindowFocusListener(new WindowFocusListener() {
			
			@Override
			public void windowLostFocus(WindowEvent arg0) {
				foucusLoses++;
			}
			
			@Override
			public void windowGainedFocus(WindowEvent arg0) {
				if (foucusLoses>=2) {
					refresh();
					foucusLoses=0;
				}
			}
		});
		refresh();
		pack();
	}
	private int foucusLoses = 0;
}