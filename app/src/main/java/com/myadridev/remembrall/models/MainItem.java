package com.myadridev.remembrall.models;

import android.content.Context;

import com.myadridev.remembrall.enums.MainItemEnum;

/**
 * Created by adrien on 29/08/15.
 */
public abstract class MainItem implements Comparable<MainItem> {
    protected Context context;
    private MainItemEnum itemType;

    public MainItem(Context _context, MainItemEnum _itemType) {
        context = _context;
        itemType = _itemType;
    }

    public MainItemEnum getItemType() {
        return itemType;
    }

    public abstract void onClick();
}
