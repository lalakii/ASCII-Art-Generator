/*
 * Copyright (c) 2018 Muhammad Umar (https://github.com/zelin)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package com.neberox.library.asciicreator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.neberox.library.asciicreator.models.PixelBlock;
import com.neberox.library.asciicreator.models.PixelGrid;
import com.neberox.library.asciicreator.utilities.ASCIIConverterHelper;
import com.neberox.library.asciicreator.utilities.ASCIIUtilities;
import com.neberox.library.asciicreator.utilities.OnBitmapTaskListener;
import com.neberox.library.asciicreator.utilities.OnStringTaskListener;

import java.security.InvalidParameterException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;

/**
 * Convert Bitmap into its ASCII equivalent bitmap or String.
 * <p>
 * API endpoints are defined as methods on an interface with annotations providing metadata about
 * the form in which the HTTP call should be made.
 * <p>
 *
 * @author Muhammad Umar (ee_umar@yahoo.com)
 */

public class ASCIIConverter {
    private Context mContext;
    /*** Grid columns count calculated based on the font size*/
    private float mColumns;
    /*** Font size of the ASCII text in pixels. */
    private float mFontSize = 72;
    /*** Background color of the bitmap. Default is transparent */
    private int mBackgroundColor = Color.TRANSPARENT;
    /*** Luminance of the text */
    private boolean mReversedLuminance = false;
    /*** Enable to disable gray scale of the bitmap */
    private boolean mGrayScale = false;
    /*** Helper class to get ASCII-Luminance dictionary map */
    private final ASCIIConverterHelper mHelper;
    private OnStringTaskListener mStringTaskListener;
    private OnBitmapTaskListener mBitmapTaskListener;

    /**
     * @param mContext Context of the app
     */
    public ASCIIConverter(Context mContext) {
        this.mContext = mContext;
        initDefaults();
        this.mHelper = new ASCIIConverterHelper();
    }

    /**
     * @param mContext Context of the app
     * @param mHelper  Customized ASCIIConverterHelper Class
     */
    @SuppressWarnings("unused")
    public ASCIIConverter(Context mContext, ASCIIConverterHelper mHelper) {
        this.mContext = mContext;
        initDefaults();
        this.mHelper = mHelper;
    }

    /**
     * @param mContext  Context of the app
     * @param mFontSize font size of the ascii text in bitmap
     */
    @SuppressWarnings("unused")
    public ASCIIConverter(Context mContext, int mFontSize) {
        this.mContext = mContext;
        initDefaults();
        this.mFontSize = ASCIIUtilities.spToPx(mFontSize, mContext);
        this.mHelper = new ASCIIConverterHelper();
    }

    /**
     * @param mContext  Context of the app
     * @param mHelper   Customized ASCIIConverterHelper Class
     * @param mFontSize font size of the ascii text in bitmap
     */
    @SuppressWarnings("unused")
    public ASCIIConverter(Context mContext, ASCIIConverterHelper mHelper, int mFontSize) {
        this.mContext = mContext;
        initDefaults();
        this.mFontSize = ASCIIUtilities.spToPx(mFontSize, mContext);
        this.mHelper = mHelper;
    }

    /**
     * @param dictionary Accepts key pair value of character and its luminance
     */
    @SuppressWarnings("unused")
    public ASCIIConverter(Map<String, Float> dictionary) {
        this.mHelper = new ASCIIConverterHelper(dictionary);
        initDefaults();
    }

    private void initDefaults() {
        setFontSize(18);
        setReversedLuminance(false);
        setGrayScale(false);
        mColumns = 0;
    }

    /**
     * @param mGrayScale Enable to disable gray scale of the bitmap. Default is false to create colored ASCII Bitmap
     */
    public void setGrayScale(boolean mGrayScale) {
        this.mGrayScale = mGrayScale;
    }

    /**
     * @param mReversedLuminance Reverses the luminance by subtracting from 1. Default is false
     */
    public void setReversedLuminance(boolean mReversedLuminance) {
        this.mReversedLuminance = mReversedLuminance;
    }

    /**
     * @param mFontSize Set the font size in sp
     */
    public void setFontSize(float mFontSize) {
        this.mFontSize = ASCIIUtilities.spToPx(mFontSize, mContext);
    }

    /**
     * @param mBackgroundColor Adds a background color to ASCII Bitmap. Default is transparent
     */
    @SuppressWarnings("unused")
    public void setBackgroundColor(int mBackgroundColor) {
        this.mBackgroundColor = mBackgroundColor;
    }

    /**
     * Creates an ASCII String of a bitmap
     *
     * @param originalBitmap bitmap to create ASCII Bitmap
     * @return returns ASCII String
     */
    @SuppressWarnings("unused")
    public String createASCIIString(Bitmap originalBitmap) throws InvalidParameterException {
        return createASCIIString(originalBitmap, null);
    }

