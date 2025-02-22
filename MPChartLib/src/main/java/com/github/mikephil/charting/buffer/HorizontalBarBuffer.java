
package com.github.mikephil.charting.buffer;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

public class HorizontalBarBuffer extends BarBuffer {

    public HorizontalBarBuffer(int size, int dataSetCount, boolean containsStacks) {
        super(size, dataSetCount, containsStacks);
    }

    @Override
    public void feed(IBarDataSet data) {
        float size = data.getEntryCount() * phaseX;
        float barWidthHalf = mBarWidth / 2f;

        for (int i = 0; i < size; i++) {
            BarEntry e = data.getEntryForIndex(i);

            if (e == null) continue;

            if (!mContainsStacks || e.getYVals() == null) {
                processSingleBar(e, barWidthHalf);
            } else {
                processStackedBar(e, barWidthHalf);
            }
        }

        reset();
    }

    private void processSingleBar(BarEntry e, float barWidthHalf) {
        float x = e.getX();
        float y = e.getY();
        float bottom = x - barWidthHalf;
        float top = x + barWidthHalf;
        float left, right;

        if (mInverted) {
            left = y >= 0 ? y : 0;
            right = y <= 0 ? y : 0;
        } else {
            right = y >= 0 ? y : 0;
            left = y <= 0 ? y : 0;
        }

        if (right > 0) right *= phaseY;
        else left *= phaseY;

        addBar(left, top, right, bottom);
    }

    private void processStackedBar(BarEntry e, float barWidthHalf) {
        float[] vals = e.getYVals();
        float posY = 0f;
        float negY = -e.getNegativeSum();

        for (float value : vals) {
            float y, yStart;
            if (value >= 0f) {
                y = posY;
                yStart = posY + value;
                posY = yStart;
            } else {
                y = negY;
                yStart = negY + Math.abs(value);
                negY += Math.abs(value);
            }

            addStackedBar(e.getX(), y, yStart, barWidthHalf);
        }
    }

    private void addStackedBar(float x, float y, float yStart, float barWidthHalf) {
        float bottom = x - barWidthHalf;
        float top = x + barWidthHalf;
        float left, right;

        if (mInverted) {
            left = y >= yStart ? y : yStart;
            right = y <= yStart ? y : yStart;
        } else {
            right = y >= yStart ? y : yStart;
            left = y <= yStart ? y : yStart;
        }

        right *= phaseY;
        left *= phaseY;

        addBar(left, top, right, bottom);
    }

}
