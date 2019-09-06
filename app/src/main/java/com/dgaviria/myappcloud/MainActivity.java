package com.dgaviria.myappcloud;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ImageView imgCaptura, imgdescarga;
    private Button btnCaptura, btnguardar, btndescarga;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap imgbitmap= (Bitmap)data.getExtras().get("data");
        imgCaptura.setImageBitmap(imgbitmap);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Map config = new HashMap<>();
        config.put("cloud_name","deiber");
        config.put("api_key", "784731591576447");
        config.put("api_secret","4EorvxUMUQZOdi_QGB1LLRW6oCk");
        final Cloudinary mycloud = new Cloudinary(config);

        referenciar();

        btnCaptura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i,1,null);
            }
        });

        btnguardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = ((BitmapDrawable)imgCaptura.getDrawable()).getBitmap();
                SubirImagen task = new SubirImagen();
                task.execute(bitmap,mycloud);
            }
        });

        btndescarga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Picasso.with(MainActivity.this).load(mycloud.url().generate("foto1.png")).rotate(90f).into(imgdescarga);
            }
        });

    }

    private void referenciar() {
        imgCaptura=findViewById(R.id.idimg);
        btnCaptura=findViewById(R.id.idbtncapturar);
        btnguardar=findViewById(R.id.idbtnguardar);
        imgdescarga=findViewById(R.id.idimgdescarga);
        btndescarga=findViewById(R.id.idbtndescargar);
    }


}

    class SubirImagen extends AsyncTask{

         @Override
        protected Object doInBackground(Object[] params) {
        Bitmap bitmap = (Bitmap)params[0];
        Cloudinary cloudinary=(Cloudinary)params[1];
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,80,baos);
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            try{
            Map uploadResult = cloudinary.uploader().upload(bais, ObjectUtils.asMap("public_id","foto1"));
            Log.d("url","url is"+ uploadResult.get("url"));
            }catch (IOException e){
            Log.d("CloudinaryActivity",e.getMessage());
            }
        return null;
        }
    }
