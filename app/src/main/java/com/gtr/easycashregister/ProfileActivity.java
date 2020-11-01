package com.gtr.easycashregister;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gtr.easycashregister.API.Api;

import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

public class ProfileActivity extends AppCompatActivity {

    private static final int CAMERA_PERM_CODE = 101 ;
    public static final int CAMERA_REQUEST_CODE = 102;
    String phoneNumber;
    TextView mobileNumber;
    EditText itemName,itemQuantity,itemPrice;
    Button saveButton;
    String productImage,imageTime;
    File f;
    ProgressDialog progressDialog;

    /*private String productName,productQuantity,productPrice;*/
    private String productName;
    private Double productPrice;
    private Float productQuantity;

    //main_task
    private static final int MY_PERMISSIONS_REQUEST_LOCATION =99 ;
    protected Interpreter tflite;
    private MappedByteBuffer tfliteModel;
    private TensorImage inputImageBuffer;
    private  int imageSizeX;
    private  int imageSizeY;
    private TensorBuffer outputProbabilityBuffer;
    private TensorProcessor probabilityProcessor;
    private static final float IMAGE_MEAN = 0.0f;
    private static final float IMAGE_STD = 1.0f;
    private static final float PROBABILITY_MEAN = 0.0f;
    private static final float PROBABILITY_STD = 255.0f;
    private Bitmap bitmap;
    private List<String> labels;
    ImageView imageView;
    Uri imageuri;
    Button buclassify;
    TextView classitext;
    private String currentPhotoPath;
    String storeObjectName;
    String mainNumber;

    private AdView mAdView;
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //AddMob
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-6949704799119881/2209175499");

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //InterstitialAdd
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-6949704799119881/7677124590");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        // get saved phone number
        SharedPreferences prefs =  getApplicationContext().getSharedPreferences("USER_PREF",
                Context.MODE_PRIVATE);
        phoneNumber = prefs.getString("phoneNumber", NULL);
        mainNumber = prefs.getString("mainNumber",null);

       /* mobileNumber = findViewById(R.id.mobileNumber);
        mobileNumber.setText(phoneNumber);*/



        imageView=(ImageView)findViewById(R.id.imageVIew);
        buclassify=(Button)findViewById(R.id.classify);
        classitext=(TextView)findViewById(R.id.classifytext);
        itemQuantity= findViewById(R.id.itemQuantity);
        itemPrice= findViewById(R.id.itemPrice);

        itemName= findViewById(R.id.itemName);
        saveButton= findViewById(R.id.saveButton);

        progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setMessage("Waiting");
        progressDialog.setCancelable(false);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                askCameraPermissions();

            }
        });

        try{
            tflite=new Interpreter(loadmodelfile(this));
        }catch (Exception e) {
            e.printStackTrace();
        }

        buclassify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int imageTensorIndex = 0;
                int[] imageShape = tflite.getInputTensor(imageTensorIndex).shape(); // {1, height, width, 3}
                imageSizeY = imageShape[1];
                imageSizeX = imageShape[2];
                DataType imageDataType = tflite.getInputTensor(imageTensorIndex).dataType();

                int probabilityTensorIndex = 0;
                int[] probabilityShape =
                        tflite.getOutputTensor(probabilityTensorIndex).shape(); // {1, NUM_CLASSES}
                DataType probabilityDataType = tflite.getOutputTensor(probabilityTensorIndex).dataType();

                inputImageBuffer = new TensorImage(imageDataType);
                outputProbabilityBuffer = TensorBuffer.createFixedSize(probabilityShape, probabilityDataType);
                probabilityProcessor = new TensorProcessor.Builder().add(getPostprocessNormalizeOp()).build();

                if (bitmap != null){
                    inputImageBuffer = loadImage(bitmap);

                    tflite.run(inputImageBuffer.getBuffer(),outputProbabilityBuffer.getBuffer().rewind());
                    showresult();
                }else{
                    Toast.makeText(getApplicationContext(),"Take a picture",Toast.LENGTH_SHORT).show();
                }

            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Toast.makeText(ProfileActivity.this, phoneNumber+""+storeObjectName, Toast.LENGTH_SHORT).show();*/
                progressDialog.show();
                userConfirm();

            }
        });

    }

    private void userConfirm() {

        productName = itemName.getText().toString().trim();
        productQuantity = Float.parseFloat(itemQuantity.getText().toString().trim());
        productPrice = Double.parseDouble(itemPrice.getText().toString().trim());
        if (productName.isEmpty()) {
            itemName.setError("পণ্যের নাম প্রয়োজন");
            itemName.requestFocus();
            return;
        }
        if (productQuantity==null) {
            itemQuantity.setError("পণ্যের পরিমান প্রয়োজন");
            itemQuantity.requestFocus();
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://101.2.165.189:96/api/EasyCashItem/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Api api = retrofit.create(Api.class);

        JsonObject jsonObjectFinal = new JsonObject();

        JSONObject jsonObjectName = new JSONObject();
        try {
            jsonObjectName.put("ItemName", productName);
            jsonObjectName.put("PhoneNumber", mainNumber);
            jsonObjectName.put("Quantity", productQuantity);
            jsonObjectName.put("Price", productPrice);
            jsonObjectName.put("Image", productImage);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonParser jsonParser = new JsonParser();
        jsonObjectFinal = (JsonObject) jsonParser.parse(jsonObjectName.toString());
        Call<String> call = api.createItem(jsonObjectFinal);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    String isSuccessful=response.body();
                    if (isSuccessful != null) {
                        if (isSuccessful.equals("true")) {
                            Toast.makeText(ProfileActivity.this, "আপলোড হয়েছে", Toast.LENGTH_LONG).show();
                            itemName.setText("");
                            itemQuantity.setText("");
                            itemPrice.setText("");
                            imageView.setImageResource(R.drawable.captureimage);
                        } else {
                            Toast.makeText(ProfileActivity.this, "এই ফোন নম্বরটি ইতিমধ্যে নিবন্ধভুক্ত হয়েছে", Toast.LENGTH_LONG).show();
                        }
                    }

                } else {
                    Log.d("", "onResponse: ");
                    Toast.makeText(ProfileActivity.this, "UserName Already Exist, Please try again later", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Failed", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
    }

    private void askCameraPermissions() {
        if (ContextCompat.checkSelfPermission(ProfileActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(ProfileActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(ProfileActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this,
                    Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new androidx.appcompat.app.AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("getString(R.string.profileActivityPermission)")
                        .setMessage("getString(R.string.profileActivityPremissionsecond)")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(ProfileActivity.this,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        MY_PERMISSIONS_REQUEST_LOCATION);


                                startActivity(new Intent(ProfileActivity
                                        .this, ProfileActivity.class));
                                ProfileActivity.this.overridePendingTransition(0, 0);
                            }
                        })
                        .create()
                        .show();


            } else {
                ActivityCompat.requestPermissions(ProfileActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }

        }else{
            dispatchTakePictureIntent();
        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode==RESULT_OK){
            Bitmap bOutput;
            bOutput= BitmapFactory.decodeFile(currentPhotoPath);

            float degrees = 90;//rotation degree
            Matrix matrix = new Matrix();
            matrix.setRotate(degrees);
            final float densityMultiplier = getResources().getDisplayMetrics().density;
            int h = (int) (250 * densityMultiplier);
            int w = (int) (h * bOutput.getWidth() / ((double) bOutput.getHeight()));

            bOutput = Bitmap.createScaledBitmap(bOutput, w, h, true);
            bitmap = Bitmap.createBitmap(bOutput,0,0,w,h, matrix, true);

            imageView.setImageBitmap(bitmap);
            imageView.setBackgroundResource(0);
            ImageUpload(bitmap);

            }

    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void dispatchTakePictureIntent() {

        String fileName="photo";
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile= null;
        try {
            imageFile = File.createTempFile(fileName,".jpg",storageDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentPhotoPath = imageFile.getAbsolutePath();

        Uri imageUri= FileProvider.getUriForFile(ProfileActivity.this,
                "com.gtr.easycashregister.fileprovider",imageFile);

        Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(intent,0);
    }


    private TensorImage loadImage(final Bitmap bitmap) {

        // Loads bitmap into a TensorImage.
        inputImageBuffer.load(bitmap);
        // Creates processor for the TensorImage.
        int cropSize = Math.min(bitmap.getWidth(), bitmap.getHeight());
        // TODO(b/143564309): Fuse ops inside ImageProcessor.
        ImageProcessor imageProcessor =
                new ImageProcessor.Builder()
                        .add(new ResizeWithCropOrPadOp(cropSize, cropSize))
                        .add(new ResizeOp(imageSizeX, imageSizeY, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                        .add(getPreprocessNormalizeOp())
                        .build();
        return imageProcessor.process(inputImageBuffer);
    }

    private MappedByteBuffer loadmodelfile(Activity activity) throws IOException {
        //Add tflite file

        /*AssetFileDescriptor fileDescriptor=activity.getAssets().openFd("newmodel.tflite");*/
        /*AssetFileDescriptor fileDescriptor=activity.getAssets().openFd("model.tflite");*/
         AssetFileDescriptor fileDescriptor=activity.getAssets().openFd("d.tflite");
        FileInputStream inputStream=new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel=inputStream.getChannel();
        long startoffset = fileDescriptor.getStartOffset();
        long declaredLength=fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startoffset,declaredLength);
    }

    private TensorOperator getPreprocessNormalizeOp() {
        return new NormalizeOp(IMAGE_MEAN, IMAGE_STD);
    }
    private TensorOperator getPostprocessNormalizeOp(){
        return new NormalizeOp(PROBABILITY_MEAN, PROBABILITY_STD);
    }

    private void showresult(){

        try{
            //Add txt file

            /*labels = FileUtil.loadLabels(this,"newdict.txt");*/
            /*labels = FileUtil.loadLabels(this,"labels.txt");*/
            labels = FileUtil.loadLabels(this,"d.txt");
        }catch (Exception e){
            e.printStackTrace();
        }
        Map<String, Float> labeledProbability =
                new TensorLabel(labels, probabilityProcessor.process(outputProbabilityBuffer))
                        .getMapWithFloatValue();
        float maxValueInMap =(Collections.max(labeledProbability.values()));

        for (Map.Entry<String, Float> entry : labeledProbability.entrySet()) {
            if (entry.getValue()==maxValueInMap) {
              /*  classitext.setText(entry.getKey());*/

                //variable set
                storeObjectName= entry.getKey();
                itemName.setText(storeObjectName);

                Toast.makeText(this,"Name is: "+storeObjectName, Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void ImageUpload(Bitmap bitmap) {
        byte[] data = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        data = baos.toByteArray();
        productImage = Base64.encodeToString(data, Base64.DEFAULT);


        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");

        imageTime = dateformat.format(Calendar.getInstance().getTime());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true ;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id){

            case R.id.signOut:
                signOut();
                break;
            case R.id.myProfile:
                myProfile();
                break;
            case R.id.itemHistory:
                itemHistory();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void itemHistory() {
        /*Toast.makeText(this, "phone Number :"+mainNumber, Toast.LENGTH_SHORT).show();*/
        startActivity(new Intent(ProfileActivity.this, UserProfileActivity.class)
                .putExtra("mainNumber", mainNumber));
    }

    private void myProfile() {

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");

            startActivity(new Intent(ProfileActivity.this, HistoryActivity.class)
                    .putExtra("mainNumber", mainNumber));
        }


        /*startActivity(new Intent(ProfileActivity.this, HistoryActivity.class)
                .putExtra("mainNumber", mainNumber));*/

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                startActivity(new Intent(ProfileActivity.this, HistoryActivity.class)
                        .putExtra("mainNumber", mainNumber));
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });

    }


    private void signOut() {
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Clear the Activity's bundle of the subsidiary fragments' bundles.
        outState.clear();
    }
}
