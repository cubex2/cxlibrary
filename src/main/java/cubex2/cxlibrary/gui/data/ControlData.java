package cubex2.cxlibrary.gui.data;

import cubex2.cxlibrary.gui.control.Anchor;
import cubex2.cxlibrary.gui.control.Control;
import cubex2.cxlibrary.gui.control.ControlContainer;

public class ControlData
{
    public String name;

    private Integer width = null;
    private Integer height = null;

    private Integer left = null;
    private Integer right = null;
    private Integer top = null;
    private Integer bottom = null;

    private String cleft = null; // null is parent
    private String cright = null; // null is parent
    private String ctop = null; // null is parent
    private String cbottom = null; // null is parent

    /** Overrides left and right. Sets both to 0 */
    private Boolean centerHor = null;
    /** Overrides top and bottom. Sets both to 0 */
    private Boolean centerVert = null;

    /** Overrides centerHor and centerVert. Sets both to true */
    private Boolean center = null;

    public void apply(Anchor anchor, ControlContainer parent, IControlProvider provider)
    {
        if (center != null && center)
            centerHor = centerVert = true;

        if (centerHor != null && centerHor)
            left = right = 0;
        if (centerVert != null && centerVert)
            top = bottom = 0;

        if (width != null)
            anchor.width(width);
        if (height != null)
            anchor.height(height);

        if (left != null)
        {
            Control c = cleft == null ? parent : provider.getControl(cleft);
            anchor.left(c, left, c == parent);
        }

        if (right != null)
        {
            Control c = cright == null ? parent : provider.getControl(cright);
            anchor.right(c, right, c == parent);
        }

        if (top != null)
        {
            Control c = ctop == null ? parent : provider.getControl(ctop);
            anchor.top(c, top, c == parent);
        }

        if (bottom != null)
        {
            Control c = cbottom == null ? parent : provider.getControl(cbottom);
            anchor.bottom(c, bottom, c == parent);
        }
    }

    void claimChildValues(ControlData data)
    {
        if (data.width != null)
            width = data.width;
        if (data.height != null)
            height = data.height;
        if (data.left != null)
            left = data.left;
        if (data.right != null)
            right = data.right;
        if (data.top != null)
            top = data.top;
        if (data.bottom != null)
            bottom = data.bottom;

        if (data.center != null)
            center = data.center;
        if (data.centerHor != null)
            centerHor = data.centerHor;
        if (data.centerVert != null)
            centerVert = data.centerVert;
    }


}
