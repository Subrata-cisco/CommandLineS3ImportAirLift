package com.sp.dataimport.filter;

import com.sp.dataimport.core.IPayload;
import com.sp.dataimport.storage.S3Details;

public class FileSelectorPayload implements IPayload {
	private String location;
	private S3Details s3Details = null;
	
	public FileSelectorPayload(String l,S3Details d){
		this.location = l;
		this.s3Details = d;
	}

	public String getLocation() {
		return location;
	}
	
	public S3Details getS3Details(){
		return s3Details;
	}
}
