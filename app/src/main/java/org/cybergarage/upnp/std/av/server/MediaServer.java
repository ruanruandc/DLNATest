/******************************************************************
*
*	MediaServer for CyberLink
*
*	Copyright (C) Satoshi Konno 2003
*
*	File : MediaServer.java
*
*	10/22/03
*		- first revision.
*	03/30/05
*		- Added a constructor that read the description from memory instead of the file.
*		- Changed it as the default constructor.
*
******************************************************************/

package org.cybergarage.upnp.std.av.server;

import android.content.Context;
import android.util.Log;

import com.meizu.ruandongchuan.dlnatest.util.DLNAUtil;
import com.meizu.ruandongchuan.dlnatest.util.HttpServer;

import org.cybergarage.http.HTTPRequest;
import org.cybergarage.http.HTTPResponse;
import org.cybergarage.http.HTTPStatus;
import org.cybergarage.net.HostInterface;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.Service;
import org.cybergarage.upnp.UPnP;
import org.cybergarage.upnp.device.InvalidDescriptionException;
import org.cybergarage.upnp.std.av.server.object.Format;
import org.cybergarage.util.Debug;
import org.cybergarage.util.DeviceUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;


public class MediaServer extends Device
{
	////////////////////////////////////////////////
	// Constants
	////////////////////////////////////////////////
	
	public final static String DEVICE_TYPE = "urn:schemas-upnp-org:device:MediaServer:1";

	private HttpServer mHttpServer;
	
	public final static int DEFAULT_HTTP_PORT = 38520;
	
	public final static String DESCRIPTION =
		"<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
		"<root xmlns=\"urn:schemas-upnp-org:device-1-0\">\n" +
		"   <specVersion>\n" +
		"      <major>1</major>\n" +
		"      <minor>0</minor>\n" +
		"   </specVersion>\n" +
		"   <device>\n" +
		"       <deviceType>" + DEVICE_TYPE + "</deviceType>\n" +
		"		<friendlyName>%s</friendlyName>\n" +
		"		<manufacturer></manufacturer>\n" +
		"		<manufacturerURL>http://www.www.com</manufacturerURL>\n" +
		"		<modelDescription>Provides service</modelDescription>\n" +
		"		<modelName>MediaServer</modelName>\n" +
		"		<modelNumber>1.0</modelNumber>\n" +
		"		<modelURL>http://www.www.com</modelURL>\n" +
		"		<UDN>uuid:%s</UDN>\n" +
		"      <serviceList>\n" +
		"         <service>\n" +
		"            <serviceType>urn:schemas-upnp-org:service:ContentDirectory:1</serviceType>\n" +
		"            <serviceId>urn:upnp-org:serviceId:urn:schemas-upnp-org:service:ContentDirectory</serviceId>\n" +
		"            <SCPDURL>/service/ContentDirectory1.xml</SCPDURL>\n" +
		"            <controlURL>/service/ContentDirectory_control</controlURL>\n" +
		"            <eventSubURL>/service/ContentDirectory_event</eventSubURL>\n" +
		"         </service>\n" +
		"         <service>\n" +
		"            <serviceType>urn:schemas-upnp-org:service:ConnectionManager:1</serviceType>\n" +
		"            <serviceId>urn:upnp-org:serviceId:urn:schemas-upnp-org:service:ConnectionManager</serviceId>\n" +
		"            <SCPDURL>/service/ConnectionManager1.xml</SCPDURL>\n" +
		"            <controlURL>/service/ConnectionManager_control</controlURL>\n" +
		"            <eventSubURL>/service/ConnectionManager_event</eventSubURL>\n" +
		"         </service>\n" +
		"      </serviceList>\n" +
		"   </device>\n" +
		"</root>";
	
	////////////////////////////////////////////////
	// Constructor
	////////////////////////////////////////////////
	
	private final static String DESCRIPTION_FILE_NAME = "description/description.xml";
	
	public MediaServer(String descriptionFileName) throws InvalidDescriptionException
	{
		super(new File(descriptionFileName));
		initialize();
	}

	public MediaServer()
	{
		super();
		try {
			initialize(DESCRIPTION, ContentDirectory.SCPD, ConnectionManager.SCPD);
		}
		catch (InvalidDescriptionException ide) {}
	}

	public MediaServer(Context context) throws InvalidDescriptionException
	{
		this(String.format(DESCRIPTION, DeviceUtil.getFriendlyName(context, "MediaServer"),
				DeviceUtil.getUUID(context, "MediaServer")), ContentDirectory.SCPD, ConnectionManager.SCPD);
	}
	
	public MediaServer(String description, String contentDirectorySCPD, String connectionManagerSCPD) throws InvalidDescriptionException
	{
		super();
		initialize(description, contentDirectorySCPD, connectionManagerSCPD);
	}
	
	private void initialize(String description, String contentDirectorySCPD, String connectionManagerSCPD) throws InvalidDescriptionException
	{
		loadDescription(description);
		
		Service servConDir = getService(ContentDirectory.SERVICE_TYPE);
		servConDir.loadSCPD(contentDirectorySCPD);
		
		Service servConMan = getService(ConnectionManager.SERVICE_TYPE);
		servConMan.loadSCPD(connectionManagerSCPD);
		
		initialize();
	}
	
