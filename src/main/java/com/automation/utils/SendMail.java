package com.automation.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Stack;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;

import com.automation.controllers.BaseActions;

/**
 * @author Chandu
 *
 */
public class SendMail{
	
	
	protected static Properties PROP = new Properties();
	public static String USERNAME =  null;
	public static String PASSWORD = null;
	public static String EMAILTO = null;
	public static String EMAILTOCC = null;
	public static String STARTTLS = null;
	public static String HOST =null;
	public static String PORT =null;
	
	public static String SOCKETFACTORYCLASS = null;
	public static String FALLBACK = null;
	public static String PATH = null;
	public static String MODULENAME = null;
	public static int INDEXOFCOMMA = 0;
	public static String USERFULLNAME = null;
	public static String EMAIL_REGEX = "[a-z0-9\\_\\-\\.]+@[a-z0-9\\_\\-\\.]+\\.[a-z]+";
	public static String REPORT_PATH="\\ExecutionReports\\ExecutionReports";
	public static String RESULT_FOLDER_NAME = "ExecutionReports";
	public static String DIR_PATH="user.dir";
	public static String BLANK_VARIABLE="";
	public static Properties PROPS=System.getProperties();
	public static String USERDIR= System.getProperty("user.dir");
	
	
	

	/**
	 * @throws IOException 
	 * @throws MessagingException 
	 * @throws FileNotFoundException 
	 * @throws Exception
	 */
	public static void sendEmailToClient() throws IOException, MessagingException {
		
		String mailPropertiesFile = USERDIR+ ConfigReader.getValue("emailconfig");
		PROP.load(new FileInputStream(mailPropertiesFile));

		final String subject = PROP.getProperty("subject");
		
		
		USERNAME =  PROP.getProperty("USERNAME");
		PASSWORD = PROP.getProperty("PASSWORD");
		EMAILTO = PROP.getProperty("EMAILTO");
		EMAILTOCC = PROP.getProperty("EMAILTOCC");
		STARTTLS = PROP.getProperty("starttls");
		HOST = PROP.getProperty("HOST");
		PORT =PROP.getProperty("PORT");
		SOCKETFACTORYCLASS = PROP.getProperty("socketFactoryClass");
		FALLBACK = PROP.getProperty("fallback");

		PROPS.put("mail.smtp.user", USERNAME);
		PROPS.put("mail.smtp.HOST", HOST);
		PROPS.put("mail.smtp.auth", "true");

		if (!"".equals(PORT)) {
			PROPS.put("mail.smtp.port", PORT);
			PROPS.put("mail.smtp.socketFactory.port", PORT);
		}

		if (!"".equals(STARTTLS))
			PROPS.put("mail.smtp.starttls.enable", STARTTLS);

		if (!"".equals(SOCKETFACTORYCLASS))
			PROPS.put("mail.smtp.socketFactory.class", SOCKETFACTORYCLASS);

		if (!"".equals(FALLBACK))
			PROPS.put("mail.smtp.socketFactory.fallback", FALLBACK);

		Session session = Session.getDefaultInstance(PROPS, null);
		session.setDebug(false);


			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(USERNAME, PROP.getProperty("userFullName")));
			msg.setSubject(subject);

			if (!"".equals(EMAILTOCC)) {

				if (EMAILTO.contains(",")) {
					String[] multipleEmailTo = EMAILTO.split(",");
					for (int j = 0; j < multipleEmailTo.length; j++) {
						if (j == 0)
							msg.addRecipient(Message.RecipientType.TO, new InternetAddress(multipleEmailTo[j]));
						else
							msg.addRecipient(Message.RecipientType.CC, new InternetAddress(multipleEmailTo[j]));
					}

				} else {
					msg.addRecipient(Message.RecipientType.TO, new InternetAddress(EMAILTO));
				}
			}

			

