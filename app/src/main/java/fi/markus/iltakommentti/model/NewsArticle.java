package fi.markus.iltakommentti.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "newsarticles")
public class NewsArticle {
    @PrimaryKey
    private int articleId;

    @NonNull
    private String articleName;

    @NonNull
    private String articleImageSrc;

    @NonNull
    private Integer addedByUser;

    public NewsArticle(@NonNull Integer articleId, @NonNull String articleName, @NonNull String articleImageSrc, @NonNull Integer addedByUser) {
        this.articleId = articleId;
        this.articleName = articleName;
        this.articleImageSrc = articleImageSrc;
        this.addedByUser = addedByUser;
    }

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    @NonNull
    public String getArticleName() {
        return articleName;
    }

    public void setArticleName(@NonNull String articleName) {
        this.articleName = articleName;
    }

    @NonNull
    public String getArticleImageSrc() {
        return articleImageSrc;
    }

    public void setArticleImageSrc(@NonNull String articleImageSrc) {
        this.articleImageSrc = articleImageSrc;
    }

    @NonNull
    public Integer getAddedByUser() {
        return addedByUser;
    }

    public void setAddedByUser(@NonNull Integer addedByUser) {
        this.addedByUser = addedByUser;
    }

    @Override
    public String toString() {
        return "NewsArticle{" +
                "articleId=" + articleId +
                ", articleName='" + articleName + '\'' +
                ", articleImageSrc='" + articleImageSrc + '\'' +
                ", addedByUser=" + addedByUser +
                '}';
    }
}
