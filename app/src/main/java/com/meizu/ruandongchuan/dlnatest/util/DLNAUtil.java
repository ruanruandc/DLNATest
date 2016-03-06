package com.meizu.ruandongchuan.dlnatest.util;

import android.os.Environment;
import android.util.Log;

import com.meizu.ruandongchuan.dlnatest.data.Audio;
import com.meizu.ruandongchuan.dlnatest.data.Image;
import com.meizu.ruandongchuan.dlnatest.data.MediaItem;
import com.meizu.ruandongchuan.dlnatest.data.Video;

import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.std.av.renderer.MediaRenderer;
import org.cybergarage.upnp.std.av.server.MediaServer;
import org.cybergarage.upnp.std.av.server.UPnP;
import org.cybergarage.upnp.std.av.server.object.ContentNode;
import org.cybergarage.upnp.std.av.server.object.DIDLLiteNode;
import org.cybergarage.upnp.std.av.server.object.container.ContainerNode;
import org.cybergarage.upnp.std.av.server.object.item.ItemNode;
import org.cybergarage.upnp.std.av.server.object.item.ResourceNode;
import org.cybergarage.xml.Attribute;
import org.cybergarage.xml.AttributeList;
import org.cybergarage.xml.Node;
import org.cybergarage.xml.Parser;
import org.cybergarage.xml.ParserException;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.StringTokenizer;

public class DLNAUtil {

	/**
	 * Check if the device is a media render device
	 *
	 * @param device
	 * @return
	 */
	public static boolean isMediaRenderer(Device device) {
		if (device != null
				&& MediaRenderer.DEVICE_TYPE.equalsIgnoreCase(device.getDeviceType())) {
			return true;
		}

		return false;
	}

	public static boolean isMediaServer(Device device) {
		if (device != null
				&& MediaServer.DEVICE_TYPE.equalsIgnoreCase(device.getDeviceType())) {
			return true;
		}

		return false;
	}

	public static String getMimeType(String url){
		String mime = null;
		int dot = url.lastIndexOf( '.' );
		if (url.contains(".m3u8")){
			mime = "video/mp4";
			return mime;
		}
		if ( dot >= 0 )
			mime = (String)theMimeTypes.get( url.substring( dot + 1 ).toLowerCase());
		if ( mime == null )
			mime = "application/octet-stream";
		return mime;
	}

	public static String getMetaData(MediaItem item){
		DIDLLiteNode didlLite = new DIDLLiteNode();
		ItemNode itemNode = new ItemNode();

		String dir = "";
		String size = "";
		String title = "";
		if (item instanceof Audio){
			itemNode.setID(((Audio) item).getId());
			dir = ((Audio) item).getPath();
			size = String.valueOf(((Audio) item).getSize());
			title= ((Audio) item).getTilte();
			itemNode.setUPnPClass(UPnP.OBJECT_ITEM_AUDIOITEM);
		}else if (item instanceof Image){
			itemNode.setID(((Image) item).getId());
			dir = ((Image) item).getDirectory();
			size = String.valueOf(((Image) item).getSize());
			title = ((Image) item).getTitle();
			itemNode.setUPnPClass(UPnP.OBJECT_ITEM_IMAGEITEM_PHOTO);
		}else if (item instanceof Video){
			itemNode.setID(((Video) item).getId());
			dir = ((Video) item).getDirectory();
			size = String.valueOf(((Video) item).getSize());
			title = ((Video) item).getTitle();
			itemNode.setUPnPClass(UPnP.OBJECT_ITEM_VIDEOITEM_MOVIE);
		}
		itemNode.setTitle(title);
		String protocolinfo = "http-get:*:"+
				getMimeType(dir)+
				":*;DLNA.ORG_OP=11;DLNA.ORG_FLAGS=01700000000000000000000000000000";
		AttributeList attributeList = new AttributeList();
		attributeList.add(new Attribute(ResourceNode.SIZE,size));
		itemNode.setResource(getUrl(dir),protocolinfo,attributeList);
		didlLite.addContentNode(itemNode);
		return didlLite.toString();
	}

