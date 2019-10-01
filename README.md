# LightController
Use Bluetooth to Controll Light's Brightness and Temperature
/app/main/java/com/example/lightcontroller目录下有一个文件+两个Package，其中MainActivity.java就是主界面所使用的代码。
### MainActivity.java
它包含对灯泡图片ImageView组件、底部导航栏、导航栏对应的四个Fragment的定义，该class中包含的方法说明如下：
- findViewByIds():所有组件都整合在这个方法中进行定位；
- initImageViews():灯泡图片明暗、（伪）色温的调节需要借助颜色矩阵ColorMatrix；在该方法中，原始图片和转化后的图片都被转化及定义为位图(Bitmap)，并实例化了各个对象；
- initFragments():导航栏对应四个Fragment，在该方法中实例化了四个fragment；因为需要首先连接蓝牙，因此在初始化时最先显示蓝牙连接的那一个fragment；
- bottomNavigationBarInit():底部导航栏的初始化；给导航栏设定了选中/未选中的颜色，并添加元素和对应的图片，同时添加监听，选中某个元素时切换到对应的fragment；
- switchFragment():切换fragment
- initFragments():导航栏对应四个Fragment，在该方法中实例化了四个fragment；因为需要首先连接蓝牙，因此在初始化时最先显示蓝牙连接的那一个fragment；
