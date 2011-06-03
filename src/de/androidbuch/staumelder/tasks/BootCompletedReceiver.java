package de.androidbuch.staumelder.tasks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.util.Log;

/**
 *
 * @author Arno Becker, 2009 visionera gmbh
 * 
 */
public class BootCompletedReceiver extends BroadcastReceiver {
  
  private static final String TAG = "Staumelder";
  
  static final String ACTION = "android.intent.action.BOOT_COMPLETED"; 

  @Override
  public void onReceive(Context ctxt, Intent intent) { 
    Log.d(TAG, "BootCompletedReceiver->onReceive(): entered...");
    if (intent.getAction().equals(ACTION)) {
      Log.d(TAG, "BootCompletedReceiver->onReceive(): PID: " + Process.myPid());  
    } 
  }
}
