package com.example.musicshare.ui.home

import android.annotation.SuppressLint
import android.content.Context
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

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.textView1)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val textView2: TextView = itemView.findViewById(R.id.textView2)
        val textView3: TextView = itemView.findViewById(R.id.textView3)
        val imageButton: ImageButton = itemView.findViewById(R.id.button)
        val likeText: TextView = itemView.findViewById(R.id.likeNumber)
        var isClicked: Boolean = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.scroll_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = dataList[position].username

        Glide.with(context)
            .load(dataList[position].imgLink)
            .into(holder.imageView)
        holder.textView2.text= dataList[position].song
        holder.textView3.text= dataList[position].artist
        holder.likeText.text= dataList[position].likes.toString()

        holder.imageButton.setOnClickListener {
            val backendHandler = BackendHandler()
            val coroutineScope = CoroutineScope(Dispatchers.Main)

            if (!holder.isClicked){
                coroutineScope.launch(Dispatchers.IO) {
                    val status: Boolean = backendHandler.addLike(dataList[position]._id)
                    withContext(Dispatchers.Main) {
                        if (status){
                            holder.likeText.text = (dataList[position].likes + 1).toString()
                            showToast("<3", context)
                            holder.isClicked = true
                            holder.imageButton.setImageResource(R.drawable.ic_like_foreground)
                        }
                        else{
                            showToast("Oops...something went wrong", context)
                        }
                    }
                }
            }
            else{
                coroutineScope.launch(Dispatchers.IO) {
                    var status: Boolean = backendHandler.removeLike(dataList[position]._id)
                    withContext(Dispatchers.Main) {
                        if (status){
                            holder.likeText.text = (dataList[position].likes).toString()
                            showToast("</3", context)
                            holder.isClicked = false
                            holder.imageButton.setImageResource(R.drawable.ic_nolike_foreground)
                        }
                        else{
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

    fun getLastItemId(): String{
        return dataList.last()._id
    }

    fun getFirstItemId(): String{
        return dataList.first()._id
    }

    fun addData(newDataList: List<Item>) {
        val startPosition = dataList.size
        dataList.addAll(newDataList)
        notifyItemRangeInserted(startPosition, newDataList.size)
    }

    fun addDataFront(itemsNew: List<Item>) {
        dataList.addAll(0, itemsNew)
        notifyItemRangeInserted(0, itemsNew.size)
    }

    private fun showToast(message: String, context: Context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
