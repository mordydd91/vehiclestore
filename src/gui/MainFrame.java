//Pavel Shvarchov - 319270583, Mordy Dabah - 203507017

package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import classes.Vehicle;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class MainFrame extends JFrame {

	private JPanel contentPanel;
	private JButton addVehicleButton;
	private JButton buyVehicleButton;
	private JButton testDriveButton;
	private JButton resetDistancesButton;
	private JButton changeFlagsButton;
	private DataPanel dataPanel = new DataPanel();
	private JTextArea toStringTextArea;
	private String defaultToStringLabel = "current vehicle: ";
	TestDrive testDriveWindow;
	private DBConnect db = DBConnect.getConnection();

	public MainFrame() {
		setTitle("Car Agency");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(600, 350));
		contentPanel = new JPanel();
		setContentPane(contentPanel);
		contentPanel.setLayout(new BorderLayout(0, 0));

		JPanel rightPanel = new JPanel();
		contentPanel.add(rightPanel, BorderLayout.EAST);
		rightPanel.setLayout(new GridLayout(5, 1, 0, 0));

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

		rightPanel.add(addVehicleButton);
		rightPanel.add(buyVehicleButton);
		rightPanel.add(testDriveButton);
		rightPanel.add(resetDistancesButton);
		rightPanel.add(changeFlagsButton);

		// actions
		addVehicleButton.addActionListener((event) -> {
			SwingUtilities.invokeLater(() -> {
				AddVehicle addVehicleWindow = new AddVehicle();
				addVehicleWindow.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				addVehicleWindow.setLocationRelativeTo(null);
				addVehicleWindow.setVisible(true);
			});
		});

		buyVehicleButton.addActionListener((event) -> {
			VehicleSelectButton vS = dataPanel.getVehicleSelectButton();
			if (vS == null)
				return;
			vS.setSelected(false);
			db.buyVehicle(vS.getVehicle());
			SwingUtilities.invokeLater(() -> {
				ConfirmationDialog confirmation = new ConfirmationDialog("The vehicle bought succesfully!");
				confirmation.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				confirmation.setLocationRelativeTo(null);
				confirmation.setVisible(true);
			});
		});

		testDriveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				Vehicle currVehicle = dataPanel.getVehicleSelectButton().getVehicle();
				if (currVehicle == null)
					return;
				SwingUtilities.invokeLater(() -> {
					testDriveWindow = new TestDrive(currVehicle);
					testDriveWindow.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					testDriveWindow.setVisible(true);
					testDriveWindow.setLocationRelativeTo(null);
				});
			}
		});

		resetDistancesButton.addActionListener((event) -> {
			db.resetDistances();
			SwingUtilities.invokeLater(() -> {
				ConfirmationDialog confirmation = new ConfirmationDialog("all vehicles distances were reset!");
				confirmation.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				confirmation.setLocationRelativeTo(null);
				confirmation.setVisible(true);
			});
		});
		
		changeFlagsButton.addActionListener((event)->{
			SwingUtilities.invokeLater(()->{
				ChangeFlags changeFlagsWindow = new ChangeFlags();
				changeFlagsWindow.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				changeFlagsWindow.setLocationRelativeTo(null);
				changeFlagsWindow.setVisible(true);
			});
		});

		dataPanel.addPropertyChangeListener("isEmpty", (event) -> {
			if (event.getNewValue().equals(true)) {
				resetDistancesButton.setEnabled(false);
				changeFlagsButton.setEnabled(false);
				buyVehicleButton.setEnabled(false);
				testDriveButton.setEnabled(false);
				changeFlagsButton.setEnabled(false);
			} else {
				resetDistancesButton.setEnabled(true);
			}
		});
		dataPanel.addPropertyChangeListener("isSelected", (event) -> {
			if (event.getNewValue().equals(true)) {
				buyVehicleButton.setEnabled(true);
				testDriveButton.setEnabled(true);
			} else {
				buyVehicleButton.setEnabled(false);
				testDriveButton.setEnabled(false);
			}
			updateToString();
		});
		dataPanel.addPropertyChangeListener("hasSeaVehicles", (event)->{
			if (event.getNewValue().equals(true)) {
				changeFlagsButton.setEnabled(true);
			} else {
				changeFlagsButton.setEnabled(false);
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

		toStringPanel.add(toStringTextArea, BorderLayout.CENTER);
		contentPanel.add(toStringPanel, BorderLayout.SOUTH);

		/// Initial refresh does'nt account for the button creation.
		dataPanel.refresh();
	}

	private void updateToString() {
		VehicleSelectButton vS = dataPanel.getVehicleSelectButton();
		toStringTextArea.setText((vS == null ? defaultToStringLabel : defaultToStringLabel + vS.getVehicle().toString()));
	}
}
