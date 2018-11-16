/*
 * Created by Sujoy Datta. Copyright (c) 2018. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.finland.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dd.processbutton.iml.ActionProcessButton;
import com.google.android.material.snackbar.Snackbar;
import com.morningstar.finland.R;
import com.morningstar.finland.utility.DrawerUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Toolbar toolbar;
    private final String URL_POST_IMAGE = "http://192.168.0.101:5000/upload";
    private ActionProcessButton uploadImage;
    private Bitmap bitmap;
    private ImageView image;
    private TextView prediction, probability;
    private CardView cardView;
    private CardView output;
    private ConstraintLayout constraintLayout;
    private final int REQUEST_CODE_GALLERY = 1;
    private final int EXTERNAL_STORAGE_PERMISSION_CODE = 69;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.mainToolbar);
        toolbar.setTitle("FinLand");
        toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        DrawerUtils.getDrawer(this, toolbar);

        uploadImage = findViewById(R.id.uploadImage);
        uploadImage.setEnabled(true);
        image = findViewById(R.id.image);
        prediction = findViewById(R.id.prediction);
        probability = findViewById(R.id.probability);
        cardView = findViewById(R.id.cardView);
        output = findViewById(R.id.output);
        constraintLayout = findViewById(R.id.rootLayout);

        uploadImage.setMode(ActionProcessButton.Mode.ENDLESS);
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                    requestPermission();
                } else {
                    chooseImageFromGallery();
                }
            }
        });
    }

    private void chooseImageFromGallery() {
        uploadImage.setProgress(1);
        uploadImage.setText(getString(R.string.please_wait));
        uploadImage.setEnabled(false);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), REQUEST_CODE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                image.setImageBitmap(bitmap);
                sendRequest();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendRequest() {
        RequestQueue queue = Volley.newRequestQueue(this);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_IMAGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String probab = jsonResponse.getString("probability");
                            String landType = jsonResponse.getString("land-type");

                            if (probab.compareTo("null") == 0) {
                                showSnackBar();
                                uploadImage.setProgress(-1);
                                uploadImage.setText("Try Again");
                            } else {
                                prediction.setText(landType);
                                probability.setText(probab);
                                uploadImage.setText("Upload New Image");
                                output.setVisibility(View.VISIBLE);
                                uploadImage.setProgress(100);
                            }

                            uploadImage.setEnabled(true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        output.setVisibility(View.INVISIBLE);
                        uploadImage.setProgress(-1);
                        uploadImage.setText("Try Again");
                        uploadImage.setEnabled(true);
                        showSnackBar();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("image", imageString);
                return params;
            }
        };

        queue.add(stringRequest);
    }

    public void showSnackBar() {
        Snackbar snackbar = Snackbar
                .make(constraintLayout, "Failed to fetch prediction", Snackbar.LENGTH_LONG)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        uploadImage.setProgress(1);
                        sendRequest();
                    }
                })
                .setActionTextColor(Color.WHITE);
        snackbar.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission Required")
                    .setMessage("External Storage Permission is needed to get images from your gallery")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CODE);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXTERNAL_STORAGE_PERMISSION_CODE) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "This feature requires external storage permission", Toast.LENGTH_SHORT).show();
            } else
                chooseImageFromGallery();
        }
    }
}