			else if (EMAILTOCC.equals(BLANK_VARIABLE) || EMAILTOCC == null)  {
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress(EMAILTO));
			}

			BodyPart messageBodyPart = new MimeBodyPart();

			messageBodyPart.setText("Hi client, \n Please find Email Report for suite:- "
					+ ConfigReader.getValue("SuiteName") + " \n \n \n Thanks & Regards \n Test Engineer");
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);

			if (new File(USERDIR + REPORT_PATH).exists()) {
				delDirectory(new File(USERDIR + REPORT_PATH));
			}

			if (ConfigReader.getValue("HtmlReport").contains("Y")) {
				copyDirectoryData("HtmlReport", "HtmlReport");
			}

			/*if (getCommonSettings().getXlsReport().contains("Y")) {
				copyDirectoryData("ExcelReport", "ExcelReport");
			}*/

			if (ConfigReader.getValue("Logs").contains("Y")) {
				copyDirectoryData("Logs", "Logs");
			}

			createZipFile();

			messageBodyPart = new MimeBodyPart();
			String path = USERDIR + "\\ExecutionReports\\ExecutionReports.zip";
			DataSource source = new FileDataSource(path);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName("ExecutionReports.zip");
			multipart.addBodyPart(messageBodyPart);

			msg.setContent(multipart);

			Transport transport = session.getTransport("smtp");
			transport.connect(HOST, USERNAME, PASSWORD);
			transport.sendMessage(msg, msg.getAllRecipients());
			transport.close();
			delDirectory(new File(USERDIR + "\\ExecutionReports\\ExecutionReports"));
	 }
		
	

	/**
	 * @param sourceDir
	 * @param targetDir
	 * @throws IOException
	 */
	public static void copyDirectoryData(String sourceDir, String targetDir) throws IOException {
		File srcDir = new File(USERDIR+ "\\ExecutionReports\\" + sourceDir);
		File destDir = new File(USERDIR+ "\\ExecutionReports\\ExecutionReports\\" + targetDir);
		FileUtils.copyDirectory(srcDir, destDir);
	}

	
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

			/*
			 * Common functions for date time and file IO
			 * ===========================================================
			 */
			/**
			 * @return
			 */
			public String getDateTime() {
				Date date = new Date();
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				return dateFormat.format(date);
			}

			/**
			 * 
			 */
			public void renameFile() {
				Date date = new Date();
				DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HHmmss");
				String timeStamp = dateFormat.format(date);
				try {
					File oldFile = new File(System.getProperty(USERDIR) + ConfigReader.getValue("testResultExcelPath"));
					String newFilePath = oldFile.getAbsolutePath().replace(oldFile.getName(), "") + "\\ReportHistory\\"
							+ timeStamp + "-TestResult.xls";
					File newFile = new File(newFilePath);

					FileUtils.copyFile(oldFile, newFile);
					LogUtil.infoLog(BaseActions.class, "History File successfully created... ");

				} catch (IOException e) {
					LogUtil.errorLog(BaseActions.class, "Exception caught", e);
				}
			}

			/**
			 * 
			 */
			public static void checkFileOpen() {
				String fileName = USERDIR + "\\ExecutionReports\\ExcelReport\\TestResult.xls";
				File file = new File(fileName);
				File sameFileName = new File(fileName);

				if (file.renameTo(sameFileName)) {

					LogUtil.infoLog(BaseActions.class,"**********TestResult.xls is closed**********");
				} else {
					JOptionPane.showMessageDialog(null, "TestResult.xls is opened");
					Thread.currentThread().stop();
				}
			}

			/**
			 * @return
			 * @throws IOException
			 */
			public static String createZipFile() throws IOException {
				RESULT_FOLDER_NAME = RESULT_FOLDER_NAME.replace("\\", "/");
				String outputFile = RESULT_FOLDER_NAME + ".zip";
				FileOutputStream fos = new FileOutputStream(outputFile);
				ZipOutputStream zos = new ZipOutputStream(fos);
				packCurrentDirectoryContents(RESULT_FOLDER_NAME, zos);
				zos.closeEntry();
				zos.close();
				fos.close();
				return outputFile;
			}

			/**
			 * @param directoryPath
			 * @param zos
			 * @throws IOException
			 */
			public static void packCurrentDirectoryContents(String directoryPath, ZipOutputStream zos) throws IOException {
				for (String dirElement : new File(directoryPath).list()) {
					String dirElementPath = directoryPath + "/" + dirElement;
					if (new File(dirElementPath).isDirectory()) {
						packCurrentDirectoryContents(dirElementPath, zos);
					} else {
						ZipEntry ze = new ZipEntry(dirElementPath.replaceAll(RESULT_FOLDER_NAME + "/", ""));
						zos.putNextEntry(ze);
						FileInputStream fis = new FileInputStream(dirElementPath);
						byte[] bytesRead = new byte[512];
						int bytesNum;
						while ((bytesNum = fis.read(bytesRead)) > 0) {
							zos.write(bytesRead, 0, bytesNum);
						}

						fis.close();
					}
				}
			}

			/**
			 * @param dir
			 */
			public static void delDirectory(File dir) {
				File[] currList;
				Stack<File> stack = new Stack<File>();
				stack.push(dir);
				while (!stack.isEmpty()) {
					if (stack.lastElement().isDirectory()) {
						currList = stack.lastElement().listFiles();
						if (currList.length > 0) {
							for (File curr : currList) {
								stack.push(curr);
							}
						} else {
							stack.pop().delete();
						}
					} else {
						stack.pop().delete();
					}
				}
				if (new File(USERDIR+ "/ExecutionReports/ExecutionReports.zip").exists()) {
					delDirectory(new File(USERDIR+ "/ExecutionReports/ExecutionReports.zip"));
				}
			}

}