package lappo.fit.bstu.myplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lappo.fit.bstu.myplayer.database.AppDatabase;
import lappo.fit.bstu.myplayer.database.Playlist;
import lappo.fit.bstu.myplayer.database.PlaylistDao;

public class AddSongToPlaylistActivity extends AppCompatActivity {

    ListView listView;
    private String[] items;
    private Playlist playlist;
    private AppDatabase db;
    private PlaylistDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "music-db")
                .allowMainThreadQueries()
                .build();

        dao = db.playlistDao();

        Intent crIntent = getIntent();
        Bundle bundle = crIntent.getExtras();

        long playlistId = bundle.getLong("playlistId");

        playlist = dao.findById(playlistId);

        setContentView(R.layout.activity_add_song_to_playlist);
        listView = findViewById(R.id.listViewSongToPlaylist);
        displaySongsToAddToPlaylist();

    }
    public ArrayList<File> findSong(File file){
        ArrayList<File> arrayList = new ArrayList<>();

        File[] files = file.listFiles();
        for(File singleFile: files){
            if(singleFile.isDirectory() && !singleFile.isHidden()) {
                arrayList.addAll(findSong(singleFile));
            }
            else{
                if(singleFile.getName().endsWith(".mp3")||singleFile.getName().endsWith(".wav")){
                    arrayList.add(singleFile);
                }
            }
        }
        return arrayList;
    }
    void displaySongsToAddToPlaylist(){
        final ArrayList<File> myAddSongs = findSong(Environment.getExternalStorageDirectory());
        items = new String[myAddSongs.size()];
        for(int i = 0; i<myAddSongs.size(); i++){
            items[i] = myAddSongs.get(i).getName().toString().replace(".mp3","" ).replace("wav", "");
        }
        customAdapterForPlaylist customAddAdapter = new customAdapterForPlaylist();
        listView.setAdapter(customAddAdapter);

    }
    class customAdapterForPlaylist extends BaseAdapter{

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View myPlView = getLayoutInflater().inflate(R.layout.add_item_song, null);
            TextView textsong = myPlView.findViewById(R.id.txtsongname);
            ImageButton addBtn = myPlView.findViewById(R.id.addSongBtnPlaylist);
            System.out.println(addBtn);
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(playlist.songPaths == null) {
                        playlist.songPaths = "";
                    }
                    List<String> songs = new ArrayList();
                    Collections.addAll(songs, playlist.songPaths.split(","));

                    if(!songs.contains(items[i])) {
                        songs.add(items[i]);
                    }

                    playlist.songPaths = String.join(",", songs);

                    dao.updatePlaylist(playlist);
                }
            });
            textsong.setSelected(true);
            textsong.setText(items[i]);
            return myPlView;
        }
    }
}
