package fi.markus.iltakommentti.dao;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import fi.markus.iltakommentti.model.Comment;
import fi.markus.iltakommentti.model.NewsArticle;

// repo adds a Currency and get all the Currencies
public class IltakommenttiRepository {

    private IltakommenttiDao mDao;
    private LiveData<List<NewsArticle>> newsArticles;

    public IltakommenttiRepository(Application application) {
        IltakommenttiDatabase db = IltakommenttiDatabase.getDatabase(application);
        mDao = db.iltakommenttiDao();
        newsArticles = mDao.getNewsArticles();
    }

    public LiveData<List<NewsArticle>> getNewsArticles() {
        return newsArticles;
    }

    public LiveData<List<Comment>> getComments() { return mDao.getComments(); }

    public void insertNewsArticle (NewsArticle na) {
        IltakommenttiDatabase.databaseWriteExecutor.execute(() -> {
            mDao.insertNewsArticle(na);
        });
    }

    public void updateComment(Comment comment) {
        IltakommenttiDatabase.databaseWriteExecutor.execute(() -> {
            mDao.updateComment(comment);
        });
    }

    public void insertComment (Comment comment) {
        IltakommenttiDatabase.databaseWriteExecutor.execute(() -> {
            mDao.insertComment(comment);
        });
    }

    public void deleteComment(Comment selectedComment) {
        IltakommenttiDatabase.databaseWriteExecutor.execute(() -> {
            mDao.deleteComment(selectedComment);
        });
    }
}
