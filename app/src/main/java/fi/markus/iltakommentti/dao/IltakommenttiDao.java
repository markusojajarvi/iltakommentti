package fi.markus.iltakommentti.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fi.markus.iltakommentti.model.Comment;
import fi.markus.iltakommentti.model.NewsArticle;

@Dao
public interface IltakommenttiDao {

    @Insert
    void insertNewsArticle(NewsArticle newsArticle);

    @Insert
    void insertComment(Comment comment);

    @Update
    void updateComment(Comment comment);

    @Delete
    void deleteComment(Comment comment);

    @Query("SELECT * from newsarticles")
    LiveData<List<NewsArticle>> getNewsArticles();

    @Query("SELECT * from comments")
    LiveData<List<Comment>> getComments();

    @Query("SELECT * from newsarticles WHERE articleID = (:articleId)")
    LiveData<List<NewsArticle>> getNewsArticle(Integer articleId);

    @Query("DELETE from newsarticles")
    void deleteAllNewsArticles();

    @Query("DELETE from newsarticles WHERE addedByUser = 0")
    void deletePopulatedNewsArticles();

    @Query("DELETE from comments WHERE addedByUser = 0")
    void deletePopulatedComments();

}
