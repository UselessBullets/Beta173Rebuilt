// 
// Decompiled by Procyon v0.6.0
// 

package util;

public class SmoothFloat {
    private float targetValue;
    private float remainingValue;
    private float lastAmount;

    public float getNewDeltaValue(float deltaValue, final float accelerationAmount) {
        this.targetValue += deltaValue;

        deltaValue = (this.targetValue - this.remainingValue) * accelerationAmount;
        this.lastAmount += (deltaValue - this.lastAmount) * 0.5f;
        if ((deltaValue > 0.0f && deltaValue > this.lastAmount) || (deltaValue < 0.0f && deltaValue < this.lastAmount)) {
            deltaValue = this.lastAmount;
        }
        this.remainingValue += deltaValue;

        return deltaValue;
    }

    // Useless - In LCE unused, makes sense to exist here
    public float getTargetValue() {
        return this.targetValue;
    }
}
