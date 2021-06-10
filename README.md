# YCSeatView
#### 座位控件demo
- 00.业务目标需求
- 01.如何支持滚动
- 02.座位控件实现
- 03.座位拖拽实现
- 04.座位控件截图
- 05.使用Map集合
- 06.如何使用lib
- 07.可能遇到问题




### 00.业务目标需求
- 1.设置行、列。通过行和列自动生成座位表
    - 1.1 正常座位的行、列等信息
    - 1.2 判断设置的座位总数是否能填下输入的所有信息（需要扣除不能坐的座位）
- 2.设置过道。过道是一列
    - 2.1 添加过道（支持多个）
    - 2.2 长按过道，可移动至座位表中的任何一列
- 3.添加调课位（无、最左侧、最右侧、最后排）。也就是可以添加竖直方向一列数据，也可以添加横向方向一行数据
    - 3.1 添加左侧调课位
    - 3.2 添加右侧调课位
    - 3.3 添加右侧调课位
- 4.暴露调课位信息（列、行等信息）
    - 4.1 点击座位抛出点击事件
    - 4.2 长按座位进入交换座位状态（座位晃动类编辑手机桌面 App 的特效）
    - 4.3 设置座位不可坐/可坐（解决实际教室中某个位置有柱子之类的）
    - 4.4 返回排座位信息
- 5.拖拽滑动
    - 5.1 座位表支持左右、上下滑动。注意是两两互换位置
    - 5.2 过道可以拖动
    - 5.3 一个item座位，可以拖到过道另一边的item上，比如3和11互换位置
        ```
        0   gd   5   10   15
        1        6   11   16
        2        7   12   17
        3        8   13   18
        4        9   14   19
        ```
- 其他
    - 支持重置
- 直接截屏
    - 需要设置recyclerView不复用
- 问题点




### 01.如何支持滚动
- 如何支持滚动
    - NestedScrollView + SeatHorizontalView
    - NestedScrollView支持竖直方向滚动视图，SeatHorizontalView支持横向滚动视图。
- 超出视图上下，左右滑动bug
    -



### 02.座位控件实现
- 座位控件
    - RecyclerView + GridLayoutManager【span，RecyclerView.HORIZONTAL】
    - 比如设置Column为4列，Line为5行。则设置span为5，每个item的span为1
- 座位数据是如何显示绘制
    - 比如设置4列，5行，关于数据如下。为何要这样设置。方便过道item可以占用一列。否则过道很难设置！
    ```
    0   5   10   15
    1   6   11   16
    2   7   12   17
    3   8   13   18
    4   9   14   19
    ```
- 如何设置过道
    - 过道其实就是一个item，只不过span占5
- 如何添加左边调课位
    - 只需要在该集合的前面添加元素即可
    ```
    x   0   5   10   15
    x   1   6   11   16
    x   2   7   12   17
    x   3   8   13   18
    x   4   9   14   19
    ```
- 如何添加右边调课位
    - 只需要在该集合的最后面添加元素即可
    ```
    0   5   10   15    x
    1   6   11   16    x
    2   7   12   17    x
    3   8   13   18    x
    4   9   14   19    x
    ```
- 如何添加后边调课位
    - 这个和添加左边一列，右边一列数据有所不同。
        - 竖直方向添加一列数据，只需要在list中添加数据后，刷新adapter即可。当前设置manager是GridLayoutManager(mContext, line)
        - 横向方向添加一行数据，需要重新设置manager，则是GridLayoutManager(mContext, line+1)
    - 思路：重新设置manager，同时修改数据索引
    ```
    //之前的数据
    0   5   10   15
    1   6   11   16
    2   7   12   17
    3   8   13   18
    4   9   14   19

    //添加后的数据
    0   6   12   18
    1   7   13   19
    2   8   14   20
    3   9   15   21
    4   10  16   22
    x5  x11 x17  x23
    ```



