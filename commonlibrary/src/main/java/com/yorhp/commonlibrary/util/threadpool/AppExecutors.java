package com.yorhp.commonlibrary.util.threadpool;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;

/**
 * Global executor pools for the whole application.
 * <p>
 * Grouping tasks like this avoids the effects of task starvation (e.g. disk reads don't wait behind
 * webservice requests).
 *
 * @author dhht
 */
public class AppExecutors {

    private final ExecutorService cpuThread;
    private final ExecutorService ioThread;
    private final Executor uiThread;
    private final ScheduledExecutorService mExecutorService;

    private AppExecutors() {
        cpuThread = new ThreadPoolExecutor(4,
                4,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new MyThreadFactory("DiskIoThreadExecutor"));

        ioThread = new ThreadPoolExecutor(
                0,
                Integer.MAX_VALUE,
                0L,
                TimeUnit.MILLISECONDS,
                new SynchronousQueue<Runnable>(),
                new MyThreadFactory("NetworkIOExecutor"));
        uiThread = new MainThreadExecutor();
        mExecutorService = new ScheduledThreadPoolExecutor(1, new MyThreadFactory("ScheduledExecutorService"));
    }

    public ExecutorService cpuThread() {
        return cpuThread;
    }

    public ExecutorService ioThread() {
        return ioThread;
    }

    public Executor uiThread() {
        return uiThread;
    }

    public ScheduledExecutorService scheduledExecutorService() {
        return mExecutorService;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }


    public static AppExecutors getInstance() {
        return Holder.sAppExecutors;
    }

    static class Holder {
        static AppExecutors sAppExecutors = new AppExecutors();
    }

}
