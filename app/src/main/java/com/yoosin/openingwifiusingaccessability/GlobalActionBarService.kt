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
    private var MODE_TALK_BACK_SCREEN: Boolean=false
    var mLayout: FrameLayout? = null
    private val TAG = "GlobalActionBarService"
    var wifiFunction = false
    var switchOn = false

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.e(TAG, "ACC::onAccessibilityEvent: " + (event?.getEventType() ?: "uknown"));
        //TYPE_WINDOW_STATE_CHANGED == 32
        if (event != null) {
            if (AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED == event
                    .getEventType()
                && wifiFunction
            ) {
                val nodeInfo = event!!.source
                Log.e(TAG, "ACC::onAccessibilityEvent: nodeInfo=$nodeInfo")
                if (nodeInfo == null) {
                    return
                }
                var list2 = nodeInfo.childCount
//                Log.e(TAG, "onAccessibilityEvent-final: "+goThroughChildren(nodeInfo)?.className )
                explore(nodeInfo,switchOn)

//                var list = nodeInfo
//                    .findAccessibilityNodeInfosByViewId("com.android.settings:id/left_button")
//                for (node in list) {
//                    Log.e(TAG, "ACC::onAccessibilityEvent: left_button $node")
////                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
//                }
//                list = nodeInfo
//                    .findAccessibilityNodeInfosByViewId("android:id/button1")
//                for (node in list) {
//                    Log.e(TAG, "ACC::onAccessibilityEvent: button1 $node")
////                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
//                }
            } else {
                Log.e(TAG, "onAccessibilityEvent: ${wifiFunction.toString()}")
            }
        } else {
            Log.e(TAG, "onAccessibilityEvent: event is null")
        }
    }

    private fun initializeButtons() {

        val magic2Button = mLayout!!.findViewById<View>(R.id.sOff) as Button
        val magicButton = mLayout!!.findViewById<View>(R.id.sOn) as Button
        magicButton.setOnClickListener {
            val i = Intent(Settings.ACTION_WIFI_SETTINGS)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            wifiFunction = true
            switchOn=true
            magicButton.isEnabled=false
            magic2Button.isEnabled=true
            startActivity(i);

        }

        magic2Button.setOnClickListener {
            val i = Intent(Settings.ACTION_WIFI_SETTINGS)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            wifiFunction = true
            switchOn=false
            magicButton.isEnabled=true
            magic2Button.isEnabled=false
            startActivity(i);

        }

    }

    fun goThroughChildren(nodeInfo: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        if (nodeInfo.childCount > 0) {
            for (i in 0 until nodeInfo.childCount) {
                val node = nodeInfo.getChild(i)
                Log.e(TAG, "onAccessibilityEvent-subChild: " + node.childCount)
                if (node.className.contains("Switch")) {
//                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
//                    if (node.isChecked) {
//                        Log.e(TAG, "onAccessibilityEvent: ischecked")
//                        node.isChecked = false
//                    } else {
//                        Log.e(TAG, "onAccessibilityEvent: isnotchecked")
//                        node.isChecked = true
//                    }
                    return node
                } else {
                    if (node.className.contains("TextView")&&node.text!=null&&node.text.contains("Wi-Fi")) {
                        node.parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        Log.e(TAG, "THIS IS WIFI " )
                        Log.e(TAG, "goThroughChildren-parent: "+node.parent?.className )
                        Log.e(TAG, "goThroughChildren--parent: "+node.parent?.parent?.className )
                        Log.e(TAG, "goThroughChildren-parent,children: "+node.parent?.childCount )
                    }
                    Log.e(TAG, "onAccessibilityEvent-not checkable: " + node.className)
                    if (node.childCount>0){
                        return goThroughChildren(node)
                    }
                }
            }
        } else {

            if (nodeInfo.className.contains("Switch")) {
//                if (nodeInfo.isChecked) {
//                    Log.e(TAG, "onAccessibilityEvent: ischecked")
//                    nodeInfo.isChecked = false
//                } else {
//                    Log.e(TAG, "onAccessibilityEvent: isnotchecked")
//                    nodeInfo.isChecked = true
//                }
                return nodeInfo
            } else {
                Log.e(TAG, "onAccessibilityEvent-not checkable: " + nodeInfo.className)
            }
        }
        return null
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
    private fun explore(view: AccessibilityNodeInfo,switchOn:Boolean) {
        if (!wifiFunction)
            return
        val count = view.childCount
        for (i in 0 until count) {
            val child = view.getChild(i)
            if (wifiFunction) {
                if (child!=null&&child.text != null && child.text.toString().toLowerCase()
                        .contains("wi-fi")&&child.className.contains("Switch")
                ) {
                    Log.e(TAG, "explore: "+child.className )
                    Log.e(TAG, "explore: "+child.text )
                    wifiFunction = false

                    if (child.isChecked&&!switchOn){
                            child.performAction(AccessibilityNodeInfo.ACTION_CLICK)

                performGlobalAction(GLOBAL_ACTION_HOME)
                    }else if (!child.isChecked&&switchOn){

                        child.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        performGlobalAction(GLOBAL_ACTION_HOME)
                    }else{

                        performGlobalAction(GLOBAL_ACTION_HOME)
                    }
                    Log.e(TAG, "explore: "+child.isChecked )
                }
            }
            explore(child,switchOn)
            child.recycle()
        }
    }

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