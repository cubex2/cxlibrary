package cubex2.cxlibrary.gui.control;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cubex2.cxlibrary.gui.data.IControlProvider;

import java.util.List;
import java.util.Map;

public class ControlContainer<T extends Control> extends Control implements IControlProvider
{
    private final List<T> children = Lists.newLinkedList();
    private final Map<String, T> childrenMap = Maps.newHashMap();

    public ControlContainer(Anchor anchor, ControlContainer parent)
    {
        super(anchor, parent);
    }

    public void addChild(T child, String name)
    {
        children.add(child);
        if (name != null)
            childrenMap.put(name, child);
    }

    @Override
    public Control getControl(String name)
    {
        if (childrenMap.containsKey(name))
            return childrenMap.get(name);
        /*if (parent != null && parent != this)
            return parent.getControl(name);*/

        return null;
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);

        children.forEach(c -> c.setEnabled(enabled));
    }

    @Override
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);

        children.forEach(c -> c.setVisible(visible));
    }

    @Override
    public void updateBounds()
    {
        super.updateBounds();

        children.forEach(T::updateBounds);
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        super.draw(mouseX, mouseY, partialTicks);

        children.stream()
                .filter(Control::isVisible)
                .forEach(c -> c.draw(mouseX, mouseY, partialTicks));
    }

    @Override
    public void update(int mouseX, int mouseY)
    {
        super.update(mouseX, mouseY);

        children.forEach(c -> c.update(mouseX, mouseY));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button)
    {
        super.mouseClicked(mouseX, mouseY, button);

        children.stream()
                .filter(Control::isEnabled)
                .forEach(c -> c.mouseClicked(mouseX, mouseY, button));
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int button)
    {
        super.mouseReleased(mouseX, mouseY, button);

        children.stream()
                .filter(Control::isEnabled)
                .forEach(c -> c.mouseReleased(mouseX, mouseY, button));
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int button, long timeSinceLastClick)
    {
        super.mouseClickMove(mouseX, mouseY, button, timeSinceLastClick);

        children.stream()
                .filter(Control::isEnabled)
                .forEach(c -> c.mouseClickMove(mouseX, mouseY, button, timeSinceLastClick));
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {
        super.keyTyped(typedChar, keyCode);

        children.stream()
                .filter(Control::isEnabled)
                .forEach(c -> c.keyTyped(typedChar, keyCode));
    }

    @Override
    public void onClosed()
    {
        super.onClosed();

        children.forEach(Control::onClosed);
    }


}
