package com.ibm.rqm.utils;

import android.content.Context;

import com.ibm.rqm.AutoUpdater;
import com.ibm.rqm.Model.Report;

import java.util.List;
import java.util.Vector;

/**
 * ����ͼƬ����
 */
public class ImageUpdateListener implements AutoUpdater.UpdateListener{

    AutoUpdater.UpdateListener mListener;
    AutoUpdater mUpdater;
    boolean isUnavailable;
    Vector<Report> mList;
    Context mCtx;
    int curPos;
    public ImageUpdateListener(final Context context, final AutoUpdater.UpdateListener listener, List<Report> list, AutoUpdater updater) {
        mListener = listener;
        mList = new Vector<>(list);
        mUpdater = updater;
        curPos = 0;
        mCtx = context;
        isUnavailable = false;
    }

    public void start() {
        if (mList.size() > 0) {
            Report report = mList.get(curPos++);
            mUpdater.getReportPicture(report.getQueryUUID(), report.getName(), mCtx, this);
        } else {
            if (mListener != null)
                mListener.onUpdateFail(-1);
        }
    }


    @Override
    public void onUpdateSuccess() {
        mListener.onUpdateSuccess();
    }

    @Override
    public void onUpdateFail(int errorCode) {
        if (errorCode == -2) {
            isUnavailable = true;
        }
        if (curPos != mList.size()) {
            Report report = mList.get(curPos++);
            mUpdater.getReportPicture(report.getQueryUUID(), report.getName(), mCtx, this);
        } else {
            if (mListener != null)
                mListener.onUpdateFail(isUnavailable ? -2 : -1);
        }
    }
}
