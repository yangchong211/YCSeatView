/*
Copyright 2017 yangchong211（github.com/yangchong211）

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.yc.ycseatview.lib;

import android.util.Log;

/**
 * <pre>
 *     @author yangchong
 *     blog  :
 *     time  : 2017/4/22
 *     desc  : 日志工具类
 * </pre>
 */
public final class SeatLogUtils {

    private static final String TAG = "SeatLogUtils";
    private static boolean mIsLog = true;

    public static void setLog(boolean isLog){
        mIsLog = isLog;
    }

    public static void d(String message) {
        if(mIsLog){
            Log.d(TAG, message);
        }
    }

    public static void i(String message) {
        if(mIsLog){
            Log.i(TAG, message);
        }

    }

    public static void e(String message) {
        if(mIsLog){
            Log.e(TAG, message);
        }
    }

    public static void e(String message, Throwable throwable) {
        if(mIsLog){
            Log.e(TAG, message, throwable);
        }
    }

}
