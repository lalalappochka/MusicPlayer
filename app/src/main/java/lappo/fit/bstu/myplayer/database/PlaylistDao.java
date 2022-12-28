package lappo.fit.bstu.myplayer.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PlaylistDao {
    @Query("SELECT * FROM playlist")
    List<Playlist> getAll();

    @Query("SELECT * FROM playlist WHERE playlistId = :id")
    Playlist findById(long id);

    @Insert
    long insertPlaylist(Playlist playlist);

    @Update
    void updatePlaylist(Playlist playlist);

    @Delete
    void deletePlaylist(Playlist playlist);
}
