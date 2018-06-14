package classes;

import java.awt.Color;
import java.util.Collection;

import javax.swing.BorderFactory;

import gui.VehicleSelectButton;
import interfaces.Commercial;
import interfaces.IAirVehicle;
import interfaces.ILandVehicle;
import interfaces.ISeaVehicle;
import interfaces.IVehicle;
import interfaces.Motorized;
import interfaces.NonMotorized;

public class ColoredBorder extends VehicleDelegator {
	private static int borderThickness = 3;
	private Color color;

	public ColoredBorder(IVehicle v, Color c) {
		super(v);
		setColor(c);
	}

	private void setColor(Color c) {
		this.color = c;
	}

	public Color getColor() {
		return color;
	}

	@Override
	public VehicleSelectButton draw() {
		VehicleSelectButton vS = getVehicle().draw();
		if (color != null)
			vS.setBorder(BorderFactory.createCompoundBorder(vS.getBorder(),
					BorderFactory.createLineBorder(color, ColoredBorder.borderThickness)));
		return vS;
	}

	public ColoredBorder(ColoredBorder toCopy) {
		super(toCopy.getVehicle().clone());
		setColor(toCopy.getColor());
	}

	@Override
	public ColoredBorder clone() {
		return new ColoredBorder(this);
	}

}
