package com.example.apiphotos_kotlin

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.net.URL

class MyExpandableListAdapter(
    private var context: Context
) : BaseExpandableListAdapter() {

    private var photoInfoJSONArray: JSONArray? = null
    private lateinit var listOfAllAlbumIds: List<Int>

    fun setPhotoInfoJSONArray(jsonArray: JSONArray) {
        photoInfoJSONArray = jsonArray
    }

    override fun getGroup(groupPosition: Int) = listOfAllAlbumIds.elementAt(groupPosition)

    override fun isChildSelectable(groupPosition: Int, childPosition: Int) = true

    override fun hasStableIds() = false

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View? {
        val groupTitle: String = listOfAllAlbumIds.get(groupPosition).toString()
        var view = convertView
        if (view == null) {
            val layoutInflater: LayoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = layoutInflater.inflate(R.layout.list_group, null)
        }
        val groupTitleTextView: TextView? = view?.findViewById(R.id.groupTitle)
        groupTitleTextView?.text = groupTitle
        return view
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        var count = 0
        for (i: Int in 0 until (photoInfoJSONArray?.length() ?: 0)) {
            val currentLoopPhotoAlbumId = photoInfoJSONArray?.getJSONObject(i)?.getInt("albumId")

            if (currentLoopPhotoAlbumId == groupPosition + 1) {
                count++
            }
        }
        return count
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        val photosInAlbumAtGroupPosition: MutableList<JSONObject> = ArrayList()
        for (i: Int in 0 until (photoInfoJSONArray?.length() ?: 0)) {
            if (photoInfoJSONArray?.getJSONObject(i)?.getInt("albumId") ?: 0 == groupPosition + 1) {
                photoInfoJSONArray?.getJSONObject(i)?.let { photosInAlbumAtGroupPosition.add(it) }
            }
        }
        return photosInAlbumAtGroupPosition[childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View? {
        var stringAlbumID: String? = null
        var stringID: String? = null
        var stringTitle: String? = null
        var stringUrl: String? = null
        var stringThumbnailUrl: String? = null
        //Get text values from JSON
        stringAlbumID = (getChild(groupPosition, childPosition) as JSONObject).getString("albumId")
        stringID = (getChild(groupPosition, childPosition) as JSONObject).getString("id")
        stringTitle = (getChild(groupPosition, childPosition) as JSONObject).getString("title")
        stringUrl = (getChild(groupPosition, childPosition) as JSONObject).getString("url")
        stringThumbnailUrl = (getChild(groupPosition, childPosition) as JSONObject).getString("thumbnailUrl")

        var view: View? = convertView
        if (view == null) {
            val layoutInflater: LayoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = layoutInflater.inflate(R.layout.list_item, null)
        }

        //Setting text values
        val albumId: TextView? = view?.findViewById(R.id.albumId)
        albumId?.text = stringAlbumID
        val id: TextView? = view?.findViewById(R.id.id)
        id?.text = stringID
        val title: TextView? = view?.findViewById(R.id.title)
        title?.text = stringTitle
        val url: TextView? = view?.findViewById(R.id.url)
        url?.text = stringUrl
        val tnUrl: TextView? = view?.findViewById(R.id.thumbnailUrl)
        tnUrl?.text = stringThumbnailUrl

        //Setting image
        val imageView: ImageView? = view?.findViewById(R.id.image)
        Thread {
            //Load image from URL
            val bitmap = BitmapFactory.decodeStream(
                URL((getChild(groupPosition, childPosition) as JSONObject).getString("url")).content as InputStream
            )
            //Set image to imageView
            (context as MainActivity).runOnUiThread { imageView?.setImageBitmap(bitmap) }
        }.start()
        return view
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun getGroupCount(): Int {
        val uniqueAlbumIdsSet: HashSet<Int> = HashSet()

        for (i: Int in 0 until (photoInfoJSONArray?.length() ?: 0)) {
            photoInfoJSONArray?.getJSONObject(i)?.getInt("albumId")?.let {
                uniqueAlbumIdsSet.add(
                    it
                )
            }
        }
        listOfAllAlbumIds = uniqueAlbumIdsSet.sorted()
        return listOfAllAlbumIds.size
    }

}