### 03.座位拖拽实现
- 自定义ItemTouchCallback + attachToRecyclerView
    - 在onMove回调方法中，可以拿到当前拖拽srcViewHolder，还可以拿到目标targetViewHolder
    ```
    // 更换数据源中的数据Item的位置
    Collections.swap(mList, srcPosition, mTargetPosition);
    // 更新UI中的Item的位置，主要是给用户看到交互效果
    seatTypeAdapter.notifyItemMoved(srcPosition, mTargetPosition);
    ```
- 存在很大的问题
    - 比如交换7，14的位置。则自身处理会逐个改变7到14之间的item(8,9,10,11,12,13)的位置，依次向前挪动一位。而目标要求是两两互换！
- 如何拖动过道
    - 过道是一个item，只不过它的span是5。如下图所示，gd是一个整体过道。
    - 往后拖动，如何交换一列数据，那么就是过道滑动到5，6，7，8，9任意一个位置，最终让其过道和9这个item交换位置。
    ```
    0   gd   5   10   15
    1        6   11   16
    2        7   12   17
    3        8   13   18
    4        9   14   19
    ```
    - 往前拖动，如何交换一列数据，那么就是过道滑动到10，11，12，13，14任意一个位置，最终让其过道和10这个item交换位置。
    ```
    0   5   10   gd   15
    1   6   11        16
    2   7   12        17
    3   8   13        18
    4   9   14        19
    ```



### 04.座位控件截图
- 座位控件
    - LinearLayout + NestedScrollView【竖直方向滚动】 + SeatHorizontalView【横向方向滚动】
    - 会超出屏幕，上下方向会超出屏幕，左右方向也会超出屏幕
    - 例如recyclerView展示30行时，此时屏幕绘制可能是10行，在未滑动到底部时，实际上后面的20行是没有draw出来的。【设置recyclerView不缓存，item全部展示出来】
- 截图思路
    - 1.先截取recyclerView控件截图。
        - a.拿到该adapter所有数据的数量
        - b.for循环遍历数据，然后获取到item。依次对item进行测量，绘制，获取每个item的bitmap存到集合中。注意item的高度是累加的【特别注意gridLayoutManager特殊处理】
        - c.通过上面循环得到的总高度，总宽度。用Bitmap.createBitmap创建一个大图用于展示控件视图，这个是容器
        - d.创建Canvas画板，拿到所有item的bitmap集合。然后遍历，依次绘制到容器中，注意摆放的位置
    - 2.将recyclerView控件截图保存起来
        - a.创建新activity，比如：LinearLayout【讲台 + ImageView】
        - b.将recyclerView控件截图填充到ImageView中。然后在针对当前activity的LinearLayout进行截图
        - c.新的截图应该就是座位图片。实际上这里分了两步操作
- 存在问题
    - 遍历recyclerView所有item，然后分别依次测量绘制item，然后绘制到bitmap上。这种截图有点难点，那就是如果item是瀑布流或者不过则的item，那么便会很困难
    - recyclerView默认是有缓存机制，也就是说比如item有15列，15行，那么在一个屏幕展示不下。这个时候有部分不可见的item实际上是没有draw出来的。
        - 此时获取控件的宽高，并不是所有item都显示的宽高。（因为有部分item存在复用）
    - 设置recyclerView不缓存，所有item都绘制出来，存在问题。
        ```
        //设置默认不回收
        viewHolder.setIsRecyclable(false);

        //屏幕外缓存
        //当列表滑动出了屏幕时，ViewHolder会被缓存在 mCachedViews ，其大小由mViewCacheMax决定，
        //默认DEFAULT_CACHE_SIZE为2，可通过Recyclerview.setItemViewCacheSize()动态设置。
        mRecyclerView.setItemViewCacheSize(225);


        //缓存池
        RecyclerView.RecycledViewPool recycledViewPool = new RecyclerView.RecycledViewPool();
        recycledViewPool.clear();
        recycledViewPool.setMaxRecycledViews(seatTypeAdapter.getItemViewType(0),225);
        mRecyclerView.setRecycledViewPool(recycledViewPool);

        //自定义缓存
        mRecyclerView.setViewCacheExtension(new RecyclerView.ViewCacheExtension() {
            @Nullable
            @Override
            public View getViewForPositionAndType(@NonNull RecyclerView.Recycler recycler, int i, int i1) {
                return null;
            }
        });
        ```
