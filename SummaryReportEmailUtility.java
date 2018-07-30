package com.configure;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SummaryReportEmailUtility extends com.configure.Configure {
	public static void main(String[] args) throws IOException {
		mailReport(args[0]);
		String path = System.getProperty("user.dir") + "\\TestSummary\\";
		discardSummaryReport(path, Integer.parseInt(args[1]));
	}

	public static void mailReport(String toAddress) {
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
			// Getting the path of the html file
			String filename = System.getProperty("user.dir")
					+ "\\TestSummary\\" + getProperty("SummaryReport")
					+ ".html";
			BufferedReader br = new BufferedReader(new FileReader(filename));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
			String message2 = sb.toString();
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(toAddress));
			// Setting subject line
			message.setSubject(" Automation Summary Report for "
					+ getProperty("Date") + "- Pizza ranch Test Execution");
			// Adding message body
			BodyPart messageBodyPart = new MimeBodyPart();
			Multipart multipart = new MimeMultipart();
			messageBodyPart
					.setContent(
							"<html><body>Summary for test automation of Pizza ranch site has been generated. Please download the attachement and view.</body></html>",
							"text/html");
			multipart.addBodyPart(messageBodyPart);
			// Adding html text to email body
			messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(message2, "text/html");
			multipart.addBodyPart(messageBodyPart);
			// Add report as attachment
			messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(filename);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName("SummaryReport.html");
			// Send the complete message parts
			multipart.addBodyPart(messageBodyPart);
			message.setContent(multipart);
			// Sending message
			Transport.send(message);
			System.out.println("Mail sent with the test report attached.");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void discardSummaryReport(String TestSummary, int range) {
		try {
			System.out.println("Test Summary location : " + TestSummary);
			System.out.println("Backup of the builds for the past " + range
					+ " days are maintained.");
			File directory = new File(TestSummary);
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
					System.out.println("Deleted the old summary report files");

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
