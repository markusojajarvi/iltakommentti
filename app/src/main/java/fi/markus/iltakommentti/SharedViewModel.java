package fi.markus.iltakommentti;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import fi.markus.iltakommentti.dao.IltakommenttiRepository;
import fi.markus.iltakommentti.model.Comment;
import fi.markus.iltakommentti.model.NewsArticle;

public class SharedViewModel extends AndroidViewModel {

    private IltakommenttiRepository mRepository;

    // HOMEFRAGMENT -> NEWSARTICLES
    private LiveData<List<NewsArticle>> newsArticles;
    private final MutableLiveData<NewsArticle> selectedNewsArticle = new MutableLiveData<NewsArticle>();

    // HOMEFRAGMENT -> COMMENTS
    private LiveData<List<Comment>> comments;
    private final MutableLiveData<Comment> selectedComment = new MutableLiveData<Comment>();


    public SharedViewModel(Application application) {
        super(application);
        mRepository = new IltakommenttiRepository(application);

        // Room-kanta kutsut:
        newsArticles = mRepository.getNewsArticles();
        comments = mRepository.getComments();
    }

    public LiveData<List<NewsArticle>> getNewsArticles() { return newsArticles; }

    public LiveData<NewsArticle> getSelectedNewsArticle() { return selectedNewsArticle; }

    public void setSelectedNewsArticle(NewsArticle newsArticle) { selectedNewsArticle.setValue(newsArticle); }

    public LiveData<List<Comment>> getComments() { return comments; }

    public LiveData<Comment> getSelectedComment() { return selectedComment; }

    public void setSelectedComment(Comment comment) { selectedComment.setValue(comment); }

    public void insertComment(Comment comment) { mRepository.insertComment(comment); }

    public void updateComment(Comment comment) { mRepository.updateComment(comment); }

    public void deleteComment(Comment selectedComment) { mRepository.deleteComment(selectedComment); }
}
