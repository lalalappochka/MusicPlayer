package lappo.fit.bstu.myplayer.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Playlist.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PlaylistDao playlistDao();
}