	public static ItemNode parseMetaData(String didl){
		Node node = null;
		didl = didl.replaceAll("&","&amp;");
		Parser parser = org.cybergarage.upnp.UPnP.getXMLParser();
		try {
			node = parser.parse(didl);
		} catch (ParserException e) {
			e.printStackTrace();
			return null;
		}
		if ( node != null) {
			Log.i("DLNAUtil","node != null");
			node =  node.getNode(ItemNode.NAME);
			if (ItemNode.isItemNode(node)){
				ContentNode contentNode = new ItemNode();
				contentNode.set(node);
				if (contentNode.isItemNode()) {
					Log.i("DLNAUtil", "node isItemNode");
					return (ItemNode) contentNode;
				}
			}
		}
		return null;
	}

	public static Hashtable<String, String> theMimeTypes = new Hashtable<String, String>();
	static {
		StringTokenizer st = new StringTokenizer("css		text/css "
				+ "htm		text/html " + "html		text/html " + "xml		text/xml "
				+ "txt		text/plain " + "asc		text/plain " + "gif		image/gif "
				+ "jpg		image/jpeg " + "jpeg		image/jpeg " + "png		image/png "
				+ "mp3		audio/x-mpeg " + "m3u		audio/mpeg-url "
				+ "mp4		video/mp4 " + "ogv		video/ogg " + "flv		video/x-flv "
				+ "mov		video/quicktime "
				+ "avi		video/mpeg "
				+ "mpg		video/mpeg "
				+ "3gp      video/3gpp "
				+ "swf		application/x-shockwave-flash "
				+ "js			application/javascript " + "pdf		application/pdf "
				+ "doc		application/msword " + "ogg		application/x-ogg "
				+ "zip		application/octet-stream "
				+ "exe		application/octet-stream "
				+ "class		application/octet-stream ");
		while (st.hasMoreTokens())
			theMimeTypes.put(st.nextToken(), st.nextToken());
	}

	public static String getUrl(String dir){
		String url = null;
		if(dir.toLowerCase().startsWith("http://") || dir.toLowerCase().startsWith("https://"))
			return dir;


		try {
			url = "http://"+ NetUtil.getIp()+":"+ MediaServer.DEFAULT_HTTP_PORT + "/local"+URLEncoder.encode(dir, "UTF-8")+"";
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return url;
	}

	public static boolean isContainer(ContentNode content) {
		if(null == content.getUPnPClass()) {
			return false;
		}
		return content.getUPnPClass().startsWith(ContainerNode.OBJECT_CONTAINER);
	}

	public static boolean isImage(ContentNode content) {
		if(null == content.getUPnPClass()) {
			return false;
		}
		return content.getUPnPClass().startsWith(UPnP.OBJECT_ITEM_IMAGEITEM);
	}

	public static boolean isVideo(ContentNode content) {
		if(null == content.getUPnPClass()) {
			return false;
		}
		return content.getUPnPClass().startsWith(UPnP.OBJECT_ITEM_VIDEOITEM);
	}

	public static boolean isAudio(ContentNode content) {
		if(null == content.getUPnPClass()) {
			return false;
		}
		return content.getUPnPClass().startsWith(UPnP.OBJECT_ITEM_AUDIOITEM);
	}

	public static String getTitle(String url)
	{
		int idx = url.lastIndexOf(".");
		int slant = url.lastIndexOf("/");
		if (idx < 0)
			idx = url.length();
		if(slant < 0)
			slant = 0;
		String title = url.substring(slant, idx);
		return title;
	}
	public static File getFileFromBytes(String name,String path) {
		byte[] b=name.getBytes();
		BufferedOutputStream stream = null;
		File file = null;
		try {
			file = new File(Environment.getExternalStorageDirectory()+path);
			FileOutputStream fstream = new FileOutputStream(file);
			stream = new BufferedOutputStream(fstream);
			stream.write(b);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return file;
	}
}
