package com.lowtech.fixviz.controller;

import java.util.Iterator;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import quickfix.DataDictionary;
import quickfix.Field;
import quickfix.FieldMap;
import quickfix.Group;
import quickfix.fix50.Message;

import com.lowtech.fixviz.model.FixString;

public class FixTools
{
    private boolean showTag = true;

    public JTree treeify(FixString fixStr) {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode("FIX Tree");
        JTree fixTree = new JTree(top);

        DefaultMutableTreeNode header = new DefaultMutableTreeNode("Header");
        DefaultMutableTreeNode body = new DefaultMutableTreeNode("Body");
        DefaultMutableTreeNode trailer = new DefaultMutableTreeNode("Trailer");
        top.add(header);
        top.add(body);
        top.add(trailer);

        Message fixMessage = fixStr.toFixMessage();
        if (fixMessage != null) {
        	treeify(fixMessage.getHeader(), header, fixStr.getDictionary());
	        treeify(fixMessage, body, fixStr.getDictionary());
	        treeify(fixMessage.getTrailer(), trailer, fixStr.getDictionary());
        }

        return fixTree;
    }

    private void treeify(final FieldMap fieldMap, DefaultMutableTreeNode node, DataDictionary dictionary) {
        final Iterator<Field<?>> fieldItr = fieldMap.iterator();
        while (fieldItr.hasNext()) {
            final Field<?> field = fieldItr.next();
            String nodeTxt;
            if (showTag) {
                String tagName = dictionary.getFieldName(field.getTag());
                tagName = (tagName != null) ? tagName : "UNKNOWN";
                nodeTxt = field.getTag() + "(" + tagName + ")=" + field.getObject();
            }
            else {
                nodeTxt = field.getTag() + "=" + field.getObject();
            }

            node.add(new DefaultMutableTreeNode(nodeTxt));
        }

        final Iterator<Integer> groupKeyItr = fieldMap.groupKeyIterator();
        while (groupKeyItr.hasNext()) {
            final int groupKey = (groupKeyItr.next()).intValue();
            final List<Group> groups = fieldMap.getGroups(groupKey);

            final Iterator<Group> groupItr = groups.iterator();
            while (groupItr.hasNext()) {
                final Group group = groupItr.next();

                final DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(group.getFieldTag() + "=");
                node.add(groupNode);

                treeify(group, groupNode, dictionary);
            }
        }
    }

    public void setShowTag(boolean b) {
        showTag = b;
    }
}