- 解决办法
    - 当RecyclerView界面绘制完成后，获取一个item的宽度和高度，然后知道几行几列。即可以计算出控件总的宽度和高度
    - 然后给RecyclerView动态设置控件总的宽度，总的高度，这样可以让RecyclerView所有item绘制出来。然后截图！
    - 最后注意一点：对RecyclerView的父控件进行测量获取宽高时，需要测量mode类型一定要设置成View.MeasureSpec.UNSPECIFIED



### 05.使用Map集合



### 06.如何使用lib
- 这里首先定义一个接口。接口中有这些抽象方法……
    ```
    public interface InterSeatView {

        /**
         * 添加过道
         */
        void addCorridor();

        /**
         * 恢复自动排座
         */
        void restoreSeat();

        /**
         * 更改座位布局
         */
        void changeSeat();

        /**
         * 添加调课位
         * @param type                          类型
         */
        void addTypeClass(@SeatConstant.SeatType int type);
    }
    ```
- 一行代码即可设置过道，调课位
    ```
    /**
     * 座位类型
     */
    @Retention(RetentionPolicy.SOURCE)
    public @interface SeatType {
        //正常
        int TYPE_1 = 1;
        //调课位
        int TYPE_2 = 2;
        //过道
        int TYPE_3 = 3;
        //不可坐
        int TYPE_4 = 4;
    }

    /**
     * 添加调课位座位类型
     */
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
        //左侧增加一列
        int TYPE_1 = 1;
        //右侧增加一列
        int TYPE_2 = 2;
        //后方增加一列
        int TYPE_3 = 3;
    }

    //左侧增加一列
    addTypeClass(SeatConstant.Type.TYPE_1);
    //右侧增加一列
    addTypeClass(SeatConstant.Type.TYPE_2);
    //后方增加一列
    addTypeClass(SeatConstant.Type.TYPE_3);

    /**
     * 添加调课位
     * @param type                          类型
     */
    private void addTypeClass(@SeatConstant.SeatType int type) {
        mSeatView.addTypeClass(type);
    }


    /**
     * 添加过道
     */
    private void addCorridor() {
        mSeatView.addCorridor();
    }

    /**
     * 恢复自动排座
     */
    private void restoreSeat() {
        mSeatView.restoreSeat();
    }

    /**
     * 更改座位布局
     */
    private void changeSeat() {
        mSeatView.changeSeat();
    }
    ```


### 07.可能遇到问题
- 截图太大如何避免OOM
    - 对图片进行压缩，内存压缩，质量压缩，缩放压缩。发现压缩后可以将截图4，5MB大小的图片压缩到几百kb图片，这样极大节省了内存……
    - BitmapFactory.Options.inSampleSize内存压缩，inJustDecodeBounds属性为true，可以在Bitmap不被加载到内存；然后计算缩放比，这样就会很大减少图片宽高呢，然后inJustDecodeBounds属性为false，加载出bitmap。
    - Bitmap.compress()质量压缩，不会对内存产生影响，它是在保持像素的前提下改变图片的位深及透明度等，来达到压缩图片的目的，不会减少图片的像素。
    - 图片占用的内存 = width * height * nTargetDensity/inDensity 一个像素所占的内存。具体压缩可以看demo
- 如何避免传递大图导致intent传递数据崩溃
    - 在Activity间使用Intent传递List含有大量序列化的对象的时候，或者传递较大bitmap等较大量数据的时候会引起页面卡顿。而且Android本身也限制了能够传递的数据大小在1MB左右。
    - 通过创建对象的缓存区解决intent传递大对象，不过需要注意在不用大对象的时候需要把对象设置成null
    ```
    public class ModelStorage {

        private Bitmap bitmap;

        public static ModelStorage getInstance(){
            return SingletonHolder.instance;
        }

        private static class SingletonHolder{
            private static final ModelStorage instance = new ModelStorage();
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }
    }
    ```










