package com.urawaredsmylife.util;

import java.util.Date;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.exception.ExceptionUtils;

public class Mail {
	private static final String HOST = "localhost";
	private static final String FROM = "motoy@smartj.mobi";
	private static final String TO = "motoy3d@gmail.com";
	private static boolean debug = true;
	private static Properties props = new Properties();
	{
		props.put("mail.smtp.host", HOST);
		props.put("mail.smtp.from", FROM);
//		props.put("mail.host", HOST);
//		props.put("mail.from", FROM);
		if (true) {
			props.put("mail.debug", "true");
		}
	}
	/**
	 * エラーメール送信
	 * @param th
	 */
	public static void send(String msgText) {
		if (!"true".equals(ResourceBundle.getBundle("app").getString("mail.enable"))) {
			return;
		}
		String subject = "SmartJ Error " + new Date();
		Session session = Session.getInstance(props);
		session.setDebug(debug);
		try {
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(FROM));
			InternetAddress[] address = InternetAddress.parse(TO);
			msg.setRecipients(Message.RecipientType.TO, address);
			msg.setSubject(subject, "ISO-2022-JP");
			msg.setSentDate(new Date());
			
			msg.setHeader("Content-Type","text/plain; charset=ISO-2022-JP");
			msg.setText(msgText, "ISO-2022-JP");

			System.out.println("----------------------------------------");
			System.out.println("Return-Path=" + msg.getHeader("Return-Path"));
			System.out.println("----------------------------------------");
			Transport.send(msg);
		} catch (MessagingException mex) {
			System.out.println("¥n--Exception handling in msgsendsample.java");
			mex.printStackTrace();
		}
	}
	
	/**
	 * エラーメール送信
	 * @param th
	 */
	public static void send(Throwable th) {
		if (!"true".equals(ResourceBundle.getBundle("app").getString("mail.enable"))) {
			return;
		}
		String msgText = ExceptionUtils.getFullStackTrace(th);
		send(msgText);
	}
	
	public static void main(String[] args) {
		Mail.send(new Exception("test"));
	}
}
