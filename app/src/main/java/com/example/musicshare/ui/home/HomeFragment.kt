package com.example.musicshare.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicshare.BackendHandler
import com.example.musicshare.Item
import com.example.musicshare.MainActivity
import com.example.musicshare.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: InfiniteScrollAdapter
    private var dataList: MutableList<Item> = mutableListOf()
    private val backendHandler:BackendHandler = BackendHandler()
    private var isScrolling = false
    private val debounceDelay = 300L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        adapter = InfiniteScrollAdapter(requireContext(), dataList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        (activity as? MainActivity)?.updateNavigationIcon(R.drawable.icon_home_white_foreground)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!isScrolling) {
                    isScrolling = true

                    recyclerView.postDelayed({
                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                        val totalItemCount = layoutManager.itemCount
                        if (lastVisibleItemPosition == totalItemCount - 1) {
                            loadData(LoadingType.Later)
                        }
                        isScrolling = false
                    }, debounceDelay)
                }
            }
        })

        isScrolling = true
        if(adapter.itemCount == 0) {
            loadData(LoadingType.Initial)
            adapter.notifyDataSetChanged()
        }
        isScrolling = false
        return view
    }

    private fun loadData(type: LoadingType) {

        val coroutineScope = CoroutineScope(Dispatchers.Main)

        coroutineScope.launch(Dispatchers.IO) {
            var responseStatusNew: Boolean = false
            var itemsNew: List<Item> = mutableListOf<Item>()
            var responseStatusOld: Boolean = false
            var itemsOld: List<Item> = mutableListOf<Item>()

            if (type == LoadingType.Initial) {
                val result = backendHandler.getAllPosts(type = "old")
                responseStatusOld = result.first
                itemsOld = result.second
            } else if (type == LoadingType.Later) {
                val resultOld = backendHandler.getAllPosts(type = "old", id = adapter.getLastItemId())
                responseStatusOld = resultOld.first
                itemsOld = resultOld.second
                val resultNew = backendHandler.getAllPosts(type = "new", id = adapter.getFirstItemId())
                responseStatusNew = resultNew.first
                itemsNew = resultNew.second
            }
            withContext(Dispatchers.Main) {
                if(responseStatusOld){
                    adapter.addData(itemsOld)
                }
                else{
                    showToast("Error, can't find any data", requireContext())
                }
                if(responseStatusNew){
                    adapter.addDataFront(itemsNew)
                }
            }
        }

        adapter.notifyDataSetChanged()
    }

    private fun showToast(message: String, context: Context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

enum class LoadingType{Initial, Later}