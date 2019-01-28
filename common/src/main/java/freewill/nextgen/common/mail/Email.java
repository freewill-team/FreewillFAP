package freewill.nextgen.common.mail;

import java.util.Date;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

// Back-end service for sending mails
// Also added inbox checking functionality

public class Email {
	private static Email INSTANCE;
	private boolean Wait = false;
	
	private Email() {
    	try {
			// Connect to the Email server here ?
    	} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Fail to connect to Email server");
		}
    }

    public synchronized static Email get() {
        if (INSTANCE == null) {
            INSTANCE = new Email();
        }
        return INSTANCE;
    }
    
    public void SendAsync(String toEmail, String subject, String message,
    		String host, String port, String user, String pass) {
    	// Sends the email asynchronously, starting a separate thread
    	EmailThread mythread = new EmailThread(toEmail, subject, message, host, port, user, pass);
    	mythread.start();
    }

	public synchronized boolean Send(String toEmail, String subject, String message,
			String host, String port, String user, String pass) {
		while(Wait){
			// waits if other thread is running
		}
		Wait = true;
		/*
			Outgoing Mail (SMTP) Server
			requires TLS or SSL: smtp.gmail.com (use authentication)
			Use Authentication: Yes
			Port for TLS/STARTTLS: 587
		*/
		// lee estos datos de la tabla de configuraciones
		final String fromEmail = user; // "velamartin@gmail.com"; // requires valid gmail id
		final String password = pass; // "Sirena2!"; // correct password for gmail id
				
		System.out.println("TLSEmail Start");
		Properties props = new Properties();
		props.put("mail.smtp.host", host); // "smtp.gmail.com"); // SMTP Host
		props.put("mail.smtp.port", port); // "587"); // TLS Port
		props.put("mail.smtp.auth", "true"); //enable authentication
		props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS
				
		//create Authenticator object to pass in Session.getInstance argument
		Authenticator auth = new Authenticator() {
			//override the getPasswordAuthentication method
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(fromEmail, password);
			}
		};
		Session session = Session.getInstance(props, auth);		
		return sendEmail(session, toEmail, subject, message);
	}
		
	private boolean sendEmail(Session session, String toEmail, String subject, String body){
		try{
			MimeMessage msg = new MimeMessage(session);
			//set message headers
			msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
			msg.addHeader("format", "flowed");
			msg.addHeader("Content-Transfer-Encoding", "8bit");

			msg.setFrom(new InternetAddress("no_reply@freewill-technologies.es", "NoReply-Freewill"));
			msg.setReplyTo(InternetAddress.parse("no_reply@freewill-technologies.es", false));
			msg.setSubject(subject, "UTF-8");
			msg.setText(body, "UTF-8");
			msg.setSentDate(new Date());

			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
			System.out.println("Message is ready");
			Transport.send(msg);  

			System.out.println("EMail Sent Successfully!!");
			Wait = false;
			return true;
		}
		catch (Exception e) {
		    e.printStackTrace();
		    Wait = false;
		    return false;
		}
	}
	
	private static class EmailThread extends Thread {
		private String toEmail;
		private String subject; 
		private String message;
		private String host;
		private String port;
		private String user;
		private String pass;
		
		public EmailThread(String toEmail, String subject, String message,
				String host, String port, String user, String pass){
			this.toEmail=toEmail;
			this.subject=subject;
			this.message=message;
			this.host=host;
			this.port=port;
			this.user=user;
			this.pass=pass;
		}
		
	    public void run() {
	        get().Send(toEmail, subject, message, host, port, user, pass);
	    }
	}
	
}
