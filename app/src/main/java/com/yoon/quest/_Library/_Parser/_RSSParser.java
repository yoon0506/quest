package com.yoon.quest._Library._Parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by singleton on 2017. 7. 28..
 */

public class _RSSParser {
    protected XmlPullParserFactory mXmlFactory;
    protected XmlPullParser mXmlParser;
    protected int mEventType;

    public Listener listener = null;

    protected ArrayList<String> mTagStack = new ArrayList<>();

    public _RSSParser() {
        try {
            mXmlFactory = XmlPullParserFactory.newInstance();
            mXmlFactory.setNamespaceAware(true);
            mXmlParser = mXmlFactory.newPullParser();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addStack(String string) {
        mTagStack.add(string);
    }

    public String getLastStack() {
        if(mTagStack == null || mTagStack.size() == 0) return "";
        return mTagStack.get(mTagStack.size() - 1);
    }

    public String getStackFromLast(int backShiftIndex) {
        if(mTagStack.size() < backShiftIndex + 1) return "";
        return mTagStack.get(mTagStack.size() - (backShiftIndex + 1));
    }

    public String getStackFronFirst(int frontShiftIndex) {
        if(mTagStack.size() > frontShiftIndex) return "";
        return mTagStack.get(frontShiftIndex);
    }

    public void removeLastStack() {
        if(mTagStack.size() > 1) mTagStack.remove(mTagStack.size() - 1);
        else mTagStack = new ArrayList<>();
    }

    public Boolean checkChildOfParentList(String... objects) {
        if(mTagStack.size() != objects.length + 1) return false;
        for(Integer i = 0; i<objects.length; i++) {
            String mmStackValue = mTagStack.get(i);
            String mmParentValue = objects[i];
            if(!mmStackValue.equals(mmParentValue)) return false;
        }
        return true;
    }

    public Boolean checkElementHierachyList(String... objects) {
        if(mTagStack.size() != objects.length) return false;
        for(Integer i = 0; i<objects.length; i++) {
            String mmStackValue = mTagStack.get(i);
            String mmHierachyValue = objects[i];
            if(!mmStackValue.equals(mmHierachyValue)) return false;
        }
        return true;
    }

    public void parsingWithData(String dataStr) {
        try {
            mXmlParser.setInput(new StringReader(dataStr));
            mEventType = mXmlParser.getEventType();
            boolean isLoop = true;
            while (isLoop) {
                switch (mEventType) {
                    case XmlPullParser.START_DOCUMENT:
                        if(listener != null) listener.rssParserDidStart(this);
                        break;
                    case XmlPullParser.START_TAG:
                        addStack(mXmlParser.getName());
                        HashMap<String, String> mmAttrDic = null;
                        if(mXmlParser.getAttributeCount() > 0) mmAttrDic = new HashMap<>();
                        for(Integer i = 0; i<mXmlParser.getAttributeCount(); i++) {
                            String mmKey = mXmlParser.getAttributeName(i);
                            String mmValue = mXmlParser.getAttributeValue(i);
                            mmAttrDic.put(mmKey, mmValue);
                        }
                        if(listener != null) listener.rssParserDidStartElement(this, getLastStack(), mmAttrDic);
                        break;
                    case XmlPullParser.TEXT:
                        if(listener != null) listener.rssParserDidFoundString(this, mXmlParser.getText());
                        break;
                    case XmlPullParser.END_TAG:
                        if(listener != null) listener.rssParserDidEndElement(this, getLastStack());
                        removeLastStack();
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        isLoop = false;
                        if(listener != null) listener.rssParserDidEnd(this);
                        break;
                }
                mEventType = mXmlParser.next();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public interface Listener {
        public void rssParserDidStart(_RSSParser parser);
        public void rssParserDidStartElement(_RSSParser parser, String element, HashMap<String, String> attrDic);
        public void rssParserDidFoundString(_RSSParser parser, String string);
        public void rssParserDidEndElement(_RSSParser parser, String element);
        public void rssParserDidEnd(_RSSParser parser);
    }
}
