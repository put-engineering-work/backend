package work.service.email;

public record EmailDetails(
        String recipient,
        String msgBody,
        String subject,
        String attachment
) {
    public EmailDetails(String recipient, String msgBody, String subject, String attachment) {
        this.recipient = recipient;
        this.msgBody = msgBody;
        this.subject = subject;
        this.attachment = attachment;
    }

    public EmailDetails(String recipient, String msgBody, String subject) {
        this(recipient, msgBody, subject, null);
    }
}