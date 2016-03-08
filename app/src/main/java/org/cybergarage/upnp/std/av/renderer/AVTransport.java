/******************************************************************
*
*	MediaServer for CyberLink
*
*	Copyright (C) Satoshi Konno 2003
*
*	File : AVTransport.java
*
*	Revision:
*
*	02/22/08
*		- first revision.
*
******************************************************************/

package org.cybergarage.upnp.std.av.renderer;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.meizu.ruandongchuan.dlnatest.util.DLNAUtil;
import com.meizu.ruandongchuan.dlnatest.util.HandlerController;
import com.meizu.ruandongchuan.dlnatest.view.DlnaApp;
import com.meizu.ruandongchuan.dlnatest.view.activity.FullscreenActivity;
import com.meizu.ruandongchuan.dlnatest.view.activity.VideoActivity;

import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.StateVariable;
import org.cybergarage.upnp.control.ActionListener;
import org.cybergarage.upnp.control.QueryListener;
import org.cybergarage.upnp.std.av.server.object.item.ItemNode;
import org.cybergarage.util.Mutex;

public class AVTransport implements ActionListener, QueryListener
{
	////////////////////////////////////////////////
	// Constants
	////////////////////////////////////////////////

	public final static String SERVICE_TYPE = "urn:schemas-upnp-org:service:AVTransport:1";
	
	// Browse Action	
	
	public final static String TRANSPORTSTATE = "TransportState";
	public final static String TRANSPORTSTATUS = "TransportStatus";
	public final static String PLAYBACKSTORAGEMEDIUM = "PlaybackStorageMedium";
	public final static String RECORDSTORAGEMEDIUM = "RecordStorageMedium";
	public final static String POSSIBLEPLAYBACKSTORAGEMEDIA = "PossiblePlaybackStorageMedia";
	public final static String POSSIBLERECORDSTORAGEMEDIA = "PossibleRecordStorageMedia";
	public final static String CURRENTPLAYMODE = "CurrentPlayMode";
	public final static String TRANSPORTPLAYSPEED = "TransportPlaySpeed";
	public final static String RECORDMEDIUMWRITESTATUS = "RecordMediumWriteStatus";
	public final static String CURRENTRECORDQUALITYMODE = "CurrentRecordQualityMode";
	public final static String POSSIBLERECORDQUALITYMODES = "PossibleRecordQualityModes";
	public final static String NUMBEROFTRACKS = "NumberOfTracks";
	public final static String CURRENTTRACK = "CurrentTrack";
	public final static String CURRENTTRACKDURATION = "CurrentTrackDuration";
	public final static String CURRENTMEDIADURATION = "CurrentMediaDuration";
	public final static String CURRENTTRACKMETADATA = "CurrentTrackMetaData";
	public final static String CURRENTTRACKURI = "CurrentTrackURI";
	public final static String AVTRANSPORTURI = "AVTransportURI";
	public final static String AVTRANSPORTURIMETADATA = "AVTransportURIMetaData";
	public final static String NEXTAVTRANSPORTURI = "NextAVTransportURI";
	public final static String NEXTAVTRANSPORTURIMETADATA = "NextAVTransportURIMetaData";
	public final static String RELATIVETIMEPOSITION = "RelativeTimePosition";
	public final static String ABSOLUTETIMEPOSITION = "AbsoluteTimePosition";
	public final static String RELATIVECOUNTERPOSITION = "RelativeCounterPosition";
	public final static String ABSOLUTECOUNTERPOSITION = "AbsoluteCounterPosition";
	public final static String CURRENTTRANSPORTACTIONS = "CurrentTransportActions";
	public final static String LASTCHANGE = "LastChange";
	public final static String SETAVTRANSPORTURI = "SetAVTransportURI";
	public final static String INSTANCEID = "InstanceID";
	public final static String CURRENTURI = "CurrentURI";
	public final static String CURRENTURIMETADATA = "CurrentURIMetaData";
	public final static String SETNEXTAVTRANSPORTURI = "SetNextAVTransportURI";
	public final static String NEXTURI = "NextURI";
	public final static String NEXTURIMETADATA = "NextURIMetaData";
	public final static String GETMEDIAINFO = "GetMediaInfo";
	public final static String NRTRACKS = "NrTracks";
	public final static String MEDIADURATION = "MediaDuration";
	public final static String PLAYMEDIUM = "PlayMedium";
	public final static String RECORDMEDIUM = "RecordMedium";
	public final static String WRITESTATUS = "WriteStatus";
	public final static String GETTRANSPORTINFO = "GetTransportInfo";
	public final static String CURRENTTRANSPORTSTATE = "CurrentTransportState";
	public final static String CURRENTTRANSPORTSTATUS = "CurrentTransportStatus";
	public final static String CURRENTSPEED = "CurrentSpeed";
	public final static String GETPOSITIONINFO = "GetPositionInfo";
	public final static String TRACK = "Track";
	public final static String TRACKDURATION = "TrackDuration";
	public final static String TRACKMETADATA = "TrackMetaData";
	public final static String TRACKURI = "TrackURI";
	public final static String RELTIME = "RelTime";
	public final static String ABSTIME = "AbsTime";
	public final static String RELCOUNT = "RelCount";
	public final static String ABSCOUNT = "AbsCount";
	public final static String GETDEVICECAPABILITIES = "GetDeviceCapabilities";
	public final static String PLAYMEDIA = "PlayMedia";
	public final static String RECMEDIA = "RecMedia";
	public final static String RECQUALITYMODES = "RecQualityModes";
	public final static String GETTRANSPORTSETTINGS = "GetTransportSettings";
	public final static String PLAYMODE = "PlayMode";
	public final static String RECQUALITYMODE = "RecQualityMode";
	public final static String STOP = "Stop";
	public final static String PLAY = "Play";
	public final static String SPEED = "Speed";
	public final static String PAUSE = "Pause";
	public final static String RECORD = "Record";
	public final static String SEEK = "Seek";
	public final static String UNIT = "Unit";
	public final static String TARGET = "Target";
	public final static String NEXT = "Next";
	public final static String PREVIOUS = "Previous";
	public final static String SETPLAYMODE = "SetPlayMode";
	public final static String NEWPLAYMODE = "NewPlayMode";
	public final static String SETRECORDQUALITYMODE = "SetRecordQualityMode";
	public final static String NEWRECORDQUALITYMODE = "NewRecordQualityMode";
	public final static String GETCURRENTTRANSPORTACTIONS = "GetCurrentTransportActions";
	public final static String ACTIONS = "Actions";

