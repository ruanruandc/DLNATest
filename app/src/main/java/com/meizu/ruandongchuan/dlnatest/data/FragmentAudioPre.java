package com.meizu.ruandongchuan.dlnatest.data;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class FragmentAudioPre {
	public static List<String> getAudioPathList(Context context) {
		List<String> audios = new ArrayList<String>();
		String[] str = new String[] { MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.DATA
		};
		Cursor cursor = context.getContentResolver()
				.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, str,
						null,null, null);
		if(cursor == null) 
			return audios;
		if (cursor.moveToFirst()) {
			do {
				String audio = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

				/*try {
					audio = URLEncoder.encode(audio, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}*/
				audios.add(audio);
				Log.i("audio", audio);
			} while (cursor.moveToNext());
		}
		cursor.close();
		Log.i("audio.size",audios.size()+"");
		return audios;
	}
	
	public static List<Audio> getAudios(Context context) {
		List<Audio> audios = new ArrayList<Audio>();
		String[] str = new String[] {
				MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.ARTIST,
				MediaStore.Audio.Media.DURATION,
				MediaStore.Audio.Media.SIZE,
				MediaStore.Audio.Media.ALBUM,
				MediaStore.Audio.Media.TITLE,
				MediaStore.Audio.Media.ALBUM_ID,
				MediaStore.Audio.Media.DATA
		};
		//默认大于10秒的可以看作是歌
		Cursor cursor = context.getContentResolver()
				.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, str,
						null,null, null);
		if(cursor == null) 
			return audios;
		if (cursor.moveToFirst()) {
			do {
				Audio audio = new Audio();
                audio.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
                audio.setArtist(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
                audio.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
                audio.setSize(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)));
                audio.setAlbum(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)));
                audio.setTilte(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
                audio.setAlbumId(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)));
				audio.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
				audios.add(audio);
			} while (cursor.moveToNext());
		}
		cursor.close();
		
		return audios;
	}
}
