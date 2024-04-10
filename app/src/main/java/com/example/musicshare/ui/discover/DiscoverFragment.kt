package com.example.musicshare.ui.discover

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.musicshare.R
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicshare.MainActivity
import com.example.musicshare.SpotifyApiConnector
import com.example.musicshare.SelectableItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DiscoverFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: InfiniteScrollAdapterDiscover
    private var dataList: List<SelectableItem> = listOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val connector = SpotifyApiConnector.getInstance()
        val view = inflater.inflate(R.layout.fragment_discover, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewDiscover)
        adapter = InfiniteScrollAdapterDiscover(requireContext(), dataList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        (activity as? MainActivity)?.updateNavigationIcon(R.drawable.icon_discover_white_foreground)

        val searchButton: Button = view.findViewById(R.id.buttonSearch)
        val searchField: EditText = view.findViewById(R.id.editTextSearch)

        searchButton.setOnClickListener {
            val searchText = searchField.text.toString()
            performSearch(searchText, connector)
        }

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


        return view
    }

    private fun performSearch(searchText: String, connector: SpotifyApiConnector) {
        Log.i("Search Text", searchText)

        val coroutineScope = CoroutineScope(Dispatchers.Main)

        coroutineScope.launch(Dispatchers.IO) {
            val dataList = connector.performSearch(searchText)
            withContext(Dispatchers.Main) {
                adapter.addData(dataList)
            }
        }

    }
}
