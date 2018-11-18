package com.sp.dataimport.storage;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import com.sp.dataimport.core.IPayload;

public class StoragePayload implements IPayload {

	private Set<Path> allFilePath = new HashSet<>();
	private S3Details s3Details = null;

	public StoragePayload(Set<Path> allFilePath,S3Details s3Details){
		this.s3Details = s3Details;
		this.allFilePath = allFilePath;
	}

	public Set<Path> getAllFilePath() {
		return allFilePath;
	}

	public S3Details getS3Details() {
		return s3Details;
	}
	
}
