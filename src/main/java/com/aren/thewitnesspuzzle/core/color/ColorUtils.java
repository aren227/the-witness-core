package com.aren.thewitnesspuzzle.core.color;

public class ColorUtils {

    public static int RGB(String hex) {
        hex = hex.replace("#", "");
        long color = Long.parseLong(hex, 16);
        if (hex.length() == 6)
            color |= 0x00000000ff000000;
        return (int) color;
    }

    public static int RGB(int r, int g, int b) {
        return ARGB(255, r, g, b);
    }

    public static int ARGB(int a, int r, int g, int b) {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static int lerp(int a, int b, float t) {
        int ab = a & 0xff;
        int ag = (a >> 8) & 0xff;
        int ar = (a >> 16) & 0xff;
        int aa = (a >> 24) & 0xff;

        int bb = b & 0xff;
        int bg = (b >> 8) & 0xff;
        int br = (b >> 16) & 0xff;
        int ba = (b >> 24) & 0xff;

        float it = 1f - t;

        int cb = (int) (ab * it + bb * t);
        int cg = (int) (ag * it + bg * t);
        int cr = (int) (ar * it + br * t);
        int ca = (int) (aa * it + ba * t);

        return ARGB(ca, cr, cg, cb);
    }

}
