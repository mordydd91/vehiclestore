package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;


import classes.Database;
import classes.Vehicle;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.DropMode;

public class MainFrame extends JFrame {

	private JPanel contentPanel;
	private JButton addVehicleButton;
	private JButton buyVehicleButton;
	private JButton testDriveButton;
	private JButton resetDistancesButton;
	private JButton changeFlagsButton;
	private DataPanel dataPanel;
	private JTextArea toStringTextArea;
	private String defaultToStringLabel = "current vehicle: ";
	private JButton refreshButton;
	TestDrive testDriveWindow;
	private Database db = Database.getInstance();

	public MainFrame() {
		setTitle("Car Agency");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(600, 350));
		contentPanel = new JPanel();
		setContentPane(contentPanel);
		contentPanel.setLayout(new BorderLayout(0, 0));

		JPanel rightPanel = new JPanel();
		contentPanel.add(rightPanel, BorderLayout.EAST);
		rightPanel.setLayout(new GridLayout(6, 1, 0, 0));

		/// buttons
		addVehicleButton = new JButton("Add Vehicle");
		addVehicleButton.setEnabled(true);

		buyVehicleButton = new JButton("Buy Vehicle");
		buyVehicleButton.setEnabled(false);

		testDriveButton = new JButton("Test Drive");
		testDriveButton.setEnabled(false);

		resetDistancesButton = new JButton("Reset Distances");
		resetDistancesButton.setEnabled(false);

		changeFlagsButton = new JButton("Change Flags");
		changeFlagsButton.setEnabled(false);

		refreshButton = new JButton("refresh");
		refreshButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				dataPanel.refresh();
			}
		});

		rightPanel.add(addVehicleButton);
		rightPanel.add(buyVehicleButton);
		rightPanel.add(testDriveButton);
		rightPanel.add(resetDistancesButton);
		rightPanel.add(changeFlagsButton);
		rightPanel.add(refreshButton);

		// actions
		addVehicleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						AddVehicle addVehicleWindow = new AddVehicle();
						addVehicleWindow.setVisible(true);
						addVehicleWindow.setLocationRelativeTo(null);
					}
				});

			}
		});

		buyVehicleButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				VehicleSelectButton vS = dataPanel.getVehicleSelectButton();
				if (vS == null)
					return;
				vS.setSelected(false);
				db.buyVehicle(vS.getVehicle());
				dataPanel.refresh();
			}
		});

		testDriveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				Vehicle currVehicle = dataPanel.getVehicleSelectButton().getVehicle();
				if (currVehicle == null)
					return;
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						testDriveWindow = new TestDrive(currVehicle);
						testDriveWindow.setVisible(true);
						testDriveWindow.setLocationRelativeTo(null);
						testDriveWindow.addWindowListener(new WindowListener() {
							
							@Override
							public void windowOpened(WindowEvent arg0) {
							}
							
							@Override
							public void windowIconified(WindowEvent arg0) {								
							}
							
							@Override
							public void windowDeiconified(WindowEvent arg0) {								
							}
							
							@Override
							public void windowDeactivated(WindowEvent arg0) {								
							}
							
							@Override
							public void windowClosing(WindowEvent arg0) {								
							}
							
							@Override
							public void windowClosed(WindowEvent event) {
								updateToString();
							}
							
							@Override
							public void windowActivated(WindowEvent arg0) {								
							}
						});
					}
				});
			}
		});

		dataPanel = new DataPanel();
		dataPanel.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				String eventName = event.getPropertyName();
				if (eventName.equals("isEmpty")) {
					if (event.getNewValue().equals(true)) {
						resetDistancesButton.setEnabled(false);
						changeFlagsButton.setEnabled(false);
						buyVehicleButton.setEnabled(false);
						testDriveButton.setEnabled(false);
					} else {
						resetDistancesButton.setEnabled(true);
						changeFlagsButton.setEnabled(true);
					}
				} else if (eventName.equals("isSelected")) {
					if (event.getNewValue().equals(true)) {
						buyVehicleButton.setEnabled(true);
						testDriveButton.setEnabled(true);
					} else {
						buyVehicleButton.setEnabled(false);
						testDriveButton.setEnabled(false);
					}
					updateToString();
				}
			}
		});

		contentPanel.add(dataPanel, BorderLayout.CENTER);

		JPanel toStringPanel = new JPanel();
		toStringPanel.setLayout(new BorderLayout());

		// labels
		toStringTextArea = new JTextArea(defaultToStringLabel);
		toStringTextArea.setOpaque(false);
		toStringTextArea.setEditable(false);
		toStringTextArea.setFocusable(false);
		toStringTextArea.setBorder(null);
		toStringTextArea.setLineWrap(true);
		toStringTextArea.setWrapStyleWord(true);
		toStringTextArea.setForeground(Color.GRAY);
		
		toStringPanel.add(toStringTextArea,BorderLayout.CENTER);
		contentPanel.add(toStringPanel, BorderLayout.SOUTH);
		
		///Initial refresh does'nt account for the button creation.
		dataPanel.refresh();
	}
	

	
	private void updateToString() {
		VehicleSelectButton vS = dataPanel.getVehicleSelectButton();
//		toStringTextArea.setText((vS == null ? defaultToStringLabel : defaultToStringLabel + vS.getVehicle().toString()));
	}
}
