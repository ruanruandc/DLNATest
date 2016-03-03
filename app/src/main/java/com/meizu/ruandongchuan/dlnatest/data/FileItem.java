package com.meizu.ruandongchuan.dlnatest.data;

import org.cybergarage.upnp.std.av.server.object.ContentNode;
import org.cybergarage.xml.Node;

/**
 * Created by ruandongchuan on 2/29/16.
 */
public class FileItem extends ContentNode {
    @Override
    public void addContentNode(ContentNode node) {

    }

    @Override
    public boolean removeContentNode(ContentNode node) {
        return false;
    }

    @Override
    public boolean set(Node node) {
        return false;
    }
}
