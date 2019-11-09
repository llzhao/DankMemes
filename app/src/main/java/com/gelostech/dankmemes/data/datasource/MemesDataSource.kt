package com.gelostech.dankmemes.data.datasource

import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import com.gelostech.dankmemes.data.models.User
import com.gelostech.dankmemes.data.repositories.MemesRepository
import com.gelostech.dankmemes.data.wrappers.ItemViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

class MemesDataSource constructor(private val repository: MemesRepository,
                                  private val scope: CoroutineScope,
                                  private val user: User? = null): ItemKeyedDataSource<String, ItemViewModel>() {

    class Factory(private val repository: MemesRepository,
                  private val scope: CoroutineScope,
                  private val user: User? = null): DataSource.Factory<String, ItemViewModel>() {

        override fun create(): DataSource<String, ItemViewModel> {
            return MemesDataSource(repository, scope, user)
        }
    }

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<ItemViewModel>) {
        Timber.e("Loading initial...")

        scope.launch {
            if (user == null) {
                val memes = repository.fetchMemes()
                callback.onResult(memes)
            } else {
                val memes = repository.fetchMemesByUser(user.userId!!)
                memes.add(0, user)
                callback.onResult(memes)
            }
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<ItemViewModel>) {
        scope.launch {
            val memes = if (user == null) {
                repository.fetchMemes(loadAfter = params.key)
            } else {
                repository.fetchMemesByUser(userId = user.userId!!, loadAfter = params.key)
            }

            callback.onResult(memes)
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<ItemViewModel>) {
//        scope.launch {
//            val memes = if (user == null) {
//                repository.fetchMemes(loadBefore = params.key)
//            } else {
//                repository.fetchMemesByUser(userId = user.userId!!, loadBefore = params.key)
//            }
//
//            callback.onResult(memes)
//        }
    }

    override fun getKey(item: ItemViewModel): String = item.id
}