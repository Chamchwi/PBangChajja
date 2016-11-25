package com.dsm.spiralmoon.pbangchajja;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;

public class ImageLoadThread extends Thread {

    private String encodedImage = "";
    private ImageView imageView = null;

    public ImageLoadThread (String encodedImage, ImageView imageView) {
        this.encodedImage = encodedImage;
        this.imageView = imageView;
    }
    public void run() {
        try {
            //Decoding
            byte[] bytePlainOrg = Base64.decode(encodedImage, 0);

            ByteArrayInputStream inStream = new ByteArrayInputStream(bytePlainOrg);
            Bitmap bitmap2 = BitmapFactory.decodeStream(inStream);

            imageView.setImageBitmap(bitmap2);
        }
        catch(Exception e) {

        }
    }
}
