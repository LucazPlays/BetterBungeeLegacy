package net.md_5.bungee.api.chat;

import java.beans.*;

public final class ScoreComponent extends BaseComponent
{
    private String name;
    private String objective;
    private String value;
    
    public ScoreComponent(final String name, final String objective) {
        this.value = "";
        this.setName(name);
        this.setObjective(objective);
    }
    
    public ScoreComponent(final ScoreComponent original) {
        super(original);
        this.value = "";
        this.setName(original.getName());
        this.setObjective(original.getObjective());
        this.setValue(original.getValue());
    }
    
    @Override
    public ScoreComponent duplicate() {
        return new ScoreComponent(this);
    }
    
    protected void toLegacyText(final StringBuilder builder) {
        builder.append(this.value);
        super.toLegacyText(builder);
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getObjective() {
        return this.objective;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setObjective(final String objective) {
        this.objective = objective;
    }
    
    public void setValue(final String value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return "ScoreComponent(name=" + this.getName() + ", objective=" + this.getObjective() + ", value=" + this.getValue() + ")";
    }
    
    @ConstructorProperties({ "name", "objective", "value" })
    public ScoreComponent(final String name, final String objective, final String value) {
        this.value = "";
        this.name = name;
        this.objective = objective;
        this.value = value;
    }
}
