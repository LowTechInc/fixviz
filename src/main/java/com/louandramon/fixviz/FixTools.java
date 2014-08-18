package com.louandramon.fixviz;

import java.util.Iterator;
import java.util.List;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import quickfix.ConfigError;
import quickfix.DataDictionary;
import quickfix.Field;
import quickfix.FieldMap;
import quickfix.Group;
import quickfix.InvalidMessage;
import quickfix.fix50.Message;

public class FixTools
{
    public static final String DEFAULT_DATA_DICTIONARY = "FIX50.xml";

    private DataDictionary dataDictionary = null;
    private boolean showTag = true;

    public FixTools() throws ConfigError {
        dataDictionary = new DataDictionary(DEFAULT_DATA_DICTIONARY);
    }

    /*
    public static void main(String[] args) throws Exception {
        String fixMsg = "8=FIXT.1.1\u00019=464\u000135=8\u000149=BOOKING\u000152=20140807-04:59:00.349\u000156=STAR\u00011128=9\u000111=20191623\u000114=3000000\u000115=AUD\u000117=199e41e9-d2b8-4ca5-bd0c-e1046ca3db99\u000137=20191623\u000138=3000000\u000139=2\u000144=7.212971535\u000154=1\u000155=AUD/HKD\u000160=20140807-04:58:06.000\u000163=0\u000164=20140806\u000175=20140804\u0001150=F\u0001151=0\u0001198=SY1070814-9020191623-m1\u0001461=RCSXXX\u0001631=0\u000110108=0\u000110109=MUREX\u000110110=OBO\u000111080=0.00\u000178=1\u000179=5154113\u0001736=HKD\u0001737=21638914.605\u000180=3000000\u0001453=2\u0001448=5154113\u0001452=3\u0001448=JWEST\u0001452=12\u0001711=1\u0001311=AUD/HKD\u00011045=7.21297154\u000110=120\u0001";
        System.out.println("FixMsg String:"+fixMsg);

        Message fixMessage = new Message();
        DataDictionary dd = new DataDictionary("FIX50_NAB.xml");
        fixMessage.fromString(fixMsg, dd, false);
        System.out.println("FIXMessage Output:" + fixMessage.toString()); // Print message after parsing
        System.out.println("FIXMessage XML Output:" + fixMessage.toXML(dd)); // Print message after parsing
        System.out.println("========================================================================================\n");

        treeify(fixMessage.getHeader(), 0);
        System.out.println("========================================================================================\n");
        treeify(fixMessage, 0);
        System.out.println("========================================================================================\n");
        treeify(fixMessage.getTrailer(), 0);
    }
    */

    public JTree treeify(String fixStr) {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode("FIX Tree");
        JTree fixTree = new JTree(top);

        Message fixMessage = new Message();
        try {
            fixMessage.fromString(fixStr, dataDictionary, false);
        }
        catch (InvalidMessage err)    {
            top = new DefaultMutableTreeNode("Parse error");
            top.add(new DefaultMutableTreeNode(err.getMessage()));

            return fixTree;
        }

        DefaultMutableTreeNode header = new DefaultMutableTreeNode("Header");
        DefaultMutableTreeNode body = new DefaultMutableTreeNode("Body");
        DefaultMutableTreeNode trailer = new DefaultMutableTreeNode("Trailer");
        top.add(header);
        top.add(body);
        top.add(trailer);

        treeify(fixMessage.getHeader(), header);
        treeify(fixMessage, body);
        treeify(fixMessage.getTrailer(), trailer);

        return fixTree;
    }

    private void treeify(final FieldMap fieldMap, DefaultMutableTreeNode node) {
        final Iterator<Field<?>> fieldItr = fieldMap.iterator();
        while (fieldItr.hasNext()) {
            final Field<?> field = fieldItr.next();
            String nodeTxt;
            if (showTag) {
                String tagName = dataDictionary.getFieldName(field.getTag());
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

                treeify(group, groupNode);
            }

        }
    }

    public void setDataDictionary(String dataDictionaryFile) throws ConfigError {
        dataDictionary = new DataDictionary(dataDictionaryFile);
    }

    public void setShowTag(boolean b) {
        showTag = b;
    }

    /*
    private void treeify(final FieldMap fieldMap, int level) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i<=level - 1; i++)
            sb.append(" ");

        final Iterator<Field<?>> fieldItr = fieldMap.iterator();
        while (fieldItr.hasNext()) {
            final Field<?> field = fieldItr.next();
            System.out.println(sb.toString() + field.getTag() + "=" + field.getObject().toString());
        }

        final Iterator<Integer> groupKeyItr = fieldMap.groupKeyIterator();
        while (groupKeyItr.hasNext()) {
            final int groupKey = (groupKeyItr.next()).intValue();
            System.out.println(sb.toString() + groupKey + "=");

            final List<Group> groups = fieldMap.getGroups(groupKey);
            final Iterator<Group> groupItr = groups.iterator();
            while (groupItr.hasNext()) {
                final Group group = groupItr.next();
                treeify(group, level+1);
            }

        }
    }*/
}
