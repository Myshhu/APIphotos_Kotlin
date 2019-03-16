package com.example.apiphotos_kotlin

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import org.json.JSONArray
import java.util.*
import kotlin.collections.HashSet

class MyExpandableListAdapter(
    private var context: Context
) : BaseExpandableListAdapter() {

    private lateinit var photoInfoJSONArray: JSONArray
    private lateinit var uniqueAlbumIdsSet: HashSet<Int>

    fun photoInfoJSONArray(jsonArray: JSONArray) {
        photoInfoJSONArray = jsonArray
    }

    override fun getGroup(groupPosition: Int): Any {
        uniqueAlbumIdsSet = HashSet()

        for(i: Int in 0..photoInfoJSONArray.length()) {
            uniqueAlbumIdsSet.add(
                photoInfoJSONArray.getJSONObject(i).getInt("albumId")
            )
        }
        return uniqueAlbumIdsSet.size
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int) = true

    override fun hasStableIds() = false

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        TODO("not implemented") //File | Settings | File Templates.
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        TODO("not implemented") //File | Settings | File Templates.
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        TODO("not implemented") //File | Settings | File Templates.
    }

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        TODO("not implemented") //File | Settings | File Templates.
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun getGroupCount(): Int {
        TODO("not implemented") //File | Settings | File Templates.
    }

}