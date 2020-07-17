package com.yc.ycseatview.lib;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchUIUtil;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.Interpolator;

import com.yc.ycseatview.R;

import java.util.ArrayList;
import java.util.List;

public class ItemTouchHelper2 extends RecyclerView.ItemDecoration implements RecyclerView.OnChildAttachStateChangeListener {
    public static final int UP = 1;
    public static final int DOWN = 2;
    public static final int LEFT = 4;
    public static final int RIGHT = 8;
    public static final int START = 16;
    public static final int END = 32;
    public static final int ACTION_STATE_IDLE = 0;
    public static final int ACTION_STATE_SWIPE = 1;
    public static final int ACTION_STATE_DRAG = 2;
    public static final int ANIMATION_TYPE_SWIPE_SUCCESS = 2;
    public static final int ANIMATION_TYPE_SWIPE_CANCEL = 4;
    public static final int ANIMATION_TYPE_DRAG = 8;
    private static final String TAG = "ItemTouchHelper";
    private static final boolean DEBUG = false;
    private static final int ACTIVE_POINTER_ID_NONE = -1;
    static final int DIRECTION_FLAG_COUNT = 8;
    private static final int ACTION_MODE_IDLE_MASK = 255;
    static final int ACTION_MODE_SWIPE_MASK = 65280;
    static final int ACTION_MODE_DRAG_MASK = 16711680;
    private static final int PIXELS_PER_SECOND = 1000;
    final List<View> mPendingCleanup = new ArrayList();
    private final float[] mTmpPosition = new float[2];
    RecyclerView.ViewHolder mSelected = null;
    float mInitialTouchX;
    float mInitialTouchY;
    private float mSwipeEscapeVelocity;
    private float mMaxSwipeVelocity;
    float mDx;
    float mDy;
    private float mSelectedStartX;
    private float mSelectedStartY;
    int mActivePointerId = -1;
    @NonNull
    ItemTouchHelper2.Callback mCallback;
    private int mActionState = 0;
    int mSelectedFlags;
    List<ItemTouchHelper2.RecoverAnimation> mRecoverAnimations = new ArrayList();
    private int mSlop;
    RecyclerView mRecyclerView;
    final Runnable mScrollRunnable = new Runnable() {
        public void run() {
            if (ItemTouchHelper2.this.mSelected != null && ItemTouchHelper2.this.scrollIfNecessary()) {
                if (ItemTouchHelper2.this.mSelected != null) {
                    ItemTouchHelper2.this.moveIfNecessary(ItemTouchHelper2.this.mSelected);
                }

                ItemTouchHelper2.this.mRecyclerView.removeCallbacks(ItemTouchHelper2.this.mScrollRunnable);
                ViewCompat.postOnAnimation(ItemTouchHelper2.this.mRecyclerView, this);
            }

        }
    };
    VelocityTracker mVelocityTracker;
    private List<RecyclerView.ViewHolder> mSwapTargets;
    private List<Integer> mDistances;
    private RecyclerView.ChildDrawingOrderCallback mChildDrawingOrderCallback = null;
    View mOverdrawChild = null;
    int mOverdrawChildPosition = -1;
    GestureDetectorCompat mGestureDetector;
    private ItemTouchHelper2.ItemTouchHelperGestureListener mItemTouchHelperGestureListener;
    private final RecyclerView.OnItemTouchListener mOnItemTouchListener = new RecyclerView.OnItemTouchListener() {
        public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent event) {
            ItemTouchHelper2.this.mGestureDetector.onTouchEvent(event);
            int action = event.getActionMasked();
            if (action == 0) {
                ItemTouchHelper2.this.mActivePointerId = event.getPointerId(0);
                ItemTouchHelper2.this.mInitialTouchX = event.getX();
                ItemTouchHelper2.this.mInitialTouchY = event.getY();
                ItemTouchHelper2.this.obtainVelocityTracker();
                if (ItemTouchHelper2.this.mSelected == null) {
                    ItemTouchHelper2.RecoverAnimation animation = ItemTouchHelper2.this.findAnimation(event);
                    if (animation != null) {
                        ItemTouchHelper2 var10000 = ItemTouchHelper2.this;
                        var10000.mInitialTouchX -= animation.mX;
                        var10000 = ItemTouchHelper2.this;
                        var10000.mInitialTouchY -= animation.mY;
                        ItemTouchHelper2.this.endRecoverAnimation(animation.mViewHolder, true);
                        if (ItemTouchHelper2.this.mPendingCleanup.remove(animation.mViewHolder.itemView)) {
                            ItemTouchHelper2.this.mCallback.clearView(ItemTouchHelper2.this.mRecyclerView, animation.mViewHolder);
                        }

                        ItemTouchHelper2.this.select(animation.mViewHolder, animation.mActionState);
                        ItemTouchHelper2.this.updateDxDy(event, ItemTouchHelper2.this.mSelectedFlags, 0);
                    }
                }
            } else if (action != 3 && action != 1) {
                if (ItemTouchHelper2.this.mActivePointerId != -1) {
                    int index = event.findPointerIndex(ItemTouchHelper2.this.mActivePointerId);
                    if (index >= 0) {
                        ItemTouchHelper2.this.checkSelectForSwipe(action, event, index);
                    }
                }
            } else {
                ItemTouchHelper2.this.mActivePointerId = -1;
                ItemTouchHelper2.this.select((RecyclerView.ViewHolder) null, 0);
            }

            if (ItemTouchHelper2.this.mVelocityTracker != null) {
                ItemTouchHelper2.this.mVelocityTracker.addMovement(event);
            }

            return ItemTouchHelper2.this.mSelected != null;
        }

