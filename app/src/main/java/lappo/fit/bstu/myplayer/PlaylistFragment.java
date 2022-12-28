package lappo.fit.bstu.myplayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import lappo.fit.bstu.myplayer.database.AppDatabase;
import lappo.fit.bstu.myplayer.database.ImageCoder;
import lappo.fit.bstu.myplayer.database.Playlist;
import lappo.fit.bstu.myplayer.database.PlaylistDao;


public class PlaylistFragment extends Fragment {
    private ListView listView;
    private ImageButton imgBtn;
    private List<Playlist> playlists;
    private PlaylistDao dao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        AppDatabase db = Room.databaseBuilder(getActivity().getApplicationContext(),
                        AppDatabase.class, "music-db")
                .allowMainThreadQueries()
                .build();

        dao = db.playlistDao();

        playlists = dao.getAll();
        customAdapter customAdapter = new customAdapter();
        listView = view.findViewById(R.id.listViewPlaylist);
        listView.setAdapter(customAdapter);
        imgBtn = view.findViewById(R.id.imageButton);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddPlaylistActivity.class);
                startActivity(intent);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(getActivity().getApplicationContext(), PlaylistActivity.class)
                        .putExtra("playlistId", playlists.get(i).playlistId));
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
        return view;
    }

    class customAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return playlists.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        public void removeItem(int i) {
            Playlist toRemove = playlists.get(i);
            dao.deletePlaylist(toRemove);

            playlists.remove(i);
        }

        @Override
        public long getItemId(int i) {
            return playlists.get(i).playlistId;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View myView = getLayoutInflater().inflate(R.layout.playlist_item, null);
            TextView playlistName = myView.findViewById(R.id.txtplaylistname);
            ImageView playlistCover = myView.findViewById(R.id.imgsong);
            playlistName.setSelected(true);
            playlistName.setText(playlists.get(i).title);
            playlistCover.setImageBitmap(ImageCoder.decodeBitmap(playlists.get(i).coverPath));
            return myView;
        }
    }
}