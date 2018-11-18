package com.sp.dataimport.cli;

import static com.google.common.collect.Lists.newArrayList;
import io.airlift.airline.Command;
import io.airlift.airline.Option;

import java.io.IOException;
import java.util.Collection;

import com.sp.dataimport.core.ICommand;
import com.sp.dataimport.core.IPayload;
import com.sp.dataimport.filter.FileSelector;
import com.sp.dataimport.filter.FileSelectorPayload;
import com.sp.dataimport.ssh.RemoteConnectionPayload;
import com.sp.dataimport.ssh.SSHCommand;
import com.sp.dataimport.storage.S3Details;
public class ImportCommand implements ICommandGroup{

	@Override
	public String name() {
		return "import";
	}

	@Override
	public String description() {
		return "1) import copy-from-unix -u <user> -p <password> -h <host> -rd <remote_dir> -ld <local_dir> \n	     2) process-file-command is supported..";
	}

	@Override
	public Collection<Class<? extends AbstractCommand>> commands() {
		return newArrayList(CopyFileFromLinuxCommand.class,FileProcessingCommand.class);
	}
	
	@Command(name = "copy-from-unix", description = "copy from unix script")
    public static final class CopyFileFromLinuxCommand extends AbstractCommand {

        @Option(title = "user", description = "linux user", required = true, name = {"--user", "-u"})
        protected String user;
        
        @Option(title = "password", description = "linux password", required = true, name = {"--password", "-p"})
        protected String password;
        
        @Option(title = "host", description = "linux host ip", required = true, name = {"--host", "-h"})
        protected String host;
        
        @Option(title = "rDir", description = "remote dir to be copied", required = true, name = {"--rDir", "-rd"})
        protected String rDir;
        
        @Option(title = "lDir", description = "locate dir to be pasted", required = true, name = {"--lDir", "-ld"})
        protected String lDir;

		@Override
		protected void invoke() throws IOException {
			System.out.println("Invoking copy-from-unix..");
			IPayload payload = new RemoteConnectionPayload(user,password,host,rDir,lDir);
			
			ICommand command = new SSHCommand();
			command.execute(payload); 
		}
	}
	

	@Command(name = "process-file-command", description = "upload data to s3 script")
    public static final class FileProcessingCommand extends AbstractCommand {

		@Option(title = "awsAccessKeyId", description = "AWS access key id", required = true, name = {"--awsAccessKeyId", "-k"})
        protected String awsAccessKeyId;
		
		@Option(title = "awsSecretAccessKey", description = "AWS access key secret", required = true, name = {"--awsSecretAccessKey", "-s"})
        protected String awsSecretAccessKey;
		
		@Option(title = "bucket", description = "s3 bucket", required = true, name = {"--bucket", "-b"})
        protected String bucket;
		
		@Option(title = "region", description = "s3 region", required = true, name = {"--region", "-r"})
        protected String region;
		
		@Option(title = "lDir", description = "local source directory", required = true, name = {"--lDir", "-ld"})
        protected String lDir;

		@Override
		protected void invoke() throws IOException {
			S3Details details = new S3Details(awsAccessKeyId,awsSecretAccessKey,bucket,region);
			
			ICommand command = new FileSelector();
			IPayload payload = new FileSelectorPayload(lDir, details);
			command.execute(payload);
		}
	}
}