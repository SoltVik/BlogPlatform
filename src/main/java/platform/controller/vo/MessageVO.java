package platform.controller.vo;

import platform.domain.Message;
import platform.domain.User;

import java.util.Date;

public class MessageVO {

    private int id;
    private String text;
    private String title;
    private Date dateCreate;
    private Date dateEdit;
    private Date dateDelete;
    private User authorId;
    private int editorId;
    private int parentId;

    public MessageVO(int id, String text, String title, Date dateCreate, Date dateEdit, Date dateDelete, User authorId, int editorId, int parentId) {
        this.id = id;
        this.text = text;
        this.title = title;
        this.dateCreate = dateCreate;
        this.dateEdit = dateEdit;
        this.dateDelete = dateDelete;
        this.authorId = authorId;
        this.editorId = editorId;
        this.parentId = parentId;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getTitle() {
        return title;
    }

    public Date getDateCreate() {
        return dateCreate;
    }

    public Date getDateEdit() {
        return dateEdit;
    }

    public Date getDateDelete() {
        return dateDelete;
    }

    public User getAuthorId() {
        return authorId;
    }

    public int getEditorId() {
        return editorId;
    }

    public int getParentId() {
        return parentId;
    }

    public static MessageVO valueOf(Message message) {
        return new MessageVO(message.getId(), message.getText(), message.getTitle(), message.getDateCreate(), message.getDateEdit(), message.getDateDelete(), message.getAuthorId(), message.getEditorId(), message.getParentId());
    }
}
