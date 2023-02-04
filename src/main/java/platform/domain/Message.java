package platform.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name="messages")
public class Message implements Comparable<Message>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private int id;

    @Size(min = 10, message= "Text must be minimum 10 characters")
    @Column(name = "text")
    private String text;

    @NotNull(message = "Title cannot be null")
    @Size(min = 5, max = 200, message= "Title must be between 5 and 200 characters")
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "editor_id")
    private User editor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Message parent;

    @Column(name = "parent_id", updatable = false, insertable = false)
    private Integer parentId;

    public Message() {

    }

    public Message(String text, String title, Date dateCreate, Date dateEdit, Date dateDelete, User author, User editorId, Message parent) {
        this.text = text;
        this.title = title;
        this.dateCreate = dateCreate;
        this.dateEdit = dateEdit;
        this.dateDelete = dateDelete;
        this.author = author;
        this.editor = editorId;
        this.parent = parent;
    }

    public Message(String text, String title, Date dateCreate, User author, Message parent) {
        this.text = text;
        this.title = title;
        this.dateCreate = dateCreate;
        this.author = author;
        this.parent = parent;
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

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public User getEditor() {
        return editor;
    }

    public void setEditor(User editor) {
        this.editor = editor;
    }

    public Integer getParentId() {
        return parentId;
    }

    public Message getParent() {
        return parent;
    }

    public void setParent(Message parent) {
        this.parent = parent;
        this.parentId = (parent != null) ? parent.getParentId() : null;
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
                ", author=" + author +
                ", editor=" + editor +
                ", parentId=" + parentId +
                '}';
    }

    @Override
    public int compareTo(Message o) {
        return this.getId() - o.getId();
    }
}
