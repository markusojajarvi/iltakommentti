package fi.markus.iltakommentti.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "comments",
        foreignKeys = @ForeignKey(entity = NewsArticle.class,
        parentColumns = "articleId",
        childColumns = "articleId",
        onDelete = ForeignKey.CASCADE))
public class Comment {
    @PrimaryKey(autoGenerate = true) private int commentId;
    @NonNull
    private String username;

    @NonNull
    private String commentDescription;

    @NonNull
    private Integer addedByUser;

    @NonNull @ColumnInfo(name = "articleId")
    private int articleId;

    @Ignore
    public Comment() {}

    public Comment(@NonNull String username, @NonNull String commentDescription, @NonNull int addedByUser, @NonNull int articleId) {
        this.username = username;
        this.commentDescription = commentDescription;
        this.addedByUser = addedByUser;
        this.articleId = articleId;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    @NonNull
    public String getCommentDescription() {
        return commentDescription;
    }

    public void setCommentDescription(@NonNull String commentDescription) {
        this.commentDescription = commentDescription;
    }

    @NonNull
    public Integer getAddedByUser() {
        return addedByUser;
    }

    public void setAddedByUser(@NonNull Integer addedByUser) {
        this.addedByUser = addedByUser;
    }

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }
}

