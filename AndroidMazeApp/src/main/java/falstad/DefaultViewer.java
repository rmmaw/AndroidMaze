package williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.falstad;

/**
 * Created by jeskay on 11/27/16.
 */

import java.io.Serializable;



public class DefaultViewer implements Viewer, Serializable {

    @Override
    public void redraw(MazePanel panel, int state, int px, int py,
                       int view_dx, int view_dy, int walk_step, int view_offset, RangeSet rset, int ang) {
        dbg("redraw") ;
    }

    @Override
    public void incrementMapScale() {
        dbg("incrementMapScale") ;
    }

    @Override
    public void decrementMapScale() {
        dbg("decrementMapScale") ;
    }


    private void dbg(String str) {
        //System.out.println("DefaultViewer" + str);
    }
}
