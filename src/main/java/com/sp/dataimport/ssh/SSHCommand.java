package com.sp.dataimport.ssh;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.sp.dataimport.core.ICommand;
import com.sp.dataimport.core.IPayload;
import com.sp.dataimport.util.LogMessage;

public class SSHCommand implements ICommand {
	
	@Override
	public void execute(IPayload payload) {
		download((RemoteConnectionPayload)payload);
	}

	private void download(RemoteConnectionPayload payload) {
		FileOutputStream fos = null;
		Session session = null;
		
		JSch jsch = new JSch();
		Properties prop = new Properties();
		prop.put("StrictHostKeyChecking", "no");
		
		String user = payload.getUser();
		String password = payload.getPassword();
		String host = payload.getHost();
		String rfile = payload.getRDir();
		String lfile = payload.getLDir();
		
		try {

			String prefix = null;
			if (new File(lfile).isDirectory()) {
				prefix = lfile + File.separator;
			}
			
			session = jsch.getSession(user, host, 22);
			session.setPassword(password);
			session.setConfig(prop);
			session.connect();
			
			LogMessage.write("Connected to host :"+host+" , will start copy from :"+rfile+" to local :"+lfile);

			// exec 'scp -f rfile' remotely
			String command = "scp -f " + rfile;
			Channel channel = session.openChannel("exec");
			((ChannelExec)channel).setCommand(command);

			// get I/O streams for remote scp
			OutputStream out = channel.getOutputStream();
			InputStream in = channel.getInputStream();

			channel.connect();
			
			LogMessage.write("SCP command is initited succesfully.");

			byte[] buf = new byte[1024];

			// send '\0'
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();

			while (true) {
				int c = checkAck(in);
				if (c != 'C') {
					break;
				}

				// read '0644 '
				in.read(buf, 0, 5);

				long filesize = 0L;
				while (true) {
					if (in.read(buf, 0, 1) < 0) {
						// error
						break;
					}
					if (buf[0] == ' ')
						break;
					filesize = filesize * 10L + (long) (buf[0] - '0');
				}

				String file = null;
				for (int i = 0;; i++) {
					in.read(buf, i, 1);
					if (buf[i] == (byte) 0x0a) {
						file = new String(buf, 0, i);
						break;
					}
				}

				// System.out.println("filesize="+filesize+", file="+file);

				// send '\0'
				buf[0] = 0;
				out.write(buf, 0, 1);
				out.flush();

				// read a content of lfile
				fos = new FileOutputStream(prefix == null ? lfile : prefix
						+ file);
				int foo;
				while (true) {
					if (buf.length < filesize)
						foo = buf.length;
					else
						foo = (int) filesize;
					foo = in.read(buf, 0, foo);
					if (foo < 0) {
						// error
						break;
					}
					fos.write(buf, 0, foo);
					filesize -= foo;
					if (filesize == 0L)
						break;
				}
				fos.close();
				fos = null;

				if (checkAck(in) != 0) {
					System.exit(0);
				}

				// send '\0'
				buf[0] = 0;
				out.write(buf, 0, 1);
				out.flush();
			}
			LogMessage.write("All files downloaded Succesfully , Please check folder : "+lfile);
		} catch (Exception e) {
			System.out.println(e);
			try {
				if (fos != null)
					fos.close();
			} catch (Exception ee) {
			}
			LogMessage.write("Error in downloading the file from host :"+host+" , error is :"+e.getMessage());
			e.printStackTrace();
		} finally {
			session.disconnect();
		}
		LogMessage.write("Good bye ..");
	}

	static int checkAck(InputStream in) throws IOException {
		int b = in.read();
		// b may be 0 for success,
		// 1 for error,
		// 2 for fatal error,
		// -1
		if (b == 0)
			return b;
		if (b == -1)
			return b;

		if (b == 1 || b == 2) {
			StringBuffer sb = new StringBuffer();
			int c;
			do {
				c = in.read();
				sb.append((char) c);
			} while (c != '\n');
			if (b == 1) { // error
				LogMessage.write("Error.. :"+sb.toString());
			}
			if (b == 2) { // fatal error
				LogMessage.write("Fatal Error.. :"+sb.toString());
			}
		}
		return b;
	}

}
