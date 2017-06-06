package com.minee9351gmail.test1.module.cropper.handle;

import android.graphics.RectF;
import android.support.annotation.NonNull;

import com.minee9351gmail.test1.module.cropper.Edge;
import com.minee9351gmail.test1.module.cropper.EdgePair;


/**
 * Created by 백은서 on 2017-05-31.
 */

class CornerHandleHelper extends HandleHelper {

    // Constructor /////////////////////////////////////////////////////////////////////////////////

    CornerHandleHelper(Edge horizontalEdge, Edge verticalEdge) {
        super(horizontalEdge, verticalEdge);
    }

    // HandleHelper Methods ////////////////////////////////////////////////////////////////////////

    @Override
    void updateCropWindow(float x,
                          float y,
                          float targetAspectRatio,
                          @NonNull RectF imageRect,
                          float snapRadius) {

        final EdgePair activeEdges = getActiveEdges(x, y, targetAspectRatio);
        final Edge primaryEdge = activeEdges.primary;
        final Edge secondaryEdge = activeEdges.secondary;

        primaryEdge.adjustCoordinate(x, y, imageRect, snapRadius, targetAspectRatio);
        secondaryEdge.adjustCoordinate(targetAspectRatio);

        if (secondaryEdge.isOutsideMargin(imageRect, snapRadius)) {
            secondaryEdge.snapToRect(imageRect);
            primaryEdge.adjustCoordinate(targetAspectRatio);
        }
    }
}