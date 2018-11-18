package com.sp.dataimport.storage;

public class S3Details {

	private String awsAccessKeyId;
	private String awsSecretAccessKey;
	private String bucket;
	private String region;
	
	public S3Details(String k, String s, String b, String r){
		this.awsAccessKeyId = k;
		this.awsSecretAccessKey = s;
		this.bucket = b;
		this.region = r;
	}

	public String getAwsAccessKeyId() {
		return awsAccessKeyId;
	}

	public String getAwsSecretAccessKey() {
		return awsSecretAccessKey;
	}

	public String getBucket() {
		return bucket;
	}

	public String getRegion() {
		return region;
	}

}
