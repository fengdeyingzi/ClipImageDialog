package com.library.admin.mosimage;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;
import com.library.admin.mosimage.view.ClipImageLayout;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * http://blog.csdn.net/lmj623565791/article/details/39761281
 *
 * @author zhy
 */
public class MainActivity extends Activity {
    private ClipImageLayout mClipImageLayout;

    private PopupWindow mImageMenuWnd = null;
    private File mCaptureFile = null;
    private static final int REQUEST_CAPTURE_IMAGE = 0;
    private View contview;
    private AlertDialog dialog;
    private ImageView imageView;
	ImageView img_clip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mClipImageLayout = (ClipImageLayout) findViewById(R.id.id_clipImageLayout);
        contview = findViewById(R.id.contive);
        
		img_clip = (ImageView) findViewById(R.id.img_clip);
		
		initImageDialog();
		//assets解压到files目录
		File file_jpg = new File(getFilesDir(),"a.jpg");
		try
		{
			InputStream input = getAssets().open("a.jpg");
			byte[] data = new byte[input.available()];
			input.read(data);
			FileOutputStream out = new FileOutputStream(file_jpg);
			out.write(data);
			out.flush();
			out.close();
			input.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		//创建裁剪对话框，参数：Context对象，图片路径
		ClipPhotoDialog dd = new ClipPhotoDialog(this, file_jpg.getPath());
		//显示对话框
		dd.show();
		//设置图片裁剪返回文件
		dd.setOnClipListener(new ClipPhotoDialog.OnClipImageListener(){

				@Override
				public void onClipImage(File file)
				{
					// TODO: Implement this method
					img_clip.setImageURI(Uri.fromFile(file));
				}
				
			
		});
		
    }

    private void initImageDialog() {
        dialog = new AlertDialog.Builder(this).create();
        imageView = new ImageView(this);
        dialog.setTitle("裁剪图片结果");
        dialog.setView(imageView);
    }

