package com.example.mytiktok;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class AddNewVideo extends AppCompatActivity {
    private ActionBar actionBar;
    private EditText Tiltle;
    private VideoView videoView;
    private Button Upload;
    private FloatingActionButton videolib;
    private static final int VIDEO_CAMERA_CODE = 100;
    private static final int VIDEO_GALLERY_CODE = 101;
    private static final int VIDEO_REQUEST_CODE = 102;
    private String []cameraPremissions;
    private Uri urlVideo = null;
    private String title;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_video);

        Tiltle = findViewById(R.id.titleVideo);
        videoView = findViewById(R.id.VideoNew);
        Upload = findViewById(R.id.uploadvideo);
        videolib = findViewById(R.id.libvideo);

        actionBar = getSupportActionBar();
        setTitle("Add Video");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Uploading Video");
        progressDialog.setCanceledOnTouchOutside(false);
        cameraPremissions = new String[]{Manifest.permission.CAMERA,Manifest.permission.MANAGE_EXTERNAL_STORAGE};

         Upload.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 title = Tiltle.getText().toString().trim();
                 if(TextUtils.isEmpty(title)){
                     Toast.makeText(AddNewVideo.this,"Title is required....?",Toast.LENGTH_SHORT).show();
                 }else if(urlVideo == null){
                     Toast.makeText(AddNewVideo.this,"Video is required....?",Toast.LENGTH_SHORT).show();
                 }
                 else {
                     upLoadFirebase();
                 }

             }
         });
         videolib.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                videoPickDialog();
             }
         });


    }
   private void upLoadFirebase() {
        progressDialog.show();
        final String timeSlap = ""+ System.currentTimeMillis();
        String filePath = "Video/" + "video_" + timeSlap;

        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePath);
        storageReference.putFile(urlVideo).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while(!uriTask.isSuccessful());
                Uri downloadUri = uriTask.getResult();
                if(uriTask.isSuccessful()){
                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("id", ""+timeSlap);
                    hashMap.put("Title", title);
                    hashMap.put("timeslap",""+ timeSlap);
                    hashMap.put("VideoUri",""+ downloadUri);

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Videos");
                    reference.child(timeSlap).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            Toast.makeText(AddNewVideo.this,"Video Uploaded.....",Toast.LENGTH_LONG);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddNewVideo.this,e.getMessage()+"",Toast.LENGTH_LONG);
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddNewVideo.this,e.getMessage()+"",Toast.LENGTH_LONG);
            }
        });
    }

    private void requestCamera(){
        ActivityCompat.requestPermissions(this, cameraPremissions,VIDEO_REQUEST_CODE);
    }
    private boolean checkPremisstions(){
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean result2 = ContextCompat.checkSelfPermission(this,Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED;
        return result1 && result2;
    }


    private void videopickGallery(){
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Videos"), VIDEO_GALLERY_CODE);
    }



    private void videoPickCamera(){
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent,VIDEO_CAMERA_CODE);

    }


    private void videoPickDialog() {
        String[] option = {"Camera","Gallery"};
        AlertDialog.Builder  builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Video From").setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    if(!checkPremisstions()){
                        requestCamera();
                    }else{
                        videoPickCamera();
                    }

                }else if(which == 1){
                    videopickGallery();
                }
            }
        }).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case VIDEO_REQUEST_CODE:
                if(grantResults.length > 0) {
                    boolean CameraAccept = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean StorageAccept = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (CameraAccept && StorageAccept) {
                        videoPickCamera();
                    } else {
                        Toast.makeText(this, "Camera & Storage are required", Toast.LENGTH_SHORT).show();
                    }
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == VIDEO_GALLERY_CODE ){
                urlVideo = data.getData();
                setVideoView();
            }
            else if (requestCode == VIDEO_CAMERA_CODE){
                urlVideo = data.getData();
                setVideoView();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setVideoView() {
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);

        videoView.setMediaController(mediaController);

        videoView.setVideoURI(urlVideo);
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.pause();
            }
        });
    }
}