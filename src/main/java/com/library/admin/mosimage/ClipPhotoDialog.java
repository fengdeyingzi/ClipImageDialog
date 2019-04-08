package com.library.admin.mosimage;


//裁剪dialog

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import com.library.admin.mosimage.view.ClipImageLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class ClipPhotoDialog extends Dialog
{
	
	ClipImageLayout clipLayout;
	
	Context context;
	OnClipImageListener listener;
	ImageView img_back,img_ok;
	
	public ClipPhotoDialog(Context context,String filename){
		super(context);
		this.context = context;
		LayoutInflater factory =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		//LayoutInflater factory = LayoutInflater.from(context);
		View Layout =  factory.inflate(R.layout.dlg_clipimage, null);
		 clipLayout = (ClipImageLayout) Layout.findViewById(R.id.dlg_clipImageLayout);
		img_ok = (ImageView) Layout.findViewById(R.id.toolbar_ok);
		img_back = (ImageView) Layout.findViewById(R.id.toolbar_back);
		img_ok.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					// TODO: Implement this method
					if(listener!=null){
						listener.onClipImage(getClipFile());
					}
					cancel();
				}
				
			 
		 });
		img_back.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					// TODO: Implement this method
					cancel();
				}
				
			 
		 });
		 ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
		setContentView(Layout,params);
		WindowManager.LayoutParams wl = getWindow().getAttributes();
        wl.x = 0;
        //wl.y = context.getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		
		Window window = this.getWindow();
		Window window1 = getWindow();
		window.getDecorView().setPadding(0, 0, 0, 0);
		WindowManager.LayoutParams layoutParams = window.getAttributes();
		layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
		layoutParams.horizontalMargin = 0;
		window.setAttributes(layoutParams);
		//window.getDecorView().setMinimumWidth(getResources().getDisplayMetrics().widthPixels);
		window.getDecorView().setBackgroundColor(Color.BLACK);

			
		File file = new File(filename);
		Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
		clipLayout.getZoomImageView().setImageBitmap(bitmap);
	}
	
	public void setOnClipListener(OnClipImageListener listener){
		this.listener = listener;
	}
	
	
	public void setBitmap(Bitmap bitmap){
		//BitmapFactory.decodeFile(
		clipLayout.getZoomImageView().setImageBitmap(bitmap);
		
	}
	
	public void setImageFile(String file){
		Bitmap bitmap = BitmapFactory.decodeFile(file);
		setBitmap(bitmap);
	}
	
	
	public Bitmap clip(){
		return clipLayout.clip();
	}
	
	public File getClipFile(){
		Bitmap bitmap = clipLayout.clip();
		File file = new File(context.getCacheDir(),"cache.png");
		try
		{
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		return file;
	}
	
	
	public interface OnClipImageListener{
		public void onClipImage(File file);
			
		
	}
	
}