	public final static String STOPPED = "STOPPED";
	public final static String PLAYING = "PLAYING";
	public final static String TRANSITIONING = "TRANSITIONING";

	public final static String OK = "OK";
	public final static String ERROR_OCCURRED = "ERROR_OCCURRED";
	public final static String NORMAL = "NORMAL";
	public final static String TRACK_NR = "TRACK_NR";
	
	public final static String NOT_IMPLEMENTED = "NOT_IMPLEMENTED";
	
	public final static String SCPD =
		"<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
		"<scpd xmlns=\"urn:schemas-upnp-org:service-1-0\">\n" +
		"   <specVersion>\n" +
		"      <major>1</major>\n" +
		"      <minor>0</minor>\n" +
		"	</specVersion>\n" +
		"    <serviceStateTable>"+
		"        <stateVariable>"+
		"            <name>TransportState</name>"+
		"            <sendEventsAttribute>no</sendEventsAttribute>"+
		"            <dataType>string</dataType>"+
		"            <allowedValueList>"+
		"                <allowedValue>STOPPED</allowedValue>"+
		"                <allowedValue>PLAYING</allowedValue>"+
		"            </allowedValueList>"+
		"        </stateVariable>"+
		"        <stateVariable>"+
		"            <name>TransportStatus</name>"+
		"            <sendEventsAttribute>no</sendEventsAttribute>"+
		"            <dataType>string</dataType>"+
		"            <allowedValueList>"+
		"                <allowedValue>OK</allowedValue>"+
		"                <allowedValue>ERROR_OCCURRED</allowedValue>           "+
		"            </allowedValueList>"+
		"        </stateVariable>"+
		"        <stateVariable>"+
		"            <name>PlaybackStorageMedium</name>"+
		"            <sendEventsAttribute>no</sendEventsAttribute>"+
		"            <dataType>string</dataType>"+
		"        </stateVariable>"+
		"	     <stateVariable>"+
		"            <name>RecordStorageMedium</name>"+
		"            <sendEventsAttribute>no</sendEventsAttribute>"+
		"            <dataType>string</dataType>"+
		"              </stateVariable>"+
		"        <stateVariable>"+
		"            <name>PossiblePlaybackStorageMedia</name>"+
		"            <sendEventsAttribute>no</sendEventsAttribute>"+
		"            <dataType>string</dataType>"+
		"        </stateVariable>"+
		"        <stateVariable>"+
		"            <name>PossibleRecordStorageMedia</name>"+
		"            <sendEventsAttribute>no</sendEventsAttribute>"+
		"            <dataType>string</dataType>"+
		"        </stateVariable>"+
		"        <stateVariable>"+
		"            <name>CurrentPlayMode</name>"+
		"            <sendEventsAttribute>no</sendEventsAttribute>"+
		"            <dataType>string</dataType>"+
		"            <allowedValueList>"+
		"                <allowedValue>NORMAL</allowedValue>"+
		"            </allowedValueList>"+
		"            <defaultValue>NORMAL</defaultValue>"+
		"        </stateVariable>"+
		"        <stateVariable>"+
		"            <name>TransportPlaySpeed</name>"+
		"            <sendEventsAttribute>no</sendEventsAttribute>"+
		"            <dataType>string</dataType>"+
		"	         <allowedValueList>"+
		"                <allowedValue>1</allowedValue>"+
		"            </allowedValueList>"+
		"        </stateVariable>"+
		"        <stateVariable>"+
		"            <sendEventsAttribute>no</sendEventsAttribute>"+
		"            <name>RecordMediumWriteStatus </name>"+
		"            <dataType>string</dataType>"+
		"         </stateVariable>"+
		"        <stateVariable>"+
		"            <name>CurrentRecordQualityMode</name>"+
		"            <sendEventsAttribute>no</sendEventsAttribute>"+
		"            <dataType>string</dataType>"+
		"          </stateVariable>"+
		"        <stateVariable>"+
		"            <name>PossibleRecordQualityModes</name>"+
		"            <sendEventsAttribute>no</sendEventsAttribute>"+
		"            <dataType>string</dataType>"+
		"        </stateVariable>"+
		"        <stateVariable>"+
		"            <name>NumberOfTracks</name>"+
		"            <sendEventsAttribute>no</sendEventsAttribute>"+
		"            <dataType>ui4</dataType>"+
		"		     <allowedValueRange>"+
		"			     <minimum>0</minimum>"+
		"		     </allowedValueRange>"+
		"         </stateVariable>"+
		"        <stateVariable>"+
		"            <name>CurrentTrack</name>"+
		"            <sendEventsAttribute>no</sendEventsAttribute>"+
		"            <dataType>ui4</dataType>"+
		"		     <allowedValueRange>"+
		"			    <minimum>0</minimum>"+
		"			    <step>1</step>"+
		"		     </allowedValueRange>"+
		"        </stateVariable>"+
		"        <stateVariable>"+
		"            <name>CurrentTrackDuration</name>"+
		"            <sendEventsAttribute>no</sendEventsAttribute>"+
		"            <dataType>string</dataType>"+
		"        </stateVariable>"+
		"	     <stateVariable>"+
		"            <name>CurrentMediaDuration</name>"+
		"            <sendEventsAttribute>no</sendEventsAttribute>"+
		"            <dataType>string</dataType>"+
		"        </stateVariable>"+
		"        <stateVariable>"+
		"            <name>CurrentTrackMetaData</name>"+
		"            <sendEventsAttribute>no</sendEventsAttribute>"+
		"            <dataType>string</dataType>"+
		"        </stateVariable>"+
		"        <stateVariable>"+
		"            <name>CurrentTrackURI</name>"+
		"            <sendEventsAttribute>no</sendEventsAttribute>"+
		"            <dataType>string</dataType>"+
		"        </stateVariable>"+
		"        <stateVariable>"+
		"            <name>AVTransportURI</name>"+
		"            <sendEventsAttribute>no</sendEventsAttribute>"+
		"            <dataType>string</dataType>"+
		"        </stateVariable>"+
		"        <stateVariable>"+
		"            <name>AVTransportURIMetaData</name>"+
		"            <sendEventsAttribute>no</sendEventsAttribute>"+
		"            <dataType>string</dataType>"+
		"        </stateVariable>"+
		"        <stateVariable>"+
		"            <name>NextAVTransportURI</name>"+
		"            <sendEventsAttribute>no</sendEventsAttribute>"+
		"            <dataType>string</dataType>"+
		"        </stateVariable>"+
		"        <stateVariable>"+
		"            <name>NextAVTransportURIMetaData</name>"+
		"            <sendEventsAttribute>no</sendEventsAttribute>"+
		"            <dataType>string</dataType>"+
		"        </stateVariable>"+
		"        <stateVariable>"+
		"            <name>RelativeTimePosition</name>"+
		"            <sendEventsAttribute>no</sendEventsAttribute>"+
		"            <dataType>string</dataType>"+
		"        </stateVariable>"+
		"        <stateVariable>"+
		"            <name>AbsoluteTimePosition</name>"+
		"            <sendEventsAttribute>no</sendEventsAttribute>"+
		"            <dataType>string</dataType>"+
		"        </stateVariable>"+
		"        <stateVariable>"+
		"            <name>RelativeCounterPosition</name>"+
		"            <sendEventsAttribute>no</sendEventsAttribute>"+
		"            <dataType>i4</dataType>"+
		"        </stateVariable>"+
		"        <stateVariable>"+
		"            <name>AbsoluteCounterPosition</name>"+
		"            <sendEventsAttribute>no</sendEventsAttribute>"+
		"            <dataType>i4</dataType>"+
		"        </stateVariable>"+
		"        <stateVariable>"+
		"		<Optional/>"+
		"            <name>CurrentTransportActions</name>"+
		"            <sendEventsAttribute>no</sendEventsAttribute>"+
		"            <dataType>string</dataType>"+
		"        </stateVariable>"+
		"        <stateVariable>"+
		"            <name>LastChange</name>"+
		"            <sendEventsAttribute>yes</sendEventsAttribute>"+
		"            <dataType>string</dataType>"+
		"        </stateVariable>"+
		"        <stateVariable>"+
		"            <name>A_ARG_TYPE_SeekMode</name>"+
		"            <sendEventsAttribute>no</sendEventsAttribute>"+
		"            <dataType>string</dataType>"+
		"            <allowedValueList>"+
		"                 <allowedValue>TRACK_NR</allowedValue>"+
		"                 <allowedValue>REL_TIME</allowedValue>" +
		"            </allowedValueList>"+
		"        </stateVariable>"+
		"        <stateVariable>"+
		"            <name>A_ARG_TYPE_SeekTarget</name>"+
		"            <sendEventsAttribute>no</sendEventsAttribute>"+
		"            <dataType>string</dataType>"+
		"        </stateVariable>"+
		"        <stateVariable>"+
		"            <name>A_ARG_TYPE_InstanceID</name>"+
		"            <sendEventsAttribute>no</sendEventsAttribute>"+
		"            <dataType>ui4</dataType>"+
		"        </stateVariable>"+
		"    </serviceStateTable>"+
		"    <actionList>"+
		"        <action>"+
		"            <name>SetAVTransportURI</name>"+
		"            <argumentList>"+
		"                <argument>"+
		"                    <name>InstanceID</name>"+
		"                    <direction>in</direction>" +
		"                    <relatedStateVariable>A_ARG_TYPE_InstanceID</relatedStateVariable>"+
		"                </argument>"+
		"                <argument>"+
		"                    <name>CurrentURI</name>"+
		"                    <direction>in</direction>" +
		"                    <relatedStateVariable>AVTransportURI</relatedStateVariable>"+
		"                </argument>"+
		"                <argument>"+
		"                    <name>CurrentURIMetaData</name>"+
		"                    <direction>in</direction>" +
		"                    <relatedStateVariable>AVTransportURIMetaData</relatedStateVariable>"+
		"                </argument>"+
		"            </argumentList>"+
		"        </action>"+
		"        <action>	<Optional/>"+
		"            <name>SetNextAVTransportURI</name>"+
		"            <argumentList>"+
		"                <argument>"+
		"                    <name>InstanceID</name>"+
		"                    <direction>in</direction>" +
		"                    <relatedStateVariable>A_ARG_TYPE_InstanceID</relatedStateVariable>"+
		"                </argument>"+
		"                <argument>"+
		"                    <name>NextURI</name>"+
		"                    <direction>in</direction>" +
		"                    <relatedStateVariable>NextAVTransportURI</relatedStateVariable>"+
		"                </argument>"+
		"                <argument>"+
		"                    <name>NextURIMetaData</name>"+
		"                    <direction>in</direction>" +
		"                    <relatedStateVariable>NextAVTransportURIMetaData</relatedStateVariable>"+
		"                </argument>"+
		"            </argumentList>"+
		"        </action>"+
		"        <action>"+
		"            <name>GetMediaInfo</name>"+
		"            <argumentList>"+
		"                <argument>"+
		"                    <name>InstanceID</name>"+
		"                    <direction>in</direction>" +
		"                 <relatedStateVariable>A_ARG_TYPE_InstanceID</relatedStateVariable>"+
		"                </argument>"+
		"                <argument>"+
		"                    <name>NrTracks</name>"+
		"                    <direction>out</direction>" +
		"                    <relatedStateVariable>NumberOfTracks</relatedStateVariable>"+
		"                </argument>"+
		"                <argument>"+
		"                    <name>MediaDuration</name>"+
		"                    <direction>out</direction>" +
		"                    <relatedStateVariable>CurrentMediaDuration</relatedStateVariable>"+
		"                </argument>"+
		"                <argument>"+
		"                    <name>CurrentURI</name>"+
		"                    <direction>out</direction>" +
		"                    <relatedStateVariable>AVTransportURI</relatedStateVariable>"+
		"                </argument>"+
		"		         <argument>"+
		"                    <name>CurrentURIMetaData</name>"+
		"                    <direction>out</direction>" +
		"                    <relatedStateVariable>AVTransportURIMetaData</relatedStateVariable>"+
		"                </argument>"+
		"                <argument>"+
		"                    <name>NextURI</name>"+
		"                    <direction>out</direction>" +
		"                    <relatedStateVariable>NextAVTransportURI</relatedStateVariable>"+
		"                </argument>"+
		"		         <argument>"+
		"                    <name>NextURIMetaData</name>"+
		"                    <direction>out</direction>" +
		"                    <relatedStateVariable>NextAVTransportURIMetaData</relatedStateVariable>"+
		"                </argument>"+
		"                <argument>"+
		"                    <name>PlayMedium</name>"+
		"                    <direction>out</direction>" +
		"                    <relatedStateVariable>PlaybackStorageMedium</relatedStateVariable>"+
		"                </argument>"+
		"                <argument>"+
		"                    <name>RecordMedium</name>"+
		"                    <direction>out</direction>" +
		"                    <relatedStateVariable>RecordStorageMedium</relatedStateVariable>"+
		"                </argument>"+
		"                <argument>"+
		"                    <name>WriteStatus</name>"+
		"                    <direction>out</direction>" +
		"                    <relatedStateVariable>RecordMediumWriteStatus </relatedStateVariable>"+
		"                </argument>"+
		"            </argumentList>"+
		"        </action>"+
		"        <action>"+
		"            <name>GetTransportInfo</name>"+
		"            <argumentList>"+
		"                <argument>"+
		"                    <name>InstanceID</name>"+
		"                    <direction>in</direction>" +
		"                    <relatedStateVariable>A_ARG_TYPE_InstanceID</relatedStateVariable>"+
		"                </argument>"+
		"                <argument>"+
		"                    <name>CurrentTransportState</name>"+
		"                    <direction>out</direction>" +
		"                    <relatedStateVariable>TransportState</relatedStateVariable>"+
		"                </argument>"+
		"                <argument>"+
		"                    <name>CurrentTransportStatus</name>"+
		"                    <direction>out</direction>" +
		"                    <relatedStateVariable>TransportStatus</relatedStateVariable>"+
		"                </argument>"+
		"                <argument>"+
		"                    <name>CurrentSpeed</name>"+
		"                    <direction>out</direction>" +
		"                    <relatedStateVariable>TransportPlaySpeed</relatedStateVariable>"+
		"                </argument>"+
		"            </argumentList>"+
		"        </action>"+
		"        <action>"+
		"            <name>GetPositionInfo</name>"+
		"            <argumentList>"+
		"                <argument>"+
		"                    <name>InstanceID</name>"+
		"                    <direction>in</direction>" +
		"                    <relatedStateVariable>A_ARG_TYPE_InstanceID</relatedStateVariable>"+
		"                </argument>"+
		"                <argument>"+
		"                    <name>Track</name>"+
		"                    <direction>out</direction>" +
		"                    <relatedStateVariable>CurrentTrack</relatedStateVariable>"+
		"                </argument>"+
		"                <argument>"+
		"                    <name>TrackDuration</name>"+
		"                    <direction>out</direction>" +
		"                    <relatedStateVariable>CurrentTrackDuration</relatedStateVariable>"+
		"                </argument>"+
		"                <argument>"+
		"                    <name>TrackMetaData</name>"+
		"                    <direction>out</direction>" +
		"                    <relatedStateVariable>CurrentTrackMetaData</relatedStateVariable>"+
		"                </argument>"+
		"                <argument>"+
		"                    <name>TrackURI</name>"+
		"                    <direction>out</direction>" +
		"                    <relatedStateVariable>CurrentTrackURI</relatedStateVariable>"+
		"                </argument>"+
		"                <argument>"+
		"                    <name>RelTime</name>"+
		"                    <direction>out</direction>" +
		"                    <relatedStateVariable>RelativeTimePosition</relatedStateVariable>"+
		"                </argument>"+
		"                <argument>"+
		"                    <name>AbsTime</name>"+
		"                    <direction>out</direction>" +
		"                    <relatedStateVariable>AbsoluteTimePosition</relatedStateVariable>"+
		"                </argument>"+
		"                <argument>"+
		"                    <name>RelCount</name>"+
		"                    <direction>out</direction>" +
		"                    <relatedStateVariable>RelativeCounterPosition</relatedStateVariable>"+
		"                </argument>"+
		"                <argument>"+
		"                    <name>AbsCount</name>"+
		"                    <direction>out</direction>" +
		"                    <relatedStateVariable>AbsoluteCounterPosition</relatedStateVariable>"+
		"                </argument>"+
		"            </argumentList>"+
		"        </action>"+
		"        <action>"+
		"            <name>GetDeviceCapabilities</name>"+
		"            <argumentList>"+
		"                <argument>"+
		"                    <name>InstanceID</name>"+
		"                    <direction>in</direction>" +
		"                    <relatedStateVariable>A_ARG_TYPE_InstanceID</relatedStateVariable>"+
		"                </argument>"+
		"                <argument>"+
		"                    <name>PlayMedia</name>"+
		"                    <direction>out</direction>" +
		"                    <relatedStateVariable>PossiblePlaybackStorageMedia</relatedStateVariable>"+
		"                </argument>"+
		"                <argument>"+
		"                    <name>RecMedia</name>"+
		"                    <direction>out</direction>" +
		"                    <relatedStateVariable>PossibleRecordStorageMedia</relatedStateVariable>"+
		"                </argument>"+
		"                <argument>"+
		"                    <name>RecQualityModes</name>"+
		"                    <direction>out</direction>" +
		"                    <relatedStateVariable>PossibleRecordQualityModes</relatedStateVariable>"+
		"                </argument>"+
		"            </argumentList>"+
		"        </action>"+
		"        <action>"+
		"            <name>GetTransportSettings</name>"+
		"            <argumentList>"+
		"                <argument>"+
		"                    <name>InstanceID</name>"+
		"                    <direction>in</direction>" +
		"                    <relatedStateVariable>A_ARG_TYPE_InstanceID</relatedStateVariable>"+
		"                </argument>"+
		"                <argument>"+
		"                    <name>PlayMode</name>"+
		"                    <direction>out</direction>" +
		"                    <relatedStateVariable>CurrentPlayMode</relatedStateVariable>"+
		"                </argument>"+
		"                <argument>"+
		"                    <name>RecQualityMode</name>"+
		"                    <direction>out</direction>" +
		"                 <relatedStateVariable>CurrentRecordQualityMode</relatedStateVariable>"+
		"                </argument>"+
		"            </argumentList>"+
		"        </action>"+
		"        <action>"+
		"            <name>Stop</name>"+
		"            <argumentList>"+
		"                <argument>"+
		"                    <name>InstanceID</name>"+
		"                    <direction>in</direction>" +
		"                    <relatedStateVariable>A_ARG_TYPE_InstanceID</relatedStateVariable>"+
		"                </argument>"+
		"            </argumentList>"+
		"        </action>"+
		"        <action>"+
		"            <name>Play</name>"+
		"            <argumentList>"+
		"                <argument>"+
		"                    <name>InstanceID</name>"+
		"                    <direction>in</direction>" +
		"                    <relatedStateVariable>A_ARG_TYPE_InstanceID</relatedStateVariable>"+
		"                </argument>"+
		"                <argument>"+
		"                    <name>Speed</name>"+
		"                    <direction>in</direction>" +
		"                    <relatedStateVariable>TransportPlaySpeed</relatedStateVariable>"+
		"                </argument>"+
		"            </argumentList>"+
		"        </action>"+
		"        <action>	<Optional/>"+
		"            <name>Pause</name>"+
		"            <argumentList>"+
		"                <argument>"+
		"                    <name>InstanceID</name>"+
		"                    <direction>in</direction>" +
		"                    <relatedStateVariable>A_ARG_TYPE_InstanceID</relatedStateVariable>"+
		"                </argument>"+
		"            </argumentList>"+
		"        </action>"+
		"        <action>	<Optional/>"+
		"            <name>Record</name>"+
		"            <argumentList>"+
		"                <argument>"+
		"                    <name>InstanceID</name>"+
		"                    <direction>in</direction>" +
		"                    <relatedStateVariable>A_ARG_TYPE_InstanceID</relatedStateVariable>"+
		"                </argument>"+
		"            </argumentList>"+
		"        </action>"+
		"        <action>"+
		"            <name>Seek</name>"+
		"            <argumentList>"+
		"                <argument>"+
		"                    <name>InstanceID</name>"+
		"                    <direction>in</direction>" +
		"                    <relatedStateVariable>A_ARG_TYPE_InstanceID</relatedStateVariable>"+
		"                </argument>"+
		"                <argument>"+
		"                    <name>Unit</name>"+
		"                    <direction>in</direction>" +
		"                    <relatedStateVariable>A_ARG_TYPE_SeekMode</relatedStateVariable>"+
		"                </argument>"+
		"                <argument>"+
		"                    <name>Target</name>"+
		"                    <direction>in</direction>" +
		"                    <relatedStateVariable>A_ARG_TYPE_SeekTarget</relatedStateVariable>"+
		"                </argument>"+
		"            </argumentList>"+
		"        </action>"+
		"        <action>"+
		"            <name>Next</name>"+
		"            <argumentList>"+
		"                <argument>"+
		"                    <name>InstanceID</name>"+
		"                    <direction>in</direction>" +
		"                    <relatedStateVariable>A_ARG_TYPE_InstanceID</relatedStateVariable>"+
		"                </argument>"+
		"            </argumentList>"+
		"        </action>"+
		"        <action>"+
		"            <name>Previous</name>"+
		"            <argumentList>"+
		"                <argument>"+
		"                    <name>InstanceID</name>"+
		"                    <direction>in</direction>" +
		"                    <relatedStateVariable>A_ARG_TYPE_InstanceID</relatedStateVariable>"+
		"                </argument>"+
		"            </argumentList>"+
		"        </action>"+
		"        <action>	<Optional/>"+
		"            <name>SetPlayMode</name>"+
		"            <argumentList>"+
		"                <argument>"+
		"                    <name>InstanceID</name>"+
		"                    <direction>in</direction>" +
		"                    <relatedStateVariable>A_ARG_TYPE_InstanceID</relatedStateVariable>"+
		"                </argument>"+
		"                <argument>"+
		"                    <name>NewPlayMode</name>"+
		"                    <direction>in</direction>" +
		"                    <relatedStateVariable>CurrentPlayMode</relatedStateVariable>"+
		"                </argument>"+
		"            </argumentList>"+
		"        </action>"+
		"        <action>	<Optional/>"+
		"            <name>SetRecordQualityMode</name>"+
		"            <argumentList>"+
		"                <argument>"+
		"                    <name>InstanceID</name>"+
		"                    <direction>in</direction>" +
		"                    <relatedStateVariable>A_ARG_TYPE_InstanceID</relatedStateVariable>"+
		"                </argument>"+
		"                <argument>"+
		"                    <name>NewRecordQualityMode</name>"+
		"                    <direction>in</direction>" +
		"                    <relatedStateVariable>CurrentRecordQualityMode</relatedStateVariable>"+
		"                </argument>"+
		"            </argumentList>"+
		"        </action>"+
		"        <action>	<Optional/>"+
		"            <name>GetCurrentTransportActions</name>"+
		"            <argumentList>"+
		"                <argument>"+
		"                    <name>InstanceID</name>"+
		"                    <direction>in</direction>" +
		"                    <relatedStateVariable>A_ARG_TYPE_InstanceID</relatedStateVariable>"+
		"                </argument>"+
		"                <argument>"+
		"                    <name>Actions</name>"+
		"                    <direction>out</direction>" +
		"                    <relatedStateVariable>CurrentTransportActions</relatedStateVariable>"+
		"                </argument>"+
		"            </argumentList>"+
		"        </action>"+
		"    </actionList>"+
		"</scpd>";



