package com.sp.dataimport.ssh;

import com.sp.dataimport.core.IPayload;

public class RemoteConnectionPayload implements IPayload {
	private String user = null;
	private String password = null;
	private String host = null;
	private String rdir = null;
	private String lDir = null;

	public RemoteConnectionPayload(String u, String p, String h, String rd,
			String ld) {
		this.user = u;
		this.password = p;
		this.host = h;
		this.rdir = rd;
		this.lDir = ld;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public String getHost() {
		return host;
	}

	public String getRDir() {
		return rdir;
	}

	public String getLDir() {
		return lDir;
	}
}
