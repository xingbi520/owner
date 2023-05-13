package com.shendun.renter.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import com.shendun.renter.config.PathConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import timber.log.Timber;

public class ImageUtils {
    public static final long MAX_IMAGE_SIZE = 50;

    public static long getFileSizeByKB(String path) {
        File file = new File(path);
        if (file.exists()) {
            return file.length() / 1024;
        }
        return 0;
    }

    public static Size getSuitableSize(String path) {
        int pictureDegree = readPictureDegree(path);
        Size size = new Size();
        int max = 800;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int width = options.outWidth;
        int height = options.outHeight;
        int larger = width >= height ? width : height;
        if (larger > max) {
            if (pictureDegree == 90 || pictureDegree == 270) {
                size.setHeight(width * max / larger);
                size.setWidth(height * max / larger);
            } else {
                size.setWidth(width * max / larger);
                size.setHeight(height * max / larger);
            }
        } else {
            if (pictureDegree == 90 || pictureDegree == 270) {
                size.setHeight(width);
                size.setWidth(height);
            } else {
                size.setWidth(width);
                size.setHeight(height);
            }
        }
        return size;
    }

    private static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Uri getUriFromPath(Context context, String path) {
        Timber.tag("Uri").e(FileProvider.getUriForFile(context,
            context.getApplicationContext().getPackageName() + ".wocloud.provider", new File(path)).toString());

        return FileProvider.getUriForFile(context,
            context.getApplicationContext().getPackageName() + ".wocloud.provider", new File(path));
    }

    public static String getHomeIconImgPath(Context context) {
        String dirPath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                + File.separator + PathConfig.HOMEICON;
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dirPath + File.separator + "homeicon_img.jpg";
    }

    public static String getFeedbackImgPath(Context context) {
        String dirPath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            + File.separator + PathConfig.FEEDBACK;
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dirPath + File.separator + "feedback_img.jpg";
    }

    public static String getCloudImgPath(Context context) {
        String dirPath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                + File.separator + PathConfig.CLOUD;
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dirPath + File.separator + "cloud_img.jpg";
    }

    public static String getPortraitImgPath(Context context) {
        String dirPath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            + File.separator + PathConfig.PORTRAIT;
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dirPath + File.separator + "portrait_img_from_take_photo.jpg";
    }

    public static String getHomeIconCropImgPath(Context context) {
        String dirPath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                + File.separator + PathConfig.HOMEICON;
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dirPath + File.separator + "homeicon_crop_img.jpg";
    }

    public static String getPortraitCropImgPath(Context context) {
        String dirPath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            + File.separator + PathConfig.PORTRAIT;
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dirPath + File.separator + "portrait_crop_img.jpg";
    }

    public static String getFrontImgPath(Context context) {
        String dirPath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            + File.separator + PathConfig.IDAUTH;
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dirPath + File.separator + "front_img.jpg";
    }

    public static String getBackImgPath(Context context) {
        String dirPath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            + File.separator + PathConfig.IDAUTH;
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dirPath + File.separator + "back_img.jpg";
    }

    public static String getCompressedHomeIconImgPath(Context context) {
        String dirPath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                + File.separator + PathConfig.HOMEICON;
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dirPath + File.separator + "compressed_homeicon_img.jpg";
    }

    public static String getCompressedFeedbackImgPath(Context context) {
        String dirPath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            + File.separator + PathConfig.FEEDBACK;
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dirPath + File.separator + "compressed_feedback_img.jpg";
    }

    public static String getCompressedCloudImgPath(Context context) {
        String dirPath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                + File.separator + PathConfig.CLOUD;
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dirPath + File.separator + "compressed_cloud_img.jpg";
    }

    public static String getCompressedFrontImgPath(Context context) {
        String dirPath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            + File.separator + PathConfig.IDAUTH;
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dirPath + File.separator + "compressed_front_img.jpg";
    }

    public static String getCompressedBackImgPath(Context context) {
        String dirPath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            + File.separator + PathConfig.IDAUTH;
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dirPath + File.separator + "compressed_back_img.jpg";
    }

    //小米手机分身会出现uri.getAuthority()为0@com.android.providers.media.documents情况，
    //这种情况下按isDocumentUri处理
    public static boolean isMediaDoucmentWhenFS(Uri uri) {
        if (uri == null) {
            return false;
        }
        String authority = uri.getAuthority();
        if (TextUtils.isEmpty(authority)) {
            return false;
        }
        if (authority.contains("@com.android.providers.media.documents")) {
            return true;
        }
        return false;
    }

