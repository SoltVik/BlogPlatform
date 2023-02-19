package platform.controller.vo;

import platform.domain.Message;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.Date;

public class MessageVO {

    private int id;

    @NotNull(message = "Text cannot be null")
    @Size(min = 10, message= "Text must be minimum 10 characters")
    private String text;

    @NotNull(message = "Title cannot be null")
    @Size(min = 5, max = 200, message= "Title must be between 5 and 200 characters")
    private String title;

    @PastOrPresent
    private Date dateCreate;

    @PastOrPresent
    private Date dateEdit;

    @PastOrPresent
    private Date dateDelete;

    @NotNull
    @Positive
    private Integer authorId;

    @Positive
    private Integer editorId;

    @Positive
    private Integer parentId;

    public MessageVO(int id, String text, String title, Date dateCreate, Date dateEdit, Date dateDelete, Integer authorId, Integer editorId, Integer parentId) {
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

    public Integer getAuthorId() {
        return authorId;
    }

    public Integer getEditorId() {
        return editorId;
    }

    public Integer getParentId() {
        return parentId;
    }

    public static MessageVO valueOf(Message message) {
        return new MessageVO(message.getId(), message.getText(), message.getTitle(), message.getDateCreate(), message.getDateEdit(), message.getDateDelete(), message.getAuthor().getId(), (message.getEditor() == null) ? null : message.getEditor().getId(), message.getParentId());
    }
}
