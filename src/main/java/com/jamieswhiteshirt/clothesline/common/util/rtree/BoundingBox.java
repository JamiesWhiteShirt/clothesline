package com.jamieswhiteshirt.clothesline.common.util.rtree;

public final class BoundingBox {
    public static BoundingBox containingAll(BoundingBox... boundingBoxes) {
        BoundingBox initial = boundingBoxes[0];
        int minX = initial.minX;
        int minY = initial.minY;
        int minZ = initial.minZ;
        int maxX = initial.maxX;
        int maxY = initial.maxY;
        int maxZ = initial.maxZ;

        for (int i = 1; i < boundingBoxes.length; i++) {
            BoundingBox boundingBox = boundingBoxes[i];
            if (boundingBox.minX < minX) minX = boundingBox.minX;
            if (boundingBox.minY < minY) minY = boundingBox.minY;
            if (boundingBox.minZ < minZ) minZ = boundingBox.minZ;
            if (boundingBox.maxX > maxX) maxX = boundingBox.maxX;
            if (boundingBox.maxY > maxY) maxY = boundingBox.maxY;
            if (boundingBox.maxZ > maxZ) maxZ = boundingBox.maxZ;
        }

        return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public final int minX, minY, minZ, maxX, maxY, maxZ;

    public BoundingBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public boolean contains(Point point) {
        if (minX > point.x) return false;
        if (minY > point.y) return false;
        if (minZ > point.z) return false;
        if (maxX < point.x) return false;
        if (maxY < point.y) return false;
        if (maxZ < point.z) return false;
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BoundingBox boundingBox = (BoundingBox) o;

        if (minX != boundingBox.minX) return false;
        if (minY != boundingBox.minY) return false;
        if (minZ != boundingBox.minZ) return false;
        if (maxX != boundingBox.maxX) return false;
        if (maxY != boundingBox.maxY) return false;
        return maxZ == boundingBox.maxZ;
    }

    @Override
    public int hashCode() {
        int result = minX;
        result = 31 * result + minY;
        result = 31 * result + minZ;
        result = 31 * result + maxX;
        result = 31 * result + maxY;
        result = 31 * result + maxZ;
        return result;
    }
}
