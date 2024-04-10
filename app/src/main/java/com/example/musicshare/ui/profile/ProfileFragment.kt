package com.example.musicshare.ui.profile

import android.content.Context
import android.opengl.Visibility
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.musicshare.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicshare.App
import com.example.musicshare.BackendHandler
import com.example.musicshare.Item
import com.example.musicshare.MainActivity
import com.example.musicshare.ui.profile.InfiniteScrollAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment(), InfiniteScrollAdapter.AdapterEventListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: InfiniteScrollAdapter
    private lateinit var emptyListMessage: TextView
    private var dataList: MutableList<Item> = mutableListOf()
    private var backendHandler: BackendHandler = BackendHandler()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val textView: TextView = view.findViewById(R.id.username)
        textView.text = App.instance.username

        recyclerView = view.findViewById(R.id.recyclerViewProfile)
        emptyListMessage = view.findViewById(R.id.noResultsText)
        adapter = InfiniteScrollAdapter(requireContext(), dataList)
        recyclerView.adapter = adapter
        adapter.setAdapterEventListener(this)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        (activity as? MainActivity)?.updateNavigationIcon(R.drawable.icon_person_white_foreground)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount
                if (lastVisibleItemPosition == totalItemCount - 1) {

                }
            }
        })

        loadInitialData()
        adapter.notifyDataSetChanged()

        return view
    }

    override fun onPostClicked(item: Item, position: Int) {
        Handler(Looper.getMainLooper()).post {
            adapter.removeData(position)
        }
        showToast("Song removed: ${item.song}", requireContext())
    }

    private fun loadInitialData() {
        val coroutineScope = CoroutineScope(Dispatchers.Main)

        coroutineScope.launch(Dispatchers.IO) {
            val (responseStatus, items) = backendHandler.getOwnPosts()


            withContext(Dispatchers.Main) {
                if(responseStatus && !items.isNullOrEmpty()){
                    emptyListMessage.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    adapter.addData(items)
                }
                else if(responseStatus){
                    emptyListMessage.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }
                else{
                    showToast("Error, can't find any data", requireContext())
                }
            }
        }

        adapter.notifyDataSetChanged()
    }

    private fun showToast(message: String, context: Context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}

