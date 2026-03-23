package com.example.testbackend.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ImageUtils {
    
    public static byte[] getImageBytes(Context context, Uri imageUri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            
            // Redimensionar para máximo 800x800 (economiza banda)
            Bitmap resized = resizeBitmap(bitmap, 800, 800);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // Comprimir JPEG com 80% de qualidade
            resized.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        
        if (width <= maxWidth && height <= maxHeight) {
            return bitmap;
        }
        
        float ratio = Math.min(
            (float) maxWidth / width,
            (float) maxHeight / height
        );
        
        int newWidth = (int) (width * ratio);
        int newHeight = (int) (height * ratio);
        
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }
}