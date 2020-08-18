package com.xpand.xface.util;

import java.util.Properties;

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

import com.jcabi.log.Logger;
import com.xpand.xface.bean.EmailInfo;


public class EmailEngine {
	public void sendEmail(String transactionId, EmailInfo emailInfo) throws Exception{
		final String localUser = emailInfo.getUserName();
		final String localPwd = emailInfo.getUserPwd();
		Logger.info(this, LogUtil.getLogInfo(transactionId,"initial session to send email"));
		Properties props = new Properties();
		props.put("mail.smtp.auth", emailInfo.getSmtpAuth());
		props.put("mail.smtp.starttls.enable", emailInfo.getSmtpStartTLS());
		props.put("mail.smtp.host", emailInfo.getSmtpHost());
		props.put("mail.smtp.port", emailInfo.getSmtpPort());
		Session session = null;
		Logger.info(this, LogUtil.getLogInfo(transactionId,"initial session done then prepare message"));
		try {
			session = Session.getInstance(props,
					  new javax.mail.Authenticator() {
						@Override
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(localUser, localPwd);
						}
					  });
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(emailInfo.getSendFrom()));
			//
			message.setRecipients(Message.RecipientType.CC,InternetAddress.parse(emailInfo.getSendTo()));			
			message.setSubject(emailInfo.getSubject());

			
			Multipart multipart = new MimeMultipart();
			BodyPart messageBodyPart = new MimeBodyPart();			
			messageBodyPart.setText(emailInfo.getMessage());		
			multipart.addBodyPart(messageBodyPart);
//			if (strAttachedFile != null){
//				MimeBodyPart attachmentPart = null;
//				if (strAttachedFile.indexOf(",")>0){
//					String arrayFile[] = strAttachedFile.split(",");
//					attachmentPart = new MimeBodyPart();
//					attachmentPart.attachFile(arrayFile[0]);
//					multipart.addBodyPart(attachmentPart);
//					attachmentPart = new MimeBodyPart();
//					attachmentPart.attachFile(arrayFile[1]);
//					multipart.addBodyPart(attachmentPart);
//				}else{
//					attachmentPart = new MimeBodyPart();
//					attachmentPart.attachFile(strAttachedFile);
//					multipart.addBodyPart(attachmentPart);
//				}												
//				this.oLog.info(strTrnId+"|Attached file:"+strAttachedFile);
//			}				       	        	      
	        // Send the complete message partsmessage.setText(strMessage);
	        message.setContent(multipart);	        
	        Logger.info(this, LogUtil.getLogInfo(transactionId,"submit to the mail server"));	        
			Transport.send(message);	        	        	       
			Logger.info(this, LogUtil.getLogInfo(transactionId,"message was send"));					
		} catch (MessagingException e) {
			throw new Exception(e);
		}finally {
			try {
				session.getTransport().close();
			}catch(Exception ex) {}
		}
		Logger.info(this, LogUtil.getLogInfo(transactionId,"out from send email"));
	}	
}
