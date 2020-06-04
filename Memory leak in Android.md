# Memory leak in Android

Java语言的核心特点之一就是是它的垃圾回收机制，从本质上讲我们只关心对象的创建和使用，至于对象的分配和释放都是虚拟机为我们处理，即使这样，如果使用不当仍然会发生内存泄漏的情况。

### What is a memory leak?

> **简单的说就是无法从内存中释放未使用的对象**，这意味着应用程序中有一些未使用的对象，垃圾回收器无法从内存中释放它们，因此内存单元将被占用，直到应用程序结束。

那么为什么会有对象无法释放？这里要提到几个知识点，内存、GC、GCRoot、以及标记清除算法，众所周知每个应用程序都需要内存作为资源来完成其工作。为了确保Android中的每个应用都有足够的内存，Android系统需要有效地管理内存分配。内存不足时，Android Davik会触发垃圾回收（GC）。GC的目的是通过清理不再有用的对象来回收内存。简单讲是通过三个步骤来实现

1. 从GCRoot遍历内存中的所有对象引用，并标记具有GCRoot引用的活动对象。
2. 所有未标记（垃圾）的对象将从内存中清除。
3. 重新排列活动对象

简言之，从内存中清除所有其他无用对象，保留有用的对象。但是，当编写代码的姿势不正确时，可访问对象会以某种方式引用未使用的对象，GC会将未使用的对象标记为有用的对象，因此将无法删除它们，这就是内存泄漏。

到这儿大家如果还不是很明白，没关系我们可以从基础开始，当然更详细的介绍推荐大家看《深入理解Java虚拟机》了解一下Java内存模型以及垃圾回收机制

#### 什么是RAM/内存？

这个问题问的就有点浮夸了，大家肯定都知道，RAM代表**随机访问内存**是Android设备/计算机中用于存储当前正在运行的应用程序及其数据的内存。接下来我们将讨论堆和堆栈，注意，堆和堆栈都存在RAM中的

#### 什么是堆和堆栈？

这里我就不展开说明了，简单说明一下，堆栈用于静态内存分配，而堆用于 动态内存分配。

