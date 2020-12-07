package fi.markus.iltakommentti.dao;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import fi.markus.iltakommentti.model.Comment;
import fi.markus.iltakommentti.model.NewsArticle;

@Database(entities = {NewsArticle.class, Comment.class}, version = 1, exportSchema = false)
public abstract class IltakommenttiDatabase extends RoomDatabase {

    public abstract IltakommenttiDao iltakommenttiDao();

    private static IltakommenttiDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static IltakommenttiDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (IltakommenttiDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            IltakommenttiDatabase.class, "iltakommentti_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }

        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback(){

                @Override
                public void onOpen (@NonNull SupportSQLiteDatabase db){
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final IltakommenttiDao mDao;

        PopulateDbAsync(IltakommenttiDatabase db) {
            mDao = db.iltakommenttiDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            try {
                NewsArticle na1 = new NewsArticle(1, "Tutkijat varoittavat: 5G-mastot aiheuttavat koronaviruksen lisäksi myös impotenssin ja ripulikohtauksia. Lue lisää..", "otsikko_5g", 0);
                NewsArticle na2 = new NewsArticle(2, "Donald Trumpin Twitter-kohu! Haukkui tasavallan presidentti Sauli Niinistöä liskoihmiseksi. Lue lisää..", "otsikko_trump", 0);
                NewsArticle na3 = new NewsArticle(3, "SUPER HELLE! Ilmastonmuutos lupaa 150-asteista intiaanikesää marraskuulle. Aurinkorasvat esiin! Lue lisää..", "otsikko_ilmasto", 0);
                NewsArticle na4 = new NewsArticle(4, "TOSI SÖPÖI HAUVOJA! UwU *rawr* Katso tästä vertikaalisesti kuvattu paskalaatuinen video 30 sekunnin ei-skipattavalla mainoksella. Lue lisää..", "otsikko_hauva", 0);
                NewsArticle na5 = new NewsArticle(5, "Jare Henrik Tiihonen elikkäpä Cheek tekee comebackin! Kuuntele tästä upouusi sinkku 'Soijapavut Mun Riisiin'! Lue lisää..", "otsikko_cheek", 0);
                NewsArticle na6 = new NewsArticle(6, "Päivi Räsänen haluaa kieltää paskalla käymisen: 'Perseen esiaviollinen pyyhkiminen johtaa kadotukseen, tms..' Lue lisää..", "otsikko_paivi", 0);

                List<NewsArticle> newsArticles = Arrays.asList(
                        na1, na2, na3, na4, na5, na6
                );

                for (NewsArticle na : newsArticles) {
                    mDao.insertNewsArticle(na);
                }

                List<Comment> comments = Arrays.asList(
                        // 5G-kommentit
                        new Comment("jaska58", "Kuinka kehtaavat? Hallitus vaarantaa tällaisilla toimilla Suomen kansalaiset..", 0, 1),
                        new Comment("kari123", "Pojan pojallani uusi puhelin, jossa viisi-Gee.. Kohta sulattaa nuorukaisen aivot!", 0, 1),
                        new Comment("Marjukka", "PITÄISI LAILLA KIELTÄÄ!!!", 0, 1),
                        new Comment("Tero", "Nyt menee kyllä kaikki DNA Sonerat ja Enirot poikottiin!!", 0, 1),
                        new Comment("jokke666", "Corona maskit on lampaille.. ", 0, 1),
                        new Comment("vauva_puumeri", "Minulla doro-puhelin.. en tuommoisia ", 0, 1),
                        new Comment("marko69", "Valkoista hetero miestä kohtaan hyökätään tässä. ", 0, 1),
                        new Comment("Kalevi", "Kyllä on nyt niin ettäs kiinan viirus on salaliitto laboratoriossa keksitty kiusa meille suomalaisille veron maksajille..", 0, 1),
                        new Comment("M4rj4-Terttu", "Kyllä meidän Sauli on tosi mies.. ei mikään oranssi juusto pallero ameriikan risudentti.", 0, 2),
                        new Comment("Jamppa123", "Kyllä trumppi on hieno mies.. ei mikään tuollainen liskomatelijamies.", 0, 2)
                );

                for (Comment comment : comments) {
                    mDao.insertComment(comment);
                }
            } catch (SQLiteConstraintException e) {
                System.out.println("Database already populated.");
            }
            return null;
        }
    }
}

