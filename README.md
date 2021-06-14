
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
 ####方式五：AsectJ
配置会因为gradle插件的一些兼容问题会导致编译存在一定的坑，大家注意下。
目前使用gradle-6.7.1-bin.zip com.android.tools.build:gradle:4.2.1   classpath 'com.hujiang.aspectjx:gradle-android-plugin-aspectjx:2.0.10'
编译会报错：Execution failed for task ':app:dexBuilderDebug'.  > java.util.zip.ZipException: zip file is empty 
修改编译版本：gradle-5.1-all.zip  classpath "com.android.tools.build:gradle:3.2.0"  classpath 'com.hujiang.aspectjx:gradle-android-plugin-aspectjx:2.0.10'
可以打包成功。
#####aspectJ的一些关键使用：
1.先定义一个切面类(所谓的切面类就是将类上加上@Aspect注解)
2.进行方法hook适时处理方法增强。
  以下写法是一样的：
      @Pointcut("execution(* android.view.View.OnClickListener.onClick(android.view.View))")
      fun getTime(){
      }
      @Around("getTime()")
      fun getExecuteTime2(proceeding: ProceedingJoinPoint) {
          val startTime = System.currentTimeMillis();
          proceeding.proceed()
          val diff = System.currentTimeMillis() - startTime
          Log.e("ZeTaDot", "耗时---》$diff")
      }
  pk:
    @Around("execution(* android.view.View.OnClickListener.onClick(android.view.View))")
        fun getExecuteTime(proceeding: ProceedingJoinPoint) {
            val startTime = System.currentTimeMillis();
            proceeding.proceed()
            val diff = System.currentTimeMillis() - startTime
            Log.e("ZeTaDot", "耗时---》$diff")
     }
 两者的效果是一样的，注意Around的注解修饰的方法，参数是ProceedingJoinPoint，可以在方法体执行前后做一些操作。
 @After @Before @AfterReturning等都可以捕捉到相应位置
 3.切点表达式：execution 与call是不一样的，一个是在方法体前后做处理，一个是在方法调用前后做处理。
              修饰符- 返回值 -方法名 - 参数- 异常模式
              修饰符可以是注解
 这里针对直接代码的setOnClickListener使用aop很简单，如果是xml配置的onClick方法，可以创建一个注解，在onClick方法上添加这个注解，然后在切面类的切点方法的修饰符那里添加上注解@xxx即可。
 例如：@Around("execution(@butterknife.onClick * *(..))") 
 
 