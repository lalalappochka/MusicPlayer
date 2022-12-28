package lappo.fit.bstu.myplayer;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.File;
import java.io.IOException;

import lappo.fit.bstu.myplayer.database.AppDatabase;
import lappo.fit.bstu.myplayer.database.ImageCoder;
import lappo.fit.bstu.myplayer.database.Playlist;
import lappo.fit.bstu.myplayer.database.PlaylistDao;

public class AddPlaylistActivity extends AppCompatActivity {
    private ImageButton imageButton;
    private EditText editTitle; //изменять поле
    private Button saveButton;
    private Playlist playlist = new Playlist();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_playlist);
        editTitle = findViewById(R.id.editTitle);
        imageButton = findViewById(R.id.imageBtn);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });
        saveButton = findViewById(R.id.saveBtn);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playlist.title = editTitle.getText().toString();
                AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "music-db")
                        .allowMainThreadQueries()
                        .build();

                PlaylistDao dao = db.playlistDao();
                long playlistId = dao.insertPlaylist(playlist);

                Intent intent = new Intent(getApplicationContext(), PlaylistActivity.class);
                intent.putExtra("playlistId", playlistId);
                startActivity(intent);
            }
        });
    }

    void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        launchSomeActivity.launch(i);
    }

    ActivityResultLauncher<Intent> launchSomeActivity
            = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
            result -> {
                        if(result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();

                            if(data != null
                            && data.getData() != null) {
                                Uri selectedImageUri = data.getData();
                                Bitmap bitmap = null;
                                try {
                                    bitmap = MediaStore.Images.Media.getBitmap(
                                            getContentResolver(), selectedImageUri);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                playlist.coverPath = ImageCoder.encodeBitmap(bitmap);

                                imageButton.setImageURI(selectedImageUri);
                            }
                        }
            }
    );
}