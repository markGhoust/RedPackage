package com.fengping.ma.sixredpackage;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class RedPackageService extends AccessibilityService {
    private boolean isOpenRP = false;
    final static String TAG = "fengping.ma.test";
    private boolean isOpened = false;

    Handler handler = new Handler();
    private boolean isRedPackage;
    private boolean isClicked;
    private int n;

    public RedPackageService() {
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        Log.i(TAG, "recive Event: " + AccessibilityEvent.eventTypeToString(eventType));
        switch (eventType) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                Log.i(TAG, "recive notification");
                List<CharSequence> texts = event.getText();
                for (CharSequence text : texts) {
                    String content = text.toString();
                    if (!TextUtils.isEmpty(content)) {
                        if (content.contains("[微信红包]")) {
                            Log.i(TAG, "find RedPackage");
                            openWeChatPage(event);
                        }
                    }
                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                isRedPackage = false;
                String className = event.getClassName().toString();
                if ("com.tencent.mm.ui.LauncherUI".equals(className)) {
                    isOpenRP = false;
                    AccessibilityNodeInfo rootNode = getRootInActiveWindow();
                    findRedPackage(rootNode);
                }
                if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI".equals(className)) {
                    isRedPackage = true;
                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                className = event.getClassName().toString();

                Log.i(TAG, "recive content" + className + "isRedPackage :" + isRedPackage + "");
                if (isRedPackage) {
                    n = 2000;
                    List<AccessibilityWindowInfo> windowInfos = getWindows();
                    AccessibilityNodeInfo rootNodes;
                    if (windowInfos.size() == 1) {
                         rootNodes = windowInfos.get(0).getRoot();
                    }else {
                        rootNodes = windowInfos.get(1).getRoot();
                    }

                    Log.i(TAG, "get windows1"+windowInfos.size());
                    while (n > 0 && (rootNodes == null || rootNodes.getChild(0).getClassName().equals(LinearLayout.class.getName()))) {

                        Log.i(TAG, "get windows1"+windowInfos.size());
                        windowInfos= getWindows();
                        rootNodes =windowInfos.get(0).getRoot();
                        n--;
                    }
                    isRedPackage = false;
                    for (AccessibilityWindowInfo info: windowInfos
                         ) {
                        rootNodes = info.getRoot();
                        if (rootNodes != null) {
                            openRedPackege(rootNodes);
                        }
                    }

                } else if (isClicked) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    performGlobalAction(GLOBAL_ACTION_BACK);
                    isClicked = false;
                    isRedPackage = false;
                } else if (TextView.class.getName().equals(className) || FrameLayout.class.getName().equals(className)) {
                    isOpenRP = false;
                    n = 500;
                    List<AccessibilityWindowInfo> windowInfos = getWindows();
                    AccessibilityNodeInfo rootNode;
                    if (windowInfos.size() == 1) {
                        rootNode = windowInfos.get(0).getRoot();
                    }else {
                        rootNode = windowInfos.get(1).getRoot();
                    }
                    while (rootNode == null && n > 0) {
                        Log.i(TAG, "get windows2");
                        windowInfos = getWindows();
                        rootNode = windowInfos.get(0).getRoot();
                        n--;
                    }
                    for (AccessibilityWindowInfo info : windowInfos) {
                        rootNode = info.getRoot();
                        if (rootNode != null) {
                            findRedPackage(rootNode);
                        }
                    }
                }

                break;
        }
    }

    private void openRedPackege(AccessibilityNodeInfo rootNode) {
        for (int i = 0; i < rootNode.getChildCount(); i++) {

            AccessibilityNodeInfo node = rootNode.getChild(i);
            Log.i(TAG, "openredpackage   nodeisclick: " + node.isClickable() + node.getClassName());

            if (node.getClassName().equals(Button.class.getName())) {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                isClicked = true;
                isOpened = true;
                break;
            }
            openRedPackege(node);
        }
    }

    private void findRedPackage(AccessibilityNodeInfo rootNode) {
        if (rootNode != null) {
            Log.i(TAG, rootNode.getChildCount() + "     ");
            int test = rootNode.getChildCount();
            for (int i = test - 1; i >= 0; i--) {
                Log.i(TAG, "current:" + i);
                AccessibilityNodeInfo node = rootNode.getChild(i);
                if (node == null) {
                    continue;
                }
                if (node.getChildCount() > 0) {
                }
                CharSequence text = node.getText();
                Log.i(TAG, "node text " + text);
                if (text != null && text.toString().equals("领取红包")) {
                    Log.i(TAG, "find red package");
                    AccessibilityNodeInfo parent = node.getParent();
                    while (parent != null) {
                        if (parent.isClickable()) {
                            parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            isOpenRP = true;
                            break;
                        }
                    }
                }
                if (isOpenRP) {
                    break;
                } else {
                    findRedPackage(node);
                }
            }
        }
    }

    private void openWeChatPage(AccessibilityEvent event) {
        if (event.getParcelableData() != null && event.getParcelableData() instanceof Notification) {
            Notification notification = (Notification) event.getParcelableData();
            PendingIntent pendingIntent = notification.contentIntent;
            try {
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onInterrupt() {
        Log.i(TAG, "服务终止");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.i(TAG, "服务启动");
        String string = Settings.Secure.getString(getContentResolver(),
                "enabled_notification_listeners");
        Log.i(TAG, "is:" + string);
        if (string == null || !string.contains(RedPkgNotification.class.getName())) {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);


            startActivity(intent);
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "服务终止");
        Intent closeintent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        closeintent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        startActivity(closeintent);
        return super.onUnbind(intent);
    }
}
