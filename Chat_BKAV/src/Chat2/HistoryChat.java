package Chat2;

public class HistoryChat {
	private static int count=0;
	private int id;
    private String userSend;
    private String userReceive;
    private String content;
    private String timeChat;

    public HistoryChat(String userSend, String userReceive, String content, String timeChat) {
    	this.id=++count;
        this.userSend = userSend;
        this.userReceive = userReceive;
        this.content = content;
        this.timeChat = timeChat;
    }

    public HistoryChat(int id, String userSend, String userReceive, String content, String timeChat) {
        this.id = id;
        this.userSend = userSend;
        this.userReceive = userReceive;
        this.content = content;
        this.timeChat = timeChat;
    }
    
    public HistoryChat() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserSend() {
        return userSend;
    }

    public void setUserSend(String userSend) {
        this.userSend = userSend;
    }

    public String getUserReceive() {
        return userReceive;
    }

    public void setUserReceive(String userReceive) {
        this.userReceive = userReceive;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimeChat() {
        return timeChat;
    }

    public void setTimeChat(String timeChat) {
        this.timeChat = timeChat;
    }
}