        public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent event) {
            ItemTouchHelper2.this.mGestureDetector.onTouchEvent(event);
            if (ItemTouchHelper2.this.mVelocityTracker != null) {
                ItemTouchHelper2.this.mVelocityTracker.addMovement(event);
            }

            if (ItemTouchHelper2.this.mActivePointerId != -1) {
                int action = event.getActionMasked();
                int activePointerIndex = event.findPointerIndex(ItemTouchHelper2.this.mActivePointerId);
                if (activePointerIndex >= 0) {
                    ItemTouchHelper2.this.checkSelectForSwipe(action, event, activePointerIndex);
                }

                RecyclerView.ViewHolder viewHolder = ItemTouchHelper2.this.mSelected;
                if (viewHolder != null) {
                    switch (action) {
                        case 2:
                            ItemTouchHelper2.this.updateDxDy(event, ItemTouchHelper2.this.mSelectedFlags, activePointerIndex);
                            ItemTouchHelper2.this.mRecyclerView.invalidate();
                            break;
                        case 3:
                            if (ItemTouchHelper2.this.mVelocityTracker != null) {
                                ItemTouchHelper2.this.mVelocityTracker.clear();
                            }
                        case 1:
                            if (activePointerIndex >= 0) {
                                ItemTouchHelper2.this.moveIfNecessary(viewHolder);
                                ItemTouchHelper2.this.mRecyclerView.removeCallbacks(ItemTouchHelper2.this.mScrollRunnable);
                                ItemTouchHelper2.this.mScrollRunnable.run();
                                ItemTouchHelper2.this.mRecyclerView.invalidate();
                            }
                            ItemTouchHelper2.this.select((RecyclerView.ViewHolder) null, 0);
                            ItemTouchHelper2.this.mActivePointerId = -1;
                        case 4:
                        case 5:
                        default:
                            break;
                        case 6:
                            int pointerIndex = event.getActionIndex();
                            int pointerId = event.getPointerId(pointerIndex);
                            if (pointerId == ItemTouchHelper2.this.mActivePointerId) {
                                int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                                ItemTouchHelper2.this.mActivePointerId = event.getPointerId(newPointerIndex);
                                ItemTouchHelper2.this.updateDxDy(event, ItemTouchHelper2.this.mSelectedFlags, pointerIndex);
                            }
                    }

                }
            }
        }

        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            if (disallowIntercept) {
                ItemTouchHelper2.this.select((RecyclerView.ViewHolder) null, 0);
            }
        }
    };
    private Rect mTmpRect;
    private long mDragScrollStartTimeInMs;

    public ItemTouchHelper2(@NonNull ItemTouchHelper2.Callback callback) {
        this.mCallback = callback;
    }

    private static boolean hitTest(View child, float x, float y, float left, float top) {
        return x >= left && x <= left + (float) child.getWidth() && y >= top && y <= top + (float) child.getHeight();
    }

    public void attachToRecyclerView(@Nullable RecyclerView recyclerView) {
        if (this.mRecyclerView != recyclerView) {
            if (this.mRecyclerView != null) {
                this.destroyCallbacks();
            }

            this.mRecyclerView = recyclerView;
            if (recyclerView != null) {
                Resources resources = recyclerView.getResources();
                this.mSwipeEscapeVelocity = resources.getDimension(R.dimen.item_touch_helper_swipe_escape_velocity1);
                this.mMaxSwipeVelocity = resources.getDimension(R.dimen.item_touch_helper_swipe_escape_max_velocity1);
                this.setupCallbacks();
            }

        }
    }

    private void setupCallbacks() {
        ViewConfiguration vc = ViewConfiguration.get(this.mRecyclerView.getContext());
        this.mSlop = vc.getScaledTouchSlop();
        this.mRecyclerView.addItemDecoration(this);
        this.mRecyclerView.addOnItemTouchListener(this.mOnItemTouchListener);
        this.mRecyclerView.addOnChildAttachStateChangeListener(this);
        this.startGestureDetection();
    }

    private void destroyCallbacks() {
        this.mRecyclerView.removeItemDecoration(this);
        this.mRecyclerView.removeOnItemTouchListener(this.mOnItemTouchListener);
        this.mRecyclerView.removeOnChildAttachStateChangeListener(this);
        int recoverAnimSize = this.mRecoverAnimations.size();

        for (int i = recoverAnimSize - 1; i >= 0; --i) {
            ItemTouchHelper2.RecoverAnimation recoverAnimation = (ItemTouchHelper2.RecoverAnimation) this.mRecoverAnimations.get(0);
            this.mCallback.clearView(this.mRecyclerView, recoverAnimation.mViewHolder);
        }

        this.mRecoverAnimations.clear();
        this.mOverdrawChild = null;
        this.mOverdrawChildPosition = -1;
        this.releaseVelocityTracker();
        this.stopGestureDetection();
    }

    private void startGestureDetection() {
        this.mItemTouchHelperGestureListener = new ItemTouchHelper2.ItemTouchHelperGestureListener();
        this.mGestureDetector = new GestureDetectorCompat(this.mRecyclerView.getContext(), this.mItemTouchHelperGestureListener);
    }

    private void stopGestureDetection() {
        if (this.mItemTouchHelperGestureListener != null) {
            this.mItemTouchHelperGestureListener.doNotReactToLongPress();
            this.mItemTouchHelperGestureListener = null;
        }

        if (this.mGestureDetector != null) {
            this.mGestureDetector = null;
        }

    }

    private void getSelectedDxDy(float[] outPosition) {
        if ((this.mSelectedFlags & 12) != 0) {
            outPosition[0] = this.mSelectedStartX + this.mDx - (float) this.mSelected.itemView.getLeft();
        } else {
            outPosition[0] = this.mSelected.itemView.getTranslationX();
        }

        if ((this.mSelectedFlags & 3) != 0) {
            outPosition[1] = this.mSelectedStartY + this.mDy - (float) this.mSelected.itemView.getTop();
        } else {
            outPosition[1] = this.mSelected.itemView.getTranslationY();
        }

    }

    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        float dx = 0.0F;
        float dy = 0.0F;
        if (this.mSelected != null) {
            this.getSelectedDxDy(this.mTmpPosition);
            dx = this.mTmpPosition[0];
            dy = this.mTmpPosition[1];
        }

        this.mCallback.onDrawOver(c, parent, this.mSelected, this.mRecoverAnimations, this.mActionState, dx, dy);
    }

    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        this.mOverdrawChildPosition = -1;
        float dx = 0.0F;
        float dy = 0.0F;
        if (this.mSelected != null) {
            this.getSelectedDxDy(this.mTmpPosition);
            dx = this.mTmpPosition[0];
            dy = this.mTmpPosition[1];
        }

        this.mCallback.onDraw(c, parent, this.mSelected, this.mRecoverAnimations, this.mActionState, dx, dy);
    }

    void select(@Nullable RecyclerView.ViewHolder selected, int actionState) {
        if (selected != this.mSelected || actionState != this.mActionState) {
            this.mDragScrollStartTimeInMs = -9223372036854775808L;
            int prevActionState = this.mActionState;
            this.endRecoverAnimation(selected, true);
            this.mActionState = actionState;
            if (actionState == 2) {
                if (selected == null) {
                    throw new IllegalArgumentException("Must pass a ViewHolder when dragging");
                }

                this.mOverdrawChild = selected.itemView;
                this.addChildDrawingOrderCallback();
            }

            int actionStateMask = (1 << 8 + 8 * actionState) - 1;
            boolean preventLayout = false;
            if (this.mSelected != null) {
                final RecyclerView.ViewHolder prevSelected = this.mSelected;
                if (prevSelected.itemView.getParent() != null) {
                    final int swipeDir = prevActionState == 2 ? 0 : this.swipeIfNecessary(prevSelected);
                    this.releaseVelocityTracker();
                    float targetTranslateX;
                    float targetTranslateY;
                    switch (swipeDir) {
                        case 1:
                        case 2:
                            targetTranslateX = 0.0F;
                            targetTranslateY = Math.signum(this.mDy) * (float) this.mRecyclerView.getHeight();
                            break;
                        case 4:
                        case 8:
                        case 16:
                        case 32:
                            targetTranslateY = 0.0F;
                            targetTranslateX = Math.signum(this.mDx) * (float) this.mRecyclerView.getWidth();
                            break;
                        default:
                            targetTranslateX = 0.0F;
                            targetTranslateY = 0.0F;
                    }

                    byte animationType;
                    if (prevActionState == 2) {
                        animationType = 8;
                    } else if (swipeDir > 0) {
                        animationType = 2;
                    } else {
                        animationType = 4;
                    }

                    this.getSelectedDxDy(this.mTmpPosition);
                    float currentTranslateX = this.mTmpPosition[0];
                    float currentTranslateY = this.mTmpPosition[1];
                    ItemTouchHelper2.RecoverAnimation rv = new ItemTouchHelper2.RecoverAnimation(prevSelected, animationType, prevActionState, currentTranslateX, currentTranslateY, targetTranslateX, targetTranslateY) {
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (!this.mOverridden) {
                                if (swipeDir <= 0) {
                                    ItemTouchHelper2.this.mCallback.clearView(ItemTouchHelper2.this.mRecyclerView, prevSelected);
                                } else {
                                    ItemTouchHelper2.this.mPendingCleanup.add(prevSelected.itemView);
                                    this.mIsPendingCleanup = true;
                                    if (swipeDir > 0) {
                                        ItemTouchHelper2.this.postDispatchSwipe(this, swipeDir);
                                    }
                                }

                                if (ItemTouchHelper2.this.mOverdrawChild == prevSelected.itemView) {
                                    ItemTouchHelper2.this.removeChildDrawingOrderCallbackIfNecessary(prevSelected.itemView);
                                }

                            }
                        }
                    };
                    long duration = this.mCallback.getAnimationDuration(this.mRecyclerView, animationType, targetTranslateX - currentTranslateX, targetTranslateY - currentTranslateY);
                    rv.setDuration(duration);
                    this.mRecoverAnimations.add(rv);
                    rv.start();
                    preventLayout = true;
                } else {
                    this.removeChildDrawingOrderCallbackIfNecessary(prevSelected.itemView);
                    this.mCallback.clearView(this.mRecyclerView, prevSelected);
                }

                this.mSelected = null;
            }

            if (selected != null) {
                this.mSelectedFlags = (this.mCallback.getAbsoluteMovementFlags(this.mRecyclerView, selected) & actionStateMask) >> this.mActionState * 8;
                this.mSelectedStartX = (float) selected.itemView.getLeft();
                this.mSelectedStartY = (float) selected.itemView.getTop();
                this.mSelected = selected;
                if (actionState == 2) {
                    this.mSelected.itemView.performHapticFeedback(0);
                }
            }

            ViewParent rvParent = this.mRecyclerView.getParent();
            if (rvParent != null) {
                rvParent.requestDisallowInterceptTouchEvent(this.mSelected != null);
            }

            if (!preventLayout) {
                this.mRecyclerView.getLayoutManager().requestSimpleAnimationsInNextLayout();
            }

            this.mCallback.onSelectedChanged(this.mSelected, this.mActionState);
            this.mRecyclerView.invalidate();
        }
    }

    void postDispatchSwipe(final ItemTouchHelper2.RecoverAnimation anim, final int swipeDir) {
        this.mRecyclerView.post(new Runnable() {
            public void run() {
                if (ItemTouchHelper2.this.mRecyclerView != null && ItemTouchHelper2.this.mRecyclerView.isAttachedToWindow() && !anim.mOverridden && anim.mViewHolder.getAdapterPosition() != -1) {
                    RecyclerView.ItemAnimator animator = ItemTouchHelper2.this.mRecyclerView.getItemAnimator();
                    if ((animator == null || !animator.isRunning((RecyclerView.ItemAnimator.ItemAnimatorFinishedListener) null)) && !ItemTouchHelper2.this.hasRunningRecoverAnim()) {
                        ItemTouchHelper2.this.mCallback.onSwiped(anim.mViewHolder, swipeDir);
                    } else {
                        ItemTouchHelper2.this.mRecyclerView.post(this);
                    }
                }

            }
        });
    }

    boolean hasRunningRecoverAnim() {
        int size = this.mRecoverAnimations.size();

        for (int i = 0; i < size; ++i) {
            if (!((ItemTouchHelper2.RecoverAnimation) this.mRecoverAnimations.get(i)).mEnded) {
                return true;
            }
        }

        return false;
    }

    boolean scrollIfNecessary() {
        if (this.mSelected == null) {
            this.mDragScrollStartTimeInMs = -9223372036854775808L;
            return false;
        } else {
            long now = System.currentTimeMillis();
            long scrollDuration = this.mDragScrollStartTimeInMs == -9223372036854775808L ? 0L : now - this.mDragScrollStartTimeInMs;
            RecyclerView.LayoutManager lm = this.mRecyclerView.getLayoutManager();
            if (this.mTmpRect == null) {
                this.mTmpRect = new Rect();
            }

            int scrollX = 0;
            int scrollY = 0;
            lm.calculateItemDecorationsForChild(this.mSelected.itemView, this.mTmpRect);
            int curY;
            int topDiff;
            int bottomDiff;
            if (lm.canScrollHorizontally()) {
                curY = (int) (this.mSelectedStartX + this.mDx);
                topDiff = curY - this.mTmpRect.left - this.mRecyclerView.getPaddingLeft();
                if (this.mDx < 0.0F && topDiff < 0) {
                    scrollX = topDiff;
                } else if (this.mDx > 0.0F) {
                    bottomDiff = curY + this.mSelected.itemView.getWidth() + this.mTmpRect.right - (this.mRecyclerView.getWidth() - this.mRecyclerView.getPaddingRight());
                    if (bottomDiff > 0) {
                        scrollX = bottomDiff;
                    }
                }
            }

            if (lm.canScrollVertically()) {
                curY = (int) (this.mSelectedStartY + this.mDy);
                topDiff = curY - this.mTmpRect.top - this.mRecyclerView.getPaddingTop();
                if (this.mDy < 0.0F && topDiff < 0) {
                    scrollY = topDiff;
                } else if (this.mDy > 0.0F) {
                    bottomDiff = curY + this.mSelected.itemView.getHeight() + this.mTmpRect.bottom - (this.mRecyclerView.getHeight() - this.mRecyclerView.getPaddingBottom());
                    if (bottomDiff > 0) {
                        scrollY = bottomDiff;
                    }
                }
            }

            if (scrollX != 0) {
                scrollX = this.mCallback.interpolateOutOfBoundsScroll(this.mRecyclerView, this.mSelected.itemView.getWidth(), scrollX, this.mRecyclerView.getWidth(), scrollDuration);
            }

            if (scrollY != 0) {
                scrollY = this.mCallback.interpolateOutOfBoundsScroll(this.mRecyclerView, this.mSelected.itemView.getHeight(), scrollY, this.mRecyclerView.getHeight(), scrollDuration);
            }

            if (scrollX == 0 && scrollY == 0) {
                this.mDragScrollStartTimeInMs = -9223372036854775808L;
                return false;
            } else {
                if (this.mDragScrollStartTimeInMs == -9223372036854775808L) {
                    this.mDragScrollStartTimeInMs = now;
                }

                this.mRecyclerView.scrollBy(scrollX, scrollY);
                return true;
            }
        }
    }

    private List<RecyclerView.ViewHolder> findSwapTargets(RecyclerView.ViewHolder viewHolder) {
        if (this.mSwapTargets == null) {
            this.mSwapTargets = new ArrayList();
            this.mDistances = new ArrayList();
        } else {
            this.mSwapTargets.clear();
            this.mDistances.clear();
        }

        int margin = this.mCallback.getBoundingBoxMargin();
        int left = Math.round(this.mSelectedStartX + this.mDx) - margin;
        int top = Math.round(this.mSelectedStartY + this.mDy) - margin;
        int right = left + viewHolder.itemView.getWidth() + 2 * margin;
        int bottom = top + viewHolder.itemView.getHeight() + 2 * margin;
        int centerX = (left + right) / 2;
        int centerY = (top + bottom) / 2;
        RecyclerView.LayoutManager lm = this.mRecyclerView.getLayoutManager();
        int childCount = lm.getChildCount();

        for (int i = 0; i < childCount; ++i) {
            View other = lm.getChildAt(i);
            if (other != viewHolder.itemView && other.getBottom() >= top && other.getTop() <= bottom && other.getRight() >= left && other.getLeft() <= right) {
                RecyclerView.ViewHolder otherVh = this.mRecyclerView.getChildViewHolder(other);
                if (this.mCallback.canDropOver(this.mRecyclerView, this.mSelected, otherVh)) {
                    int dx = Math.abs(centerX - (other.getLeft() + other.getRight()) / 2);
                    int dy = Math.abs(centerY - (other.getTop() + other.getBottom()) / 2);
                    int dist = dx * dx + dy * dy;
                    int pos = 0;
                    int cnt = this.mSwapTargets.size();

                    for (int j = 0; j < cnt && dist > (Integer) this.mDistances.get(j); ++j) {
                        ++pos;
                    }

                    this.mSwapTargets.add(pos, otherVh);
                    this.mDistances.add(pos, dist);
                }
            }
        }

        return this.mSwapTargets;
    }

    void moveIfNecessary(RecyclerView.ViewHolder viewHolder) {
        if (!this.mRecyclerView.isLayoutRequested()) {
            if (this.mActionState == 2) {
                float threshold = this.mCallback.getMoveThreshold(viewHolder);
                int x = (int) (this.mSelectedStartX + this.mDx);
                int y = (int) (this.mSelectedStartY + this.mDy);
                if ((float) Math.abs(y - viewHolder.itemView.getTop()) >= (float) viewHolder.itemView.getHeight() * threshold || (float) Math.abs(x - viewHolder.itemView.getLeft()) >= (float) viewHolder.itemView.getWidth() * threshold) {
                    List<RecyclerView.ViewHolder> swapTargets = this.findSwapTargets(viewHolder);
                    if (swapTargets.size() != 0) {
                        RecyclerView.ViewHolder target = this.mCallback.chooseDropTarget(viewHolder, swapTargets, x, y);
                        if (target == null) {
                            this.mSwapTargets.clear();
                            this.mDistances.clear();
                        } else {
                            int toPosition = target.getAdapterPosition();
                            int fromPosition = viewHolder.getAdapterPosition();
                            if (this.mCallback.onMove(this.mRecyclerView, viewHolder, target)) {
                                this.mCallback.onMoved(this.mRecyclerView, viewHolder, fromPosition, target, toPosition, x, y);
                            }
                        }
                    }
                }
            }
        }
    }

    public void onChildViewAttachedToWindow(@NonNull View view) {
    }

    public void onChildViewDetachedFromWindow(@NonNull View view) {
        this.removeChildDrawingOrderCallbackIfNecessary(view);
        RecyclerView.ViewHolder holder = this.mRecyclerView.getChildViewHolder(view);
        if (holder != null) {
            if (this.mSelected != null && holder == this.mSelected) {
                this.select((RecyclerView.ViewHolder) null, 0);
            } else {
                this.endRecoverAnimation(holder, false);
                if (this.mPendingCleanup.remove(holder.itemView)) {
                    this.mCallback.clearView(this.mRecyclerView, holder);
                }
            }

        }
    }

    void endRecoverAnimation(RecyclerView.ViewHolder viewHolder, boolean override) {
        int recoverAnimSize = this.mRecoverAnimations.size();

        for (int i = recoverAnimSize - 1; i >= 0; --i) {
            ItemTouchHelper2.RecoverAnimation anim = (ItemTouchHelper2.RecoverAnimation) this.mRecoverAnimations.get(i);
            if (anim.mViewHolder == viewHolder) {
                anim.mOverridden |= override;
                if (!anim.mEnded) {
                    anim.cancel();
                }

                this.mRecoverAnimations.remove(i);
                return;
            }
        }

    }

    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.setEmpty();
    }

    void obtainVelocityTracker() {
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
        }

        this.mVelocityTracker = VelocityTracker.obtain();
    }

    private void releaseVelocityTracker() {
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }

    }

    private RecyclerView.ViewHolder findSwipedView(MotionEvent motionEvent) {
        RecyclerView.LayoutManager lm = this.mRecyclerView.getLayoutManager();
        if (this.mActivePointerId == -1) {
            return null;
        } else {
            int pointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
            float dx = motionEvent.getX(pointerIndex) - this.mInitialTouchX;
            float dy = motionEvent.getY(pointerIndex) - this.mInitialTouchY;
            float absDx = Math.abs(dx);
            float absDy = Math.abs(dy);
            if (absDx < (float) this.mSlop && absDy < (float) this.mSlop) {
                return null;
            } else if (absDx > absDy && lm.canScrollHorizontally()) {
                return null;
            } else if (absDy > absDx && lm.canScrollVertically()) {
                return null;
            } else {
                View child = this.findChildView(motionEvent);
                return child == null ? null : this.mRecyclerView.getChildViewHolder(child);
            }
        }
    }

    void checkSelectForSwipe(int action, MotionEvent motionEvent, int pointerIndex) {
        if (this.mSelected == null && action == 2 && this.mActionState != 2 && this.mCallback.isItemViewSwipeEnabled()) {
            if (this.mRecyclerView.getScrollState() != 1) {
                RecyclerView.ViewHolder vh = this.findSwipedView(motionEvent);
                if (vh != null) {
                    int movementFlags = this.mCallback.getAbsoluteMovementFlags(this.mRecyclerView, vh);
                    int swipeFlags = (movementFlags & '\uff00') >> 8;
                    if (swipeFlags != 0) {
                        float x = motionEvent.getX(pointerIndex);
                        float y = motionEvent.getY(pointerIndex);
                        float dx = x - this.mInitialTouchX;
                        float dy = y - this.mInitialTouchY;
                        float absDx = Math.abs(dx);
                        float absDy = Math.abs(dy);
                        if (absDx >= (float) this.mSlop || absDy >= (float) this.mSlop) {
                            if (absDx > absDy) {
                                if (dx < 0.0F && (swipeFlags & 4) == 0) {
                                    return;
                                }

                                if (dx > 0.0F && (swipeFlags & 8) == 0) {
                                    return;
                                }
                            } else {
                                if (dy < 0.0F && (swipeFlags & 1) == 0) {
                                    return;
                                }

                                if (dy > 0.0F && (swipeFlags & 2) == 0) {
                                    return;
                                }
                            }

                            this.mDx = this.mDy = 0.0F;
                            this.mActivePointerId = motionEvent.getPointerId(0);
                            this.select(vh, 1);
                        }
                    }
                }
            }
        }
    }

    View findChildView(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (this.mSelected != null) {
            View selectedView = this.mSelected.itemView;
            if (hitTest(selectedView, x, y, this.mSelectedStartX + this.mDx, this.mSelectedStartY + this.mDy)) {
                return selectedView;
            }
        }

        for (int i = this.mRecoverAnimations.size() - 1; i >= 0; --i) {
            ItemTouchHelper2.RecoverAnimation anim = (ItemTouchHelper2.RecoverAnimation) this.mRecoverAnimations.get(i);
            View view = anim.mViewHolder.itemView;
            if (hitTest(view, x, y, anim.mX, anim.mY)) {
                return view;
            }
        }

        return this.mRecyclerView.findChildViewUnder(x, y);
    }

    public void startDrag(@NonNull RecyclerView.ViewHolder viewHolder) {
        if (!this.mCallback.hasDragFlag(this.mRecyclerView, viewHolder)) {
            Log.e("ItemTouchHelper", "Start drag has been called but dragging is not enabled");
        } else if (viewHolder.itemView.getParent() != this.mRecyclerView) {
            Log.e("ItemTouchHelper", "Start drag has been called with a view holder which is not a child of the RecyclerView which is controlled by this ItemTouchHelper.");
        } else {
            this.obtainVelocityTracker();
            this.mDx = this.mDy = 0.0F;
            this.select(viewHolder, 2);
        }
    }

    public void startSwipe(@NonNull RecyclerView.ViewHolder viewHolder) {
        if (!this.mCallback.hasSwipeFlag(this.mRecyclerView, viewHolder)) {
            Log.e("ItemTouchHelper", "Start swipe has been called but swiping is not enabled");
        } else if (viewHolder.itemView.getParent() != this.mRecyclerView) {
            Log.e("ItemTouchHelper", "Start swipe has been called with a view holder which is not a child of the RecyclerView controlled by this ItemTouchHelper.");
        } else {
            this.obtainVelocityTracker();
            this.mDx = this.mDy = 0.0F;
            this.select(viewHolder, 1);
        }
    }

    ItemTouchHelper2.RecoverAnimation findAnimation(MotionEvent event) {
        if (this.mRecoverAnimations.isEmpty()) {
            return null;
        } else {
            View target = this.findChildView(event);

            for (int i = this.mRecoverAnimations.size() - 1; i >= 0; --i) {
                ItemTouchHelper2.RecoverAnimation anim = (ItemTouchHelper2.RecoverAnimation) this.mRecoverAnimations.get(i);
                if (anim.mViewHolder.itemView == target) {
                    return anim;
                }
            }

            return null;
        }
    }

    void updateDxDy(MotionEvent ev, int directionFlags, int pointerIndex) {
        float x = ev.getX(pointerIndex);
        float y = ev.getY(pointerIndex);
        this.mDx = x - this.mInitialTouchX;
        this.mDy = y - this.mInitialTouchY;
        if ((directionFlags & 4) == 0) {
            this.mDx = Math.max(0.0F, this.mDx);
        }

        if ((directionFlags & 8) == 0) {
            this.mDx = Math.min(0.0F, this.mDx);
        }

        if ((directionFlags & 1) == 0) {
            this.mDy = Math.max(0.0F, this.mDy);
        }

        if ((directionFlags & 2) == 0) {
            this.mDy = Math.min(0.0F, this.mDy);
        }

    }

    private int swipeIfNecessary(RecyclerView.ViewHolder viewHolder) {
        if (this.mActionState == 2) {
            return 0;
        } else {
            int originalMovementFlags = this.mCallback.getMovementFlags(this.mRecyclerView, viewHolder);
            int absoluteMovementFlags = this.mCallback.convertToAbsoluteDirection(originalMovementFlags, ViewCompat.getLayoutDirection(this.mRecyclerView));
            int flags = (absoluteMovementFlags & '\uff00') >> 8;
            if (flags == 0) {
                return 0;
            } else {
                int originalFlags = (originalMovementFlags & '\uff00') >> 8;
                int swipeDir;
                if (Math.abs(this.mDx) > Math.abs(this.mDy)) {
                    if ((swipeDir = this.checkHorizontalSwipe(viewHolder, flags)) > 0) {
                        if ((originalFlags & swipeDir) == 0) {
                            return ItemTouchHelper2.Callback.convertToRelativeDirection(swipeDir, ViewCompat.getLayoutDirection(this.mRecyclerView));
                        }

                        return swipeDir;
                    }

                    if ((swipeDir = this.checkVerticalSwipe(viewHolder, flags)) > 0) {
                        return swipeDir;
                    }
                } else {
                    if ((swipeDir = this.checkVerticalSwipe(viewHolder, flags)) > 0) {
                        return swipeDir;
                    }

                    if ((swipeDir = this.checkHorizontalSwipe(viewHolder, flags)) > 0) {
                        if ((originalFlags & swipeDir) == 0) {
                            return ItemTouchHelper2.Callback.convertToRelativeDirection(swipeDir, ViewCompat.getLayoutDirection(this.mRecyclerView));
                        }

                        return swipeDir;
                    }
                }

                return 0;
            }
        }
    }

    private int checkHorizontalSwipe(RecyclerView.ViewHolder viewHolder, int flags) {
        if ((flags & 12) != 0) {
            int dirFlag = this.mDx > 0.0F ? 8 : 4;
            float threshold;
            if (this.mVelocityTracker != null && this.mActivePointerId > -1) {
                this.mVelocityTracker.computeCurrentVelocity(1000, this.mCallback.getSwipeVelocityThreshold(this.mMaxSwipeVelocity));
                threshold = this.mVelocityTracker.getXVelocity(this.mActivePointerId);
                float yVelocity = this.mVelocityTracker.getYVelocity(this.mActivePointerId);
                int velDirFlag = threshold > 0.0F ? 8 : 4;
                float absXVelocity = Math.abs(threshold);
                if ((velDirFlag & flags) != 0 && dirFlag == velDirFlag && absXVelocity >= this.mCallback.getSwipeEscapeVelocity(this.mSwipeEscapeVelocity) && absXVelocity > Math.abs(yVelocity)) {
                    return velDirFlag;
                }
            }

            threshold = (float) this.mRecyclerView.getWidth() * this.mCallback.getSwipeThreshold(viewHolder);
            if ((flags & dirFlag) != 0 && Math.abs(this.mDx) > threshold) {
                return dirFlag;
            }
        }

        return 0;
    }

    private int checkVerticalSwipe(RecyclerView.ViewHolder viewHolder, int flags) {
        if ((flags & 3) != 0) {
            int dirFlag = this.mDy > 0.0F ? 2 : 1;
            float threshold;
            if (this.mVelocityTracker != null && this.mActivePointerId > -1) {
                this.mVelocityTracker.computeCurrentVelocity(1000, this.mCallback.getSwipeVelocityThreshold(this.mMaxSwipeVelocity));
                threshold = this.mVelocityTracker.getXVelocity(this.mActivePointerId);
                float yVelocity = this.mVelocityTracker.getYVelocity(this.mActivePointerId);
                int velDirFlag = yVelocity > 0.0F ? 2 : 1;
                float absYVelocity = Math.abs(yVelocity);
                if ((velDirFlag & flags) != 0 && velDirFlag == dirFlag && absYVelocity >= this.mCallback.getSwipeEscapeVelocity(this.mSwipeEscapeVelocity) && absYVelocity > Math.abs(threshold)) {
                    return velDirFlag;
                }
            }

            threshold = (float) this.mRecyclerView.getHeight() * this.mCallback.getSwipeThreshold(viewHolder);
            if ((flags & dirFlag) != 0 && Math.abs(this.mDy) > threshold) {
                return dirFlag;
            }
        }

        return 0;
    }

    private void addChildDrawingOrderCallback() {
        if (Build.VERSION.SDK_INT < 21) {
            if (this.mChildDrawingOrderCallback == null) {
                this.mChildDrawingOrderCallback = new RecyclerView.ChildDrawingOrderCallback() {
                    public int onGetChildDrawingOrder(int childCount, int i) {
                        if (ItemTouchHelper2.this.mOverdrawChild == null) {
                            return i;
                        } else {
                            int childPosition = ItemTouchHelper2.this.mOverdrawChildPosition;
                            if (childPosition == -1) {
                                childPosition = ItemTouchHelper2.this.mRecyclerView.indexOfChild(ItemTouchHelper2.this.mOverdrawChild);
                                ItemTouchHelper2.this.mOverdrawChildPosition = childPosition;
                            }

                            if (i == childCount - 1) {
                                return childPosition;
                            } else {
                                return i < childPosition ? i : i + 1;
                            }
                        }
                    }
                };
            }

            this.mRecyclerView.setChildDrawingOrderCallback(this.mChildDrawingOrderCallback);
        }
    }

    void removeChildDrawingOrderCallbackIfNecessary(View view) {
        if (view == this.mOverdrawChild) {
            this.mOverdrawChild = null;
            if (this.mChildDrawingOrderCallback != null) {
                this.mRecyclerView.setChildDrawingOrderCallback((RecyclerView.ChildDrawingOrderCallback) null);
            }
        }

    }

    private static class RecoverAnimation implements Animator.AnimatorListener {
        final float mStartDx;
        final float mStartDy;
        final float mTargetX;
        final float mTargetY;
        final RecyclerView.ViewHolder mViewHolder;
        final int mActionState;
        private final ValueAnimator mValueAnimator;
        final int mAnimationType;
        boolean mIsPendingCleanup;
        float mX;
        float mY;
        boolean mOverridden = false;
        boolean mEnded = false;
        private float mFraction;

        RecoverAnimation(RecyclerView.ViewHolder viewHolder, int animationType, int actionState, float startDx, float startDy, float targetX, float targetY) {
            this.mActionState = actionState;
            this.mAnimationType = animationType;
            this.mViewHolder = viewHolder;
            this.mStartDx = startDx;
            this.mStartDy = startDy;
            this.mTargetX = targetX;
            this.mTargetY = targetY;
            this.mValueAnimator = ValueAnimator.ofFloat(new float[]{0.0F, 1.0F});
            this.mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    ItemTouchHelper2.RecoverAnimation.this.setFraction(animation.getAnimatedFraction());
                }
            });
            this.mValueAnimator.setTarget(viewHolder.itemView);
            this.mValueAnimator.addListener(this);
            this.setFraction(0.0F);
        }

        public void setDuration(long duration) {
            this.mValueAnimator.setDuration(duration);
        }

        public void start() {
            this.mViewHolder.setIsRecyclable(false);
            this.mValueAnimator.start();
        }

        public void cancel() {
            this.mValueAnimator.cancel();
        }

        public void setFraction(float fraction) {
            this.mFraction = fraction;
        }

        public void update() {
            if (this.mStartDx == this.mTargetX) {
                this.mX = this.mViewHolder.itemView.getTranslationX();
            } else {
                this.mX = this.mStartDx + this.mFraction * (this.mTargetX - this.mStartDx);
            }

            if (this.mStartDy == this.mTargetY) {
                this.mY = this.mViewHolder.itemView.getTranslationY();
            } else {
                this.mY = this.mStartDy + this.mFraction * (this.mTargetY - this.mStartDy);
            }

        }

        public void onAnimationStart(Animator animation) {
        }

        public void onAnimationEnd(Animator animation) {
            if (!this.mEnded) {
                this.mViewHolder.setIsRecyclable(true);
            }

            this.mEnded = true;
        }

        public void onAnimationCancel(Animator animation) {
            this.setFraction(1.0F);
        }

        public void onAnimationRepeat(Animator animation) {
        }
    }

    private class ItemTouchHelperGestureListener extends GestureDetector.SimpleOnGestureListener {
        private boolean mShouldReactToLongPress = true;

        ItemTouchHelperGestureListener() {
        }

        void doNotReactToLongPress() {
            this.mShouldReactToLongPress = false;
        }

        public boolean onDown(MotionEvent e) {
            return true;
        }

        public void onLongPress(MotionEvent e) {
            if (this.mShouldReactToLongPress) {
                View child = ItemTouchHelper2.this.findChildView(e);
                if (child != null) {
                    RecyclerView.ViewHolder vh = ItemTouchHelper2.this.mRecyclerView.getChildViewHolder(child);
                    if (vh != null) {
                        if (!ItemTouchHelper2.this.mCallback.hasDragFlag(ItemTouchHelper2.this.mRecyclerView, vh)) {
                            return;
                        }

                        int pointerId = e.getPointerId(0);
                        if (pointerId == ItemTouchHelper2.this.mActivePointerId) {
                            int index = e.findPointerIndex(ItemTouchHelper2.this.mActivePointerId);
                            float x = e.getX(index);
                            float y = e.getY(index);
                            ItemTouchHelper2.this.mInitialTouchX = x;
                            ItemTouchHelper2.this.mInitialTouchY = y;
                            ItemTouchHelper2.this.mDx = ItemTouchHelper2.this.mDy = 0.0F;
                            if (ItemTouchHelper2.this.mCallback.isLongPressDragEnabled()) {
                                ItemTouchHelper2.this.select(vh, 2);
                            }
                        }
                    }
                }

            }
        }
    }

    public abstract static class SimpleCallback extends ItemTouchHelper2.Callback {
        private int mDefaultSwipeDirs;
        private int mDefaultDragDirs;

        public SimpleCallback(int dragDirs, int swipeDirs) {
            this.mDefaultSwipeDirs = swipeDirs;
            this.mDefaultDragDirs = dragDirs;
        }

        public void setDefaultSwipeDirs(int defaultSwipeDirs) {
            this.mDefaultSwipeDirs = defaultSwipeDirs;
        }

        public void setDefaultDragDirs(int defaultDragDirs) {
            this.mDefaultDragDirs = defaultDragDirs;
        }

        public int getSwipeDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            return this.mDefaultSwipeDirs;
        }

        public int getDragDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            return this.mDefaultDragDirs;
        }

        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags(this.getDragDirs(recyclerView, viewHolder), this.getSwipeDirs(recyclerView, viewHolder));
        }
    }

    public abstract static class Callback {
        public static final int DEFAULT_DRAG_ANIMATION_DURATION = 200;
        public static final int DEFAULT_SWIPE_ANIMATION_DURATION = 250;
        static final int RELATIVE_DIR_FLAGS = 3158064;
        private static final int ABS_HORIZONTAL_DIR_FLAGS = 789516;
        private static final Interpolator sDragScrollInterpolator = new Interpolator() {
            public float getInterpolation(float t) {
                return t * t * t * t * t;
            }
        };
        private static final Interpolator sDragViewScrollCapInterpolator = new Interpolator() {
            public float getInterpolation(float t) {
                --t;
                return t * t * t * t * t + 1.0F;
            }
        };
        private static final long DRAG_SCROLL_ACCELERATION_LIMIT_TIME_MS = 2000L;
        private int mCachedMaxScrollSpeed = -1;

        public Callback() {
        }

        @NonNull
        public static ItemTouchUIUtil getDefaultUIUtil() {
            return ItemTouchUIUtilImpl.INSTANCE;
        }

        public static int convertToRelativeDirection(int flags, int layoutDirection) {
            int masked = flags & 789516;
            if (masked == 0) {
                return flags;
            } else {
                flags &= ~masked;
                if (layoutDirection == 0) {
                    flags |= masked << 2;
                    return flags;
                } else {
                    flags |= masked << 1 & -789517;
                    flags |= (masked << 1 & 789516) << 2;
                    return flags;
                }
            }
        }

        public static int makeMovementFlags(int dragFlags, int swipeFlags) {
            return makeFlag(0, swipeFlags | dragFlags) | makeFlag(1, swipeFlags) | makeFlag(2, dragFlags);
        }

        public static int makeFlag(int actionState, int directions) {
            return directions << actionState * 8;
        }

        public abstract int getMovementFlags(@NonNull RecyclerView var1, @NonNull RecyclerView.ViewHolder var2);

        public int convertToAbsoluteDirection(int flags, int layoutDirection) {
            int masked = flags & 3158064;
            if (masked == 0) {
                return flags;
            } else {
                flags &= ~masked;
                if (layoutDirection == 0) {
                    flags |= masked >> 2;
                    return flags;
                } else {
                    flags |= masked >> 1 & -3158065;
                    flags |= (masked >> 1 & 3158064) >> 2;
                    return flags;
                }
            }
        }

        final int getAbsoluteMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int flags = this.getMovementFlags(recyclerView, viewHolder);
            return this.convertToAbsoluteDirection(flags, ViewCompat.getLayoutDirection(recyclerView));
        }

        boolean hasDragFlag(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int flags = this.getAbsoluteMovementFlags(recyclerView, viewHolder);
            return (flags & 16711680) != 0;
        }

        boolean hasSwipeFlag(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int flags = this.getAbsoluteMovementFlags(recyclerView, viewHolder);
            return (flags & '\uff00') != 0;
        }

        public boolean canDropOver(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder current, @NonNull RecyclerView.ViewHolder target) {
            return true;
        }

        public abstract boolean onMove(@NonNull RecyclerView var1, @NonNull RecyclerView.ViewHolder var2, @NonNull RecyclerView.ViewHolder var3);

        public boolean isLongPressDragEnabled() {
            return true;
        }

        public boolean isItemViewSwipeEnabled() {
            return true;
        }

        public int getBoundingBoxMargin() {
            return 0;
        }

        public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
            return 0.5F;
        }

        public float getMoveThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
            return 0.5F;
        }

        public float getSwipeEscapeVelocity(float defaultValue) {
            return defaultValue;
        }

        public float getSwipeVelocityThreshold(float defaultValue) {
            return defaultValue;
        }

        public RecyclerView.ViewHolder chooseDropTarget(@NonNull RecyclerView.ViewHolder selected, @NonNull List<RecyclerView.ViewHolder> dropTargets, int curX, int curY) {
            int right = curX + selected.itemView.getWidth();
            int bottom = curY + selected.itemView.getHeight();
            RecyclerView.ViewHolder winner = null;
            int winnerScore = -1;
            int dx = curX - selected.itemView.getLeft();
            int dy = curY - selected.itemView.getTop();
            int targetsSize = dropTargets.size();

            for (int i = 0; i < targetsSize; ++i) {
                RecyclerView.ViewHolder target = (RecyclerView.ViewHolder) dropTargets.get(i);
                int diff;
                int score;
                if (dx > 0) {
                    diff = target.itemView.getRight() - right;
                    if (diff < 0 && target.itemView.getRight() > selected.itemView.getRight()) {
                        score = Math.abs(diff);
                        if (score > winnerScore) {
                            winnerScore = score;
                            winner = target;
                        }
                    }
                }

                if (dx < 0) {
                    diff = target.itemView.getLeft() - curX;
                    if (diff > 0 && target.itemView.getLeft() < selected.itemView.getLeft()) {
                        score = Math.abs(diff);
                        if (score > winnerScore) {
                            winnerScore = score;
                            winner = target;
                        }
                    }
                }

                if (dy < 0) {
                    diff = target.itemView.getTop() - curY;
                    if (diff > 0 && target.itemView.getTop() < selected.itemView.getTop()) {
                        score = Math.abs(diff);
                        if (score > winnerScore) {
                            winnerScore = score;
                            winner = target;
                        }
                    }
                }

                if (dy > 0) {
                    diff = target.itemView.getBottom() - bottom;
                    if (diff < 0 && target.itemView.getBottom() > selected.itemView.getBottom()) {
                        score = Math.abs(diff);
                        if (score > winnerScore) {
                            winnerScore = score;
                            winner = target;
                        }
                    }
                }
            }

            return winner;
        }

        public abstract void onSwiped(@NonNull RecyclerView.ViewHolder var1, int var2);

        public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
            if (viewHolder != null) {
                ItemTouchUIUtilImpl.INSTANCE.onSelected(viewHolder.itemView);
            }

        }

        private int getMaxDragScroll(RecyclerView recyclerView) {
            if (this.mCachedMaxScrollSpeed == -1) {
                this.mCachedMaxScrollSpeed = recyclerView.getResources().getDimensionPixelSize(R.dimen.item_touch_helper_max_drag_scroll_per_frame1);
            }
            return this.mCachedMaxScrollSpeed;
        }

        public void onMoved(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, int fromPos, @NonNull RecyclerView.ViewHolder target, int toPos, int x, int y) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof ItemTouchHelper2.ViewDropHandler) {
                ((ItemTouchHelper2.ViewDropHandler) layoutManager).prepareForDrop(viewHolder.itemView, target.itemView, x, y);
            } else {
                int minTop;
                int maxBottom;
                if (layoutManager.canScrollHorizontally()) {
                    minTop = layoutManager.getDecoratedLeft(target.itemView);
                    if (minTop <= recyclerView.getPaddingLeft()) {
                        recyclerView.scrollToPosition(toPos);
                    }

                    maxBottom = layoutManager.getDecoratedRight(target.itemView);
                    if (maxBottom >= recyclerView.getWidth() - recyclerView.getPaddingRight()) {
                        recyclerView.scrollToPosition(toPos);
                    }
                }

                if (layoutManager.canScrollVertically()) {
                    minTop = layoutManager.getDecoratedTop(target.itemView);
                    if (minTop <= recyclerView.getPaddingTop()) {
                        recyclerView.scrollToPosition(toPos);
                    }

                    maxBottom = layoutManager.getDecoratedBottom(target.itemView);
                    if (maxBottom >= recyclerView.getHeight() - recyclerView.getPaddingBottom()) {
                        recyclerView.scrollToPosition(toPos);
                    }
                }

            }
        }

        void onDraw(Canvas c, RecyclerView parent, RecyclerView.ViewHolder selected, List<ItemTouchHelper2.RecoverAnimation> recoverAnimationList, int actionState, float dX, float dY) {
            int recoverAnimSize = recoverAnimationList.size();

            int count;
            for (count = 0; count < recoverAnimSize; ++count) {
                ItemTouchHelper2.RecoverAnimation anim = (ItemTouchHelper2.RecoverAnimation) recoverAnimationList.get(count);
                anim.update();
                int count1 = c.save();
                this.onChildDraw(c, parent, anim.mViewHolder, anim.mX, anim.mY, anim.mActionState, false);
                c.restoreToCount(count1);
            }

            if (selected != null) {
                count = c.save();
                this.onChildDraw(c, parent, selected, dX, dY, actionState, true);
                c.restoreToCount(count);
            }

        }

        void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.ViewHolder selected, List<ItemTouchHelper2.RecoverAnimation> recoverAnimationList, int actionState, float dX, float dY) {
            int recoverAnimSize = recoverAnimationList.size();

            int count;
            for (count = 0; count < recoverAnimSize; ++count) {
                ItemTouchHelper2.RecoverAnimation anim = (ItemTouchHelper2.RecoverAnimation) recoverAnimationList.get(count);
                int count1 = c.save();
                this.onChildDrawOver(c, parent, anim.mViewHolder, anim.mX, anim.mY, anim.mActionState, false);
                c.restoreToCount(count1);
            }

            if (selected != null) {
                count = c.save();
                this.onChildDrawOver(c, parent, selected, dX, dY, actionState, true);
                c.restoreToCount(count);
            }

            boolean hasRunningAnimation = false;

            for (int i = recoverAnimSize - 1; i >= 0; --i) {
                ItemTouchHelper2.RecoverAnimation anim = (ItemTouchHelper2.RecoverAnimation) recoverAnimationList.get(i);
                if (anim.mEnded && !anim.mIsPendingCleanup) {
                    recoverAnimationList.remove(i);
                } else if (!anim.mEnded) {
                    hasRunningAnimation = true;
                }
            }

            if (hasRunningAnimation) {
                parent.invalidate();
            }

        }

        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            ItemTouchUIUtilImpl.INSTANCE.clearView(viewHolder.itemView);
        }

        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            ItemTouchUIUtilImpl.INSTANCE.onDraw(c, recyclerView, viewHolder.itemView, dX, dY, actionState, isCurrentlyActive);
        }

        public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            ItemTouchUIUtilImpl.INSTANCE.onDrawOver(c, recyclerView, viewHolder.itemView, dX, dY, actionState, isCurrentlyActive);
        }

        public long getAnimationDuration(@NonNull RecyclerView recyclerView, int animationType, float animateDx, float animateDy) {
            RecyclerView.ItemAnimator itemAnimator = recyclerView.getItemAnimator();
            if (itemAnimator == null) {
                return animationType == 8 ? 200L : 250L;
            } else {
                return animationType == 8 ? itemAnimator.getMoveDuration() : itemAnimator.getRemoveDuration();
            }
        }

        public int interpolateOutOfBoundsScroll(@NonNull RecyclerView recyclerView, int viewSize, int viewSizeOutOfBounds, int totalSize, long msSinceStartScroll) {
            int maxScroll = this.getMaxDragScroll(recyclerView);
            int absOutOfBounds = Math.abs(viewSizeOutOfBounds);
            int direction = (int) Math.signum((float) viewSizeOutOfBounds);
            float outOfBoundsRatio = Math.min(1.0F, 1.0F * (float) absOutOfBounds / (float) viewSize);
            int cappedScroll = (int) ((float) (direction * maxScroll) * sDragViewScrollCapInterpolator.getInterpolation(outOfBoundsRatio));
            float timeRatio;
            if (msSinceStartScroll > 2000L) {
                timeRatio = 1.0F;
            } else {
                timeRatio = (float) msSinceStartScroll / 2000.0F;
            }

            int value = (int) ((float) cappedScroll * sDragScrollInterpolator.getInterpolation(timeRatio));
            if (value == 0) {
                return viewSizeOutOfBounds > 0 ? 1 : -1;
            } else {
                return value;
            }
        }
    }

    public interface ViewDropHandler {
        void prepareForDrop(@NonNull View var1, @NonNull View var2, int var3, int var4);
    }
}
