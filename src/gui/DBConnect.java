//Pavel Shvarchov - 319270583, Mordy Dabah - 203507017

package gui;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import classes.Database;
import classes.Vehicle;



public class DBConnect extends JComponent {
	private static final long serialVersionUID = 1L;
	///a singleton connection to a database adapting the database to swing capable of firing swing propertyChange events
	///each operation is a SwingWorker background thread returning the operations ending status with the capability of overriding the SwingWorker's done method,
	///or just used as a normal method call invoking the background thread.  
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////// Utilities
	enum Status{STOP,RETRY,DONE,CANCEL,ABORT,FAILED};
	private TransactionLock transactionLock = new TransactionLock();
	
	abstract class DBThread extends SwingWorker<DBConnect.Status,Object>{		
		public Status getStatus() {
			try {
				return get();
			} catch (InterruptedException | ExecutionException e) {
				return Status.ABORT;
			}
		}
	}
	
	long getWaitTime() {
		return (long)Utilities.getRand(3000, 8000);
	}

	static volatile DBConnect self = null;
	private Database db;

	private DBConnect() {
		db = new Database();
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void addVehicle(Vehicle v) {
		new AddVehicleThread(v).execute();
	}
	private Lock addVehicleLock = new ReentrantLock(true); 
	class AddVehicleThread extends DBThread{
		private Vehicle vehicle;
		private void addVehicleMethod() {
			db.addVehicle(vehicle);
			getConnection().firePropertyChange("addVehicle", null, vehicle);
		}
		public AddVehicleThread(Vehicle vehicle) {
			this.vehicle = vehicle;
		}
		@Override
		final protected Status doInBackground() {
			addVehicleLock.lock();
			try {
				new DBWaitDialog(getWaitTime(),()-> {
					addVehicleMethod();
				});
			} catch (InterruptedException e) {
				addVehicleLock.unlock();
				JOptionPane.showMessageDialog(null, "adding\n"+ vehicle.toString() +"\nwas canceled");
			}
			addVehicleLock.unlock();
			JOptionPane.showMessageDialog(null, "the vehicle:\n"+ vehicle.toString() +"\nwas added succesfully!");
			return Status.DONE;	
		}	
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void buyVehicle(Vehicle vehicle) {
		new buyVehicleThread(vehicle).execute();
	}
	
	class buyVehicleThread extends DBThread{
		private Vehicle vehicle;
		public buyVehicleThread(Vehicle vehicle) {
			this.vehicle = vehicle;
			}		
			public void buyVehicleMethod() {
			db.buyVehicle(vehicle);
			getConnection().firePropertyChange("buyVehicle", vehicle, null);
			}
			@Override
			final protected DBConnect.Status doInBackground() {
				if(!transactionLock.aquireBuyVehicle(vehicle)) {
					JOptionPane.showMessageDialog(null, "the vehicle:\n" + vehicle.toString() + "\nis during/awaiting the buying process, please try again later");
					return Status.RETRY;
				}
				try {
					Thread.sleep((long)Utilities.getRand(5000, 10000));
					int result = JOptionPane.showOptionDialog(null, "are you sure you want to buy this vehicle?\n" + vehicle.toString(), "buying confirmation",
							JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
					if(result == JOptionPane.NO_OPTION || result == JOptionPane.CLOSED_OPTION) {
						transactionLock.releaseBuyVehicle(vehicle);
						return Status.CANCEL;
					}
					new DBWaitDialog(getWaitTime(),()->{
						buyVehicleMethod();
					});
					JOptionPane.showMessageDialog(null,"The vehicle bought succesfully!");
					transactionLock.releaseBuyVehicle(vehicle);
					return Status.DONE;
				} catch (InterruptedException e) {
					transactionLock.releaseBuyVehicle(vehicle);
					JOptionPane.showMessageDialog(null, "the buying process for\n"+ vehicle.toString() +"\nwas canceled");
					return Status.ABORT;
				}
		}
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void testDriveVehicle(Vehicle vehicle,double distance) {
		new testDriveVehicleThread(vehicle,distance).execute();
	}
	
	class testDriveVehicleThread extends DBThread{
		private Vehicle vehicle;
		private double distance;
		public testDriveVehicleThread(Vehicle vehicle, double distance) {
			this.vehicle = vehicle; this.distance = distance;
		}
		private void testDriveVehicleMehod() {
			db.testDriveVehicle(vehicle,distance);
			getConnection().firePropertyChange("testDriveVehicle", vehicle, vehicle.getTotalDistance());
		}
		
		@Override	
		final protected DBConnect.Status doInBackground() {
			try {
				if(!transactionLock.aquireTestDrive(vehicle)) {
					JOptionPane.showMessageDialog(null,"the vehicle:\n" + vehicle.toString() + "\nis during/awaiting a test drive, please retry later");
					return Status.RETRY;
				}
				if(!db.containsIdentical(vehicle)) {
					transactionLock.releaseTestDrive(vehicle);
					JOptionPane.showMessageDialog(null,"The vehicle\n"+ vehicle.toString() +"\nwas bought already, closing...");
					return Status.STOP;
				}
				Thread.sleep((long)(distance*100));
				testDriveVehicleMehod();
				transactionLock.releaseTestDrive(vehicle);
				JOptionPane.showMessageDialog(null, "the vehicle \n"+ vehicle.toString() +"\nwas taken for a test drive of " + distance + "km succesfully!");
				return Status.DONE;
				} 
			catch (InterruptedException e) {
				transactionLock.releaseTestDrive(vehicle);
				JOptionPane.showMessageDialog(null, "the "+distance+" testdrive with the vehicle \n"+ vehicle.toString() +"\nwas canceled");
				return Status.ABORT;
			}
		}
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void resetDistances() {
		new ResetDistancesThread().execute();
	}
	
	private Lock resetDistancesLock = new ReentrantLock(true); 
	class ResetDistancesThread extends DBThread{
		private Status status;
		private void resetDistancesMethod() {
			db.resetDistances();
			getConnection().firePropertyChange("resetDistances", null, null);
		}
		@Override
		final protected Status doInBackground() {
			resetDistancesLock.lock();
			try {
				new DBWaitDialog(getWaitTime(),()-> {
					if(db.isEmpty()) {
						resetDistancesLock.unlock();
						JOptionPane.showMessageDialog(null, "the database doesnt have any vehicles, canceling!");
						status = Status.FAILED;
						return;
					}
					resetDistancesMethod();
					resetDistancesLock.unlock();
					JOptionPane.showMessageDialog(null, "all vehicles distances were reset succesfully!");
					status = Status.DONE;
				});
			} catch (InterruptedException e) {
				resetDistancesLock.unlock();
				JOptionPane.showMessageDialog(null, "reseting all vehicle's distances was canceled");
				status = Status.ABORT;
			}
			resetDistancesLock.unlock();
			return status;	
		}	
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void changeFlags(String flag) {
		new ChangeFlagsThread(flag).execute();
	}	
	
	private Lock changeFlagsLock = new ReentrantLock(true); 
	class ChangeFlagsThread	extends DBThread{
		private String flag;
		private Status status;
		ChangeFlagsThread(String flag){
			this.flag=flag;
		}
		private void changeFlagsMethod() {
			db.changeFlags(flag);
			getConnection().firePropertyChange("changeFlags", null, flag);
		}
		@Override
		final protected Status doInBackground() {
			changeFlagsLock.lock();
			try {
				new DBWaitDialog(getWaitTime(),()->{
					if(!db.hasSeaVehicles()) {
						changeFlagsLock.unlock();
						JOptionPane.showMessageDialog(null, "the database doesnt have vehicles with flags, canceling!");
						status = Status.FAILED;
						return;
					}
					changeFlagsMethod();
					changeFlagsLock.unlock();
					JOptionPane.showMessageDialog(null, "changing all vehicle's flags was done succesfully!");
					status = Status.DONE;
				});
			} catch (InterruptedException e) {
				changeFlagsLock.unlock();
				JOptionPane.showMessageDialog(null, "changing all vehicle's flags was canceled");
				status = Status.ABORT;
			}
			return status;
		}
		
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public List<Vehicle> getVehicles() {
		return db.getVehicles();
	}

	public boolean hasSeaVehicles() {
		return db.hasSeaVehicles();
	}
	
	public boolean containsIdentical(Vehicle v) {
		return db.containsIdentical(v);
	}
	
	public boolean isEmpty() {
		return db.isEmpty();
	}
	
	public static DBConnect getConnection() {
		if (self == null) {
			synchronized (DBConnect.class) {
				if (self == null) {
					self = new DBConnect();
				}
			}
		}
		return self;
	}
	
	public boolean hasDuringTransaction() {
		return !transactionLock.isEmpty();
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
	
}
