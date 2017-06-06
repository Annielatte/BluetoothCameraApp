package com.minee9351gmail.test1.module.cropper;

/**
 * Created by 백은서 on 2017-05-31.
 */

public class EdgePair {

    // Member Variables ////////////////////////////////////////////////////////

    public Edge primary;
    public Edge secondary;

    // Constructor /////////////////////////////////////////////////////////////

    public EdgePair(Edge edge1, Edge edge2) {
        primary = edge1;
        secondary = edge2;
    }
}