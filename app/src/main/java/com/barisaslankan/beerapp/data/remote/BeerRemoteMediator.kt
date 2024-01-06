package com.barisaslankan.beerapp.data.remote

import android.net.http.HttpException
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.barisaslankan.beerapp.data.local.BeerDao
import com.barisaslankan.beerapp.data.local.BeerDatabase
import com.barisaslankan.beerapp.data.local.BeerEntity
import com.barisaslankan.beerapp.data.mappers.toBeerEntity
import java.io.IOException

@ExperimentalPagingApi
class BeerRemoteMediator(
    private val beerDatabase: BeerDatabase,
    private val beerApi: BeerApi
) : RemoteMediator<Int, BeerEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, BeerEntity>
    ): MediatorResult {

        return try {
            val loadKey = when(loadType){
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(
                    endOfPaginationReached = true
                )
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if(lastItem == null){
                        1
                    } else {
                        (lastItem.id / state.config.pageSize + 1)
                    }
                }
            }

            val beers = beerApi.getBeers(
                page = loadKey,
                pageCount = state.config.pageSize
            )

            beerDatabase.withTransaction {
                if(loadType == LoadType.REFRESH) {
                    beerDatabase.dao.clearAll()
                }
                val beerEntities = beers.map {
                    it.toBeerEntity()
                }
                beerDatabase.dao.upsertAll(beerEntities)
            }

            MediatorResult.Success(
                endOfPaginationReached = true
            )

        }catch (e: IOException){
            MediatorResult.Error(e)

        }catch (e: Exception){
            MediatorResult.Error(e)
        }
    }
}