package com.zp.snakeviewmaker;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;


/**
 * Created by zengp on 2017/10/20.
 */

public class RoundTransformation extends BitmapTransformation {

    private static final int CORNER_TYPE_CIRCLE = -1;
    public static final int CORNER_TYPE_ALL = 0;
    public static final int CORNER_TYPE_TOP_LEFT = 1;
    public static final int CORNER_TYPE_TOP_RIGHT = 10;
    public static final int CORNER_TYPE_BOTTOM_LEFT = 100;
    public static final int CORNER_TYPE_BOTTOM_RIGHT = 1000;

    private float radius;  // dp
    private int cornerType;

    private int drawWidth, drawHeight;

    public RoundTransformation(Context context) {
        this(context, 0, CORNER_TYPE_CIRCLE);
    }

    public RoundTransformation(Context context, float radius) {
        this(context, radius, CORNER_TYPE_ALL);
    }

    public RoundTransformation(Context context, float radius, int cornerType) {
        super(context);
        this.radius = Resources.getSystem().getDisplayMetrics().density * radius;
        this.cornerType = cornerType;
    }

    @Override
    public String getId() {
        return "RoundedTransformation(radius=" + radius + ", cornerType=" + cornerType + ")";
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        if (null == toTransform)
            return null;

        // outWidth is the width of the target ImageView,and the same to outHeight
        // all the ori bitmaps loaded may have different size, in order to the clipped
        // the bitmaps have the same size and shape,we use the target ImageView's size
        // to create bitmaps
        updateDrawBound(toTransform.getWidth(), toTransform.getHeight(), outWidth, outHeight);

        Bitmap bitmap = pool.get(drawWidth, drawHeight, Bitmap.Config.ARGB_8888);
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(drawWidth, drawHeight, Bitmap.Config.ARGB_8888);
        }
        bitmap.setHasAlpha(true);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(toTransform, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        drawRoundRect(canvas, paint);
        return bitmap;
    }

    private void updateDrawBound(int oriW, int oriH, int targetW, int targetH) {
        if (targetH <= 0 || targetW <= 0) {
            // wrap_content
            if (cornerType == CORNER_TYPE_CIRCLE) {
                drawWidth = drawHeight = Math.min(oriW, oriH);
                radius = drawWidth / 2;
            } else {
                drawWidth = oriW;
                drawHeight = oriH;
            }

        } else {
            if (cornerType == CORNER_TYPE_CIRCLE) {
                int oriMin = Math.min(oriH, oriW);
                int targetMin = Math.min(targetW, targetH);
                drawWidth = drawHeight = Math.min(oriMin, targetMin);
                radius = drawWidth / 2;
            } else {
                drawWidth = Math.min(oriW, targetW);
                drawHeight = Math.min(oriH, targetH);
                float radio = targetW * 1f / targetH;
                float drawDimenRadio = drawWidth * 1f / drawHeight;
                if (drawDimenRadio > radio) {
                    drawWidth = (int) (drawHeight * radio);
                } else {
                    drawHeight = (int) (drawWidth / radio);
                }
                radius = radius * drawWidth / targetW;
            }
        }

    }

