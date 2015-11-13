package com.toe.plain.chat;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;

import android.content.Context;
import android.util.Log;

public class Serialization {

	Context context;

	public Boolean serializeObject(String file_name, Context context,
			Object object) {

		FileOutputStream fos = null;
		try {
			
			fos = context.openFileOutput(file_name, Context.MODE_PRIVATE);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ObjectOutputStream os = null;
		try {
			os = new ObjectOutputStream(fos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			os.writeObject(object);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d("successfuly serialized","success");
		return true;
	}

	public Object retrieveSerialized(String file_name, Context context) {

		FileInputStream fis = null;
		try {
			fis = context.openFileInput(file_name);
		} catch (FileNotFoundException e) {
			Log.e("file not found", e.toString());
		}
		ObjectInputStream is = null;
		try {
			is = new ObjectInputStream(fis);
		} catch (StreamCorruptedException e) {
			Log.e("stream corrupted ", e.toString());
		} catch (IOException e) {
			Log.e("io 73 exception ", e.toString());
		}

		try {
			is.close();
		} catch (IOException e) {
			Log.e("io 79 exception ", e.toString());
		}
		try {
			fis.close();
		} catch (IOException e) {
			Log.e("io 84 exception ", e.toString());
		}
		Object obj = null;
		try {
			obj = is.readObject();
		} catch (OptionalDataException e) {
			Log.e("optional data exception", e.toString());
		} catch (ClassNotFoundException e) {
			Log.e("class not found exception ", e.toString());
		} catch (IOException e) {
			Log.e("io 94 exception ", e.toString());
		}
		return obj;

	}
}
