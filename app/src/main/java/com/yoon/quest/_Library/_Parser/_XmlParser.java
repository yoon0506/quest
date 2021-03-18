package com.yoon.quest._Library._Parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by singleton on 2017. 7. 21..
 */

public class _XmlParser {
    protected XmlPullParserFactory mXmlFactory;
    protected XmlPullParser mXmlParser;
    protected String mError;
    protected int mEventType;

    public Listener listener = null;

    protected ArrayList<String> mTagStack = new ArrayList<>();

    public _XmlParser() {
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
            mError = "";
            mEventType = mXmlParser.getEventType();
            boolean isLoop = true;
            while (isLoop) {
                switch (mEventType) {
                    case XmlPullParser.START_DOCUMENT:
                        if(listener != null) listener.parserDidStart(this);
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
                        if(listener != null) listener.parserDidStartElement(this, getLastStack(), mmAttrDic);
                        break;
                    case XmlPullParser.TEXT:
                        if(listener != null) listener.parserDidFoundString(this, mXmlParser.getText());
                        break;
                    case XmlPullParser.END_TAG:
                        if(listener != null) listener.parserDidEndElement(this, getLastStack());
                        removeLastStack();
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        isLoop = false;
                        if(listener != null) listener.parserDidEnd(this);
                        break;
                }
                mEventType = mXmlParser.next();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    public interface Listener {
        public void parserDidStart(_XmlParser parser);
        public void parserDidStartElement(_XmlParser parser, String element, HashMap<String, String> attrDic);
        public void parserDidFoundString(_XmlParser parser, String string);
        public void parserDidEndElement(_XmlParser parser, String element);
        public void parserDidEnd(_XmlParser parser);
    }
}
