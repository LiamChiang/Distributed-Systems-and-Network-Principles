public class Transaction {

    private String sender;
    private String content;
    private boolean isValid;

    public Transaction() {
        isValid = false;
    }

    public synchronized String getSender() {
        return sender;
    }

    public synchronized void setSender(String sender) {
        this.sender = sender;
        isValid = validate();
    }

    public synchronized String getContent() {
        return content;
    }

    public synchronized void setContent(String content) {
        this.content = content;
        isValid = validate();
    }

    public synchronized String toString() {
        if (isValid) {
            return String.format("|%s|%70s|\n", sender, content);
        }
        return null;
    }

    public synchronized boolean equals(Object other) {
        if (!isValid) {
            return false;
        }
        if (other == null) {
            return false;
        }
        if (getClass() != other.getClass()) {
            return false;
        }
        Transaction tx = (Transaction) other;
        if (content.equals(tx.getContent()) && sender.equals(tx.getSender())) {
            return true;
        }
        return false;
    }

    private synchronized boolean validate() {
        if (sender == null || content == null) {
            return false;
        }
        if (!sender.matches("^[a-z]{4}[0-9]{4}$")) {
            return false;
        }
        if (content.contains("\\|") || content.length() > 70) {
            return false;
        }
        return true;
    }

    public boolean isValid() {
        return isValid;
    }

}
