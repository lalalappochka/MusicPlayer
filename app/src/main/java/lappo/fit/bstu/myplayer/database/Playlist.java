package lappo.fit.bstu.myplayer.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Playlist {
    @PrimaryKey(autoGenerate = true)
    public long playlistId;
    public String title;
    public String coverPath;
    public String songPaths;
}
