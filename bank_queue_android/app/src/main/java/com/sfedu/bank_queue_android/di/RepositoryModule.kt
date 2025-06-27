package com.sfedu.bank_queue_android.di

import com.sfedu.bank_queue_android.repository.AuthRepository
import com.sfedu.bank_queue_android.repository.AuthRepositoryImpl
import com.sfedu.bank_queue_android.repository.UserRepository
import com.sfedu.bank_queue_android.repository.UserRepositoryImpl
import com.sfedu.bank_queue_android.repository.TicketRepository
import com.sfedu.bank_queue_android.repository.TicketRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindAuthRepo(impl: AuthRepositoryImpl): AuthRepository

    @Binds @Singleton
    abstract fun bindUserRepo(impl: UserRepositoryImpl): UserRepository

    @Binds @Singleton
    abstract fun bindTicketRepo(impl: TicketRepositoryImpl): TicketRepository
}