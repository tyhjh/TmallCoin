# 淘宝双十一领喵币

标签（空格分隔）： Android

---

![未标题-1.jpg-151kB](http://static.zybuluo.com/Tyhj/7tje3tzqh9mgze9oz70saxx4/%E6%9C%AA%E6%A0%87%E9%A2%98-1.jpg)

> 原文：https://www.jianshu.com/p/e8cc1f3731df

## 领喵币
淘宝双十一的领喵币活动又开始了，使用Android的辅助功能可以实现自动领喵币的功能；具体的效果可以下载下面的apk体验
> apk地址：http://share.tyhjh.com/share/TmallCoin.apk



## 功能
要做的功能就是自动点击按钮，完成这些只需要等待15秒就可以完成的任务；去年淘宝领喵币还比较简单，按钮的位置和功能都是固定的，只需要截图，然后判断固定位置的按钮的颜色是红色还是已完成的灰色，就可以点击按钮去另一个界面等待15秒后再返回，缺点就是只能适配一个分辨率的手机，并且今年按钮的位置不是固定的，我们需要区分哪些是可以通过脚本完成的，哪些是通过简单的脚本无法完成的


## 思路
具体的思路是后台开启服务（AccessibilityService），提供一个悬浮窗按钮，进入到领喵币中心以后，点击按钮开始对屏幕进行截屏；通过图像识别识别出按钮上的文字及位置，通过文字判断当前的任务是否是简单浏览就可以完成的，比如**去浏览、去逛逛，去搜索**等为简单操作，使用Android辅助功能进行模拟点击该位置，然后等待15秒后即可完成，然后再返回继续进行识别；但是比如**去完成**这样的按钮一般都是比较复杂的，难以通过脚本完成，需要避开；但是也可能**去完成**按钮的任务也是一个比较简单的浏览的任务，就需要一些技巧去判断；
> Android辅助功能建议阅读：https://www.jianshu.com/p/8935bde74c50


## 具体实现

在辅助服务（AccessibilityService）里面，开启线程循环截图进行图像识别，识别出所有的文字及位置；OcrResult类为识别出来的结果，包含文字结果和所在位置；文字识别使用的框架是PaddleOCR，也可以通过jitpack库进行引入这个封装的库，可以看实现的源码：https://github.com/tyhjh/WordsFind
```gradle
implementation 'com.github.tyhjh:WordsFind:v1.0.3'
```

第一次识别会对屏幕右方按钮部分的图像进行裁剪和识别，可以识别出大多数简单的操作由脚本去执行；但是有部分操作按钮显示为**去完成**，实际也是浏览任务的，在按钮的左侧有文字进行说明一般也是带有**浏览、逛逛**等文字，就需要在按钮部分识别完成并且未找到可执行的任务时，再次对全图进行查找，如果查找到匹配的文字就进行点击；


```java
//获取屏幕截屏
Bitmap bitmap = ScreenShotUtil.getInstance().getScreenShot();
//裁剪出识别区域，只识别按钮
Bitmap wordsBitmap = Bitmap.createBitmap(bitmap, startX, startY, (int) ((1 - START_X_SCALE) * width), (int) ((1 - START_Y_SCALE) * height));
//获取文字所在的区域
List<OcrResult> rectList = WordsFindManager.getInstance().runModel(wordsBitmap);
//遍历文字找到按钮进行点击
boolean isFindTxt = findBtn(rectList);
//还是没有可以点击的文字，识别全图
if (!isFindTxt) {
        //获取文字所在的区域
        rectList = WordsFindManager.getInstance().runModel(bitmap);
        //继续遍历文字进行点击
        boolean notFinished = findBtn(rectList);
    }
```

`findBtn`方法就是通过识别到的文字判断该任务是否可以执行，如果可以执行就进行点击；


```java
private static final List<String> btnTexts = Arrays.asList(new String[]{"去浏览", "去逛逛", "去搜索", "去观看"});
private boolean findBtn(List<OcrResult> rectList) {
        //识别到去浏览的按钮
        for (OcrResult result : rectList) {
            //获取识别的文字
            String txt = result.getTxt();
            //如果文字为可数组里面的文字，表示可以点击
            if (btnTexts.contains(txt)) {
                //获取喵币
                getCatCoin(startX, startY, result);
                return true;
            }

            //任务描述的文字处理，有字代表任务可以点
            if (txt.contains("浏览") || txt.contains("逛一逛")) {
                //判断该任务是不是已完成的任务
                if (notFinished(result)) {
                    //获取喵币
                    getCatCoin(startX, startY, result);
                    return true;
                }
            }
        }
        return false;
    }
```

获取喵币的代码还是比较简单的，就是点击按钮进入浏览的界面，然后等15秒就返回；因为淘宝页面加载等原因，等待的时间大于15秒才能完成任务
```java
//点击去浏览
clickOnScreen(rect.left + startX, rect.top + startY, 10, null);
//等待页面加载3秒+浏览18秒
SystemClock.sleep(22 * 1000);
//返回
performBackClick();
```

还有个问题如果文字不是在按钮上识别出来的，比如任务的描述文字包含**浏览**，但是其实这个任务其实已经完成了，如果识别不到这种情况就会一直点击该任务，所以会保存识别出的**已完成**文字位置，通过对比识别出来的描述文字**浏览**和**已完成**文字的Y坐标，判断是不是同一个任务，判断该任务是否已经完成
```java
for (Rect rect : rectListFinished) {
    //计算两个文字的顶部的距离
    int value = Math.abs(rect.top - result.getRect().top);
    //如果大于70像素，判断不是一个任务，该任务未被执行过
    if (value < 70) {
        return false;
    }
}
```

其中录屏截图框架使用的是：https://github.com/tyhjh/ScreenShot，也可以通过jitpack库进行引入
```
implementation 'com.github.tyhjh:ScreenShot:v1.0.2'
```
思路还是比较清晰的，图像识别找可以浏览完成的任务，定时浏览完成任务；代码也比较的简单，示例代码有所删减，详细的实现可以看源码；
> 项目地址：https://github.com/tyhjh/TmallCoin