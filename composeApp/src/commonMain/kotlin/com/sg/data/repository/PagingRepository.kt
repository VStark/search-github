package com.sg.data.repository

import androidx.paging.PagingData
import com.sg.data.model.Repo
import kotlinx.coroutines.flow.Flow

interface PagingRepository {
    fun getPagedRepos(query: String, perPage: Int): Flow<PagingData<Repo>>
}