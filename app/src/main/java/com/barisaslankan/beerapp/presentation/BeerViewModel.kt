package com.barisaslankan.beerapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import androidx.paging.map
import com.barisaslankan.beerapp.data.local.BeerDatabase
import com.barisaslankan.beerapp.data.local.BeerEntity
import com.barisaslankan.beerapp.data.mappers.toBeer
import com.barisaslankan.beerapp.data.remote.BeerApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class BeerViewModel @Inject constructor(
    pager: Pager<Int, BeerEntity>
): ViewModel(){

    val beerPagingFlow = pager
        .flow
        .map { pagingData ->
            pagingData.map { it.toBeer() }
        }
        .cachedIn(viewModelScope)

}