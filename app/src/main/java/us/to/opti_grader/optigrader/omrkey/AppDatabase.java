package us.to.opti_grader.optigrader.omrkey;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {OMRKey.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract OMRKeyDao omrKeyDao();
}