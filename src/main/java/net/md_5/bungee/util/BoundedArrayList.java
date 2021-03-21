package net.md_5.bungee.util;

import com.google.common.base.*;
import java.util.*;

public class BoundedArrayList<E> extends ArrayList<E>
{
    private final int maxSize;
    
    public BoundedArrayList(final int maxSize) {
        this.maxSize = maxSize;
    }
    
    private void checkSize(final int increment) {
        Preconditions.checkState(this.size() + increment <= this.maxSize, "Adding %s elements would exceed capacity of %s", increment, this.maxSize);
    }
    
    @Override
    public boolean add(final E e) {
        this.checkSize(1);
        return super.add(e);
    }
    
    @Override
    public void add(final int index, final E element) {
        this.checkSize(1);
        super.add(index, element);
    }
    
    @Override
    public boolean addAll(final Collection<? extends E> c) {
        this.checkSize(c.size());
        return super.addAll(c);
    }
    
    @Override
    public boolean addAll(final int index, final Collection<? extends E> c) {
        this.checkSize(c.size());
        return super.addAll(index, c);
    }
}
