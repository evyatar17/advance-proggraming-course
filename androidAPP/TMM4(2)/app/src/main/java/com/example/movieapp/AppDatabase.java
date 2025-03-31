package com.example.movieapp;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.TypeConverters;

@Database(entities = {MovieEntity.class}, version = 2 ,exportSchema = true)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract MovieDao movieDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "movie_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