	///////////////////////
	// Customer
	///////////////////////
	private String mCurPlayState = STOPPED;
	private int mCurrentTrack = 1;
	private String mCurTrackDuration = "00:00:00";
	private String mCurrentTrackMetaData;
	private String mCurrentTrackURI;
	private String mRelativeTimePosition;

	////////////////////////////////////////////////
	// Constructor 
	////////////////////////////////////////////////
	
	public AVTransport(MediaRenderer render)
	{
		setMediaRenderer(render);
		
		avTransInfoList = new AVTransportInfoList();
	}
	
	////////////////////////////////////////////////
	// MediaRender
	////////////////////////////////////////////////

	private MediaRenderer mediaRenderer;
	
	private void setMediaRenderer(MediaRenderer render)
	{
		mediaRenderer = render;	
	}
	
	public MediaRenderer getMediaRenderer()
	{
		return mediaRenderer;	
	}
	
	////////////////////////////////////////////////
	// Mutex
	////////////////////////////////////////////////
	
	private Mutex mutex = new Mutex();
	
	public void lock()
	{
		mutex.lock();
	}
	
	public void unlock()
	{
		mutex.unlock();
	}
	
	////////////////////////////////////////////////
	// AVTransportInfoList
	////////////////////////////////////////////////
	
	private AVTransportInfoList avTransInfoList;
	
