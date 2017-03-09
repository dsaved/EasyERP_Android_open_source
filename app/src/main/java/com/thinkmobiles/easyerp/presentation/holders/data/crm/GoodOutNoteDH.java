package com.thinkmobiles.easyerp.presentation.holders.data.crm;

import com.thinkmobiles.easyerp.data.model.inventory.goods_out_notes.GoodsOutNoteItem;
import com.thinkmobiles.easyerp.presentation.base.rules.master.selectable.SelectableDHHelper;

/**
 * Created by Lynx on 3/7/2017.
 */

public class GoodOutNoteDH extends SelectableDHHelper {

    private GoodsOutNoteItem item;

    public GoodOutNoteDH(GoodsOutNoteItem item) {
        this.item = item;
    }

    public GoodsOutNoteItem getItem() {
        return item;
    }

    @Override
    public String getId() {
        return item.id;
    }
}
