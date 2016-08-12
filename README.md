Tracker
=======

Platform: Android Studio 2.0
----------------------------

## 获取Android签名证书的SHA1值的方式
由于百度开发指南中给出的获取方式是需要通过**手动**在命令行窗口中输入命令才可以得到，感觉没有必要这么麻烦，所以写了一个简单的批处理程序`keystore.bat`，只要双击运行即可。
    
    :: Platform: Windows
    cd /d c:\Users\%username%\.android\
    :: "keytool -help" for all available commands 
    :: "keytool -list -help" for usage of list
    keytool -list -v -keystore debug.keystore -storepass android
    :: wait for exit
    pause
    
## 百度地图支持的缩放级别
由于没有找到百度开发指南中的类似申明，故只能自己通过测试来验证并得到结果：

    //在BaiduMap.setMaxAndMinZoomLevel中有提到：
    //百度地图支持的放大级别范围为：[3, 21]
    float min = mBaiduMap.getMinZoomLevel();    //最小放大级别
    float max = mBaiduMap.getMaxZoomLevel();    //最大放大级别

|索引号|比例尺|缩放级别(float)|
|:-----:|:-----|:------:|
|1|5m|21|
|2|10m|20|
|3|	20m	|19|
|4|	50m|18|
|5|	100m|17|
|6|	200m|16|
|7|	500m|15|
|8|	1km|14|
|9|	2km|13|
|10|5km|12(default)|
|11|10km|11|
|12|20km|10|
|13|25km|9|
|14|50km|8|
|15|100km|7|
|16|200km|6|
|17|500km|5|
|18|1000km|	4|
|19|2000km|	3|

百度地图开发参考
========

*   百度官方的开发指南

1. [百度地图开放平台][1]
2. [Android地图SDK][2] & [类参考][21]
3. [Android定位SDK][3] & [类参考][31]
4. [Android导航SDK][4] & [类参考][41]
5. [鉴权认证机制 (AK/SK)][5]

*   友情推荐大神`hyman`学习路线

1. [百度地图在Android中的使用][6]
2. [Android 百度地图 SDK v3.0.0 （三） 添加覆盖物Marker与InfoWindow的使用][7]

[1]: http://lbsyun.baidu.com/ "点击访问"
[2]: http://lbsyun.baidu.com/index.php?title=androidsdk
[21]: http://wiki.lbsyun.baidu.com/cms/androidsdk/doc/v4_0_0/index.html
[3]: http://lbsyun.baidu.com/index.php?title=android-locsdk
[31]: http://wiki.lbsyun.baidu.com/cms/androidloc/doc/v7.0/index.html
[4]: http://lbsyun.baidu.com/index.php?title=android-navsdk
[41]: http://wiki.lbsyun.baidu.com/cms/androidnav/doc/v3_1_0/
[5]: https://cloud.baidu.com/doc/Reference/AuthenticationMechanism.html
[6]: http://www.imooc.com/learn/238
[7]: http://blog.csdn.net/lmj623565791/article/details/37737213