    public void showHeadPopWindow(View view) {
        if (mImageMenuWnd == null) {
            initImagePopWin();
        }
        mImageMenuWnd.showAtLocation(contview, Gravity.BOTTOM
                | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private void initImagePopWin() {
        View imageMenu = LayoutInflater.from(this).inflate(
                R.layout.item_2item_pop_menu, null);
        imageMenu.findViewById(R.id.item_00).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mImageMenuWnd.dismiss();
                        mCaptureFile = new File(
                                getExternalFilesDir(Environment.DIRECTORY_DCIM),
                                "" + System.currentTimeMillis() + ".jpg");
                        Intent intent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(mCaptureFile));
                        try {
                            startActivityForResult(intent,
                                    REQUEST_CAPTURE_IMAGE);
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "无相机服务", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        imageMenu.findViewById(R.id.item_01).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mImageMenuWnd.dismiss();
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                                .addCategory(Intent.CATEGORY_OPENABLE).setType(
                                        "image/*");
                        startActivityForResult(intent, REQUEST_CAPTURE_IMAGE);
                    }
                });
        imageMenu.findViewById(R.id.cancel).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mImageMenuWnd.dismiss();
                    }
                });

        // initialize the image select menu window.
        mImageMenuWnd = new PopupWindow(imageMenu,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mImageMenuWnd.setFocusable(true);
        mImageMenuWnd.setOutsideTouchable(true);
        mImageMenuWnd.setBackgroundDrawable(new ColorDrawable(0x00000000));
        mImageMenuWnd.update();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if(Build.VERSION.SDK_INT<21){
        if (resultCode == Activity.RESULT_OK
                && requestCode == REQUEST_CAPTURE_IMAGE) {
            try {
                final Cursor cr = getContentResolver().query(data.getData(),
                        new String[]{MediaStore.Images.Media.DATA}, null,
                        null, null);
                if (cr.moveToFirst()) {
                    String localPath = cr.getString(cr
                            .getColumnIndex(MediaStore.Images.Media.DATA));
                    //加载图片
                    mClipImageLayout.getZoomImageView()
                            .setImageBitmap(BitmapFactory.decodeFile(localPath));
                }
                cr.close();
            } catch (Exception e) {
                if (mCaptureFile != null && mCaptureFile.exists()) {
                    mClipImageLayout.getZoomImageView()
                            .setImageBitmap(BitmapFactory.decodeFile(mCaptureFile.getAbsolutePath()));
                }
            }
        }
		}
		else{
			onActivityResultAboveL(requestCode, resultCode,data);
		}
        super.onActivityResult(requestCode, resultCode, data);
    }
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        if (requestCode != REQUEST_CAPTURE_IMAGE)
            return;
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                String dataString = intent.getDataString();
				toast(dataString);
                ClipData clipData = intent.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
						toast( getPathFromInputStreamUri(this,results[i],"a.jpg"));
						mClipImageLayout.getZoomImageView()
                            .setImageBitmap(BitmapFactory.decodeFile(getPathFromInputStreamUri(this,results[i],"a.jpg")));
						
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        //uploadMessageAboveL.onReceiveValue(results);
       // uploadMessageAboveL = null;  
	   String localPath = results[0].getPath();
	   //toast(localPath);
		mClipImageLayout.getZoomImageView()
			.setImageBitmap(BitmapFactory.decodeFile(localPath));
		}
		
		public void toast(String text){
			Toast.makeText(this,text,0).show();
		}
		
		
		
	public static File getFileFromUri(Uri uri, Context context) {
        if (uri == null) {
            return null;
        }
        switch (uri.getScheme()) {
            case "content":
                return getFileFromContentUri(uri, context);
            case "file":
                return new File(uri.getPath());
            default:
                return null;
        }
    }
	/**
     * Gets the corresponding path to a file from the given content:// URI
     *
     * @param contentUri The content:// URI to find the file path from
     * @param context    Context
     * @return the file path as a string
     */

    private static File getFileFromContentUri(Uri contentUri, Context context) {
        if (contentUri == null) {
            return null;
        }
        File file = null;
        String filePath;
        String fileName;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME};
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(contentUri, filePathColumn, null,
											  null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            filePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
            fileName = cursor.getString(cursor.getColumnIndex(filePathColumn[1]));
            cursor.close();
            if (!TextUtils.isEmpty(filePath)) {
                file = new File(filePath);
            }
            if (!file.exists() || file.length() <= 0 || TextUtils.isEmpty(filePath)) {
                filePath = getPathFromInputStreamUri(context, contentUri, fileName);
            }
            if (!TextUtils.isEmpty(filePath)) {
                file = new File(filePath);
            }
        }
        return file;
    }
	
	/**
     * 用流拷贝文件一份到自己APP目录下
     *
     * @param context
     * @param uri
     * @param fileName
     * @return
     */
    public static String getPathFromInputStreamUri(Context context, Uri uri, String fileName) {
        InputStream inputStream = null;
        String filePath = null;

        if (uri.getAuthority() != null) {
            try {
                inputStream = context.getContentResolver().openInputStream(uri);
                File file = createTemporalFileFrom(context, inputStream, fileName);
                filePath = file.getPath();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return filePath;
    }

    private static File createTemporalFileFrom(Context context, InputStream inputStream, String fileName)
	throws IOException {
        File targetFile = null;

        if (inputStream != null) {
            int read;
            byte[] buffer = new byte[8 * 1024];
            //自己定义拷贝文件路径
            targetFile = new File(context.getCacheDir(), fileName);
            if (targetFile.exists()) {
                targetFile.delete();
            }
            OutputStream outputStream = new FileOutputStream(targetFile);

            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();

            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return targetFile;
    }
	

    public void chip(View view) {
        Bitmap bitmap = mClipImageLayout.clip();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] datas = baos.toByteArray();

        imageView.setImageBitmap(BitmapFactory.decodeByteArray(datas, 0, datas.length));
        dialog.show();
    }
}
