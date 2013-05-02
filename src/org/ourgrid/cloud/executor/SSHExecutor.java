package org.ourgrid.cloud.executor;

import java.io.File;
import java.net.Socket;
import java.security.PublicKey;
import java.util.List;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.transport.verification.HostKeyVerifier;
import net.schmizz.sshj.userauth.keyprovider.PKCS8KeyFile;
import net.schmizz.sshj.xfer.FileSystemFile;
import net.schmizz.sshj.xfer.scp.SCPFileTransfer;

import org.apache.commons.io.IOUtils;
import org.ourgrid.cloud.broker.Configuration;
import org.ourgrid.cloud.broker.Scheduler;
import org.ourgrid.cloud.broker.model.instance.Instance;
import org.ourgrid.cloud.broker.model.job.IOOperation;
import org.ourgrid.cloud.broker.model.job.Task;

public class SSHExecutor {

	private final Scheduler scheduler;
	private final Instance instance;
	private Task task;

	public SSHExecutor(Scheduler scheduler, Instance instance, 
			Task task) {
		this.scheduler = scheduler;
		this.instance = instance;
		this.task = task;
	}
	
	public void execute() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					checkInstanceRunning();
					checkSSHActive();
					executeSSH();
					scheduler.finished(task, instance);
				} catch (Exception e) {
					scheduler.failed(task, instance);
				}
			}

		}).start();
	}
	
	private void checkSSHActive() throws InterruptedException {
		while (true) {
			try {
				new Socket(instance.getAddress(), 22);
				break;
			} catch (Exception e) {
			}
			Thread.sleep(5000);
		}
	}
	
	private void checkInstanceRunning() throws InterruptedException {
		while (true) {
			com.amazonaws.services.ec2.model.Instance ec2Instance = scheduler.getEc2()
					.getInstanceStatus(instance.getId());
			if (ec2Instance.getState().getName().equals("running") && 
					ec2Instance.getPublicIpAddress() != null) {
				instance.setAddress(ec2Instance.getPublicIpAddress());
				break;
			}
			Thread.sleep(5000);
		}
	}
	
	private void executeSSH() throws Exception {
		SSHClient sshClient = null;
		Session session = null;
		try {
			sshClient = createSSHClient();
			
			String sandbox = scheduler.getProperties().getProperty(Configuration.SSH_SANDBOX);
			session = sshClient.startSession();
			Command createSandboxCommand = session.exec("mkdir -p " + sandbox);
			createSandboxCommand.join();
			session.close();
			
			for (IOOperation initOperation : task.getInitOperations()) {
				SCPFileTransfer ft = sshClient.newSCPFileTransfer();
				ft.upload(new FileSystemFile(new File(initOperation.getLocal())), 
						sandbox + "/" + initOperation.getRemote());
			}
			
			session = sshClient.startSession();
			Command command = session.exec("cd " + sandbox + " ;" + task.getRemote());
			command.join();
			Integer exitStatus = command.getExitStatus();
//			List<String> stdOut = IOUtils.readLines(command.getInputStream());
			List<String> stdErr = IOUtils.readLines(command.getErrorStream());
			
			if (exitStatus != 0) {
				throw new Exception(stdErr.toString());
			}
			
			for (IOOperation finalOperation : task.getFinalOperations()) {
				SCPFileTransfer ft = sshClient.newSCPFileTransfer();
				ft.download(sandbox + "/" + finalOperation.getRemote(), 
						new FileSystemFile(new File(finalOperation.getRemote())));
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			if (session != null) {
				if (session.isOpen()) {
					session.close();
				}
			}
			if (sshClient != null) {
				sshClient.disconnect();
			}
		}
	}
	
	private SSHClient createSSHClient() throws Exception {
		
		SSHClient ssh = new SSHClient();
		ssh.addHostKeyVerifier(createBlankHostKeyVerifier());
		ssh.connect(instance.getAddress());
		
		String authType = scheduler.getProperties().getProperty(Configuration.SSH_AUTH);
		String sshUser = scheduler.getProperties().getProperty(Configuration.SSH_USER);
		
		if (Configuration.SSH_AUTH_PK.equals(authType)) {
			PKCS8KeyFile keyFile = new PKCS8KeyFile();
			String keyFilePath = scheduler.getProperties().getProperty(Configuration.SSH_KEY);
			keyFile.init(new File(keyFilePath));
			ssh.authPublickey(sshUser, keyFile);
		} else if (Configuration.SSH_AUTH_PASSWORD.equals(authType)) {
			String password = scheduler.getProperties().getProperty(Configuration.SSH_PASSWORD);
			ssh.authPassword(sshUser, password);
		} else {
			throw new Exception("SSH auth type not supported.");
		}
		
		return ssh;
	}

	private static HostKeyVerifier createBlankHostKeyVerifier() {
		return new HostKeyVerifier() {
			@Override
			public boolean verify(String arg0, int arg1, PublicKey arg2) {
				return true;
			}
		};
	}
}
