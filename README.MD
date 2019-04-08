封装的一个图片裁剪Dialog，方便调用  

##调用方法：  
~~~

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

~~~
