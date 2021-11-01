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
                        // 5G
                        new Comment("jaska58", "Kuinka kehtaavat? Hallitus vaarantaa tällaisilla toimilla Suomen kansalaiset..", 0, 1),
                        new Comment("kari123", "Pojan pojallani uusi puhelin, jossa viisi-Gee.. Kohta sulattaa nuorukaisen aivot!", 0, 1),
                        new Comment("Marjukka", "PITÄISI LAILLA KIELTÄÄ!!!", 0, 1),
                        new Comment("Tero", "Nyt menee kyllä kaikki DNA Sonerat ja Enirot poikottiin!!", 0, 1),
                        new Comment("jokke666", "Corona maskit on lampaille.. ", 0, 1),
                        new Comment("vauva_puumeri", "Minulla doro-puhelin.. en tuommoisia ", 0, 1),
                        new Comment("marko69", "Valkoista hetero miestä kohtaan hyökätään tässä. ", 0, 1),
                        new Comment("Kalevi", "Kyllä on nyt niin ettäs kiinan viirus on salaliitto laboratoriossa keksitty kiusa meille suomalaisille veron maksajille..", 0, 1),
                        // TRUMP
                        new Comment("M4rj4-Terttu", "Kyllä meidän Sauli on tosi mies.. ei mikään oranssi ameriikan risudentti.", 0, 2),
                        new Comment("Jamppa123", "Kyllä trumppi on hieno mies.. ei mikään tuollainen liskomies.", 0, 2),
                        new Comment("kolmen_lapsen_äityli", "Miksi aina uutislööpit hyökkäävät amerikan presidenttejä kohtaan?! Suomessa asutaan, meillä kaikki hyvin täällä..", 0, 2),
                        new Comment("lööppääjä", "Kaikkea kanssa.. Nykyään on kaikkia liskoja ja muita vähemmistöjä. Kohta saa mennä palopostin kanssa naimisiin?! Huhhuh..", 0, 2),
                        new Comment("Maka-Riina", "Totuus tulee aina julki!", 0, 2),
                        // ILMASTONMUUTOS
                        new Comment("Rööki-Reijo", "Kyllä on ihan höpön löpön pöppöötä tuo ilmastousko. Meillä Keravalla satoi lunta kerran viime jouluna..", 0, 3),
                        new Comment("Seppo Taalasmaa", "Ja meidän suomalaisten verorahoilla sitten kaikki pelastetaan! Ei pääse diesel autolla enää kohta minnekään!!", 0, 3),
                        new Comment("Ulla Taalasmaa", "Hyvin tarkenee! Suomessa aina niin kylmä :-/", 0, 3),
                        // KOIRANPENTU
                        new Comment("Miksi", "En ymmärrä?! Miksi ei osata puhelinta kääntää? Ikävät mustat palkit sivuilla.. :-(", 0, 4),
                        new Comment("Sirpa-täti", "Kyllä on nyt törkeätä resurssien haaskausta, kun lukijalle näytetään hirveän pitkiä mainoksia vain! Köyhät köyhtyy ja rikkaat vaan rikastuu!", 0, 4),
                        new Comment("hyi", "Lopetan lehden tilauksen, en tykkää!", 0, 4),
                        // CHEEK
                        new Comment("Risto55", "Kuka Cherk? En tunnista.. Pitäisikö tietää?", 0, 5),
                        new Comment("Liisi15", "IIIIIIIH! JHT <33333 Cheek on kyl mun lempi pändi!", 0, 5),
                        // RÄSÄNEN
                        new Comment("hillitön_pappi", "Nyt riittää, Päivi.. Erosin kirkosta!", 0, 6),
                        new Comment("PasiP", "Olen iäkäs mies, enkä ikinä persettäni pyyhkinyt.. Paskon muutenkin aina housuihini.", 0, 6)
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

