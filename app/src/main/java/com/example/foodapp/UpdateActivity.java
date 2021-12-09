package com.example.foodapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.Calendar;

public class UpdateActivity extends AppCompatActivity {


    ImageView recepieImage;
    Uri uri;
    EditText txt_name,txt_description,txt_price;
    String imageUrl;
    String  key,oldImageUrl;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    String recipename,recipeDescription,recipePrice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        recepieImage = (ImageView)findViewById(R.id.iv_foodImage);
        txt_name = (EditText)findViewById(R.id.text_recipe_name);
        txt_description = (EditText)findViewById(R.id.text_Description);
        txt_price = (EditText)findViewById(R.id.text_Price);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null)
        {
            Glide.with(UpdateActivity.this)
                    .load(bundle.getString("oldimageurl"))
                    .into(recepieImage);
            txt_name.setText(bundle.getString("recipeNamekey"));
            txt_description.setText(bundle.getString("descriptionkey"));
            txt_price.setText(bundle.getString("pricekey"));
            key = bundle.getString("key");
            oldImageUrl = bundle.getString("oldimageurl");

        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Recepie").child(key);

    }

    public void btnSelectImage(View view) {
        Intent photoPicker = new Intent(Intent.ACTION_PICK);
        photoPicker.setType("image/*");
        startActivityForResult(photoPicker,1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {
            uri = data.getData();
            recepieImage.setImageURI(uri);

        }
        else Toast.makeText(this, "You haven't Picked any Image", Toast.LENGTH_SHORT).show();
    }



    public void btnUpdateRecepie(View view) {
         recipename = txt_name.getText().toString().trim();
          recipeDescription = txt_description.getText().toString().trim();
          recipePrice = (txt_price.getText().toString());

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Recipe Updating.....");
        progressDialog.show();
        storageReference = FirebaseStorage.getInstance()
                .getReference().child("RecipeImage").child(uri.getLastPathSegment());

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlImage = uriTask.getResult();
                imageUrl = urlImage.toString();
                uploadRecepie();
                progressDialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
            }
        });
    }
    public void uploadRecepie()
    {



        FoodData foodData = new FoodData(
                recipename,
                recipeDescription,
                recipePrice,
                imageUrl

        );

        databaseReference.setValue(foodData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                StorageReference storageReference1 = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageUrl);
                storageReference1.delete();
                Toast.makeText(UpdateActivity.this, "Recipe Updated", Toast.LENGTH_SHORT).show();

            }
        });
            }




}