![](https://docimg3.docs.qq.com/image/42pzaAwPjlZXZ16V4tPzKg?w=649&h=447)

#### 那么它在现实中到底是如何运作？

让我们用一个简单的程序了解堆栈和堆的用法。

```java
class Memory {
    public static void main(String[] args) { //line 1
        int i=1; //line 2
        Object obj = new Object(); //line3
        Memory memory = new Memory(); //line 4
        memory.foo(obj); //line 5
    }// line 9
    
    private void foo(Object param){ //line 6 
        String str = param.toString(); //line 7
        System.out.println(str);
    } //Line 8
}
```

我们看下程序的执行步骤

![](https://qqadapt.qpic.cn/txdocpic/0/157b19426e38da753f5c8abcaa617655/0?w=1073&h=569)

```java
public static void main(String[] args) { //line 1
```

**第1行** Java运行时创建main方法线程使用的堆栈内存

```java
int i=1; //line 2
```

**第2行** 创建一个局部变量，该变量已创建并存储在main方法的堆栈存储器中。

```java
Object obj = new Object(); //line3
```

**第3行** 在堆中创建一个新对象，在栈中创建一个变量obj，该变的引用（指针）指向对中的对象。

```java
Memory memory = new Memory(); //line 4
```

**第4行**与第3 **行**类似，在堆中创建一个新的Memory对象，该对象在栈中创建并存储在堆中，并且栈包含其引用。

```java
memory.foo(obj); //line 5
```

**第5行** 在栈中创建的栈帧，供foo方法使用。

```java
private void foo(Object param){ //line 6
```

**第6行** 在foo方法栈中创建的新对象，并引用堆中传递的对象。

```java
String str = param.toString(); //line 7
```

**第7行** 在堆中创建一个StringPool 的字符串对象 ，在栈中创建变量并引用该对象。

```java
} //Line 8
```

**第8行** foo方法结束，并且对象从foo方法的栈帧中释放

```java
}// line 9
```

**第9行** main方法结束，并且main方法的栈帧也会释放。

#### 方法完成后会发生什么？

每个函数都有自己的作用域，当函数执行完成后，在栈帧中创建的变量会自动释放并从栈中回收。

![](https://qqadapt.qpic.cn/txdocpic/0/832fac78227ef0ed810cd1d84bb8445d/0?w=1073&h=569)

<center>图1</center>

在**上图中**，`foo`完成该功能后，将自动释放/回收foo栈帧及其所有变量。

![](https://docimg1.docs.qq.com/image/mjF_madqN287wot-Zdl3ow?w=1073&h=569)

<center>图2</center>

与`foo`功能相同，当`main`功能结束时，该栈帧也被释放。

因此，现在我们已经了解到栈中的对象是临时的，并且在完成功能后将立即释放它们。

#### 那么堆呢？

堆与堆栈不同，函数完成后将不会自动对其进行回收。

为此，java设计了一个超级英雄，我们将其称为**垃圾回收器**，该**垃圾回收器**将关心检测并回收那些未使用的对象以在内存中获得更多的空间。

#### 垃圾回收器如何工作？

​	![](https://qqadapt.qpic.cn/txdocpic/0/9232184873aaa1e1e7c7abdf8b7f4291/0?w=1200&h=627)

垃圾回收器正在寻找无法访问的对象，换句话说，如果堆中有一个不包含任何对它的引用的对象，它将被释放

#### 真正的内存泄漏是如何发生的？

**当堆中仍有未使用的对象仍在栈中引用时**,为了直观的理解，下面是一个简单的视觉表示

​	![](https://qqadapt.qpic.cn/txdocpic/0/2b92c403dc2f18e690414ed483d2e622/0?w=650&h=400)



在这种情况下，垃圾收集器将永远不会释放内存。尽管这些对象不再使用，因为它们仍然被引用

### 内存泄漏是怎么引起的？

**导致内存泄漏的点可能有各种姿势，概况来说，主要就三类。**

1. Activity 引用泄漏到工作线程
2. 将Activity 上下文泄漏到静态引用中
3. 工作线程本身的泄漏



#### 1. Activity 引用泄漏到工作线程

我们来分析第一类Activity 引用泄漏到工作线程，另外内部类是内存泄漏的一个常见来源，因为内部类可以持有外部类的引用，那我们来看一个实例，创建一个线程，该线程在后台执行一个20秒的任务。

```java
public class ThreadActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_task);

        new DownloadTask(this).start();
    }
    
    private class DownloadTask extends Thread {
        
        Activity activity;
        
        public DownloadTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void run() {
            SystemClock.sleep(2000 * 10);
        }
    }
}
```

正如我们所知，内部类拥有对其容器类的引用，它将Activity 作为参数 传递给内部类，内部类持有activity的引用

那么我们分析一下这个类的堆栈执行情况

启动 ThreadActivity， ActivityThread 执行 Activity 创建流程 ，那么此时ThreadActivity 对象会在堆中创建，并且main方法中持有 activity的引用，接着执行到 onCreate方法 ，onCreate方法会在栈中创建一个新的栈帧压入栈中，然后在onCreate方法中我们创建了DownloadThread ，最后我们start DownloadThread onCreate方法执行结束 ，当线程启动后，run方法被执行，那么此时run方法创建的栈帧也会压入栈，在run方法中sleep 20秒

![](https://qqadapt.qpic.cn/txdocpic/0/35121c490b46db48cb1a9b053bfb5f3b/0?w=1148&h=636)

在正常情况下用户打开Activity 等待  20秒，直到完成下载任务。

![](https://qqadapt.qpic.cn/txdocpic/0/e5780abd8eef301fa591d85312851062/0?w=1112&h=610)

完成任务后,堆栈释放所有对象。

![img](https://qqadapt.qpic.cn/txdocpic/0/f25d4051d4f159523ce4c6799a4bf892/0?w=1112&h=610)

然后，下一次垃圾收集器工作时，堆中创建的DownloadTask对象被释放

![](https://qqadapt.qpic.cn/txdocpic/0/d0d0caf6bdbc9f05cbb2e6268230fccd/0?w=1112&h=610)

并且当用户关闭活动时，main方法将从堆栈中释放，ThreadActivity也将从堆中回收，一切都按需工作，没有泄漏。

![](https://qqadapt.qpic.cn/txdocpic/0/c1b48d8ebd5589fdaa7764137c2acf0e/0?w=1073&h=569)

然而，现实并没有想想的这么美好，如果当用户在10秒后把Activity关闭，那结果就炸了

![](https://qqadapt.qpic.cn/txdocpic/0/dcfbd65d2b5fc20e577ad38f3452442d/0?w=1112&h=610)

是不是已经明白了什么，Activity 结束后 main的栈帧从栈中弹出，但是，run方法还未执行完毕，DownloadTask中还持有Activty的引用，那么在堆中创建的Activity对象就不会被垃圾收集器回收，内存就这样泄漏了



**注意：**完成download run（）任务后，堆栈释放对象。因此，当垃圾回收器下次工作时，由于没有引用对象，最后对象从堆中回收。

![](https://qqadapt.qpic.cn/txdocpic/0/bc81206608c3f16804e0d418bb4b9952/0?w=1112&h=610)

### 

#### 2. 将Activity 上下文泄漏到静态引用中

**常见的就是单例类导致的内存泄漏** 该单例需要持有Context获取资源等工作，但是这样会导致内存泄漏

```java
public class SingletonManager {

    private static SingletonManager singleton;
    private Context context;

    private SingletonManager(Context context) {
        this.context = context;
    }

    public synchronized static SingletonManager getInstance(Context context) {
        if (singleton == null) {
            /**
             * 持有Activity context引用. Leak!!!
             * 该Activity对象存会保存在堆内存中，直到应用程序结束，内存泄漏的时间很长 要特别注意这样的泄漏
             * **/
            singleton = new SingletonManager(context);
        }
        return singleton;
    }

}
```

**什么时候发生的内存泄漏？**

在Activity初始化单例实例时，单例类会长期持有Activity的引用。

```java
public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_world);

        /**
         * 将Activity context 传递给单例类
         **/
        SingletonManager.getInstance(this);
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, LoginActivity.class);
        context.startActivity(starter);
    }

```

然后，单例类的生命周期和应用的生命周期一样长，在这里简单提一下静态方法和静态变量存储在方法区，GC一般只会清理堆上分配的内存。所以我劲量避免在方法区开销配大的内存，因为当方法区无法满足内存分配需求是，将抛出OutOfMemoryError 错误。



**如何避免单例泄漏？**

单例类持有application的上下文来避免这种泄漏

```java
SingletonManager.getInstance(getApplicationContext());
```



#### 3. 工作线程本身的泄漏

每次从Activity启动工作线程时，都要自己负责管理工作线程。因为工作线程的寿命可能比Activity更长，所以应该在Activity被销毁时正确地停止工作线程。如果忘记这样做，就有泄漏工作线程本身的风险



```java
public class ThreadActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_world);

        new DownloadTask().start();
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, ThreadActivity.class);
        context.startActivity(starter);
    }

    private class DownloadTask extends Thread {
        @Override
        public void run() {
          	/**
          	*注意如果关闭页面是在 10秒内完成的，那么工作线程的寿命就比Activity长，那么此时Activity的
          	**/
            SystemClock.sleep(2000 * 10);
        }
    }
}
```



```java
public class ThreadActivity1 extends Activity {

    /**
     * if the task done before to move to another activity
     * or rotate the device every thing will works fine with out leak.
     **/

    private DownloadTask thread = new DownloadTask();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_world);

        thread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        /**
         * Interrupts/stops this thread.
         * **/
        thread.interrupt();
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, ThreadActivity1.class);
        context.startActivity(starter);
    }

    /**
     * make it static so it does not have referenced to the containing activity class
     **/
    private static class DownloadTask extends Thread {
        @Override
        public void run() {
            while (!isInterrupted()) {
                SystemClock.sleep(2000 * 10);
            }
        }
    }
}
```



### Why care about memory leaks

内存泄漏不可小觑，你觉得泄漏又不会立刻崩溃，而且当前程序也不会造成什么太大影响，如果有这样的想法是不对的，我们要对自己写的代码有敬畏之心，避免编写任何可能导致内存泄漏的代码，并修复应用程序中现有的所有内存泄漏。内存泄漏后果真的很严重，如果不去排查和解决，可能会有以下几个方面严重性问题的产生。

#### 1. 卡顿



发生内存泄漏时，可用内存较少。结果，Android系统将触发更频发的GC事件。GC被触发这意味着 CPU时间片会被抢夺，那么就会导致UI线程所获得的时间片资源变少，Android有一个16ms的 drawing window。当GC花费的时间超过此时间时，Android将开始丢失帧。通常，100到200ms是阈值，超过此阈值，用户讲感觉到应用程序运行缓慢

![img](https://qqadapt.qpic.cn/txdocpic/0/a506f26e07651ad915513487acbbd4d6/0?w=425&h=197)

<center>Android Draw Window</center>

![img](https://qqadapt.qpic.cn/txdocpic/0/d15e9a1c7eec00fafe3dc594a6cfd493/0?w=401&h=253)

<center>由于频发的GC而丢失帧</center>

#### 2. ANR

内存泄漏可能导致ANR错误（应用程序无响应），为什么会发生ANR？

上文已经提到过内存泄漏导致内存不足，系统会频繁触发GC，大家都知道GC会导致CPU资源被占用，那么在Android中，Activity 响应超时时间是5s 如果由于频繁GC 导致主线程在5秒内没有响应输入事件（例如按键或屏幕触摸事件），那么Android系统将会弹出ANR的对话框，当然广播即是不是在UI上发生的，广播和service同样会产生ANR

![](https://qqadapt.qpic.cn/txdocpic/0/93ff65f96a9ccf415bdb5fb5b2935750/0?w=540&h=290)

我确定，任何一个Android用户都不喜欢看到这个界面，如果频发出现那真的要考虑卸载了。

#### 3. OOM

**什么是OOM?** OOM 全称“Out Of Memory”  来源java.lang.OutOfMemoryError，官方对OOM的解释是这样的，

当JVM因为没有足够的内存来为对象分配空间并且垃圾回收器也已经没有空间可回收时，就会抛出这个error（注：非exception，因为这个问题已经严重到不足以被应用处理意思就是无法catch）

上文中我们也提到过，发生内存泄漏会导致系统内存不足，因此，应用向Android系统申请更多的内存，但是每个应用可申请的内存是有限的，系统最终会拒绝为应用分配更多内存，当这种情况发生，就会抛出java.lang.OutOfMemoryError。

其实发生OOM不仅仅只有为内存泄漏导致，当然还有加载大图等，OOM 一般可以分为Java堆内存异常，Java永久代溢出，StackOverflow其实也属于内存溢出，他只是不会报OOM error，出现这种情况是以为方法递归太深无法分配更多的内存资源。



#### 4.泄漏的内存有多大

并非所有的内存泄漏都相等。有些泄漏了几千KB。有些可能会泄漏许多MB。这个一般由在内存中开辟内存的单元来决定，例如一个Activity 发生泄漏其实所占内存还是比较大的。

#### 5. 泄漏的对象在内存中保留多长时间

只要工作线程本身存在，工作线程中的某些泄漏就会继续存在。应该检查工作线程在最坏的情况下生存的时间。但是实际上，大多数工作线程都执行一些简单的任务，例如访问文件系统或进行网络调用，这些任务要么短暂，要么通常会设置超时。有些泄漏可能他的周期是和应用生命周期相同，这样就有点严重了，那么我们解决内存泄漏也要根据他的泄漏时间长短等因素按优先级来解决。

#### 6. 同一个泄漏源他可以泄漏多少垃圾

一些内存泄漏只会泄漏一个对象，有些泄漏可能会频繁泄漏无数个对象，解决内存泄漏也要把这一因素作为高低优先级的条件来解决。

### How to identify a leak and solved?

1. 如果是单例类必须要持有Context引用，最好不要保留对Activity Context的长期引用，通过传递applicaiton上下为来替换Activity上下文
2. 由Service导致的内存泄漏很多事因为没有正确stop所以建议用IntentServce 替代Service
3. 非静态内部类造成的泄漏是因为内部类持有了外部类的引用，而且内部类的生命周期大于外部类的生命周期，解决方案要不把非静态改成静态不去持有外部引用，所以我们要注意他的生命周期
4. Drawable对象的回调隐含的内存泄漏：当我们为某一个view设置背景的时候，view会在drawable对象上注册一个回调，所以drawable对象就拥有了该view的引用了，进而对整个context都有了间接的引用了，如果该drawable对象没有管理好，例如设置为静态，那么就会导致内存泄漏。所以避免用static 修饰Drawable并且要管理好期生命周期
5. 集合容器中的对象泄漏：将集合里的东西clear，然后置为null，再退出程序
6. 资源对象没关闭造成内存泄漏，在使用完及时调用他的close或destroy等
7. 监听器注册没注销造成内存泄漏，自己手动add的listener,要记得及时remove，或在对应的生命周期结束时unregister
8. 使用一些第三方依赖注入的框架时造成的泄漏，这个其实第三方框架都有提供反注销或其他对应的销毁方法，及时调用响应的销毁方法



即是我们在编码过程中小心翼翼，内存泄漏问题还是防不胜防，而且内存泄漏通常很难在质量检查/测试中找到，所以我们可以借助一些第三方工具，在调试环境下接入到APP内，当发生内存泄漏的时候可以提供更完整的泄漏的堆栈现场。解决内存泄漏可以通过工具来分析，最终要的还是避免编写会造成泄漏的代码，当然查找内存泄漏不仅是会使用工具，还要对GC工作原理有充分的了解。

#### 工具

1. Square的[LeakCanary](https://github.com/square/leakcanary)是检测应用程序内存泄漏的好工具，这个工具集成和使用都比较简单，就不在这里嗷述，不过可以阅读下LeakCanary的源码深入理解起设计思路和原理

   

   ![](https://docimg8.docs.qq.com/image/bA_YyoLlS4NzL9vjvMqkTw?w=1400&h=678)

2. Android Studio 也为我们提供了很多方便的工具，例如[Profiler](https://developer.android.com/studio/profile/android-profiler)使用Memory Profiler查看Java堆和内存分配 以及  Android Monitor等

   ![](https://qqadapt.qpic.cn/txdocpic/0/832d0292bf45b7680e74709a6e3ffc8c/0?w=871&h=576)





文中有分析的不对的地感谢积极指正。谢谢阅读，希望此文章可以帮助大家 ！

