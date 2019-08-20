import ai.jcraft.jsch.Channel;
import ai.jcraft.jsch.JSch;
import ai.jcraft.jsch.Session;
import ai.jcraft.jsch.ChannelSftp;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.Properties;


/**
 * 本地主机
 * app/Ngcrm@123
 * 10.131.156.2
 *
 * 远端主机
 * root/Root!234
 * 10.131.145.24
 * cd /opt/ufsoft/tdnchome/nchome/webapps/nc_web/terminal/
 */
public class Test {

	public static void main(String[] args) throws Exception {

		Test test = new Test();

		byte[] fileData = FileUtils.readFileToByteArray(new File("C:\\Users\\Steven\\Desktop\\readme.txt"));
		boolean b = test.uploadFile(fileData, "readme.txt");
		System.out.println("uploading, is success: " + b);

		byte[] downData = test.downloadFile("/home/aislb/readme.txt");
		//byte[] downData = test.downloadFile("/home/web/readme.txt");
		FileUtils.writeByteArrayToFile(new File("C:\\Users\\Steven\\Desktop\\readme-download.txt"), downData);

	}

	public boolean downloadFile(String filePath, OutputStream os) throws Exception {
		System.out.println("downloadFile...");
		System.out.println("filePath: " + filePath);

		boolean rtn = false;
		ChannelSftp client = null;

		try {
			client = beReady();
			client.cd("/home/aislb");
			//client.cd("/home/web");

			client.get(filePath, os);
			rtn = true;
		} finally {
			if (null != client) {
				client.quit();
				client.disconnect();
				client.getSession().disconnect();
			}
		}

		return rtn;
	}

	public byte[] downloadFile(String filePath) throws Exception {
		byte[] rtn = null;
		ByteArrayOutputStream baos = null;

		try {
			baos = new ByteArrayOutputStream();
			downloadFile(filePath, baos);
			rtn = baos.toByteArray();
		} finally {
			baos.close();
		}

		return rtn;
	}

	public boolean uploadFile(byte[] fileData, String dstFilePath) throws Exception {

		boolean rtn = false;
		ByteArrayInputStream bais = null;

		try {
			bais = new ByteArrayInputStream(fileData);
			rtn = uploadFile(bais, dstFilePath);
		} finally {
			bais.close();
		}

		return rtn;
	}

	public boolean uploadFile(InputStream is, String dstFilePath) throws Exception {

		System.out.println("uploadFile...");
		System.out.println("dstFilePath: " + dstFilePath);

		boolean rtn = false;
		ChannelSftp client = null;

		try {
			client = beReady();
			client.put(is, dstFilePath, ChannelSftp.OVERWRITE);
			rtn = true;
		} finally {
			if (null != client) {
				client.quit();
				client.disconnect();
				client.getSession().disconnect();
			}
		}

		return rtn;
	}

	private static final ChannelSftp beReady() throws Exception {
		String ftpHost = "10.135.134.115";
		//String ftpHost = "39.106.228.124";
		int ftpPort = 22;
		String ftpUserName = "aislb";
		String ftpPassword = "1q1w1e1r";
		//String ftpUserName = "web";
		//String ftpPassword = "1q1w1e1r";

		JSch jsch = new JSch();
		Session session = jsch.getSession(ftpUserName, ftpHost, ftpPort);
		session.setPassword(ftpPassword);

		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");

		session.setConfig(config);
		session.setTimeout(1000);
		session.connect();

		Channel channel = session.openChannel("sftp");
		channel.connect();

		return (ChannelSftp)channel;
	}
}