    private void drawRoundRect(Canvas canvas, Paint paint) {
        float left = 0;
        float top = 0;
        float right = drawWidth;
        float bottom = drawHeight;
        float diameter = radius * 2;

        switch (cornerType) {
            case CORNER_TYPE_CIRCLE:
            case CORNER_TYPE_ALL:
            case CORNER_TYPE_TOP_LEFT | CORNER_TYPE_TOP_RIGHT | CORNER_TYPE_BOTTOM_LEFT | CORNER_TYPE_BOTTOM_RIGHT:
                // all
                canvas.drawRoundRect(new RectF(left, top, right, bottom), radius, radius, paint);
                break;
            case CORNER_TYPE_TOP_LEFT:
                // top-left
                canvas.drawRoundRect(new RectF(left, top, diameter, diameter), radius, radius, paint);
                canvas.drawRect(new RectF(left, top + radius, radius, bottom), paint);
                canvas.drawRect(new RectF(left + radius, top, right, bottom), paint);
                break;
            case CORNER_TYPE_TOP_RIGHT:
                // top-right
                canvas.drawRoundRect(new RectF(right - diameter, top, right, diameter), radius, radius, paint);
                canvas.drawRect(new RectF(left, top, right - radius, bottom), paint);
                canvas.drawRect(new RectF(right - radius, top + radius, right, bottom), paint);
                break;
            case CORNER_TYPE_BOTTOM_LEFT:
                // bottom-left
                canvas.drawRoundRect(new RectF(left, bottom - diameter, diameter, bottom), radius, radius, paint);
                canvas.drawRect(new RectF(left, top, right, bottom - radius), paint);
                canvas.drawRect(new RectF(left + radius, bottom - radius, right, bottom), paint);
                break;
            case CORNER_TYPE_BOTTOM_RIGHT:
                // bottom-right
                canvas.drawRoundRect(new RectF(right - diameter, bottom - diameter, right, diameter), radius, radius, paint);
                canvas.drawRect(new RectF(left, top, right - radius, bottom), paint);
                canvas.drawRect(new RectF(left, bottom - radius, right - radius, bottom), paint);
                break;
            case CORNER_TYPE_TOP_LEFT | CORNER_TYPE_TOP_RIGHT:
                // top-left top-right
                canvas.drawRoundRect(new RectF(left, top, right, diameter), radius, radius, paint);
                canvas.drawRect(new RectF(left, top + radius, right, bottom), paint);
                break;
            case CORNER_TYPE_TOP_LEFT | CORNER_TYPE_BOTTOM_LEFT:
                // top-left bottom-left
                canvas.drawRoundRect(new RectF(left, top, diameter, bottom), radius, radius, paint);
                canvas.drawRect(new RectF(left + radius, top, right, bottom), paint);
                break;
            case CORNER_TYPE_TOP_LEFT | CORNER_TYPE_BOTTOM_RIGHT:
                // top-left bottom-right
                canvas.drawRoundRect(new RectF(left, top, diameter, diameter), radius, radius, paint);
                canvas.drawRoundRect(new RectF(right - radius, bottom - radius, right, bottom), radius, radius, paint);
                canvas.drawRect(new RectF(left, top + radius, right - radius, bottom), paint);
                canvas.drawRect(new RectF(left + radius, top, right, bottom - radius), paint);
                break;
            case CORNER_TYPE_TOP_RIGHT | CORNER_TYPE_BOTTOM_LEFT:
                // top-right bottom-left
                canvas.drawRoundRect(new RectF(right - diameter, top, right, diameter), radius, radius, paint);
                canvas.drawRoundRect(new RectF(left, bottom - diameter, diameter, bottom), radius, radius, paint);
                canvas.drawRect(new RectF(left, top, right - radius, bottom - radius), paint);
                canvas.drawRect(new RectF(left + radius, top + radius, right, bottom), paint);
                break;
            case CORNER_TYPE_TOP_RIGHT | CORNER_TYPE_BOTTOM_RIGHT:
                // top-right bottom-right
                canvas.drawRoundRect(new RectF(right - diameter, top, right, bottom), radius, radius, paint);
                canvas.drawRect(new RectF(left, top, right - right, bottom), paint);
                break;
            case CORNER_TYPE_BOTTOM_LEFT | CORNER_TYPE_BOTTOM_RIGHT:
                // bottom-right bottom-left
                canvas.drawRoundRect(new RectF(left, bottom - diameter, right, bottom), radius, radius, paint);
                canvas.drawRect(new RectF(left, top, right, bottom - radius), paint);
                break;
            case CORNER_TYPE_TOP_LEFT | CORNER_TYPE_TOP_RIGHT | CORNER_TYPE_BOTTOM_LEFT:
                // top-left top-right bottom-left
                canvas.drawRoundRect(new RectF(left, top, right, diameter), radius, radius, paint);
                canvas.drawRoundRect(new RectF(left, top, diameter, bottom), radius, radius, paint);
                canvas.drawRect(new RectF(left + radius, top + radius, right, bottom), paint);
                break;
            case CORNER_TYPE_TOP_LEFT | CORNER_TYPE_TOP_RIGHT | CORNER_TYPE_BOTTOM_RIGHT:
                // top-left top-right bottom-right
                canvas.drawRoundRect(new RectF(left, top, right, diameter), radius, radius, paint);
                canvas.drawRoundRect(new RectF(right - diameter, top, right, bottom), radius, radius, paint);
                canvas.drawRect(new RectF(left, top + radius, right - radius, bottom), paint);
                break;
            case CORNER_TYPE_TOP_LEFT | CORNER_TYPE_BOTTOM_LEFT | CORNER_TYPE_BOTTOM_RIGHT:
                // top-left bottom-left bottom-right
                canvas.drawRoundRect(new RectF(left, top, diameter, bottom), radius, radius, paint);
                canvas.drawRoundRect(new RectF(left, bottom - diameter, right, bottom), radius, radius, paint);
                canvas.drawRect(new RectF(left, top + radius, right - radius, bottom), paint);
                break;
            case CORNER_TYPE_TOP_RIGHT | CORNER_TYPE_BOTTOM_LEFT | CORNER_TYPE_BOTTOM_RIGHT:
                // top-right bottom-left bottom-right
                canvas.drawRoundRect(new RectF(left, bottom - diameter, right, bottom), radius, radius, paint);
                canvas.drawRoundRect(new RectF(right - diameter, top, right, bottom), radius, radius, paint);
                canvas.drawRect(new RectF(left, top, right - radius, bottom - radius), paint);
                break;
            default:
                canvas.drawRoundRect(new RectF(left, top, right, bottom), radius, radius, paint);
                break;
        }
    }
}