	public AVTransportInfoList getAvTransInfoList()
	{
		return avTransInfoList;
	}
	
	////////////////////////////////////////////////
	// AVTransportInfo (Current)
	////////////////////////////////////////////////
	
	public void setCurrentAvTransInfo(AVTransportInfo avTransInfo) 
	{
		AVTransportInfoList avTransInfoList = getAvTransInfoList();
		synchronized (avTransInfoList) {
			if (1 <= avTransInfoList.size())
				avTransInfoList.remove(0);
			avTransInfoList.insertElementAt(avTransInfo, 0);
		}
	}
	
	public AVTransportInfo getCurrentAvTransInfo() 
	{
		AVTransportInfo avTransInfo = null;
		synchronized (avTransInfoList) {
			if (avTransInfoList.size() < 1)
				return null;
			avTransInfo = avTransInfoList.getAVTransportInfo(0);
		}
		return avTransInfo;
	}

	////////////////////////////////////////////////
	// AVTransportInfo (Current)
	////////////////////////////////////////////////
	
	public void setNextAvTransInfo(AVTransportInfo avTransInfo) 
	{
		synchronized (avTransInfoList) {
			if (2 <= avTransInfoList.size())
				avTransInfoList.remove(0);
			avTransInfoList.insertElementAt(avTransInfo, 1);
		}
	}
	
