package lappo.fit.bstu.myplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
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
import lappo.fit.bstu.myplayer.database.ImageCoder;
import lappo.fit.bstu.myplayer.database.Playlist;
import lappo.fit.bstu.myplayer.database.PlaylistDao;

public class PlaylistActivity extends AppCompatActivity {

    private ListView listView;
    private TextView title;
    private ImageView cover;
    private ImageButton addButton;
    private Playlist playlist;
    private List<String> items;
    private PlaylistDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "music-db")
                .allowMainThreadQueries()
                .build();

        dao = db.playlistDao();

        Intent crIntent = getIntent();
        Bundle bundle = crIntent.getExtras();

        long playlistId = bundle.getLong("playlistId");

        playlist = dao.findById(playlistId);

        listView = findViewById(R.id.listViewPlaylistItemSong);
        title = findViewById(R.id.txtPlaylistItemName);
        cover = findViewById(R.id.imgplaylist);
        title.setText(playlist.title);
        cover.setImageBitmap(ImageCoder.decodeBitmap(playlist.coverPath));
        addButton = findViewById(R.id.addSongBtn);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddSongToPlaylistActivity.class);
                intent.putExtra("playlistId", playlistId);
                startActivity(intent);
            }
        });
        displaySongs();
    }

    public ArrayList<File> findSong(File file){
        ArrayList<File> arrayList = new ArrayList<>();
        if(playlist.songPaths == null) {
            playlist.songPaths = "";
        }
        List<String> songs = new ArrayList<String>();
        Collections.addAll(songs, playlist.songPaths.split(","));

        File[] files = file.listFiles();
        for(File singleFile: files){
            if(singleFile.isDirectory() && !singleFile.isHidden()) {
                arrayList.addAll(findSong(singleFile));
            }
            else{
                if(singleFile.getName().endsWith(".mp3")||singleFile.getName().endsWith(".wav")){
                    String name = singleFile.getName()
                            .replace(".mp3", "")
                            .replace(".wav", "");
                    if(songs.contains(name)) {
                        arrayList.add(singleFile);
                    }

                }
            }
        }
        return arrayList;
    }

    void displaySongs(){
        final ArrayList<File> mySongs = findSong(Environment.getExternalStorageDirectory());
        items = new ArrayList();
        for(int i = 0; i<mySongs.size(); i++){
            items.add(mySongs.get(i).getName().toString().replace(".mp3","" ).replace("wav", ""));
        }
        plCustomAdapter customAdapter = new plCustomAdapter();
        listView.setAdapter(customAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String songName = (String) listView.getItemAtPosition(i);
                startActivity(new Intent(getApplicationContext(), PlayerActivity.class)
                        .putExtra("songs", mySongs)
                        .putExtra("songname", songName)
                        .putExtra("pos", i));
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long arg3) {
                customAdapter.removeItem(position);
                customAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    class plCustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return items.size();
        }

        public void removeItem(int i) {
            String toRemove = items.get(i);
            List<String> songs = new ArrayList<String>();
            Collections.addAll(songs, playlist.songPaths.split(","));
            songs.remove(toRemove);
            playlist.songPaths = String.join(",", songs);
            dao.updatePlaylist(playlist);

            items.remove(i);
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
            View myView = getLayoutInflater().inflate(R.layout.list_item, null);
            TextView textsong = myView.findViewById(R.id.txtsongname);
            textsong.setSelected(true);
            textsong.setText(items.get(i));
            return myView;
        }
    }
}