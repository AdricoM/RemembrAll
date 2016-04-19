package com.adricom.remembrall.app.models;

import android.content.Context;

import com.adricom.remembrall.app.enums.MainItemEnum;

/**
 * Created by adrien on 29/08/15.
 */
public abstract class MainItem {
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
