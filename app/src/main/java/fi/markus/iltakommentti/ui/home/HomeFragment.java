package fi.markus.iltakommentti.ui.home;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import fi.markus.iltakommentti.BuildConfig;
import fi.markus.iltakommentti.R;
import fi.markus.iltakommentti.SharedViewModel;
import fi.markus.iltakommentti.model.Comment;
import fi.markus.iltakommentti.model.NewsArticle;
import fi.markus.iltakommentti.ui.notifications.NotificationsViewModel;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private SharedViewModel sharedViewModel;
    private EditText commentText, userIdText;
    private Button prevBtn, nextBtn, generateBtn, clearBtn, saveBtn, shareBtn;
    private ImageView newsArticleView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        /* Fragmentin komponenttien alustus */
        userIdText = root.findViewById(R.id.userID_txt);
        commentText = root.findViewById(R.id.comment_textfield);
        prevBtn = root.findViewById(R.id.prev_btn);
        prevBtn.setOnClickListener(this);
        nextBtn = root.findViewById(R.id.next_btn);
        nextBtn.setOnClickListener(this);
        generateBtn = root.findViewById(R.id.generate_btn);
        generateBtn.setOnClickListener(this);
        clearBtn = root.findViewById(R.id.clear_btn);
        clearBtn.setOnClickListener(this);
        saveBtn = root.findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);
        shareBtn = root.findViewById(R.id.share_btn);
        shareBtn.setOnClickListener(this);
        newsArticleView = root.findViewById(R.id.newsArticleView);

        /* OBSERVERIEN ALUSTUS */
       /* Päivitetään valittu uutisartikkeli */
        sharedViewModel.getSelectedNewsArticle().observe(getViewLifecycleOwner(), new Observer<NewsArticle>() {
            @Override
            public void onChanged(NewsArticle newsArticle) {
                // Asetetaan uutisen kuva vastaamaan valitun artikkelia
                newsArticleView.setImageResource(getImageId(HomeFragment.super.getContext(), newsArticle.getArticleImageSrc()));
                clearTextFields();
            }
        });
        /* Haetaan Room-kannasta artikkelit */
        sharedViewModel.getNewsArticles().observe(getViewLifecycleOwner(), new Observer<List<NewsArticle>>() {
            @Override
            public void onChanged(List<NewsArticle> newsArticles) {
                System.out.println(newsArticles.size() + " uutiset");
                if (newsArticles.size() > 0) {
                    if (sharedViewModel.getSelectedNewsArticle().getValue() == null) {
                        NewsArticle newsArticle = newsArticles.get(0);
                        sharedViewModel.setSelectedNewsArticle(newsArticle);
                    }
                }
            }
        });

        /* Päivitetään arvottu kommentti */
        sharedViewModel.getSelectedComment().observe(getViewLifecycleOwner(), new Observer<Comment>() {
            @Override
            public void onChanged(Comment comment) {
                commentText.setText(comment.getCommentDescription());
                commentText.clearFocus();
                userIdText.setText(comment.getUsername());
                userIdText.clearFocus();
            }
        });

        sharedViewModel.getComments().observe(getViewLifecycleOwner(), new Observer<List<Comment>>() {
            @Override
            public void onChanged(List<Comment> comments) {}
        });

        return root;
    }

    @Override
    public void onClick(View v) {
        List<NewsArticle> newsArticles = sharedViewModel.getNewsArticles().getValue();
        NewsArticle selectedNewsArticle = sharedViewModel.getSelectedNewsArticle().getValue();
        Comment selectedComment = sharedViewModel.getSelectedComment().getValue();

        switch (v.getId()) {
            case R.id.prev_btn: // Prev-btn
                for(int i = newsArticles.size() - 1; i >= 0; i--) {
                    if (selectedNewsArticle.getArticleId() > newsArticles.get(i).getArticleId()) {
                        sharedViewModel.setSelectedNewsArticle(newsArticles.get(i));
                        break;
                    }
                }
                break;
            case R.id.next_btn: // Next-btn
                for(int i = 1; i < newsArticles.size(); i++) {
                    if (selectedNewsArticle.getArticleId() < newsArticles.get(i).getArticleId()) {
                        sharedViewModel.setSelectedNewsArticle(newsArticles.get(i));
                        break;
                    }
                }
                break;
            case R.id.generate_btn:
                Observer<List<Comment>> commentGenerateObserver = new Observer<List<Comment>>() {
                    @Override
                    public void onChanged(List<Comment> comments) {
                        sharedViewModel.setSelectedComment(getRandomComment(comments, selectedNewsArticle.getArticleId()));
                    }
                };
                sharedViewModel.getComments().observe(getViewLifecycleOwner(), commentGenerateObserver);
                sharedViewModel.getComments().removeObserver(commentGenerateObserver);
                break;
            // Kommentin poisto / input-kenttien tyhjennys
            case R.id.clear_btn:
                final String[] fonts = {
                        "Poista", "Tyhjennä"
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Poistetaanko kommentti?");
                builder.setItems(fonts, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if ("Poista".equals(fonts[which])) {
                            if (selectedComment != null && selectedComment.getCommentId() > 0) {
                                sharedViewModel.deleteComment(selectedComment);
                                Toast.makeText(getContext(), "Kommentti poistettu.", Toast.LENGTH_SHORT).show();
                            }
                            sharedViewModel.setSelectedComment(new Comment());
                        } else if ("Tyhjennä".equals(fonts[which])) {
                            clearTextFields();
                        }
                }});
                builder.show();
                break;
            case R.id.save_btn: // Kommentin tallennus
                // Muokataan aiemmin tallennettua kommenttia:
                if (selectedComment != null && selectedComment.getArticleId() > 0
                        && selectedComment.getCommentDescription().equals(commentText.getText().toString())
                        && selectedComment.getUsername().equals(userIdText.getText().toString())) {
                        Toast.makeText(getContext(), "Ei muutoksia.", Toast.LENGTH_SHORT).show();
                // Uusi kommentti
                } else {
                    if (userIdText.getText().toString().isEmpty() || commentText.getText().toString().isEmpty()) {
                        Toast.makeText(getContext(), "Syötä käyttäjänimi ja kommentti.", Toast.LENGTH_SHORT).show();
                    } else {
                        Comment comment = new Comment(userIdText.getText().toString(), commentText.getText().toString(), 1, selectedNewsArticle.getArticleId());
                        sharedViewModel.insertComment(comment);
                        Toast.makeText(getContext(), "Uusi kommentti lisätty.", Toast.LENGTH_SHORT).show();
                        clearTextFields();
                    }

                }
                break;
            case R.id.share_btn:
                shareContent(selectedNewsArticle, userIdText.getText().toString(), commentText.getText().toString());
                break;
            default:
                break;
        }
    }

    public void clearTextFields() {
        userIdText.setText("");
        commentText.setText("");
    }

    public static int getImageId(Context context, String imageName) {
        return context.getResources().getIdentifier("drawable/" + imageName, null, context.getPackageName());
    }

    public Comment getRandomComment(List<Comment> comments, Integer articleId) {
        List<Comment> selectedComments = new ArrayList<>();
        for(Comment comment : comments) {
            if (comment.getArticleId() == articleId) selectedComments.add(comment);
        }
        if (selectedComments.size() > 0) {
            int commentIndex = (int)(Math.random() * selectedComments.size());
            return selectedComments.get(commentIndex);
        }
        return new Comment();
    }

    public void shareContent(NewsArticle selectedNewsArticle, String userID, String comment) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);

        if (!comment.isEmpty()) {
            String shareTxt = "ILTAKOMMENTTI: " + selectedNewsArticle.getArticleName() + "\n\n"
                    + comment + " Terveisin " + userID;
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareTxt);
        }

        Uri imageUri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID, createImageFile(selectedNewsArticle));
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("*/*");
        shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "send"));
    }

    public File createImageFile(NewsArticle newsArticle) {
        // create file from drawable image
        Bitmap bm = BitmapFactory.decodeResource(this.getResources(), getImageId(getContext(), newsArticle.getArticleImageSrc()));

        File filesDir = getActivity().getApplicationContext().getFilesDir();
        File imageFile = new File(filesDir, newsArticle.getArticleImageSrc() + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, os); // 100% quality
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
        }
        return imageFile;
    }
}