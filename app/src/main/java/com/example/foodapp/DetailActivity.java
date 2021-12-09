package com.example.foodapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DetailActivity extends AppCompatActivity {

    TextView foodDescription;
    TextView foodName;
    TextView foodPrice;
    ImageView foodImage;
    String key="";
    String imageUrl="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        foodDescription = (TextView)findViewById(R.id.txtDescription);
        foodName = (TextView)findViewById(R.id.txtName);
        foodPrice = (TextView)findViewById(R.id.txtPrice);
        foodImage = (ImageView)findViewById(R.id.ivImage);

        Bundle mBundle = getIntent().getExtras();

        if(mBundle!=null)
        {
            foodDescription.setText(mBundle.getString("Description"));
            foodName.setText(mBundle.getString("Name"));
            foodPrice.setText(mBundle.getString("Price"));

            key = mBundle.getString("keyValue");
            imageUrl = mBundle.getString("Image1");
            //foodImage.setImageResource(mBundle.getInt("Image1"));

            Glide.with(this)
                    .load(mBundle.getString("Image1"))
                    .fitCenter()
                    .override(500,200)
                    .into(foodImage);

        }

    }

    public void btnDeleteRecipe(View view) {

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Recepie");
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                reference.child(key).removeValue();
                Toast.makeText(DetailActivity.this, "Recipe Deleted ", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();

            }
        });
    }

    public void btnUpdateRecipe(View view) {

        startActivity(new Intent(getApplicationContext(),UpdateActivity.class)
        .putExtra("recipeNamekey", foodName.getText().toString())
        .putExtra("descriptionkey",foodDescription.getText().toString())
        .putExtra("pricekey",foodPrice.getText().toString())
        .putExtra("oldimageurl",imageUrl)
        .putExtra("key",key)
        );
    }
}
