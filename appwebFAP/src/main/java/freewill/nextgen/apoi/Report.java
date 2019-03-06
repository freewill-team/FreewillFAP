package freewill.nextgen.apoi;

import java.io.File;

public abstract class Report {
	private boolean success = false;
	private File tempFile = null;
	
	public boolean isSuccess() {
		return success;
	}
	
	public void setSuccess(boolean ss) {
		success = ss;
	}

	public File getFile() {
		return tempFile;
	}
	
	public void setFile(File ff) {
		tempFile = ff;
	}
	
}
