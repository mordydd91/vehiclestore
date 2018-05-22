//Pavel Shvarchov - 319270583, Mordy Dabah - 203507017

package classes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import interfaces.IAirVehicle;
import interfaces.ILandVehicle;
import interfaces.ISeaVehicle;

public class Database {
	
	
	private List<Vehicle> vehicleDatabase;
	private List<ISeaVehicle> seaVehicleDatabase;
	private List<IAirVehicle> airVehicleDatabase;
	private List<ILandVehicle> landVehicleDatabase;
	private ReadWriteLock lock = new ReentrantReadWriteLock(true);
	
	private void log(String data) {
		new Thread(()->{
			synchronized (System.out) {
				System.out.println(data);
			}
		}).start();
	}
	
	private boolean isEmpty() {
		lock.readLock().lock();
		if (vehicleDatabase.isEmpty()) {
			log("no vehicles in database, returning\n");
			lock.readLock().unlock();
			return true;
		}
		lock.readLock().unlock();
		return false;
	}
	
	public boolean hasSeaVehicles() {
		lock.readLock().lock();
		boolean result =!seaVehicleDatabase.isEmpty();
		lock.readLock().unlock();
		return result;
	}
	
	
	public boolean addVehicle(Vehicle currVehicle) {
		if (currVehicle != null) {
			lock.writeLock().lock();
			this.vehicleDatabase.add(currVehicle);
			if (currVehicle instanceof ISeaVehicle) {
				this.seaVehicleDatabase.add((ISeaVehicle) currVehicle);
			} 
			if (currVehicle instanceof ILandVehicle) {
				this.landVehicleDatabase.add((ILandVehicle) currVehicle);
			}
			if (currVehicle instanceof IAirVehicle) {
				this.airVehicleDatabase.add((IAirVehicle) currVehicle);
			}
			lock.writeLock().unlock();
			log("the vehicle: " + currVehicle.toString() + " was added succesfully, returning");
			return true;
		}
		return false;
	}

	public boolean buyVehicle(Vehicle currVehicle) {
		if (vehicleDatabase.contains(currVehicle)) {
			lock.writeLock().lock();
			this.vehicleDatabase.remove(currVehicle);
			if (currVehicle instanceof ISeaVehicle)
				this.seaVehicleDatabase.remove((ISeaVehicle)currVehicle);
			if (currVehicle instanceof ILandVehicle)
				this.landVehicleDatabase.remove((ILandVehicle)currVehicle);
			if (currVehicle instanceof IAirVehicle)
				this.airVehicleDatabase.remove((IAirVehicle)currVehicle);
			lock.writeLock().unlock();
			log("the vehicle: " + currVehicle.toString() + " was bought succesfully, returning");
			return true;
		}
		log("vehicle" + currVehicle.toString() + "doesnt exist, returning");
		return false;
	}

	public boolean testDriveVehicle(Vehicle currVehicle, double distance) {
		if (vehicleDatabase.contains(currVehicle)) {
			lock.writeLock().lock();
			vehicleDatabase.get(vehicleDatabase.indexOf(currVehicle)).moveDistance(distance);
			lock.writeLock().unlock();
			log("the vehicle: " + currVehicle.toString() + " was taken for a " + distance + "km test-drive succesfully, returning");
			return true;
		}
		return false;
	}

//	public boolean testDriveVehicle(Vehicle currVehicle) {
//		if (vehicleDatabase.contains(currVehicle)) {
//			double distance;
//			try {
//				distance = Input.inputDouble("enter test drive distance:");
//			}
//			catch (NumberFormatException e) {
//				System.out.println("bad input " + e.getLocalizedMessage() + ", returning");
//				return false;
//			}
//			return testDriveVehicle(currVehicle, distance);
//		}
//		return false;
//	}

	public boolean resetDistances() {
		if (isEmpty()) {
			log("no vehicles to reset distance, returning");
			return false;
		}
		lock.writeLock().lock();
		for (Vehicle v : vehicleDatabase) {
			v.resetTotalDistance();
		}
		lock.writeLock().unlock();
		log("all vehicle distances were reset succesfully, returning");
		return true;
	}

	public boolean changeFlags(String flag) {
		lock.readLock().lock();
		if (seaVehicleDatabase.isEmpty()) {
			lock.readLock().unlock();
			log("no vehicles to change flags, returning");
			return false;
		}
		lock.readLock().unlock();
		lock.writeLock().lock();
		for (ISeaVehicle sV : seaVehicleDatabase) {
			sV.setFlag(flag);
		}
		lock.writeLock().unlock();
		log("all vehicle flags were changed to " + flag + " succesfully, returning\n");
		return true;
	}


	public Database() {
		vehicleDatabase = new ArrayList<>();
		seaVehicleDatabase = new ArrayList<>();
		airVehicleDatabase = new ArrayList<>();
		landVehicleDatabase = new ArrayList<>();
	}

	public List<Vehicle> getVehicles() {
		lock.readLock().lock();
		List<Vehicle> result = vehicleDatabase;
		lock.readLock().unlock();
		return result;
	}
	
}
