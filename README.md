# LightController
Use Bluetooth to Controll Light's Brightness and Temperature
/app/main/java/com/example/lightcontroller目录下有一个文件+两个Package，其中MainActivity.java就是主界面所使用的代码。
### MainActivity.java
它包含对灯泡图片ImageView组件、底部导航栏、导航栏对应的四个Fragment的定义，该class中包含的方法/对象说明如下：
- serviceConnection:绑定到蓝牙服务；
- findViewByIds():所有组件都整合在这个方法中进行定位；
- initImageViews():灯泡图片明暗、（伪）色温的调节需要借助颜色矩阵ColorMatrix；在该方法中，原始图片和转化后的图片都被转化及定义为位图(Bitmap)，并实例化了各个对象；
- initFragments():导航栏对应四个Fragment，在该方法中实例化了四个fragment；因为需要首先连接蓝牙，因此在初始化时最先显示蓝牙连接的那一个fragment；
- bottomNavigationBarInit():底部导航栏的初始化；给导航栏设定了选中/未选中的颜色，并添加元素和对应的图片，同时添加监听，选中某个元素时切换到对应的fragment；
- switchFragment():切换fragment的方法；通过FragmentTransaction来添加及显示fragment；
- 定义了一个**Handler**:这个很重要，它通过接收各个fragment传入的信息来调整图片的显示状态，同时下发蓝牙指令；
- updateBulbState():根据当前的参数（开关状态、亮度、色温）来调整图片的显示状态，其中开关状态是改变图片的透明度(Alpha)，亮度就是改变图片的亮度，色温还没想好，初步打算改变色彩，让它看起来像是改变了色温；
- changeImageAlpha/changeImageBrightness/changeImageTemp(int param):它们都是对图片施加不同的颜色矩阵来改变图片的色彩；