	private void initialize()
	{
		// Netwroking initialization		
		UPnP.setEnable(UPnP.USE_ONLY_IPV4_ADDR);
		String firstIf = HostInterface.getHostAddress(0);
		setInterfaceAddress(firstIf);
		//setHTTPPort(DEFAULT_HTTP_PORT);

		conDir = new ContentDirectory(this);
		conMan = new ConnectionManager(this);
		
		Service servConDir = getService(ContentDirectory.SERVICE_TYPE);
		servConDir.setActionListener(getContentDirectory());
		servConDir.setQueryListener(getContentDirectory());

		Service servConMan = getService(ConnectionManager.SERVICE_TYPE);
		servConMan.setActionListener(getConnectionManager());
		servConMan.setQueryListener(getConnectionManager());


	}
	
	protected void finalize()
	{
		stop();		
	}
	
	////////////////////////////////////////////////
	// Memeber
	////////////////////////////////////////////////
	
	private ConnectionManager conMan;
	private ContentDirectory conDir;
	
	public ConnectionManager getConnectionManager()
	{
		return conMan;
	}

	public ContentDirectory getContentDirectory()
	{
		return conDir;
	}	
	
	////////////////////////////////////////////////
	//	ContentDirectory	
	////////////////////////////////////////////////

	public void addContentDirectory(Directory dir)
	{
		getContentDirectory().addDirectory(dir);
	}
	
	public void removeContentDirectory(String name)
	{
		getContentDirectory().removeDirectory(name);
	}

	public void removeAllContentDirectories()
	{
		getContentDirectory().removeAllDirectories();
	}
	
	public int getNContentDirectories()
	{
		return getContentDirectory().getNDirectories();
	}
	
	public Directory getContentDirectory(int n)
	{
		return getContentDirectory().getDirectory(n);
	}

	////////////////////////////////////////////////
	// PulgIn
	////////////////////////////////////////////////
	
	public boolean addPlugIn(Format format)
	{
		return getContentDirectory().addPlugIn(format);
	}
	
	////////////////////////////////////////////////
	// HostAddress
	////////////////////////////////////////////////

	public void setInterfaceAddress(String ifaddr)
	{
		HostInterface.setInterface(ifaddr);
	}			
	
	public String getInterfaceAddress()
	{
		return HostInterface.getInterface();
	}			

	////////////////////////////////////////////////
	// HttpRequestListner (Overridded)
	////////////////////////////////////////////////
	
	public void httpRequestRecieved(HTTPRequest httpReq)
	{
		String uri = httpReq.getURI();
		Debug.message("uri = " + uri);
		try {
			if (uri.startsWith(ContentDirectory.CONTENT_EXPORT_URI) == true) {
				getContentDirectory().contentExportRequestRecieved(httpReq);
				return;
			} else if (uri.startsWith("/local")) {
				String filename = uri.substring(6, uri.length());
				String fromUrl;
				fromUrl = URLDecoder.decode(filename, "UTF-8");
	
				File file = new File(fromUrl);
				Log.i("Debug","fromurl="+fromUrl);
				if (!file.exists() || !file.isFile() || !file.canRead()) {
					Log.i("Debug","file is not exist");
					return;
				}
				InputStream contentIn = new FileInputStream(file);
	
				HTTPResponse httpRes = new HTTPResponse();
				/*httpRes.setHeader("Content-Disposition",
						"filename=" + URLEncoder.encode(file.getName(), "UTF-8")
						+ ";name=" + URLEncoder.encode(file.getName(), "UTF-8"));*/
				httpRes.setHeader("Accept-Ranges", "bytes");
				httpRes.setContentType(DLNAUtil.getMimeType(fromUrl));
				httpRes.setStatusCode(HTTPStatus.OK);
				Log.i("Debug","length="+file.length());
				httpRes.setContentLength(file.length());
				httpRes.setContentInputStream(contentIn);
	
				httpReq.post(httpRes);
				return;
			}
				 
			super.httpRequestRecieved(httpReq);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	////////////////////////////////////////////////
	// start/stop (Overided)
	////////////////////////////////////////////////
	
	public boolean start()
	{
		getContentDirectory().start();
		try {
			if (mHttpServer != null){
				mHttpServer.stop();
			}
			mHttpServer = new HttpServer(DEFAULT_HTTP_PORT);
			mHttpServer.setContentDirectory(getContentDirectory());
			Log.i("mHttpServer", "start server success");
		} catch (IOException e) {
			e.printStackTrace();
			Log.i("error","Couldn't start server");
		}
		super.start();
		return true;
	}
	
	public boolean stop()
	{
		getContentDirectory().stop();
		if (mHttpServer != null) {
			mHttpServer.stop();
		}
		super.stop();
		return true;
	}
	
	////////////////////////////////////////////////
	// update
	////////////////////////////////////////////////

	public void update()
	{
	}

	/*@Override
	public int getHTTPPort() {
		return DEFAULT_HTTP_PORT;
	}*/

}

