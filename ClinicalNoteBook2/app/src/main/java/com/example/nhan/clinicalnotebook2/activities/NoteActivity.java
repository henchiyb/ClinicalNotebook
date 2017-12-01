package com.example.nhan.clinicalnotebook2.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.nhan.clinicalnotebook2.CameraPreview;
import com.example.nhan.clinicalnotebook2.R;
import com.example.nhan.clinicalnotebook2.database.RealmHandler;
import com.example.nhan.clinicalnotebook2.events.EventOpenListImage;
import com.example.nhan.clinicalnotebook2.events.EventSendDataNote;
import com.example.nhan.clinicalnotebook2.models.CacheObject;
import com.example.nhan.clinicalnotebook2.models.ContentNoteObject;
import com.example.nhan.clinicalnotebook2.models.FolderObject;
import com.example.nhan.clinicalnotebook2.models.ImagePathObject;
import com.example.nhan.clinicalnotebook2.models.NoteObject;
import com.example.nhan.clinicalnotebook2.models.RecordPathObject;
import com.example.nhan.clinicalnotebook2.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NoteActivity extends AppCompatActivity {

    private static final int RESULT_CAPTURE_IMAGE = 1;
    private static final String IMAGE_PATH_PRE = Environment.getExternalStorageDirectory().getPath() +
            "/saved_images/";
    private static final String RECORD_PATH_PRE = Environment.getExternalStorageDirectory().getPath() +
            "/saved_records/";
    private static final String IMAGE_TYPE_ID = "imageID";
    private static final String RECORD_TYPE_ID = "recordID";

    public static ActivityType activityType;
    @BindView(R.id.edt_file_name)
    EditText edtFileName;
    @BindView(R.id.spinner_folder_test)
    Spinner spinnerFolder;
    @BindView(R.id.tv_folder_name)
    TextView tvFolderName;
    @BindView(R.id.edt_folder_name)
    EditText edtFolderName;
    @BindView(R.id.btn_capture)
    ImageView captureButton;
    @BindView(R.id.btn_save_content)
    ImageView btnSaveNote;
    @BindView(R.id.btn_add_content)
    ImageView btnAddContent;
    @BindView(R.id.edt_note_name)
    EditText edtNoteName;
    @BindView(R.id.iv_smaill_image)
    ImageView ivSmallImage;
    @BindView(R.id.edt_content)
    EditText edtContent;
    @BindView(R.id.tv_number_of_content)
    TextView tvNumberContent;
    @BindView(R.id.btn_pre_content)
    ImageView btnPreContent;
    @BindView(R.id.btn_next_content)
    ImageView btnNextContent;
    @BindView(R.id.btn_delete_content)
    ImageView btnDeleteContent;
    @BindView(R.id.btn_record)
    ImageView btnRecord;

    private FrameLayout preview;
    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;

    @OnClick(R.id.btn_capture)
    public void onClickBtnCapture(){
        mCamera.takePicture(null, null, mPicture);
        captureButton.setClickable(false);
        captureButton.setEnabled(false);
    }
    @OnClick(R.id.btn_record)
    public void onClickBtnRecord() {

        if (!isRecording){
            setUpMediaRecorder();
            startRecord();
        } else {
            stopAndSaveRecord();
        }

    }
    @OnClick(R.id.btn_capture_auto)
    public void onClickBtnCaptureAuto(){
        int i = Utils.getIntFromSharedPreferrences(this, IMAGE_TYPE_ID);
        File file = new File(IMAGE_PATH_PRE + "Image-" + i++ +".jpg");
        Uri outputFileUri = Uri.fromFile(file);
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, RESULT_CAPTURE_IMAGE);
    }

    @OnClick(R.id.iv_smaill_image)
    public  void onClickSmallImage(){
        EventBus.getDefault().postSticky(new EventOpenListImage(noteObject));
        startActivity(new Intent(NoteActivity.this, ListImageAndRecordActivity.class));
    }

    @OnClick(R.id.btn_add_content)
    public void onClickAdd(){
        if (activityType == ActivityType.ADD_NOTE) {
            noteObject.getListContent().add(new ContentNoteObject("", ""));
        }
        else
            RealmHandler.getInstance().addContentToNote(noteObject,"", "");
        maxId++;
        presentID = maxId;
        tvNumberContent.setText(presentID + "/" + maxId);
        edtContent.setText("");
        edtNoteName.setText("");
    }
    @OnClick(R.id.btn_pre_content)
    public void onClickPreContent(){
        if(presentID != 1)
            presentID--;
        tvNumberContent.setText(presentID + "/" + maxId);
        edtContent.setText(noteObject.getListContent().get(presentID - 1).getContent());
        edtNoteName.setText(noteObject.getListContent().get(presentID - 1).getName());
    }

    @OnClick(R.id.btn_next_content)
    public void onClickNextContent(){
        if(presentID != maxId)
            presentID++;
        tvNumberContent.setText(presentID + "/" + maxId);
        edtContent.setText(noteObject.getListContent().get(presentID - 1).getContent());
        edtNoteName.setText(noteObject.getListContent().get(presentID - 1).getName());
    }
    private String folderName;
    @OnClick(R.id.btn_save_content)
    public void onClickSave(){
        if (!spinnerFolder.getSelectedItem().toString().equals(getString(R.string.new_folder))
                || !edtFolderName.getText().toString().equals("")) {
            if (!spinnerFolder.getSelectedItem().toString().equals(getString(R.string.new_folder)))
                folderName = spinnerFolder.getSelectedItem().toString();
            else if (spinnerFolder.getSelectedItem().toString().equals(getString(R.string.new_folder))
                    && !edtFolderName.getText().toString().equals("")) {
                FolderObject folderObject = new FolderObject();
                folderObject.setName(edtFolderName.getText().toString());
                RealmHandler.getInstance().addFolderToRealm(folderObject);
                folderName = edtFolderName.getText().toString();
            }

            new AlertDialog.Builder(this)
                    .setTitle("Save note")
                    .setMessage("Do you want to save this note?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            if(activityType == ActivityType.ADD_NOTE){
                                noteIsSaved = true;
                                noteObject.setFolderName(folderName);
                                noteObject.setName(edtFileName.getText().toString());
                                RealmHandler.getInstance().addNoteToFolder(noteObject, RealmHandler.getInstance().getFolderFromRealmByName(folderName));
                            } else {
                                RealmHandler.getInstance().addFolderNameToNote(noteObject, folderName);
                                RealmHandler.getInstance().editNameNote(noteObject, edtFileName.getText().toString());
                            }
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            Toast.makeText(NoteActivity.this, "No Folder", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btn_delete_content)
    public void onCLickDeleteContent(){
        new AlertDialog.Builder(this)
                .setTitle("Delete note content")
                .setMessage("Do you want to delete this note content?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        if (activityType == ActivityType.ADD_NOTE && presentID != 1 && maxId != 1){
                            presentID--;
                            maxId--;
                            tvNumberContent.setText(presentID + "/" + maxId);
                            edtContent.setText(noteObject.getListContent().get(presentID - 1).getContent());
                            edtNoteName.setText(noteObject.getListContent().get(presentID - 1).getName());
                            noteObject.getListContent().remove(presentID);
                        } else if (activityType == ActivityType.ADD_NOTE && presentID == 1 && maxId != 1){
                            maxId--;
                            tvNumberContent.setText(presentID + "/" + maxId);
                            edtContent.setText(noteObject.getListContent().get(presentID - 1).getContent());
                            edtNoteName.setText(noteObject.getListContent().get(presentID - 1).getName());
                            noteObject.getListContent().remove(presentID - 1);
                        }

                        if (activityType == ActivityType.EDIT_NOTE && presentID !=1 && maxId != 1) {
                            presentID--;
                            maxId--;
                            tvNumberContent.setText(presentID + "/" + maxId);
                            edtContent.setText(noteObject.getListContent().get(presentID - 1).getContent());
                            edtNoteName.setText(noteObject.getListContent().get(presentID - 1).getName());
                            RealmHandler.getInstance().removeContentInNote(noteObject, presentID);
                        }else if (activityType == ActivityType.EDIT_NOTE && presentID == 1 && maxId != 1){
                            maxId--;
                            tvNumberContent.setText(presentID + "/" + maxId);
                            edtContent.setText(noteObject.getListContent().get(presentID - 1).getContent());
                            edtNoteName.setText(noteObject.getListContent().get(presentID - 1).getName());
                            RealmHandler.getInstance().removeContentInNote(noteObject, presentID - 1);
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }


    private int presentID = 1;
    private int maxId = 1;
    private static Camera mCamera = null;
    private CameraPreview mPreview;

    private Bitmap mainbitmap;

    private NoteObject noteObject;
    private CacheObject cacheObject;
    private List<String> listFolderName;
    private Boolean noteIsSaved;

    @Subscribe(sticky = true)
    public void getDataEditNote(EventSendDataNote event){
        noteObject = event.getNoteObject();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_note);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        getSupportActionBar().hide();
        ivSmallImage.setVisibility(View.INVISIBLE);
        if (activityType == ActivityType.ADD_NOTE) {
            noteObject = new NoteObject();
            cacheObject = new CacheObject();
            noteIsSaved = false;
            noteObject.getListContent().add(new ContentNoteObject("", ""));
            Utils.saveIntToSharedPreferrences(0, this, IMAGE_TYPE_ID);
        } else {
            noteIsSaved = true;
            maxId = noteObject.getListContent().size();
            tvNumberContent.setText(presentID + "/" + maxId);
            edtFileName.setText(noteObject.getName());
            if (noteObject.getListImagePath().size() != 0){
                ivSmallImage.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(noteObject.getListImagePath().get(noteObject
                                .getListImagePath().size() - 1)
                                .getImagePath())
                        .into(ivSmallImage);
            }

        }
        resetInitActivity();


        edtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (activityType == ActivityType.ADD_NOTE)
                    noteObject.getListContent().get(presentID - 1).setContent(editable.toString());
                else
                    RealmHandler.getInstance().editContentNote(noteObject,
                            presentID - 1,
                            editable.toString());
            }
        });
        edtNoteName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (activityType == ActivityType.ADD_NOTE)
                    noteObject.getListContent().get(presentID - 1).setName(editable.toString());
                else
                    RealmHandler.getInstance().editContentName(noteObject,
                            presentID - 1,
                            editable.toString());
            }
        });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (cacheObject != null && !noteIsSaved){
            for (int i = 0; i < cacheObject.getListImagePathNotSave().size(); i++){
                File file = new File(cacheObject.getListImagePathNotSave().get(i).getImagePath());
                file.delete();
            }
        }
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            System.gc();
            BitmapWorkerTask task = new BitmapWorkerTask(data);
            task.execute(0);
        }
    };

    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference<byte[]> dataf;
        private int data = 0;
        BitmapWorkerTask(byte[] imgdata) {
            // Use a WeakReference to ensure the ImageView can be garbage
            // collected
            dataf = new WeakReference<byte[]>(imgdata);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {
            data = params[0];
            ResultActivity(dataf.get());
            return mainbitmap;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (mainbitmap != null) {
                mCamera.stopPreview();
                mCamera.startPreview();
                captureButton.setClickable(true);
                captureButton.setEnabled(true);
            }
            ivSmallImage.setImageBitmap(mainbitmap);
            ivSmallImage.setVisibility(View.VISIBLE);

        }
    }

    private void createNewFolderVisible(Boolean check){
        if (check){
            tvFolderName.setVisibility(View.VISIBLE);
            edtFolderName.setVisibility(View.VISIBLE);
        } else {
            tvFolderName.setVisibility(View.GONE);
            edtFolderName.setVisibility(View.GONE);
        }
    }

    private void resetInitActivity() {
        edtContent.setText(noteObject.getListContent().get(presentID - 1).getContent());
        edtNoteName.setText(noteObject.getListContent().get(presentID - 1).getName());
        listFolderName = new ArrayList<>();
        listFolderName.add(getString(R.string.new_folder));
        for (FolderObject folder : RealmHandler.getInstance().getAllFolderInRealm()){
            listFolderName.add(folder.getName());
        }

        tvNumberContent.setText(presentID + "/" + maxId);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item,listFolderName);
        spinnerFolder.setAdapter(adapter);
        spinnerFolder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinnerFolder.getSelectedItem().toString().equals(getString(R.string.new_folder))){
                    createNewFolderVisible(true);
                } else {
                    edtFolderName.setText("");
                    createNewFolderVisible(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (activityType == ActivityType.EDIT_NOTE){
            for (int i = 0; i < spinnerFolder.getAdapter().getCount(); i++){
                if(spinnerFolder.getItemAtPosition(i).equals(noteObject.getFolderName())){
                    spinnerFolder.setSelection(i);
                }
            }
        } else {
            spinnerFolder.setSelection(0);
        }

        preview = (FrameLayout) findViewById(R.id.camera_preview);
        try {
            mCamera = openFrontFacingCameraGingerbread();
        } catch (Exception e) {
            e.printStackTrace();

        }
        setCameraDisplayOrientation(this, Camera.CameraInfo.CAMERA_FACING_BACK, mCamera);
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        preview.addView(mPreview);
        setParaCamera();
    }

    private void setParaCamera(){
        Camera.Parameters params = mCamera.getParameters();
        List<Camera.Size> sizes = params.getSupportedPictureSizes();
        Camera.Size size = sizes.get(0);
        for(int i=0;i<sizes.size();i++)
        {
            if(sizes.get(i).width > size.width)
                size = sizes.get(i);
        }
        params.setPictureSize(size.width, size.height);
        params.setPictureFormat(ImageFormat.JPEG);
        params.setJpegQuality(100);
        params.setRotation(90);
        mCamera.setParameters(params);
    }

    public void ResultActivity(byte[] data) {
        mainbitmap = null;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mainbitmap = decodeSampledBitmapFromResource(data,
                displayMetrics.widthPixels,
                displayMetrics.heightPixels);
        Log.d("test",displayMetrics.widthPixels+" " + displayMetrics.heightPixels );

        saveBitmap(mainbitmap);
    }

    public static Bitmap decodeSampledBitmapFromResource(byte[] data,int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;

        BitmapFactory.decodeByteArray(data, 0, data.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    private Camera openFrontFacingCameraGingerbread() {
        int cameraCount = 0;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                try {
                    cam = Camera.open(camIdx);
                } catch (RuntimeException e) {
                }
            }
        }

        return cam;
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mCamera = openFrontFacingCameraGingerbread();
        } catch (Exception e) {
            e.printStackTrace();

        }
        setCameraDisplayOrientation(this, Camera.CameraInfo.CAMERA_FACING_BACK, mCamera);
        mPreview = new CameraPreview(this, mCamera);
        preview.addView(mPreview);
        setParaCamera();
        mCamera.startPreview();

    }

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    private void saveBitmap(Bitmap bitmap) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        int i = Utils.getIntFromSharedPreferrences(this, IMAGE_TYPE_ID);
        String fileName = "Image-" + i + ".jpg";
        final String imagePath = IMAGE_PATH_PRE + "Image-" + i +".jpg";
        if (activityType == ActivityType.ADD_NOTE)
            noteObject.getListImagePath().add(new ImagePathObject(imagePath));
        else {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    RealmHandler.getInstance().addImagePathNote(noteObject, imagePath);
                }
            });
        }
        Utils.saveIntToSharedPreferrences(i + 1, this, IMAGE_TYPE_ID);
        File file = new File(myDir, fileName);
        if (file.exists()) file.delete();

        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_CAPTURE_IMAGE && resultCode == RESULT_OK) {
            int i = Utils.getIntFromSharedPreferrences(this, IMAGE_TYPE_ID);
            String imagePath = IMAGE_PATH_PRE + "Image-" + i++ +".jpg";
            if (activityType == ActivityType.ADD_NOTE) {
                noteObject.getListImagePath().add(new ImagePathObject(imagePath));
                cacheObject.getListImagePathNotSave().add(new ImagePathObject(imagePath));
            }
            else
                RealmHandler.getInstance().addImagePathNote(noteObject, imagePath);
            Log.d("test", imagePath);
            Utils.saveIntToSharedPreferrences(i, this, "imageID");
            ivSmallImage.setImageBitmap(BitmapFactory.decodeFile(imagePath));
            ivSmallImage.setVisibility(View.VISIBLE);
        }
    }


    private void setUpMediaRecorder(){
        int i = Utils.getIntFromSharedPreferrences(this, RECORD_TYPE_ID) + 1;
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_records");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        mediaRecorder.setOutputFile(RECORD_PATH_PRE + "Record-" + i + ".3gpp");
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Utils.saveIntToSharedPreferrences(i, this, RECORD_TYPE_ID);
    }

    private void startRecord(){
        btnRecord.setBackgroundResource(R.drawable.ic_fiber_manual_record_red_24dp);
        isRecording = true;
        mediaRecorder.start();
    }

    private void stopAndSaveRecord(){
        btnRecord.setBackgroundResource(R.drawable.ic_fiber_manual_record_green_24dp);
        isRecording = false;
        final int i = Utils.getIntFromSharedPreferrences(this, RECORD_TYPE_ID);
        final String recordPath = RECORD_PATH_PRE + "Record-" + i +".3gpp";
        if (activityType == ActivityType.ADD_NOTE)
            noteObject.getListRecordPath().add(new RecordPathObject(recordPath));
        else {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    RealmHandler.getInstance().addRecordPathNote(noteObject, recordPath);
                }
            });
        }
        mediaRecorder.stop();
        mediaRecorder.reset();

    }
}
