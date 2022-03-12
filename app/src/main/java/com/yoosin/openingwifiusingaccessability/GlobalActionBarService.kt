package com.yoosin.openingwifiusingaccessability

//import android.R

//import android.R
import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.content.Intent
import android.graphics.Path
import android.graphics.PixelFormat
import android.media.AudioManager
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Button
import android.widget.FrameLayout
import java.util.*


class GlobalActionBarService : AccessibilityService() {
    var mLayout: FrameLayout? = null
    private val TAG = "GlobalActionBarService"
    var wifiFunction = false
    var switchOn = false

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.e(TAG, "ACC::onAccessibilityEvent: " + (event?.getEventType() ?: "unknown"));
        val source = event!!.source
        /* if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && !event.getClassName().equals("android.app.AlertDialog")) { // android.app.AlertDialog is the standard but not for all phones  */
        /* if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && !event.getClassName().equals("android.app.AlertDialog")) { // android.app.AlertDialog is the standard but not for all phones  */
        if (event!!.eventType === AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && !java.lang.String.valueOf(
                event!!.className
            ).contains("AlertDialog")
        ) {
            return
        }
        if (event!!.eventType === AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED && (source == null || source.className != "android.widget.TextView")) {
            return
        }
        if (event!!.eventType === AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED && TextUtils.isEmpty(
                source!!.text
            )
        ) {
            return
        }

        val eventText: List<CharSequence>

        eventText = if (event!!.eventType === AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            event!!.text
        } else {
            Collections.singletonList(source!!.text)
        }

        val text: String = processUSSDText(eventText)

        if (TextUtils.isEmpty(text)) return

        // Close dialog

        // Close dialog
//        performGlobalAction(GLOBAL_ACTION_BACK) // This works on 4.1+ only


        Log.d(TAG, text)
        //TYPE_WINDOW_STATE_CHANGED == 32
//        if (event != null) {
//            if (AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED == event
//                    .getEventType()
//                && wifiFunction
//            ) {
//                val nodeInfo = event!!.source
//                Log.e(TAG, "ACC::onAccessibilityEvent: nodeInfo=$nodeInfo")
//                if (nodeInfo == null) {
//                    return
//                }
//                var list2 = nodeInfo.childCount
//                exploreHuawei(nodeInfo, switchOn)
////                Not working here
////                exploreInEmulator(nodeInfo, switchOn)
//
//            } else {
//                Log.e(TAG, "onAccessibilityEvent: ${wifiFunction.toString()}")
//            }
//        } else {
//            Log.e(TAG, "onAccessibilityEvent: event is null")
//        }
    }
    private fun processUSSDText(eventText: List<CharSequence>): String {
        Log.e(TAG, "processUSSDText: -----------------------------", )
        for (s in eventText) {
            Log.e(TAG, "processUSSDText: "+s )
            val text = s.toString()
            // Return text if text is the expected ussd response
            if (true) {
                return text
            }
        }
        return "null"
    }
    private fun initializeButtons() {

        val magic2Button = mLayout!!.findViewById<View>(R.id.sOff) as Button
        val magicButton = mLayout!!.findViewById<View>(R.id.sOn) as Button
        magicButton.setOnClickListener {
            val i = Intent(Settings.ACTION_WIFI_SETTINGS)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            wifiFunction = true
            switchOn = true
            magicButton.isEnabled = false
            magic2Button.isEnabled = true
            startActivity(i);

        }

        magic2Button.setOnClickListener {
            val i = Intent(Settings.ACTION_WIFI_SETTINGS)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            wifiFunction = true
            switchOn = false
            magicButton.isEnabled = true
            magic2Button.isEnabled = false
            startActivity(i);

        }

    }

    override fun onInterrupt() {
        TODO("Not yet implemented")
    }

    private fun configurePowerButton() {
        val powerButton: Button = mLayout!!.findViewById<View>(R.id.power) as Button
        powerButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                performGlobalAction(GLOBAL_ACTION_POWER_DIALOG)
//                performGlobalAction(GLOBAL_ACTION_HOME)

            }
        })
    }

    /**
     * Tested in Huawei
     * Android 10
     */
    private fun exploreHuawei(view: AccessibilityNodeInfo, switchOn: Boolean) {
        Log.e(TAG, "exploreHuawei: start")
        if (!wifiFunction)
            return
        val count = view.childCount
        for (i in 0 until count) {
            val child = view.getChild(i)
            if (wifiFunction) {
                if (child != null && child.text != null && (child.text.toString()
                        .lowercase(Locale.getDefault())
                        .contains("wi-fi") || child.text.toString().lowercase(Locale.getDefault())
                        .contains("on") || child.text.toString().lowercase(Locale.getDefault())
                        .contains("off")) && child.className.contains("Switch")
                ) {
                    Log.e(TAG, "exploreHuawei: " + child.className)
                    Log.e(TAG, "exploreHuawei: " + child.text)
                    wifiFunction = false

                    if (child.isChecked && !switchOn) {
                        child.performAction(AccessibilityNodeInfo.ACTION_CLICK)

                        performGlobalAction(GLOBAL_ACTION_HOME)
                    } else if (!child.isChecked && switchOn) {

                        child.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        performGlobalAction(GLOBAL_ACTION_HOME)
                    } else {

                        performGlobalAction(GLOBAL_ACTION_HOME)
                    }
                    Log.e(TAG, "exploreHuawei: " + child.isChecked)
                }
            }
            exploreHuawei(child, switchOn)
            child.recycle()
        }
    }

    /**
     * Tested in android emulator
     * Android 10
     */
