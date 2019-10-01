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

### BluetoothViewer包
主要包含蓝牙连接界面中显示设备名称和地址的List的Adapter，还有就是蓝牙服务（Service）
- 因为Adapter的使用需要Getter，所以在BluetoothDeviceItem.java定义了蓝牙设备的name和address，然后设置了Setter和Getter；
- BluetoothListViewAdapter就是List的Adapter，没啥好说的；
- BluetoothServer是蓝牙服务（Service），Service是Android四大组件之一，它可以保持在后台运行而不受到前台Activity或者fragment之间切换的影响，只要它们和相应的Service绑定，就可以调用里边的方法和数据；onBind()/onStartCommand()/onUnbind()都是和绑定相关的；其他的方法功能作如下说明：
  - onCreate():包含蓝牙的初始化，获取BluetoothAdapter(蓝牙适配器)、启用BluetoothAdapter、开启扫描模式并允许该设备被发现；
  - getBondedBluetoothDevice():获取设备已配对的蓝牙设备信息，并返回一个map列表，name对应设备名，address对应设备蓝牙地址；
 
### fragmentSession包
顾名思义，就是四个fragment对应的class：SwitcherFragment是开关灯泡界面；BrightnessFragment是调节亮度；LuxFragment是调节色温（这里一开始的时候搞错了，lux应该是亮度那个，但是没办法，要改的话好多变量名字都得改，所以就适应一下吧）；ConnectFragment是连接设备的界面。
- Brightness和Lux的class类似，它们都定义了了一个SeekBar（进度条）来拖动改变数值，同时定义了长按数值显示来调出手动输入值的Dialog（在showValueChangeDialog()方法中）；每当发送数值的改变，都会调用updateValueToDevice(int value)来将亮度/色温通过handler传递到MainActivity（上文说了MainActivity中的Handler可以接收传过来的值）。
- Switcher中相对比较简单，就是按一下开，切换到调节亮度/色温的按钮生效（并变成黑色），按一下关就让它们失效（并变成灰色），在changeButtonState()方法中实现该功能；同时，开关的状态以及切换调节功能也会传递给MainActivity，这个就直接放在点击按钮后执行的onClick()方法中了。
- Connect还没做好，做好再说。
