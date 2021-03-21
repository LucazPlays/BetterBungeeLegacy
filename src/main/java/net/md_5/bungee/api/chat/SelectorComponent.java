package net.md_5.bungee.api.chat;

import java.beans.*;

public final class SelectorComponent extends BaseComponent
{
    private String selector;
    
    public SelectorComponent(final SelectorComponent original) {
        super(original);
        this.setSelector(original.getSelector());
    }
    
    @Override
    public SelectorComponent duplicate() {
        return new SelectorComponent(this);
    }
    
    protected void toLegacyText(final StringBuilder builder) {
        builder.append(this.selector);
        super.toLegacyText(builder);
    }
    
    public String getSelector() {
        return this.selector;
    }
    
    public void setSelector(final String selector) {
        this.selector = selector;
    }
    
    @Override
    public String toString() {
        return "SelectorComponent(selector=" + this.getSelector() + ")";
    }
    
    @ConstructorProperties({ "selector" })
    public SelectorComponent(final String selector) {
        this.selector = selector;
    }
}
