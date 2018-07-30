package com.configure;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class FailureNotification extends com.configure.Configure {

	public static void main(String[] args) throws IOException {
		boolean Flag = false;
		File dir = new File(args[0]);
		File[] files = dir.listFiles();
		File x = files[0];
		for (int i = 1; i < files.length; i++) {
			if (x.lastModified() < files[i].lastModified()) {
				x = files[i];
			}
		}
		System.out.println("The latest TestNG Report folder : " + x);
		String latestFolder = x.getAbsolutePath();
		File newdir = new File(x.getAbsolutePath());
		File[] fileNew = newdir.listFiles();
		for (int j = 1; j < fileNew.length; j++) {
			if (fileNew[j].getName().equals("testng-failed.xml")) {
				Flag = true;
			}
		}
		if (Flag == true) {
			System.out.println("Failed report is present.");
			MailSetUp(latestFolder, args[1]);
		} else {
			System.out.println("Failed report does not exist");
		}
		String TestReport = System.getProperty("user.dir") + "\\" + args[0];
		DiscardOldBuilds(TestReport, Integer.parseInt(args[2]));
	}

	public static void MailSetUp(String latestFolder, String toAddress) {
		final String username = "qaautomationtester9@gmail.com";
		final String password = "mobomo123";
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(toAddress));
			// Set Subject: header field
			message.setSubject("Automate Pizza ranch test execution - Test Failure alert !");
			// Create the message part
			BodyPart messageBodyPart = new MimeBodyPart();
			// Fill the message
			messageBodyPart
					.setText("Automated Pizza ranch test execution report has been generated. The tests failed. Please refer the attachement.");
			// Create a multipart message
			Multipart multipart = new MimeMultipart();
			// Set text message part
			multipart.addBodyPart(messageBodyPart);
			// Part two is attachment
			messageBodyPart = new MimeBodyPart();
			String filename;
			filename = latestFolder + "\\emailable-report.html";
			DataSource source = new FileDataSource(filename);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName("report.html");
			multipart.addBodyPart(messageBodyPart);
			// Send the complete message parts
			message.setContent(multipart);
			Transport.send(message);
			System.out.println("Mail sent with the test report attached.");
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	public static void DiscardOldBuilds(String TestReport, int range) {
		try {
			System.out.println("Test Report location : " + TestReport);
			System.out.println("Backup of the builds for the past " + range
					+ " days are maintained.");
			File directory = new File(TestReport);
			long diff;
			for (File x : directory.listFiles()) {
				// System.out.println(x);
				diff = new Date().getTime() - x.lastModified();
				// System.out.println(diff + ">" + range * 24 * 60 * 60 * 1000);
				if (diff > range * 24 * 60 * 60 * 1000) {
					if (!directory.exists()) {
						System.out.println("Directory does not exist.");
						System.exit(0);
					} else {
						try {
							delete(x);
						} catch (IOException e) {
							e.printStackTrace();
							System.exit(0);
						}
					}
					System.out.println("Deleted the old build report files");

				}
			}
		} catch (Exception e) {
			System.out.println("Exception caught");
		}
	}

	public static void delete(File file) throws IOException {

		if (file.isDirectory()) {
			if (file.list().length == 0) {
				file.delete();
			} else {
				String files[] = file.list();

				for (String temp : files) {
					File fileDelete = new File(file, temp);
					delete(fileDelete);
				}

				if (file.list().length == 0) {
					file.delete();
				}
			}

		} else {
			file.delete();
		}
	}
}
