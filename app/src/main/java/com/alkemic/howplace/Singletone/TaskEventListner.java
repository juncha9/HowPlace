package com.alkemic.howplace.Singletone;

import android.os.AsyncTask;

public interface TaskEventListner {
    void OnTaskComplete(AsyncTask task);
    void OnTaskCanceled(AsyncTask task);
}
