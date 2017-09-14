package williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.falstad;

/**
 * Created by jeskay on 11/27/16.
 */

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;

import java.io.Serializable;

import williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.ui.PlayActivity;

public class MazePanel implements Serializable{


    public Canvas canvas;
    public Paint currentColor = new Paint();
    private PlayActivity playActivity;
    public int setColor;


    public MazePanel() {
        super() ;
    }



    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public void setPlayActivity(PlayActivity playActivity) {
        this.playActivity = playActivity;

    }

    public void update() {
        playActivity.runOnUiThread(new Runnable() {public void run() {
            playActivity.updateGraphics();}});
    }



    public void setColor(String color) {

        if ("Red".equals(color)) setColor = Color.RED;
        else if ("Blue".equals(color)) setColor = Color.BLUE;
        else if ("Gray".equals(color)) setColor = Color.GRAY;
        else if ("Black".equals(color)) setColor = Color.BLACK;
        else if ("White".equals(color)) setColor = Color.WHITE;
        else if ("Orange".equals(color)) setColor = (0xFFFF8800);
        else if ("Yellow".equals(color)) setColor = Color.YELLOW;
        else if ("DarkGray".equals(color)) setColor = Color.DKGRAY;
        else {setColor = (0xFFFF8800); System.out.println("Used wrong color"); }
        currentColor.setColor(setColor);
    }


    public void setSegColor(String color, int val1) {
        if ("ColorOne".equals(color)) setColor = rgbInt(val1,20,20);
        else if ("ColorTwo".equals(color)) setColor = rgbInt(20,val1,20);
        else if ("ColorThree".equals(color)) setColor = rgbInt(20,20,val1);
        else if ("ColorFour".equals(color)) setColor = rgbInt(val1,val1,20);
        else if ("ColorFive".equals(color)) setColor = rgbInt(20,val1,val1);
        else if ("ColorSix".equals(color)) setColor = rgbInt(val1,20,val1);
        else setColor = rgbInt(20,20,20);
        currentColor.setColor(setColor);
    }

    public static int rgbInt(int red, int green, int blue){
        int rgb = (255)<<24|((red)<<16)|((green)<<8)|(blue);
        return rgb;
    }

    public static int getRedInt(int colorInt){
        return (colorInt >> 16) & 0xFF;
    }
    public static int getGreenInt(int colorInt){
        return (colorInt >> 8) & 0xFF;
    }
    public static int getBlueInt(int colorInt){
        return colorInt & 0xFF;
    }

    public static int getRGB(String color, int val1){
        if ("ColorOne".equals(color)) return  rgbInt(val1,20,20);
        else if ("ColorTwo".equals(color)) return rgbInt(20,val1,20);
        else if ("ColorThree".equals(color)) return rgbInt(20,20,val1);
        else if ("ColorFour".equals(color)) return rgbInt(val1,val1,20);
        else if ("ColorFive".equals(color)) return  rgbInt(20,val1,val1);
        else if ("ColorSix".equals(color)) return rgbInt(val1,20,val1);
        else return rgbInt(20,20,20);
    }

    public static String decipherSegColorValue(int colorInt) {
        if (getRedInt(colorInt) == 20 && getGreenInt(colorInt) == 20 && getBlueInt(colorInt) == 20)
            return "ColorSeven";
        else if (getGreenInt(colorInt) == 20 && getBlueInt(colorInt) == 20)
            return "ColorOne";
        else if (getRedInt(colorInt) == 20 && getBlueInt(colorInt) == 20)
            return "ColorTwo";
        else if (getRedInt(colorInt) == 20 && getGreenInt(colorInt) == 20)
            return "ColorThree";
        else if (getBlueInt(colorInt) == 20)
            return "ColorFour";
        else if (getRedInt(colorInt) == 20)
            return "ColorFive";
        else if (getGreenInt(colorInt) == 20)
            return "ColorSix";
        return "ColorOne";
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        canvas.drawLine(x1, y1, x2, y2, currentColor);

    }

    public void fillOval(int x, int y, int width, int height) {
        canvas.drawOval(new RectF(x, y, x + width, y + height), currentColor);
    }

    public void fillRect(int x, int y, int width, int height) {

            canvas.drawRect(new Rect(x, y, x+width, y+height), currentColor);

    }

    public void drawString(String str, int x, int y) {
        canvas.drawText(str, x, y, currentColor);
    }

    public void fillPolygon(int[] xps, int[] yps, int j) {
        final Path p = new Path();
        p.moveTo(xps[0], yps[0]);
        for (int i = 0; i < j; i++){
            p.lineTo(xps[i], yps[i]);
        }
        p.lineTo(xps[0], yps[0]);

        Paint segColor = new Paint();
        segColor.setColor(setColor);
        segColor.setStyle(Paint.Style.FILL);
        canvas.drawPath(p, segColor);

    }

}