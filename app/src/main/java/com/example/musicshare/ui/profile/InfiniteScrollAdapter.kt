package com.example.musicshare.ui.profile

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.musicshare.R
import com.bumptech.glide.Glide
import com.example.musicshare.BackendHandler
import com.example.musicshare.Item
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InfiniteScrollAdapter(private val context: Context, private val dataList: MutableList<Item>) : RecyclerView.Adapter<InfiniteScrollAdapter.ViewHolder>() {

    interface AdapterEventListener {
        fun onPostClicked(item: Item, position: Int)
    }

    private var adapterEventListener: AdapterEventListener? = null

    fun setAdapterEventListener(listener: AdapterEventListener) {
        adapterEventListener = listener
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val textView2: TextView = itemView.findViewById(R.id.textView2)
        val textView3: TextView = itemView.findViewById(R.id.textView3)
        val imageButton: ImageButton = itemView.findViewById(R.id.button)
        var isClicked: Boolean = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.scroll_item_profile, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (dataList.isNotEmpty() && position < dataList.size) {
            val currentItem = dataList[position]
            Glide.with(context)
                .load(currentItem.imgLink)
                .into(holder.imageView)
            holder.textView2.text = currentItem.song
            holder.textView3.text = currentItem.artist
            holder.imageButton.setOnClickListener {
                val backendHandler = BackendHandler()
                val coroutineScope = CoroutineScope(Dispatchers.Main)

                adapterEventListener?.onPostClicked(dataList[position], position)
                coroutineScope.launch(Dispatchers.IO) {
                    val status: Boolean = backendHandler.deletePost(dataList[position]._id)
                    withContext(Dispatchers.Main) {
                        if (status) {
                            holder.isClicked = true
                        } else {
                            showToast("Oops...something went wrong", context)
                        }
                    }
                }


            }
        }
    }

    override fun getItemCount(): Int {
        Log.i("List length", dataList.size.toString())
        return dataList.size
    }

    fun addData(newDataList: List<Item>) {
        val startPosition = dataList.size
        dataList.addAll(newDataList)
        notifyItemRangeInserted(startPosition, newDataList.size)
    }

    fun removeData(position: Int) {

        Handler(Looper.getMainLooper()).post {
            dataList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, dataList.size - position)
        }

    }

    private fun showToast(message: String, context: Context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

