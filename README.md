
##全埋点技术处理
###从点击事件直接处理
####方式一：静态代理View.OnClickListener
View->ListenerInfo->OnClickListener 递归遍历 通过反射每一个设置了onClickListener的控件，可以从android.R.id.content获取我们写的布局。
代码设置点击时间 xml设置  bufferknife设置的点击事件 DataBinding设置的点击事件
我们会在application的registerActivityLifecycleCallbacks的onActivityResumed里面去处理。
#####1. 因DataBinding给控件设置的点击时间比onActivityResumed稍晚，我们可以使用handler延时处理。
#####2. 如果是在onResume后设置的点击事件，我们可以拿到rootView后通过addOnGlobalLayoutListener监听去获取mOnClickListener。
####方式二：静态代理View.AccessibilityDelegate 需要开启辅助功能支持，局限比较大
 public boolean performClick() {
        notifyAutofillManagerOnClick();
        final boolean result;
        final ListenerInfo li = mListenerInfo;
        if (li != null && li.mOnClickListener != null) {
            playSoundEffect(SoundEffectConstants.CLICK);
            li.mOnClickListener.onClick(this);
            result = true;
        } else {
            result = false;
        }
        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);
        notifyEnterOrExitForAutoFillIfNeeded(true);
        return result;
    }
    public void sendAccessibilityEvent(int eventType) {
           if (mAccessibilityDelegate != null) {
               mAccessibilityDelegate.sendAccessibilityEvent(this, eventType);
           } else {
               sendAccessibilityEventInternal(eventType);
           }
 }
 点击事件处理的时候，会发送辅助事件，我们可以代理AccessibilityDelegate获得能力。
###从事件分发处理
####方式三：静态代理Window.Callback
  Window.Callback的dispatchTouchEvent(MotionEvent event)，在onActivityCreated方法里面去处理。
  activity.getWindow().getCallback()获取Window.Callback
  代理处理dispatchTouchEvent(MotionEvent event)，捕捉MotionEvent.ACTION_UP事件，基于decorView获取目的View
  #####目的View满足3个条件：
  1.按下的屏幕位置，View.getGlobalVisibleRect(rect);rect.contains(ev.getRawX(),ev.getRawY()),按下的点在View的范围内
  2.View是Visible的
  3.View是isClickable一定是true（一般来讲，未设置点击监听，那么isClickable是false 设置后就是true）
   public void setOnClickListener(@Nullable OnClickListener l) {
          if (!isClickable()) {
              setClickable(true);
          }
          getListenerInfo().mOnClickListener = l;
   }
 ####方式四：透明层处于页面的最上层，将elevation设置比较大的值
 透明层可以用自定义FrameLayout实现，处理onTouchEvent(MotionEvent ev),在其中获取目的View，如同方式三中的处理。
###以上方案都不能处理Dialog的点击事件。
 
  
 