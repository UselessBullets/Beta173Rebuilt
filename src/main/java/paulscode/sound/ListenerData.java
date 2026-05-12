// 
// Decompiled by Procyon v0.6.0
// 

package paulscode.sound;

public class ListenerData
{
    public Vector3D position;
    public Vector3D lookAt;
    public Vector3D up;
    public float angle;
    
    public ListenerData() {
        this.angle = 0.0f;
        this.position = new Vector3D(0.0f, 0.0f, 0.0f);
        this.lookAt = new Vector3D(0.0f, 0.0f, -1.0f);
        this.up = new Vector3D(0.0f, 1.0f, 0.0f);
        this.angle = 0.0f;
    }
    
    public ListenerData(final float float1, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8, final float float9, final float float10) {
        this.angle = 0.0f;
        this.position = new Vector3D(float1, float2, float3);
        this.lookAt = new Vector3D(float4, float5, float6);
        this.up = new Vector3D(float7, float8, float9);
        this.angle = float10;
    }
    
    public ListenerData(final Vector3D vector3D1, final Vector3D vector3D2, final Vector3D vector3D3, final float float4) {
        this.angle = 0.0f;
        this.position = vector3D1.clone();
        this.lookAt = vector3D2.clone();
        this.up = vector3D3.clone();
        this.angle = float4;
    }
    
    public void setData(final float float1, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8, final float float9, final float float10) {
        this.position.x = float1;
        this.position.y = float2;
        this.position.z = float3;
        this.lookAt.x = float4;
        this.lookAt.y = float5;
        this.lookAt.z = float6;
        this.up.x = float7;
        this.up.y = float8;
        this.up.z = float9;
        this.angle = float10;
    }
    
    public void setData(final Vector3D vector3D1, final Vector3D vector3D2, final Vector3D vector3D3, final float float4) {
        this.position.x = vector3D1.x;
        this.position.y = vector3D1.y;
        this.position.z = vector3D1.z;
        this.lookAt.x = vector3D2.x;
        this.lookAt.y = vector3D2.y;
        this.lookAt.z = vector3D2.z;
        this.up.x = vector3D3.x;
        this.up.y = vector3D3.y;
        this.up.z = vector3D3.z;
        this.angle = float4;
    }
    
    public void setData(final ListenerData listenerData) {
        this.position.x = listenerData.position.x;
        this.position.y = listenerData.position.y;
        this.position.z = listenerData.position.z;
        this.lookAt.x = listenerData.lookAt.x;
        this.lookAt.y = listenerData.lookAt.y;
        this.lookAt.z = listenerData.lookAt.z;
        this.up.x = listenerData.up.x;
        this.up.y = listenerData.up.y;
        this.up.z = listenerData.up.z;
        this.angle = listenerData.angle;
    }
    
    public void setPosition(final float float1, final float float2, final float float3) {
        this.position.x = float1;
        this.position.y = float2;
        this.position.z = float3;
    }
    
    public void setPosition(final Vector3D vector3D) {
        this.position.x = vector3D.x;
        this.position.y = vector3D.y;
        this.position.z = vector3D.z;
    }
    
    public void setOrientation(final float float1, final float float2, final float float3, final float float4, final float float5, final float float6) {
        this.lookAt.x = float1;
        this.lookAt.y = float2;
        this.lookAt.z = float3;
        this.up.x = float4;
        this.up.y = float5;
        this.up.z = float6;
    }
    
    public void setOrientation(final Vector3D vector3D1, final Vector3D vector3D2) {
        this.lookAt.x = vector3D1.x;
        this.lookAt.y = vector3D1.y;
        this.lookAt.z = vector3D1.z;
        this.up.x = vector3D2.x;
        this.up.y = vector3D2.y;
        this.up.z = vector3D2.z;
    }
    
    public void setAngle(final float float1) {
        this.angle = float1;
        this.lookAt.x = -1.0f * (float)Math.sin(this.angle);
        this.lookAt.z = -1.0f * (float)Math.cos(this.angle);
    }
}
