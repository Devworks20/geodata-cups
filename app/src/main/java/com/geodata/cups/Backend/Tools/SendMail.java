package com.geodata.cups.Backend.Tools;

import android.content.Context;
import android.os.AsyncTask;

import com.geodata.cups.Backend.Retrofit.Model.Other.UsernameInfo;

import java.util.Properties;
import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
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

public class SendMail extends AsyncTask<Void, Void, Void>
{

    //Declaring Variables

    private Context context;
    private Session session;

    public SendMail(Context context, String email, String subject, String message, String filepath)
    {
        this.context = context;
        this.email = email;
        this.subject = subject;
        this.message = message;
        this.filepath = filepath;
    }

    public SendMail(Context context, String email, String subject, String message)
    {
        this.context = context;
        this.email = email;
        this.subject = subject;
        this.message = message;
    }

    //Information to send email
    private String email;
    private String subject;
    private String message;
    private String filepath;

    //Progressdialog to show while sending email
    //private ProgressDialog progressDialog;

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        //Showing progress dialog while sending email
        //progressDialog = ProgressDialog.show(context,"Sending message","Please wait...",false,false);
    }

    @Override
    protected void onPostExecute(Void aVoid)
    {
        super.onPostExecute(aVoid);
        //Dismissing the progress dialog
        //progressDialog.dismiss();
        //Showing a success message
        //Toast.makeText(context,"Message Sent",Toast.LENGTH_LONG).show();
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        //Creating properties
        Properties props = new Properties();

        //Configuring properties for gmail
        //If you are not using gmail you may need to change the values
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        //Creating a new session
        session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator()
                {
                    //Authenticating the password
                    protected PasswordAuthentication getPasswordAuthentication()
                    {
                        return new PasswordAuthentication(Config.EMAIL, Config.PASSWORD);
                    }
                });

        try {

            if(filepath==null)
            {
                //Creating MimeMessage object
                MimeMessage mm = new MimeMessage(session);

                //Setting sender address
                mm.setFrom(new InternetAddress(Config.EMAIL));
                //mm.setFrom(new InternetAddress("Safe City"));
                //Adding receiver
                mm.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
                //mm.addRecipient(Message.RecipientType.TO, new InternetAddress("Safe City"));
                //Adding subject
                mm.setSubject(subject);
                //Adding message
                mm.setText(message);

                //Sending email
                Transport.send(mm);
            }
            else
                {
                //Creating MimeMessage object
                MimeMessage mm = new MimeMessage(session);

                //Setting sender address
                mm.setFrom(new InternetAddress(Config.EMAIL));
                //mm.setFrom(new InternetAddress("Safe City"));
                //Adding receiver
                mm.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
                //mm.addRecipient(Message.RecipientType.TO, new InternetAddress("Safe City"));
                //Adding subject
                mm.setSubject(subject);

                BodyPart messageBodyPart = new MimeBodyPart();

                // Fill the message
                messageBodyPart.setText(message);

                // Create a multipar message
                Multipart multipart = new MimeMultipart();

                // Set text message part
                multipart.addBodyPart(messageBodyPart);

                // Part two is attachment
                messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(filepath);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(UsernameInfo.CompleteName + "_Daily Report" +".pdf");

                multipart.addBodyPart(messageBodyPart);

                MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
                mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
                mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
                mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
                mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
                mc.addMailcap("message/rfc822;; x-java-content- handler=com.sun.mail.handlers.message_rfc822");

                // Send the complete message parts
                mm.setContent(multipart);

                //Sending email
                Transport.send(mm);
            }
        }
        catch (MessagingException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
