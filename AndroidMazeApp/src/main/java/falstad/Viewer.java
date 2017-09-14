package williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.falstad;

/**
 * Created by jeskay on 11/27/16.
 */

public interface Viewer {
    /**
     * Updates what is on display on the screen. The behavior depends on the particular state of the maze.
     * Classes that implement this interface use the state parameter to recognize if they should react to
     * the method call or ignore it.
     *
     * @param panel        graphics handler for the buffer image that this class draws on
     * @param state        is the state of the maze
     * @param px           x coordinate of current position, only used to get viewx
     * @param py           y coordinate of current position, only used to get viewy
     * @param view_dx      view direction, x coordinate
     * @param view_dy      view direction, y coordinate
     * @param rset
     * @param ang
     * @param walk_step,   only used to get viewx and viewy
     * @param view_offset, only used to get viewx and viewy
     */
    public void redraw(MazePanel panel, int state, int px, int py, int view_dx, int view_dy, int walk_step, int view_offset, RangeSet rset, int ang);

    /**
     * Increases the map if the map is on display.
     */
    public void incrementMapScale();

    /**
     * Shrinks the map if the map is on display.
     */
    public void decrementMapScale();


}