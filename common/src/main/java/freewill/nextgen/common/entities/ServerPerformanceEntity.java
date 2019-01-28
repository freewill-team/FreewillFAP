package freewill.nextgen.common.entities;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

/** 
 * File:   ServerPerformanceEntity.java
 * Date:   27/08/2016
 * Author: Benito Vela
 * Refs:   None
 * 
**/

@SuppressWarnings("serial")
public class ServerPerformanceEntity implements Serializable
{
	private String ID = "";						// Fullname of the Server = Site:Server - Unique
	private Date timestamp;						// Last Timestamp when the Performance Indices were updated
	
	// Variables to store the Performance monitors
	private long FreeJVMMemorySize = 0; 		// Amount of free JVM memory in bytes
	private long TotalJVMMemorySize= 0; 		// Total amount of JVM memory in bytes 
	private long FreeRAMSpaceSize = 0;         	// Amount of free RAM space in Mb
	private long TotalRAMSpaceSize= 0; 			// Total amount of RAM space in Mb
	private double FreeRAMMemoryPrct = 0;  		// Amount of free RAM memory in %
	private long SystemCpuTime = 0; 			// CPU time used in nanoseconds
	private double SystemCpuLoad = 0; 			// "recent % cpu usage" for the whole system
	
	private String[] PartitionName = null;		// Hard Disk/Partition Name
    private long[] TotalPartitionSize = null;   // Total hard disk capacity in GB
    private double[] FreePartitionSpace = null;	// Free hard disk capacity in %
    private long[] UsablePartitionSpace = null;	// Usable hard disk capacity in GB
    
    private double NetworkUsage = 0;			// Network Usage in %
    private long NetworkBandwith = 0;			// Network Bandwith in MBps
    
    public ServerPerformanceEntity(String id)
	{
    	setID(id);
    	setTimestamp(new Date());
    	// Get the local disk partitions
    	File[] partitions = File.listRoots();
    	if( partitions!=null && partitions.length>0 )
    	{
    		// This makes room for the internal Partitions Array
    		this.setPartitionSize(partitions.length); 
    		// Then Stores the Partition Names
    		int i=0;
    		for( File diskPartition:partitions)
    		{
    			this.setPartitionName( i, diskPartition.getPath() );
    			i++;
    		}
    	}
	}
    
    // Alternative constructor
 	public ServerPerformanceEntity()
 	{
 		// EMPTY OBJECT - required for some features
 	}
    
	public String ToString()
	{
		// Print the Performance Monitors to the console for Debug
		String cad = "\n";
		cad += "Computer Id                = " + ID + "\n";
		cad += "FreeJVMMemorySize          = " + FreeJVMMemorySize + " kbytes\n";
		cad += "TotalJVMMemorySize         = " + TotalJVMMemorySize + " kbytes\n";
		cad += "FreeRAMSpaceSize           = " + FreeRAMSpaceSize + " Mbytes\n";
		cad += "TotalRAMSpaceSize          = " + TotalRAMSpaceSize + " Mbytes\n";
		cad += "FreeRAMMemoryPrct          = " + FreeRAMMemoryPrct + " %\n";
		cad += "SystemCpuTime              = " + SystemCpuTime + " nanoseconds\n";
		cad += "SystemCpuLoad              = " + SystemCpuLoad + " %\n";
		for(int i=0; i<PartitionName.length; i++)
		{
			cad += "Total "+PartitionName[i]+" partition size  \t= " + TotalPartitionSize[i] + " GB\n";
			cad += "Usable Space in drive "+PartitionName[i]+" \t= " + UsablePartitionSpace[i] + " GB\n";
			cad += "Free Space in drive "+PartitionName[i]+"   \t= " + FreePartitionSpace[i] + " %\n";
		}
		cad += "NetworkUsage               = " + NetworkUsage + " %\n";
		cad += "NetworkBandwith            = " + NetworkBandwith + " MBps\n";
		cad += "Timestamp                  = " + timestamp + "\n";
		
		return cad;
	}

	public String getID() 
	{
		return this.ID;
	}
	
	public void setID(String id){
		this.ID = id;
	}
     
    public double getSystemCpuLoad()
	{
		return this.SystemCpuLoad;
	}
	
    public void setSystemCpuLoad(double value)
    {
    	this.SystemCpuLoad = value;
    }
    
    public int getPartitionSize()
    {
		return this.PartitionName.length;
	}
    
    public void setPartitionSize(int i)
    {
    	// This makes room for mbean internal Partitions Array
    	PartitionName = new String[i];
        TotalPartitionSize = new long[i];
        FreePartitionSpace = new double[i];
        UsablePartitionSpace = new long[i];
    }
    
    public String getPartitionName(int i)
	{
		return this.PartitionName[i];
	}
	
    public void setPartitionName(int i, String value)
    {
    	this.PartitionName[i] = value;
    }
    
    public long getTotalPartitionSize(int i)
	{
		return this.TotalPartitionSize[i];
	}
	
    public void setTotalPartitionSize(int i, long value)
    {
    	this.TotalPartitionSize[i] = value;
    }
    
    public double getFreePartitionSpace(int i)
	{
		return this.FreePartitionSpace[i];
	}
	
    public void setFreePartitionSpace(int i, double value)
    {
    	this.FreePartitionSpace[i] = value;
    }
	 
    public long getUsablePartitionSpace(int i)
	{
		return this.UsablePartitionSpace[i];
	}
	
    public void setUsablePartitionSpace(int i, long value)
    {
    	this.UsablePartitionSpace[i] = value;
    }
    
    public double getNetworkUsage()
    {
    	return this.NetworkUsage;
    }
    
    public void setNetworkUsage(double value)
    {
    	this.NetworkUsage = value;
    }
    
    public long getNetworkBandwith()
    {
    	return this.NetworkBandwith;
    }
    
    public void setNetworkBandwith(long value)
    {
    	this.NetworkBandwith = value;
    }

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public long getFreeRAMSpaceSize() {
		return FreeRAMSpaceSize;
	}

	public void setFreeRAMSpaceSize(long freeRAMSpaceSize) {
		FreeRAMSpaceSize = freeRAMSpaceSize;
	}

	public long getTotalRAMSpaceSize() {
		return TotalRAMSpaceSize;
	}

	public void setTotalRAMSpaceSize(long totalRAMSpaceSize) {
		TotalRAMSpaceSize = totalRAMSpaceSize;
	}

	public double getFreeRAMMemoryPrct() {
		return FreeRAMMemoryPrct;
	}

	public void setFreeRAMMemoryPrct(double freeRAMMemoryPrct) {
		FreeRAMMemoryPrct = freeRAMMemoryPrct;
	}

	public long getSystemCpuTime() {
		return SystemCpuTime;
	}

	public void setSystemCpuTime(long systemCpuTime) {
		SystemCpuTime = systemCpuTime;
	}

	public long getFreeJVMMemorySize() {
		return FreeJVMMemorySize;
	}

	public void setFreeJVMMemorySize(long freeJVMMemorySize) {
		FreeJVMMemorySize = freeJVMMemorySize;
	}

	public long getTotalJVMMemorySize() {
		return TotalJVMMemorySize;
	}

	public void setTotalJVMMemorySize(long totalJVMMemorySize) {
		TotalJVMMemorySize = totalJVMMemorySize;
	}

}