    public String createASCIIString(Bitmap originalBitmap, OnStringTaskListener mTask) throws InvalidParameterException {
        mColumns = getGridWidth(originalBitmap.getWidth());

        if (mColumns < 5) {
            throw new InvalidParameterException("Columns count is very small. Font size needs to be reduced");
        }

        mStringTaskListener = mTask;

        AsciiArtData asciiArtData = CreateASCIIData(originalBitmap);
        if (mStringTaskListener == null) {
            return asciiArtData.text;
        }
        return null;
    }

    /**
     * Creates an ASCII String of a bitmap
     *
     * @param bitmap bitmap to create ASCII Bitmap
     * @return returns ASCII Bitmap
     */

    public Bitmap createASCIIImage(Bitmap bitmap) throws ExecutionException, InterruptedException, InvalidParameterException {
        return createASCIIImage(bitmap, null);
    }

    public Bitmap createASCIIImage(final Bitmap originalBitmap, final OnBitmapTaskListener mTask) throws InvalidParameterException {
        mColumns = getGridWidth(originalBitmap.getWidth());
        if (mColumns < 5) {
            throw new InvalidParameterException("Columns count is very small. Font size needs to be reduced");
        }

        mBitmapTaskListener = mTask;

        AsciiArtData asciiArtData = CreateASCIIData(originalBitmap);
        if (mBitmapTaskListener == null) {
            return asciiArtData.bmp;
        }
        return null;
    }

    ExecutorCompletionService<AsciiArtData> service = new ExecutorCompletionService<>(Executors.newCachedThreadPool());

    static class AsciiArtData {
        Bitmap bmp;
        String text;
    }

    AsciiArtData data = new AsciiArtData();

    private AsciiArtData CreateASCIIData(Bitmap originalBitmap) {
        try {
            data = service.submit(() -> {
                Bitmap scaledImage = ASCIIUtilities.resizeBitmap(originalBitmap, (int) mColumns);
                PixelGrid grid = getGridData(scaledImage);
                Bitmap newBitmap = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), originalBitmap.getConfig());
                Canvas canvas = new Canvas(newBitmap);
                if (mBackgroundColor != Color.TRANSPARENT) {
                    Paint mPaint = new Paint();
                    mPaint.setColor(mBackgroundColor);
                    mPaint.setStyle(Paint.Style.FILL);
                    canvas.drawPaint(mPaint);
                }
                Paint paint = new Paint();
                paint.setColor(Color.WHITE); // Text Color
                paint.setTextSize(mFontSize); // Text Size
                canvas.drawBitmap(newBitmap, 0, 0, paint);
                StringBuilder mainString = new StringBuilder();
                for (int row = 0; row < grid.height; row++) {
                    for (int col = 0; col < grid.width; col++) {
                        PixelBlock block = grid.blocks[col][row];//(index);
                        float luminance = ASCIIConverterHelper.getLuminance(block, mReversedLuminance);
                        String ascii = mHelper.asciiFromLuminance(luminance);
                        mainString.append(ascii).append("\t");
                        int color = ASCIIUtilities.getColor(block);
                        if (!mGrayScale) {
                            paint.setColor(color); // Text Color
                        } else {
                            paint.setAlpha((int) (luminance * 255.0f));
                            paint.setColor(Color.GRAY);
                        }
                        canvas.drawText(ascii, col * mFontSize, row * mFontSize, paint);
                    }
                    mainString.append("\n");
                }
                if (mBitmapTaskListener != null) {
                    mBitmapTaskListener.onTaskCompleted(newBitmap);
                }
                if (mStringTaskListener != null) {
                    mStringTaskListener.onTaskCompleted(data.text);
                }
                data.bmp = newBitmap;
                data.text = mainString.toString();
                return data;
            }).get();
        } catch (InterruptedException | ExecutionException ignored) {
        }
        return data;
    }

    /**
     * @param width width of the image
     * @return returns the columns count
     */
    private float getGridWidth(int width) {
        return width / mFontSize;
    }

    /**
     * @param bitmap Bitmap for fetching rgba data
     * @return returns grid containing rgba data of bitmap
     */
    private PixelGrid getGridData(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        PixelGrid grid = new PixelGrid(width, height, bitmap.getWidth() * bitmap.getHeight());
        for (int row = 0; row < grid.width; row++) {
            for (int col = 0; col < grid.height; col++) {
                int pixel = bitmap.getPixel(row, col);

                float r = ((pixel >> 16) & 0xff) / 255.0f;
                float g = ((pixel >> 8) & 0xff) / 255.0f;
                float b = ((pixel) & 0xff) / 255.0f;
                float a = ((pixel >> 24) & 0xff) / 255.0f;

                grid.add(r, g, b, a, row, col);
            }
        }

        return grid;
    }
}