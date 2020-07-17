# YCSeatView
#### 座位控件demo
- 01.如何支持滚动
- 02.座位控件实现
- 03.座位拖拽实现




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