	public AVTransportInfo getNextAvTransInfo() 
	{
		AVTransportInfo avTransInfo = null;
		synchronized (avTransInfoList) {
			if (avTransInfoList.size() < 2)
				return null;
			avTransInfo = avTransInfoList.getAVTransportInfo(1);
		}
		return avTransInfo;
	}

	public void setmCurPlayState(String mCurPlayState) {
		this.mCurPlayState = mCurPlayState;
	}

	public void updatePositionInfo(String position,String duration){
		this.mRelativeTimePosition = position;
		this.mCurTrackDuration = duration;
	}

	public void setStateVariable(String ServiceId, String value) {
		if(getMediaRenderer().getService(ServiceId) != null) {
			getMediaRenderer().getService(ServiceId).getStateVariable("LastChange")
					.setValue("<Event xmlns=\"urn:schemas-upnp-org:metadata-1-0/AVT/\"><" + value + "</Event>");
		}
	}

	public void play(String url, String metadata) {
		Uri uri = Uri.parse(url);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("metadata", metadata);
		String type = "";
		if (TextUtils.isEmpty(metadata)){
			type = DLNAUtil.getMimeType(url);
		}else {
			ItemNode itemNode = DLNAUtil.parseMetaData(metadata);
			if (itemNode != null) {
				if (itemNode.isAudioClass()) {
					type = "audio/*";
				} else if (itemNode.isImageClass()) {
					type = "image/*";
				} else if (itemNode.isMovieClass()) {
					type = "video/*";
				}
			}
		}
		if (type.startsWith("audio")){
			intent.setClass(DlnaApp.getInstance().getApplicationContext(), VideoActivity.class);
		}else if (type.startsWith("image")){
			intent.setClass(DlnaApp.getInstance().getApplicationContext(), FullscreenActivity.class);
		}else if (type.startsWith("video")){
			intent.setClass(DlnaApp.getInstance().getApplicationContext(), VideoActivity.class);
		}
		//intent.setData(uri);
		intent.setDataAndType(uri, type);
		Log.i("type=", intent.getType());
		//LogUtil.i("playlog","url="+url+"metadata="+metadata);
		Log.i("playmetadata", metadata + "-" );
		Log.i("playurl", url);
		//Toast.makeText(getApplicationContext(),metadata,Toast.LENGTH_SHORT).show();
		DlnaApp.getInstance().startActivity(intent);
	}

