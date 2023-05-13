package com.shendun.renter.ui.popup;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.shendun.renter.ui.popup.SortMultiItemEntity.TYPE_CONTENT;


/**
 * Author:xw
 * Time: 2:14 PM
 */
public class RoomsPopupList {

    public static final String RoomsAll = "全部房型";
    public static final String RoomsBigBed = "大床房";
    public static final String RoomsCheap = "特价房";
    public static final String RoomsExpensive = "豪华套房";
    public static final String RoomsSingle = "单人房";
    public static final String RoomsLandscape = "景观房";

    public RoomsPopupList() {
    }

    /**
     * 返回选中的标题
     *
     * @param position
     * @return
     */
    public static String getPopTitle(int position) {
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add(RoomsAll);
        arrayList.add(RoomsBigBed);
        arrayList.add(RoomsCheap);
        arrayList.add(RoomsExpensive);
        arrayList.add(RoomsSingle);
        arrayList.add(RoomsLandscape);
        return arrayList.get(position);
    }

    public static List<SortMultiItemEntity> createFilesContentList(final String filesContent){
        Map<String, SortMultiItemEntity> map = new LinkedHashMap<>();
        map.put(RoomsAll, new SortMultiItemEntity(TYPE_CONTENT, false, RoomsAll));
        map.put(RoomsBigBed, new SortMultiItemEntity(TYPE_CONTENT, false, RoomsBigBed));
        map.put(RoomsCheap, new SortMultiItemEntity(TYPE_CONTENT, false, RoomsCheap));
        map.put(RoomsExpensive, new SortMultiItemEntity(TYPE_CONTENT, false, RoomsExpensive));
        map.put(RoomsSingle, new SortMultiItemEntity(TYPE_CONTENT, false, RoomsSingle));
        map.put(RoomsLandscape, new SortMultiItemEntity(TYPE_CONTENT, false, RoomsLandscape));

        if(RoomsAll.equals(filesContent)){
            map.put(RoomsAll,new SortMultiItemEntity(TYPE_CONTENT, true, RoomsAll));
        } else if(RoomsBigBed.equals(filesContent)){
            map.put(RoomsBigBed,new SortMultiItemEntity(TYPE_CONTENT, true, RoomsBigBed));
        } else if(RoomsCheap.equals(filesContent)){
            map.put(RoomsCheap,new SortMultiItemEntity(TYPE_CONTENT, true, RoomsCheap));
        } else if(RoomsExpensive.equals(filesContent)){
            map.put(RoomsExpensive,new SortMultiItemEntity(TYPE_CONTENT, true, RoomsExpensive));
        } else if(RoomsSingle.equals(filesContent)){
            map.put(RoomsSingle,new SortMultiItemEntity(TYPE_CONTENT, true, RoomsSingle));
        } else if(RoomsLandscape.equals(filesContent)){
            map.put(RoomsLandscape,new SortMultiItemEntity(TYPE_CONTENT, true, RoomsLandscape));
        }

        return new ArrayList<>(map.values());
    }
}
