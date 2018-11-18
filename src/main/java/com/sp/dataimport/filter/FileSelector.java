package com.sp.dataimport.filter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

import com.sp.dataimport.core.Constants;
import com.sp.dataimport.core.ICommand;
import com.sp.dataimport.core.IPayload;
import com.sp.dataimport.storage.StorageCommand;
import com.sp.dataimport.storage.StoragePayload;
import com.sp.dataimport.util.LogMessage;

public class FileSelector implements ICommand{
	
	@Override
	public void execute(IPayload payload) {
		FileSelectorPayload pld = (FileSelectorPayload) payload;
		File file = new File(pld.getLocation());
		if(!file.isDirectory()){
			throw new RuntimeException(pld.getLocation()+ " is not a directory from where file will be read to store in s3");
		}
		
		Set<Path> set = null;
		try {
			     set = Files.list(Paths.get(pld.getLocation()))
			     .filter(path -> {
			    	 String fileName = path.getFileName().toString();
			    	 if(fileName.contains(Constants.clickString) || 
			    			 fileName.contains(Constants.impressionString) || 
			    			 fileName.contains(Constants.activityString)|| 
			    			 fileName.contains(Constants.matchString)){
			    		 return true;
			    	 }
			    	 return false;
			     })
				 .sorted().collect(Collectors.toSet());
		} catch (IOException e) {
			LogMessage.write("Error in reading the files :"+e.getMessage()+" , from location :"+pld.getLocation());
			e.printStackTrace();
		}
		
		ICommand command = new StorageCommand();
		IPayload storagePayload = new StoragePayload(set,pld.getS3Details());
		command.execute(storagePayload);
		
	}

}