	////////////////////////////////////////////////
	// ActionListener
	////////////////////////////////////////////////

	public boolean actionControlReceived(Action action)
	{
		boolean isActionSuccess;
		
		String actionName = action.getName();

		if (actionName == null)
			return false;
		
		isActionSuccess = false;


		Log.i("AVTransport", "actionControlReceived: "+ actionName);
		if (actionName.equals(SETAVTRANSPORTURI)) {
			AVTransportInfo avTransInfo = new AVTransportInfo();
			String currentUri = action.getArgument(CURRENTURI).getValue();
			String currentMetaData = action.getArgument(CURRENTURIMETADATA).getValue();
			avTransInfo.setInstanceID(action.getArgument(INSTANCEID).getIntegerValue());
			avTransInfo.setURI(currentUri);
			avTransInfo.setURIMetaData(currentMetaData);
			setCurrentAvTransInfo(avTransInfo);
			isActionSuccess = true;

			this.mCurrentTrackMetaData = currentMetaData;
			this.mCurPlayState = STOPPED;
			this.mCurrentTrack = 1;
			Log.i(CURRENTURI,currentUri);
			Log.i(CURRENTURIMETADATA, currentMetaData);
			//DLNAService.getInstance().play(avTransInfo.getURI(), avTransInfo.getURIMetaData());
		}

		if (actionName.equals(SETNEXTAVTRANSPORTURI)) {
			AVTransportInfo avTransInfo = new AVTransportInfo();
			avTransInfo.setInstanceID(action.getArgument(INSTANCEID).getIntegerValue());
			avTransInfo.setURI(action.getArgument(NEXTURI).getValue());
			avTransInfo.setURIMetaData(action.getArgument(NEXTURIMETADATA).getValue());
			setNextAvTransInfo(avTransInfo);
			isActionSuccess = true;
			//DLNAService.getInstance().play(avTransInfo.getURI(), avTransInfo.getURIMetaData());
		}

		if (actionName.equals(GETMEDIAINFO)) {
			int instanceID = action.getArgument(INSTANCEID).getIntegerValue();
			synchronized (avTransInfoList) {
				int avTransInfoCnt = avTransInfoList.size();
				for (int n=0; n<avTransInfoCnt; n++) {
					AVTransportInfo avTransInfo = avTransInfoList.getAVTransportInfo(n);
					if (avTransInfo == null)
						continue;
					if (avTransInfo.getInstanceID() != instanceID)
						continue;
					action.getArgument(CURRENTURI).setValue(avTransInfo.getURI());
					action.getArgument(CURRENTURIMETADATA).setValue(avTransInfo.getURIMetaData());
					isActionSuccess = true;
				}
			}
			return false;
		}

		if (actionName.equals(PLAY)) {
			int instanceID = action.getArgument(INSTANCEID).getIntegerValue();
			int speed = action.getArgument(SPEED).getIntegerValue();
			if (speed == 1){
				this.mCurPlayState = TRANSITIONING;
			}
			play(getCurrentAvTransInfo().getURI(), getCurrentAvTransInfo().getURIMetaData());

			//action.getArgument(SPEED).getRelatedStateVariable().setValue(speed);
			/*synchronized (avTransInfoList) {
				AVTransportInfo avTransInfo = getCurrentAvTransInfo();
				if (avTransInfo != null) {
					Track track = avTransInfo.getCurrentTrack();
					if (mUIListener != null)
						mUIListener.onPlay(track, speed);
					action.getService().getStateVariable(TRANSPORTPLAYSPEED).setValue(speed);
					action.getService().getStateVariable(CURRENTTRACK).setValue(speed);
					action.getService().getStateVariable(CURRENTTRACKDURATION).setValue(speed);
					action.getService().getStateVariable(CURRENTTRACKMETADATA).setValue(speed);
					action.getService().getStateVariable(CURRENTTRACKURI).setValue();

							setStateVariable(TRANSPORTPLAYSPEED, speed);
					setStateVariable(CURRENTTRACK, String.valueOf(avTransInfo.get()));
					setStateVariable(CURRENTTRACKDURATION, String.valueOf(track.duration));
					setStateVariable(CURRENTTRACKMETADATA, track.metaData);
					setStateVariable(CURRENTTRACKURI, track.uri);
					updateStateVariable(action, TRANSPORTSTATE, PLAYING);
					isActionSuccess = true;
				}
			}*/
			this.mCurPlayState = PLAYING;
			isActionSuccess = true;
		}

		if (actionName.equals(STOP)) {
			int instanceID = action.getArgument(INSTANCEID).getIntegerValue();
			//change play state
			this.mCurPlayState = STOPPED;
			this.mCurrentTrack = 0;

			Message msg = Message.obtain();
			msg.what = HandlerController.STOP;
			DlnaApp.broadcastMessage(msg);
			isActionSuccess = true;
		}

		if (actionName.equals(PAUSE)) {
			int instanceID = action.getArgument(INSTANCEID).getIntegerValue();

			this.mCurPlayState = "PAUSED_PLAYBACK";
			Message msg = Message.obtain();
			msg.what = HandlerController.PAUSE;
			DlnaApp.broadcastMessage(msg);
			String value = "<Event xmlns=\"urn:schemas-upnp-org:metadata-1-0/AVT/\"><"
					+ "InstanceID val=\"" + instanceID + "\"><TransportState val=\""
					+ this.mCurPlayState + "\"/><CurrentTransportActions val=\"Pause\"/></InstanceID>"
					+ "</Event>";
			action.getService().getStateVariable("LastChange").setValue(value);
			isActionSuccess = true;
		}
		
		if (actionName.equals(GETMEDIAINFO)) {
			action.getArgument(NRTRACKS).setValue(0);
			action.getArgument(MEDIADURATION).setValue("00:00:00");
			action.getArgument(NEXTURI).setValue(NOT_IMPLEMENTED);
			action.getArgument(NEXTURIMETADATA).setValue(NOT_IMPLEMENTED);
			action.getArgument(RECORDMEDIUM).setValue(NOT_IMPLEMENTED);
			action.getArgument(WRITESTATUS).setValue(NOT_IMPLEMENTED);

			isActionSuccess = true;
		}

		if (actionName.equals(GETPOSITIONINFO)) {

			// if(action.getArgument("InstanceID").getValue().equals("1")){
			action.getArgument(TRACK).setValue(String.valueOf(this.mCurrentTrack));
			action.getArgument(TRACKDURATION).setValue(mCurTrackDuration);
			action.getArgument(TRACKURI).setValue(mCurrentTrackURI);
			action.getArgument(TRACKMETADATA).setValue(mCurrentTrackMetaData);
			action.getArgument(RELTIME).setValue(mRelativeTimePosition);
			action.getArgument(ABSTIME).setValue(mRelativeTimePosition);
			action.getArgument(RELCOUNT).setValue("2147483647");
			action.getArgument(ABSCOUNT).setValue("2147483647");
			// }
			// setAction(action);

			isActionSuccess = true;
		}

		if (actionName.equals(SEEK)){
			String unit = action.getArgumentValue("Unit");
			String seektoTime = action.getArgumentValue("Target");
			if(!unit.equals("REL_TIME") && !unit.equals("ABS_TIME") && !unit.equals("TRACK_NR")) {
				action.setStatus(710);
				return false;
			}

			if(!this.mCurPlayState.equals(STOPPED)) {
				Message msg = Message.obtain();
				msg.what = HandlerController.SEEK;
				Bundle bundle = new Bundle();
				bundle.putString("seek",seektoTime);
				msg.setData(bundle);
				DlnaApp.broadcastMessage(msg);
				return true;
			}
		}

		if (actionName.equals(GETDEVICECAPABILITIES)) {

			// if(action.getArgument("InstanceID").getValue().equals("1")){
			action.getArgument(PLAYMEDIA).setValue("NETWORK");
			action.getArgument(RECMEDIA).setValue(NOT_IMPLEMENTED);
			action.getArgument(RECQUALITYMODES).setValue(NOT_IMPLEMENTED);
			// }
			// setAction(action);

			isActionSuccess = true;
		}

		if (actionName.equals(GETTRANSPORTSETTINGS)) {

			// if(action.getArgument("InstanceID").getValue().equals("1")){
			action.getArgument(PLAYMODE).setValue(NORMAL);
			action.getArgument(RECQUALITYMODE).setValue(NOT_IMPLEMENTED);
			// }
			// setAction(action);

			isActionSuccess = true;
		}

		if (actionName.equals(GETTRANSPORTINFO)) {

			// if(action.getArgument("InstanceID").getValue().equals("1")){
			//action.getArgument(CURRENTTRANSPORTSTATE).setValue(PLAYING);
			//action.getArgument(CURRENTTRANSPORTSTATE).setValue(STOPPED);
			action.getArgument(CURRENTTRANSPORTSTATE).setValue(this.mCurPlayState);
			action.getArgument(CURRENTTRANSPORTSTATUS).setValue(OK);
			action.getArgument(CURRENTSPEED).setValue("1");
			// }
			// setAction(action);

			isActionSuccess = true;
		}
		/*
		if (actionName.equals(PREPARE_FOR_CONNECTION) == true) {
			action.getArgument(CONNECTION_ID).setValue(-1);
			action.getArgument(AV_TRNSPORT_ID).setValue(-1);
			action.getArgument(RCS_ID).setValue(-1);
			return true;
		}
		
		if (actionName.equals(CONNECTION_COMPLETE) == true) {
			return true;
		}
		
		if (actionName.equals(GET_CURRENT_CONNECTION_INFO) == true)
			return getCurrentConnectionInfo(action);
		
		
		if (actionName.equals(GET_CURRENT_CONNECTION_IDS) == true)
			return getCurrentConnectionIDs(action);
*/		

//		MediaRenderer dmr = getMediaRenderer();
//		if (dmr != null) {
//			ActionListener listener = dmr.getActionListener();
//			if (listener != null)
//				listener.actionControlReceived(action);
//		}
		
		return isActionSuccess;
	}

	////////////////////////////////////////////////
	// QueryListener
	////////////////////////////////////////////////

	public boolean queryControlReceived(StateVariable stateVar)
	{
		return false;
	}
}

