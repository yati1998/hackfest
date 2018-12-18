package com.example.hp.img_pdf;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {
    Intent intent;
    int REQUEST_CODE = 99;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView=(ImageView)findViewById(R.id.imageView);

    }

    public void openCamera(View v)
    {

        int preference = ScanConstants.OPEN_CAMERA;
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
        startActivityForResult(intent, REQUEST_CODE);


    }

    public void openGallery(View v)
    {

        int preference = ScanConstants.OPEN_MEDIA;
        intent = new Intent(this, ScanActivity.class);
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        /*if (requestCode == 99 && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                getContentResolver().delete(uri, null, null);
               // scannedImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                getContentResolver().delete(uri, null, null);
                imageView.setImageBitmap(bitmap);



                PdfDocument pdfDocument=new PdfDocument();
                PdfDocument.PageInfo pi=new PdfDocument.PageInfo.Builder(bitmap.getWidth(),bitmap.getHeight(),1).create();
                PdfDocument.Page page=pdfDocument.startPage(pi);
                Canvas canvas=page.getCanvas();
                Paint paint=new Paint();
                paint.setColor(Color.parseColor("#FFFFFF"));
                canvas.drawPaint(paint);
                bitmap=Bitmap.createScaledBitmap(bitmap,bitmap.getWidth(),bitmap.getHeight(),true);
                paint.setColor(Color.BLUE);
                canvas.drawBitmap(bitmap,0,0,null);
                pdfDocument.finishPage(page);

                //save
                File root=new File(Environment.getExternalStorageDirectory(),"PDF Folder 12");
                if(!root.exists()){
                    root.mkdir();
                }
                File file=new File(root,"picture.pdf");

                FileOutputStream fileOutputStream=new FileOutputStream(file);
                pdfDocument.writeTo(fileOutputStream);

                pdfDocument.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
