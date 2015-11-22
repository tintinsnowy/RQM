package com.ibm.rqm;

import android.content.Context;
import android.util.Xml;

import com.ibm.rqm.Model.Executionresult;
import com.ibm.rqm.Model.Project;
import com.ibm.rqm.Model.Report;
import com.ibm.rqm.Model.Testcase;
import com.ibm.rqm.Model.Testplan;
import com.ibm.rqm.Model.Workitem;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jack on 2015/4/6 0006.
 * 根据inputStream输入的xml解析为的对象数组。
 */
public class XmlParser {

    XmlPullParser mParser;

    public XmlParser() {
        //init mParse
        mParser = Xml.newPullParser();
    }



    public void test(Context context) throws IOException{
        InputStream is = context.getResources().getAssets().open("testcase.xml");
        StringBuilder builder = new StringBuilder();

        parseTestcaseXML(is);
        is = context.getResources().getAssets().open("testplan.xml");
        parseTestplanXML(is);

    }

    public ArrayList<Project> parseProjectXML(InputStream inputStream) {

        ArrayList<Project> projectList = null;
        Project project = null;
        try {
            mParser.setInput(inputStream, "UTF-8");
            int eventType = mParser.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT){
                String tagName = mParser.getName();
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:
                        projectList = new ArrayList<Project>();
                        break;
                    case XmlPullParser.START_TAG:
                        if(tagName.equals("project")){
                            project = new Project();
                        }else if(project != null && tagName.equals("identifier")){
                            project.setIdentifier(mParser.nextText());
                        }else if(project != null && tagName.equals("title")){
                            project.setTitle(mParser.nextText());
                        }else if(project != null && tagName.equals("description")){
                            project.setDescription(mParser.nextText());
                        }else if(project != null && tagName.equals("alias")){
                            project.setAlias(mParser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if(tagName.equals("project")){
                            projectList.add(project);
                            project = null;
                        }
                        break;
                    default:
                        break;
                }
                eventType = mParser.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

        return projectList;
    }

    public ArrayList<Testcase> parseTestcaseXML(InputStream inputStream){
        ArrayList<Testcase> testcaseList = null;
        Testcase testcase = null;
        try {
            mParser.setInput(inputStream, "UTF-8");
            int eventType = mParser.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT){
                String tagName = mParser.getName();
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:
                        testcaseList = new ArrayList<Testcase>();
                        break;
                    case XmlPullParser.START_TAG:
                        if(tagName.equals("entry")){
                            testcase = new Testcase();
                        }else if(testcase != null && tagName.equals("updated")){
                            testcase.setUpdated(mParser.nextText());
                        }else if(testcase != null && tagName.equals("title")){
                            testcase.setTitle(mParser.nextText());
                        }else if(testcase != null && tagName.equals("summary")){
                            testcase.setSummary(mParser.nextText());
                        }else if(testcase != null && tagName.equals("id")){
                            testcase.setId(mParser.nextText());
                        }

                        break;
                    case XmlPullParser.END_TAG:
                        if(tagName.equals("entry")){
                            testcaseList.add(testcase);
                            testcase = null;
                        }
                        break;
                    default:
                        break;
                }
                eventType = mParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        return testcaseList;
    }

    public ArrayList<Testplan> parseTestplanXML(InputStream inputStream){
        ArrayList<Testplan> testplanList = null;
        Testplan testplan = null;
        /*//更换左侧小标题----sherry
        ImageView tem =null;
        Drawable x= tem.getResources().getDrawable(R.drawable.icon1);*/

        try {
            mParser.setInput(inputStream, "UTF-8");
            int eventType = mParser.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT){
                String tagName = mParser.getName();
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:
                        testplanList = new ArrayList<Testplan>();
                        break;
                    case XmlPullParser.START_TAG:
                        if(tagName.equals("entry")){
                            testplan = new Testplan();
                            //Resources res = getResources();
                            //set image---sherry
                            //testplan.setImage(x);
                        }else if(testplan != null && tagName.equals("updated")){
                            testplan.setUpdated(mParser.nextText());
                        }else if(testplan != null && tagName.equals("title")){
                            testplan.setTitle(mParser.nextText());
                        }else if(testplan != null && tagName.equals("summary")){
                            testplan.setSummary(mParser.nextText());
                        }else if(testplan != null && tagName.equals("id")){
                            testplan.setId(mParser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if(tagName.equals("entry")){
                            testplanList.add(testplan);
                            testplan = null;
                        }
                        break;
                    default:
                        break;
                }
                eventType = mParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        return testplanList;
    }

    public ArrayList<Workitem> parseWorkItemXML(InputStream inputStream) {
        ArrayList<Workitem> workitemList = null;
        Workitem workitem = null;
        try {
            mParser.setInput(inputStream, "UTF-8");
            int eventType = mParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = mParser.getName();
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        workitemList = new ArrayList<Workitem>();
                        break;
                    case XmlPullParser.START_TAG:
                        if (tagname.equals("entry")) {
                            workitem = new Workitem();
                        } else if (workitem != null && tagname.equals("updated")) {
                            workitem.setUpdated(mParser.nextText());
                        } else if (workitem != null && tagname.equals("title")) {
                            workitem.setTitle(mParser.nextText());
                        } else if (workitem != null && tagname.equals("summary")) {
                            workitem.setSummary(mParser.nextText());
                        } else if (workitem != null && tagname.equals("id")) {
                            workitem.setId(mParser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (tagname.equals("entry")) {
                            workitemList.add(workitem);
                            workitem = null;
                        }
                        break;
                    default:
                        break;
                }
                eventType = mParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return workitemList;
    }

    public ArrayList<Executionresult> parseExecutionresultXML(InputStream inputStream){
        ArrayList<Executionresult> executionresultList = null;
        Executionresult executionresult = null;
        try{
            mParser.setInput(inputStream,"UTF-8");
            int eventType = mParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT){
                String tagname = mParser.getName();
                switch(eventType){
                    case XmlPullParser.START_DOCUMENT:
                        executionresultList = new ArrayList<Executionresult>();
                        break;
                    case XmlPullParser.START_TAG:
                        if (tagname.equals("entry")) {
                            executionresult = new Executionresult();
                        } else if (executionresult != null && tagname.equals("updated")) {
                            executionresult.setUpdated(mParser.nextText());
                        } else if (executionresult != null && tagname.equals("title")) {
                            executionresult.setTitle(mParser.nextText());
                        } else if (executionresult != null && tagname.equals("summary")) {
                            executionresult.setSummary(mParser.nextText());
                        } else if (executionresult != null && tagname.equals("id")) {
                            executionresult.setId(mParser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if(tagname.equals("entry")){
                            executionresultList.add(executionresult);
                            executionresult = null;
                        }
                        break;
                    default:
                        break;
                }
                eventType = mParser.next();
            }
        }catch (XmlPullParserException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return executionresultList;
    }

    public HashMap<String, String> parseProjectUUID(InputStream inputStream){
        HashMap<String, String> uuidMap = null;
        String name = "", uuid = "";

        try{
            mParser.setInput(inputStream, "UTF-8");
            int eventType = mParser.getEventType();
            int rootDepth = 0;
            while(eventType != XmlPullParser.END_DOCUMENT){
                String tagName = mParser.getName();
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:
                        uuidMap = new HashMap<String, String>();
                        break;
                    case XmlPullParser.START_TAG:
                        if(tagName.equals("values")){
                            rootDepth = mParser.getDepth();
                        }else if(tagName.equals("itemId") && mParser.getDepth() == rootDepth + 1){
                            uuid = mParser.nextText();
                        }else if(tagName.equals("name") && mParser.getDepth() == rootDepth + 1){
                            name = mParser.nextText();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if(tagName.equals("values")){
                            uuidMap.put(name, uuid);
                        }
                        break;
                    default:
                        break;
                }
                eventType = mParser.next();
            }
        } catch (XmlPullParserException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        return uuidMap;
    }

    public ArrayList<Report> parseReportXML(InputStream inputStream){
        ArrayList<Report> reportList = null;
        Report report = null;
        try{
            mParser.setInput(inputStream, "UTF-8");
            int eventType = mParser.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT){
                String tagName = mParser.getName();
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:
                        reportList = new ArrayList<Report>();
                        break;
                    case XmlPullParser.START_TAG:
                        if(tagName.equals("values")){
                            report = new Report();
                        }else if (tagName.equals("queryUUID")){
                            report.setQueryUUID(mParser.nextText());
                        }else if (tagName.equals("name")){
                            report.setName(mParser.nextText());
                        }else if (tagName.equals("description")){
                            report.setDescription(mParser.nextText());
                        }else if (tagName.equals("reportUUID")){
                            report.setReportUUID(mParser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if(tagName.equals("values")){
                            reportList.add(report);
                            report = null;
                        }
                        break;
                    default:
                        break;
                }
                eventType = mParser.next();
            }
        } catch (XmlPullParserException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        return reportList;
    }
}
