package com.ibm.rqm;

/**
 * Created by Jack on 2015/4/6 0006.
 */
public class RQMConstant {
    public static final int PROJECTS = 0;
    public static final int TEST_CASE = 1;
    public static final int TEST_PLAN = 2;
    public static final int PROJECTS_UUID = 3;
    public static final int QUERY_UUID = 4;
    public static final int WORK_ITEM = 5;
    public static final int EXECUTION_RESULT = 6;

    public static final int CONNECT_FAILED = -1;
    public static final int NO_FRESH = -2;
    //public static final int SIZE = 5;
    private static int type;

    //四个主要报表的名字。
    public static final String ExecStatusByTC_cn = "执行状态（按测试用例类别排列，使用 TCER 计数，实时 ）";
    public static final String ExecStatusByOwner_cn = "执行状态（按所有者排列，使用 TCER 计数，实时）";
    public static final String ExecTrendReport_cn = "执行趋势报告";
    public static final String RequireCoverage_cn = "计划需求覆盖（按测试用例排列）";
    public static final String[] ReportNames = {ExecStatusByTC_cn, ExecStatusByOwner_cn,
            ExecTrendReport_cn, RequireCoverage_cn};


    public static String toString(int _type){
        RQMConstant.type = _type;
        String str = "";
        switch (type){
            case PROJECTS: str = "projects"; break;
            case TEST_CASE: str = "testcase"; break;
            case TEST_PLAN: str = "testplan"; break;
            case QUERY_UUID: str = "queryUUID"; break;
            case PROJECTS_UUID: str = "projectsUUID"; break;
            case WORK_ITEM: str = "workitem";break;
            case EXECUTION_RESULT: str = "executionresults";break;
            default:
                break;
        }
        return str;
    }

}

