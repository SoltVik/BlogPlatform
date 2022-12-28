package platform.domain;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Table(name="messages")
public class Message implements Comparable<Message>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private int id;

    @Column(name = "text")
    private String text;

    @Column(name = "title")
    private String title;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_create")
    private Date dateCreate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_edit")
    private Date dateEdit;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_delete")
    private Date dateDelete;

    @Column(name = "author_id")
    private int authorId;

    @Column(name = "editor_id")
    private int editorId;

    @Column(name = "parent_id")
    private int parentId;

    public Message() {

    }

    public Message(String text, String title, Date dateCreate, Date dateEdit, Date dateDelete, int authorId, int editorId, int parentId) {
        this.text = text;
        this.title = title;
        this.dateCreate = dateCreate;
        this.dateEdit = dateEdit;
        this.dateDelete = dateDelete;
        this.authorId = authorId;
        this.editorId = editorId;
        this.parentId = parentId;
    }

    public Message(String text, String title, Date dateCreate, int authorId, int parentId) {
        this.id = id;
        this.text = text;
        this.title = title;
        this.dateCreate = dateCreate;
        this.authorId = authorId;
        this.parentId = parentId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(Date dateCreate) {
        this.dateCreate = dateCreate;
    }

    public Date getDateEdit() {
        return dateEdit;
    }

    public void setDateEdit(Date dateEdit) {
        this.dateEdit = dateEdit;
    }

    public Date getDateDelete() {
        return dateDelete;
    }

    public void setDateDelete(Date dateDelete) {
        this.dateDelete = dateDelete;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public int getEditorId() {
        return editorId;
    }

    public void setEditorId(int editorId) {
        this.editorId = editorId;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }



    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", title='" + title + '\'' +
                ", dateCreate=" + dateCreate +
                ", dateEdit=" + dateEdit +
                ", dateDelete=" + dateDelete +
                ", authorId=" + authorId +
                ", editorId=" + editorId +
                ", parentId=" + parentId +
                '}';
    }

    @Override
    public int compareTo(Message o) {
        return this.getId() - o.getId();
    }
}
