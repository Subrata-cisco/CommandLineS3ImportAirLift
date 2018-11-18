package com.sp.dataimport.storage;

import java.nio.file.Path;
import java.util.Set;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.sp.dataimport.core.Constants;
import com.sp.dataimport.core.ICommand;
import com.sp.dataimport.core.IPayload;
import com.sp.dataimport.util.LogMessage;

public class StorageCommand implements ICommand{
	
	private AWSCredentials credentials = null;
	private AmazonS3 s3client = null;
	
	@Override
	public void execute(IPayload payload) {
		StoragePayload pld = (StoragePayload) payload;
		S3Details s3Details = pld.getS3Details();
		Set<Path> allFilePath = pld.getAllFilePath();
		
		 credentials = new BasicAWSCredentials(s3Details.getAwsAccessKeyId(), s3Details.getAwsSecretAccessKey());
		 s3client = AmazonS3ClientBuilder
				  .standard()
				  .withCredentials(new AWSStaticCredentialsProvider(credentials))
				  .withRegion(s3Details.getRegion())
				  .build();
		
        
        allFilePath.stream().forEach(path -> {
			String fileName = path.getFileName().toString();
			int yearMonthDayStringIndex = -1;
			String s3FileName = null;
			
			if(fileName.contains(Constants.clickString)){
				yearMonthDayStringIndex = fileName.indexOf(Constants.clickString)+ Constants.clickString.length() + 12;
	    	}else if(fileName.contains(Constants.impressionString)){
				yearMonthDayStringIndex = fileName.indexOf(Constants.impressionString)+ Constants.impressionString.length() + 12;
			}else if(fileName.contains(Constants.activityString)){
				yearMonthDayStringIndex = fileName.indexOf(Constants.activityString)+ Constants.activityString.length() + 12;
	    	}else if(fileName.contains(Constants.matchString)){
				yearMonthDayStringIndex = fileName.indexOf(Constants.matchString)+ Constants.matchString.length() + 12;
			}
			
			s3FileName = fileName.substring(yearMonthDayStringIndex,yearMonthDayStringIndex+8);
			s3FileName = s3FileName.substring(0, 4)
					+ "/" + s3FileName.substring(4, 6) + "/"
					+ s3FileName.substring(6, 8) + "/"+fileName;
			
			s3client.putObject(s3Details.getBucket(),s3FileName, path.toFile());
			
			LogMessage.write("File written to S3 is :"+fileName);
		});
		
        s3client.shutdown();
	}
	
}