//    private fun exploreInEmulator(view: AccessibilityNodeInfo, switchOn: Boolean) {
//        Log.e(TAG, "exploreInfinix: start" )
//        if (!wifiFunction)
//            return
//        val count = view.childCount
//        for (i in 0 until count) {
//            val child = view.getChild(i)
//            if (wifiFunction) {
//                Log.e(TAG, "exploreInfinix: " + child.className)
//                Log.e(TAG, "exploreInfinix: " + child.text)
//                if (child.className.contains("Switch")
//                ) {
//                    Log.e(TAG, "exploreInfinix: " + child.toString())
////                    Log.e(TAG, "exploreInfinix: " + child.text)
////                    wifiFunction = false
//
//                    if (child.isChecked && !switchOn) {
//                        Log.e(TAG, "exploreInfinix: try to click -ischecked & command not on", )
//                        var res=child.performAction(AccessibilityNodeInfo.ACTION_CLICK)
//                        Log.e(TAG, "exploreInEmulator clickResults:${res} ", )
//                        if (!res){
//                            child.isChecked=true
//                        }
//
////                        performGlobalAction(GLOBAL_ACTION_HOME)
//                    } else if (!child.isChecked && switchOn) {
//                        Log.e(TAG, "exploreInfinix: try to click2--isnotchecked & command on", )
//
//
//                        var res=child.performAction(AccessibilityNodeInfo.ACTION_CLICK)
//                        Log.e(TAG, "exploreInEmulator clickResults:${res} ", )
//                        if (!res){
//                            child.isChecked=true
//                        }
////                        performGlobalAction(GLOBAL_ACTION_HOME)
//                    } else {
//
//                        Log.e(TAG, "exploreInfinix: command same as current state Command-On:${switchOn}State-On:${child.isChecked}", )
//                        performGlobalAction(GLOBAL_ACTION_HOME)
//                    }
//                    Log.e(TAG, "explore: " + child.isChecked)
//                }else if(child.className.contains("TextView")&& child.text!=null&& child.text.toString()
//                        .lowercase(Locale.getDefault()).contains("use wi‑fi")
//                ){
//                    Log.e(TAG, "exploreInEmulator: "+child.toString() )
//                    var res=child.performAction(AccessibilityNodeInfo.ACTION_CLICK)
//                    Log.e(TAG, "exploreInEmulator clickResults:${res} ", )
//                    if (!res){
//                        child.isChecked=true
//                    }
//                }
//            }
//            exploreInEmulator(child, switchOn)
//            child.recycle()
//        }
//    }

    private fun configureVolumeButton() {
        val volumeUpButton = mLayout!!.findViewById<View>(R.id.volume_up) as Button
        volumeUpButton.setOnClickListener {
            val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
            audioManager.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI
            )
        }
    }

    private fun findScrollableNode(root: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        val deque: Deque<AccessibilityNodeInfo> = java.util.ArrayDeque()
        deque.add(root)
        while (!deque.isEmpty()) {
            val node: AccessibilityNodeInfo = deque.removeFirst()
            if (node.actionList.contains(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD)) {
                return node
            }
            for (i in 0 until node.childCount) {
                deque.addLast(node.getChild(i))
            }
        }
        return null
    }

    private fun configureScrollButton() {
        val scrollButton = mLayout!!.findViewById<View>(R.id.scroll) as Button
        scrollButton.setOnClickListener {
            val scrollable = findScrollableNode(rootInActiveWindow)
            scrollable?.performAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD.id)
        }
    }

    private fun configureSwipeButton() {
        val swipeButton = mLayout!!.findViewById<View>(R.id.swipe) as Button
        swipeButton.setOnClickListener {
            val swipePath = Path()
            swipePath.moveTo(10F, 10F)
            swipePath.lineTo(1500F, 1500F)
            val gestureBuilder = GestureDescription.Builder()
            gestureBuilder.addStroke(StrokeDescription(swipePath, 0, 1000))
            dispatchGesture(gestureBuilder.build(), object : GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription?) {
                    super.onCompleted(gestureDescription)
                    //clic
                }
            }, null)
        }
    }


    override fun onServiceConnected() {
        // Create an overlay and display the action bar
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        mLayout = FrameLayout(this)
        val lp = WindowManager.LayoutParams()
        lp.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
        lp.format = PixelFormat.TRANSLUCENT
        lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.TOP
        val inflater = LayoutInflater.from(this)
        inflater.inflate(R.layout.action_bar, mLayout)
        wm.addView(mLayout, lp)
        configurePowerButton()
        configureScrollButton()
        configureVolumeButton()
        configureSwipeButton()
        initializeButtons()

    }

}