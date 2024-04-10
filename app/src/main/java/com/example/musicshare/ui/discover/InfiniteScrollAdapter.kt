package com.example.musicshare.ui.discover

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.musicshare.R
import com.bumptech.glide.Glide
import com.example.musicshare.BackendHandler
import com.example.musicshare.SelectableItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InfiniteScrollAdapterDiscover(private val context: Context, private var dataList: List<SelectableItem>) : RecyclerView.Adapter<InfiniteScrollAdapterDiscover.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val textView2: TextView = itemView.findViewById(R.id.textView2)
        val textView3: TextView = itemView.findViewById(R.id.textView3)
        val imageButton: ImageButton = itemView.findViewById(R.id.button)
        var isClicked: Boolean = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.scroll_item_discover, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Glide.with(context)
            .load(dataList[position].urlCover)
            .into(holder.imageView)
        holder.textView2.text= dataList[position].name

        var artists = ""

        for ((index, artist) in dataList[position].artists.withIndex()) {
            artists += artist

            if (index < dataList[position].artists.size - 1) {
                artists += ", "
            }
        }

        holder.textView3.text= artists

        holder.imageButton.setOnClickListener {
            val backendHandler = BackendHandler()
            val coroutineScope = CoroutineScope(Dispatchers.Main)

            if (!holder.isClicked){
                coroutineScope.launch(Dispatchers.IO) {
                    val status: Boolean = backendHandler.addPost(SelectableItem(urlCover =  dataList[position].urlCover, artists = dataList[position].artists, name = dataList[position].name))
                    withContext(Dispatchers.Main) {
                        if (status){
                            showToast("Song has been posted!", context)
                            holder.isClicked = true
                            holder.imageButton.setImageResource(R.drawable.ic_added_discover_foreground)
                        }
                        else{
                            showToast("Oops...something went wrong", context)
                        }
                    }
                }
            }
            else{
                showToast("Already added!", context)
            }
        }
    }

    override fun getItemCount(): Int {
        Log.i("List length", dataList.size.toString())
        return dataList.size
    }

    fun addData(newDataList: List<SelectableItem>) {
        val startPosition = dataList.size
        dataList = newDataList
        Log.i("Data List", "$dataList")
        notifyItemRangeInserted(startPosition, newDataList.size)
    }

    private fun showToast(message: String, context: Context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}
