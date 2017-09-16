package com.cx.filesave;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	private Button button1;
	private Button button2;
	private Button button3;
	private Button button4;
	private String BASE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		button1 = (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);
		button3 = (Button) findViewById(R.id.button3);
		button4 = (Button) findViewById(R.id.button4);
		
		BASE = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
		File path = new File(BASE + "/cx");
		if (!path.exists()) {// 目录存在返回false 
			path.mkdirs();// 创建一个目录 
		} 
		
		button1.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				fileSave();
			}
		});
		
		button2.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				assetsDataToSD(BASE + "/cx/cx.zip");
			}
		});
		
		button3.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				upzipFile(BASE + "/cx/cx.zip", BASE + "/cx/");
			}
		});
		
		button4.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				delFolder(BASE + "/cx/cx.zip");
			}
		});
	}
	
	/**
	 * 将raw中文件放到SD卡中
	 */
	private void fileSave() {
		// TODO Auto-generated method stub
		File f = new File(BASE + "/cx/cx.txt");// 创建文件
		
		if (!f.exists()) {
			//将raw中文件复制到内存卡中
			try{
	            File dir = new File(BASE);
	            if (!dir.exists())
	                dir.mkdir();
	            if (!f.exists()){
	                InputStream is = getResources().openRawResource(R.raw.cx);
	                FileOutputStream fos = new FileOutputStream(f);
	                byte[] buffer = new byte[8192];
	                int count = 0;
	                while ((count = is.read(buffer)) > 0){
	                    fos.write(buffer, 0, count);
	                }
	                fos.close();
	                is.close();
	            }
	        }catch (Exception e){ 
	        	e.printStackTrace(); 
	        }
		}
	}
	
	/**
	 * 将assets中文件复制到SD卡中
	 */
	private void assetsDataToSD(String fileName){  
        InputStream myInput;  
        try {
        	OutputStream myOutput = new FileOutputStream(fileName);  
			myInput = this.getAssets().open("cx.zip");
			 byte[] buffer = new byte[1024];  
		        int length = myInput.read(buffer);
		        while(length > 0){
		            myOutput.write(buffer, 0, length); 
		            length = myInput.read(buffer);
		        }
		        
		        myOutput.flush();  
		        myInput.close();  
		        myOutput.close();   
		        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
    }
	
	/**
	 * 解压文件（压缩包中不能出现中文）
	 * @param zipFileName
	 * @param targetBaseDirName
	 */
	@SuppressWarnings("rawtypes")
	public static void upzipFile(String zipFileName, String targetBaseDirName){
		if (!targetBaseDirName.endsWith(File.separator)){
			targetBaseDirName += File.separator;
		}
        try {
        	//根据ZIP文件创建ZipFile对象
        	ZipFile zipFile = new ZipFile(zipFileName);
            ZipEntry entry = null;
            String entryName = null;
            String targetFileName = null;
            byte[] buffer = new byte[4096];
            int bytes_read; 
            //获取ZIP文件里所有的entry
            Enumeration entrys = zipFile.entries();
            //遍历所有entry
            while (entrys.hasMoreElements()) {
            	entry = (ZipEntry)entrys.nextElement();
            	//获得entry的名字
            	entryName =  entry.getName();
            	targetFileName = targetBaseDirName + entryName;
            	
            	if (entry.isDirectory()){
            		//  如果entry是一个目录，则创建目录
            		new File(targetFileName).mkdirs();
            		continue;
            	} else {
            		//	如果entry是一个文件，则创建父目录
            		new File(targetFileName).getParentFile().mkdirs();
            	}
            	//否则创建文件
            	File targetFile = new File(targetFileName);
            	//打开文件输出流
            	FileOutputStream os = new FileOutputStream(targetFile);
            	//从ZipFile对象中打开entry的输入流
            	InputStream  is = zipFile.getInputStream(entry);
            	while ((bytes_read = is.read(buffer)) != -1){
            		os.write(buffer, 0, bytes_read);
            	}
            	//关闭流
            	os.close( );
            	is.close( );
            }
        } catch (IOException err) {
            System.err.println("解压缩文件失败: " + err);
        }
	}
	
	/**
	 * 删除文件
	 * @param folderPath
	 */
	public void delFolder(String folderPath) {
        try {
            delAllFile(folderPath);
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	public void delAllFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
        	return;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
                if (path.endsWith(File.separator)) {
                    temp = new File(path + tempList[i]);
                }else {
                    temp = new File(path + File.separator + tempList[i]);
                }
                if (temp.isFile()) {
                    temp.delete();
                }
                if (temp.isDirectory()) {
                    delAllFile(path+"/"+ tempList[i]);
                    delFolder(path+"/"+ tempList[i]);
                }
        }
	}

}
