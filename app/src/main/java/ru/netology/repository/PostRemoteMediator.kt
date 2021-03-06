package ru.netology.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import retrofit2.Response
import ru.netology.AppDb
import ru.netology.db.PostRemoteKeyEntity
import ru.netology.db.dao.PostRemoteKeyDao
import ru.netology.db.toEntity
import ru.netology.model.FeedItem
import ru.netology.network.ApiError
import ru.netology.network.PostAPI
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator @Inject constructor(
    private val service: PostAPI,
    private val db: AppDb,
    private val postRemoteKeyDao: PostRemoteKeyDao,
) : RemoteMediator<Int, FeedItem>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, FeedItem>
    ): MediatorResult {
        try {
            val response = when (loadType) {
                LoadType.REFRESH -> {
                    val id = postRemoteKeyDao.max()
                    if (id == null) {
                        service.getLatest(state.config.initialLoadSize)
                    } else {
                        service.getAfter(id, state.config.pageSize)
                    }

                }
                LoadType.PREPEND -> {
                    Response.success(emptyList())
                }
                LoadType.APPEND -> {
                    val id = postRemoteKeyDao.min() ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    service.getBefore(id, state.config.pageSize)
                }
            }

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(
                response.code(),
                response.message(),
            )

            if (body.isNotEmpty()) {
                db.withTransaction {
                    when (loadType) {
                        LoadType.REFRESH -> {
                            if (postRemoteKeyDao.isEmpty()) {
                                postRemoteKeyDao.insert(
                                    listOf(
                                        PostRemoteKeyEntity(
                                            type = PostRemoteKeyEntity.KeyType.AFTER,
                                            id = body.first().id,
                                        ),
                                        PostRemoteKeyEntity(
                                            type = PostRemoteKeyEntity.KeyType.BEFORE,
                                            id = body.last().id,
                                        ),
                                    )
                                )
                            } else {
                                postRemoteKeyDao.insert(
                                    PostRemoteKeyEntity(
                                        type = PostRemoteKeyEntity.KeyType.AFTER,
                                        id = body.first().id,
                                    )
                                )
                            }
                        }
                        LoadType.PREPEND -> {
                            postRemoteKeyDao.insert(
                                PostRemoteKeyEntity(
                                    type = PostRemoteKeyEntity.KeyType.AFTER,
                                    id = body.first().id,
                                )
                            )
                        }
                        LoadType.APPEND -> {
                            postRemoteKeyDao.insert(
                                PostRemoteKeyEntity(
                                    type = PostRemoteKeyEntity.KeyType.BEFORE,
                                    id = body.last().id,
                                )
                            )
                        }
                    }
                    db.postDao().insert(body.toEntity())
                }
            }
            return MediatorResult.Success(endOfPaginationReached = body.isEmpty())
        } catch (e: Exception) {
            Log.e("PostRemoteMediator", "$e")
            return MediatorResult.Error(e)
        }
    }
}