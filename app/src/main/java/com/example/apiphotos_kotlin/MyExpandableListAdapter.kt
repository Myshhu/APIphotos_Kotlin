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
        val groupTitle: String = listOfAllAlbumIds[groupPosition].toString()
        var view = convertView
        if (view == null) {
            val layoutInflater: LayoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = layoutInflater.inflate(R.layout.list_group, null)
        }
        val groupTitleTextView: TextView? = view?.findViewById(R.id.groupTitle)
        groupTitleTextView?.text = "Album: $groupTitle"
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


    enum class PhotoTextViewStrings {
        AlbumId,
        ID,
        Title,
        Url,
        ThumbnailUrl
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View? {

        val view: View? = createConvertView(convertView)

        Thread {
            val textViewStringsList: MutableList<String> = ArrayList()

            //Get text values from JSON
            initTextViewStringsList(textViewStringsList, groupPosition, childPosition)

            //Setting text values
            setTextViewsValues(view, textViewStringsList)

            //Setting image
            val imageView: ImageView? = view?.findViewById(R.id.image)
            downloadAndSetImageViewPicture(imageView, groupPosition, childPosition)
        }.start()

        return view
    }

    private fun initTextViewStringsList(
        textViewStringsList: MutableList<String>,
        groupPosition: Int,
        childPosition: Int
    ) {
        textViewStringsList.add((getChild(groupPosition, childPosition) as JSONObject).getString("albumId"))
        textViewStringsList.add((getChild(groupPosition, childPosition) as JSONObject).getString("id"))
        textViewStringsList.add((getChild(groupPosition, childPosition) as JSONObject).getString("title"))
        textViewStringsList.add((getChild(groupPosition, childPosition) as JSONObject).getString("url"))
        textViewStringsList.add((getChild(groupPosition, childPosition) as JSONObject).getString("thumbnailUrl"))
    }

    private fun createConvertView(convertView: View?): View? {
        var tempView: View? = convertView
        if (tempView == null) {
            val layoutInflater: LayoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            tempView = layoutInflater.inflate(R.layout.list_item, null)
        }
        return tempView
    }

    private fun downloadAndSetImageViewPicture(imageView: ImageView?, groupPosition: Int, childPosition: Int) {
        Thread {
            //Load image from URL
            val bitmap = BitmapFactory.decodeStream(
                URL((getChild(groupPosition, childPosition) as JSONObject).getString("thumbnailUrl")).content as InputStream
            )
            //Set image to imageView
            (context as MainActivity).runOnUiThread { imageView?.setImageBitmap(bitmap) }
        }.start()
    }

    private fun setTextViewsValues(view: View?, textViewStringsList: MutableList<String>) {
        (context as MainActivity).runOnUiThread {
            val albumId: TextView? = view?.findViewById(R.id.albumId)
            albumId?.text = "Album: " + textViewStringsList[PhotoTextViewStrings.AlbumId.ordinal]

            val id: TextView? = view?.findViewById(R.id.id)
            id?.text = "Photo id: " + textViewStringsList[PhotoTextViewStrings.ID.ordinal]

            val title: TextView? = view?.findViewById(R.id.title)
            title?.text = textViewStringsList[PhotoTextViewStrings.Title.ordinal]

            val url: TextView? = view?.findViewById(R.id.url)
            url?.text = textViewStringsList[PhotoTextViewStrings.Url.ordinal]

            val tnUrl: TextView? = view?.findViewById(R.id.thumbnailUrl)
            tnUrl?.text = textViewStringsList[PhotoTextViewStrings.ThumbnailUrl.ordinal]
        }
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun getGroupCount(): Int {
        val uniqueAlbumIdsSet: HashSet<Int> = HashSet()
        for (i: Int in 0 until (photoInfoJSONArray?.length() ?: 0)) {
            photoInfoJSONArray?.getJSONObject(i)?.getInt("albumId")?.let {
                uniqueAlbumIdsSet.add(it)
            }
        }
        listOfAllAlbumIds = uniqueAlbumIdsSet.sorted()
        return listOfAllAlbumIds.size
    }
}