package per.wsj.library;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.view.Gravity;

import androidx.appcompat.content.res.AppCompatResources;

import java.lang.reflect.Field;

public class StarDrawable extends LayerDrawable {

    public StarDrawable(RattingAttr rattingAttr) {
        super(rattingAttr.getLayerList());

        setId(0, android.R.id.background);
        // progress & secondaryProgress must be ClipDrawable
        setId(1, android.R.id.secondaryProgress);
        setId(2, android.R.id.progress);

        initStyle(rattingAttr);
    }

    private void initStyle(RattingAttr rattingAttr) {
        TileDrawable background = getTileDrawableByLayerId(android.R.id.background);
        TileDrawable secondaryProgress = getTileDrawableByLayerId(android.R.id.secondaryProgress);
        TileDrawable progress = getTileDrawableByLayerId(android.R.id.progress);

        background.setTileCount(rattingAttr.getStarCount());
        secondaryProgress.setTileCount(rattingAttr.getStarCount());
        progress.setTileCount(rattingAttr.getStarCount());

        if (rattingAttr.getBgColor() != null) {
            background.setTintList(rattingAttr.getBgColor());
        }
        if (rattingAttr.getSecondaryStarColor() != null) {
            secondaryProgress.setTintList(rattingAttr.getSecondaryStarColor());
        }
        if (rattingAttr.getStarColor() != null) {
            progress.setTintList(rattingAttr.getStarColor());
        }
    }

    public StarDrawable(Context context, int starDrawable, int bgDrawable, boolean mKeepOriginColor) {
        super(new Drawable[]{
//                createLayerDrawableWithTintAttrRes(bgDrawable, R.attr.colorControlHighlight, context, mKeepOriginColor),
//                createClippedLayerDrawableWithTintColor(starDrawable, Color.TRANSPARENT, context),
//                createClippedLayerDrawableWithTintAttrRes(starDrawable, R.attr.colorControlActivated, context, mKeepOriginColor)
        });

        setId(0, android.R.id.background);
        // progress & secondaryProgress must use ClipDrawable
        setId(1, android.R.id.secondaryProgress);
        setId(2, android.R.id.progress);
    }

//    /**
//     * create background drawable
//     *
//     * @param tileRes
//     * @param tintAttrRes
//     * @param context
//     * @param mKeepOriginColor
//     * @return
//     *//*
//    private static Drawable createLayerDrawableWithTintAttrRes(int tileRes, int tintAttrRes,
//                                                               Context context, boolean mKeepOriginColor) {
//        int tintColor = -1;
//        if (!mKeepOriginColor) {
//            tintColor = getColorFromAttrRes(tintAttrRes, context);
//        }
//        return createLayerDrawableWithTintColor(tileRes, tintColor, context);
//    }
//
//    *//**
//     * create secondaryProgress drawable
//     *
//     * @param tileResId
//     * @param tintColor
//     * @param context
//     * @return
//     *//*
//    @SuppressLint("RtlHardcoded")
//    private static Drawable createClippedLayerDrawableWithTintColor(int tileResId, int tintColor,
//                                                                    Context context) {
//        return new ClipDrawable(createLayerDrawableWithTintColor(tileResId, tintColor,
//                context), Gravity.LEFT, ClipDrawable.HORIZONTAL);
//    }
//
//    private static Drawable createLayerDrawableWithTintColor(int tileRes, int tintColor,
//                                                             Context context) {
//        TileDrawable drawable = new TileDrawable(AppCompatResources.getDrawable(context,
//                tileRes));
//        drawable.mutate();
//        if (tintColor != -1) {
//            drawable.setTint(tintColor);
//        }
//        return drawable;
//    }
//
//    @SuppressLint("RtlHardcoded")
//    private static Drawable createClippedLayerDrawableWithTintAttrRes(int tileResId,
//                                                                      int tintAttrRes,
//                                                                      Context context, boolean mKeepOriginColor) {
//        return new ClipDrawable(createLayerDrawableWithTintAttrRes(tileResId, tintAttrRes,
//                context, mKeepOriginColor), Gravity.LEFT, ClipDrawable.HORIZONTAL);
//    }

//    private static int getColorFromAttrRes(int attrRes, Context context) {
//        TypedArray a = context.obtainStyledAttributes(new int[]{attrRes});
//        try {
//            return a.getColor(0, 0);
//        } finally {
//            a.recycle();
//        }
//    }

    public float getTileRatio() {
        Drawable drawable = getTileDrawableByLayerId(android.R.id.progress).getDrawable();
        return (float) drawable.getIntrinsicWidth() / drawable.getIntrinsicHeight();
    }

    public void setStarCount(int count) {
        getTileDrawableByLayerId(android.R.id.background).setTileCount(count);
        getTileDrawableByLayerId(android.R.id.secondaryProgress).setTileCount(count);
        getTileDrawableByLayerId(android.R.id.progress).setTileCount(count);
    }

    @SuppressLint("NewApi")
    private TileDrawable getTileDrawableByLayerId(int id) {
        Drawable layerDrawable = findDrawableByLayerId(id);
        switch (id) {
            case android.R.id.background:
                return (TileDrawable) layerDrawable;
            case android.R.id.secondaryProgress:
            case android.R.id.progress: {
                ClipDrawable clipDrawable = (ClipDrawable) layerDrawable;
                // fix bug:sdk<23 class ClipDrawable has no getDrawable() #8
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    return (TileDrawable) clipDrawable.getDrawable();
                } else {
                    try {
                        String fieldState = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 ? "mState" : "mClipState";
                        Field mStateField = clipDrawable.getClass().getDeclaredField(fieldState);
                        mStateField.setAccessible(true);
                        Object clipState = mStateField.get(clipDrawable);
                        Field mDrawableField = clipState.getClass().getDeclaredField("mDrawable");
                        mDrawableField.setAccessible(true);
                        return (TileDrawable) mDrawableField.get(clipState);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            default:
                // Should never reach here.
                throw new RuntimeException();
        }
    }
}