    /**
     * 将Uri转化为路径
     *
     * @param uri 要转化的Uri
     * @param selection 4.4之后需要解析Uri，因此需要该参数
     * @return 转化之后的路径
     */
    public static String getImagePath(Context context, Uri uri, String selection) {
        String path = null;
        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    //因为Android10操作权限的问题，即使拥有存储空间的读写权限，也无法保证可以正常的进行文件的读写操作
    //上传的File没有直接传Uri的操作，所以将最终选择的文件转存到getExternalFilesDir()
    public static String getFrontImgPathFromAlbum(Context context, Uri uri) {
        String path = null;
        try {
            File imgFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                + File.separator
                + PathConfig.IDAUTH);
            if (!imgFile.exists()) {
                imgFile.mkdirs();
            }

            File file = new File(imgFile + File.separator + "front_img_from_album.jpg");

            // 使用openInputStream(uri)方法获取字节输入流
            InputStream fileInputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int byteRead;
            while (-1 != (byteRead = fileInputStream.read(buffer))) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            // 文件可用新路径 file.getAbsolutePath()
            path = file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return path;
    }

    public static String getBackImgPathFromAlbum(Context context, Uri uri) {
        String path = null;
        try {
            File imgFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                + File.separator
                + PathConfig.IDAUTH);
            if (!imgFile.exists()) {
                imgFile.mkdirs();
            }

            File file = new File(imgFile + File.separator + "back_img_from_album.jpg");
            // 使用openInputStream(uri)方法获取字节输入流
            InputStream fileInputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int byteRead;
            while (-1 != (byteRead = fileInputStream.read(buffer))) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            // 文件可用新路径 file.getAbsolutePath()
            path = file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return path;
    }

    public static String getHomeIconImgPathFromAlbum(Context context, Uri uri) {
        String path = null;
        try {
            File imgFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    + File.separator
                    + PathConfig.HOMEICON);
            if (!imgFile.exists()) {
                imgFile.mkdirs();
            }

            File file = new File(imgFile + File.separator + "homeicon_img_from_album.jpg");

            // 使用openInputStream(uri)方法获取字节输入流
            InputStream fileInputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int byteRead;
            while (-1 != (byteRead = fileInputStream.read(buffer))) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            // 文件可用新路径 file.getAbsolutePath()
            path = file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return path;
    }

    public static String getFeedbackImgPathFromAlbum(Context context, Uri uri) {
        String path = null;
        try {
            File imgFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                + File.separator
                + PathConfig.FEEDBACK);
            if (!imgFile.exists()) {
                imgFile.mkdirs();
            }

            File file = new File(imgFile + File.separator + "feedback_img_from_album.jpg");

            // 使用openInputStream(uri)方法获取字节输入流
            InputStream fileInputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int byteRead;
            while (-1 != (byteRead = fileInputStream.read(buffer))) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            // 文件可用新路径 file.getAbsolutePath()
            path = file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return path;
    }

    public static String getPortraitPathFromAlbum(Context context, Uri uri) {
        String path = null;
        try {
            File imgFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                + File.separator
                + PathConfig.PORTRAIT);
            if (!imgFile.exists()) {
                imgFile.mkdirs();
            }

            File file = new File(imgFile + File.separator + "portrait_img_from_album.jpg");
            // 使用openInputStream(uri)方法获取字节输入流
            InputStream fileInputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int byteRead;
            while (-1 != (byteRead = fileInputStream.read(buffer))) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            // 文件可用新路径 file.getAbsolutePath()
            path = file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return path;
    }

    /**
     * 删除图片文件夹
     */
    public static boolean deleteImageDir(Context context) {
        try {
            String dirPath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                + File.separator + PathConfig.IDAUTH;
            return delete(dirPath);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除文件或者文件夹
     */
    public static boolean delete(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            return false;
        } else {
            if (file.isFile()) {
                return deleteFile(fileName);
            } else {
                return deleteDirectory(fileName);
            }
        }
    }

    /**
     * 删除单个文件
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 删除文件夹以及子文件
     */
    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator)) dir = dir + File.separator;
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                // 删除子文件
                if (files[i].isFile()) {
                    flag = deleteFile(files[i].getAbsolutePath());
                    if (!flag) break;
                }
                // 删除子目录
                else if (files[i].isDirectory()) {
                    flag = deleteDirectory(files[i].getAbsolutePath());
                    if (!flag) break;
                }
            }
        }
        if (!flag) {
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    public static String getImageRealPathFromUri(Context context, Uri uri) {
        if (null == uri) {
            return null;
        }
        String imagePath = null;
        if (DocumentsContract.isDocumentUri(context, uri) || isMediaDoucmentWhenFS(uri)) {
            //如果是document类型的Uri，则提供document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())
                || isMediaDoucmentWhenFS(uri)) {
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath =
                    getImagePath(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri =
                    ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(docId));
                imagePath = getImagePath(context, contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的uri，则进行普通处理
            imagePath = getImagePath(context, uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是file类型的uri，则直接获取路径
            imagePath = uri.getPath();
        }
        return imagePath;
    }

    /**
     * Return whether it is a image according to the file name.
     *
     * @param file The file.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isImage(final File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        return isImage(file.getPath());
    }

    /**
     * Return whether it is a image according to the file name.
     *
     * @param filePath The path of file.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isImage(final String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
            return options.outWidth != -1 && options.outHeight != -1;
        } catch (Exception e) {
            return false;
        }
    }
}
