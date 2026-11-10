package com.ptilou.app.di

import android.content.Context
import androidx.room.Room
import com.ptilou.app.data.local.PtilouDatabase
import com.ptilou.app.data.local.dao.AccessoireDao
import com.ptilou.app.data.local.dao.AttemptDao
import com.ptilou.app.data.local.dao.BadgeDao
import com.ptilou.app.data.local.dao.EconomieDao
import com.ptilou.app.data.local.dao.MasteryDao
import com.ptilou.app.data.local.dao.StreakDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PtilouDatabase =
        // Effacement autorisé uniquement depuis les versions de dev (1 à 5). Depuis la v6
        // l'enfant a une vraie progression : tout futur changement de schéma DOIT fournir une
        // migration (ex. autoMigrations = [AutoMigration(from = 6, to = 7)]), sinon Room
        // plante au démarrage au lieu d'effacer silencieusement pièces et progression.
        Room.databaseBuilder(context, PtilouDatabase::class.java, "ptilou.db")
            .fallbackToDestructiveMigrationFrom(1, 2, 3, 4, 5)
            .build()

    @Provides
    fun provideMasteryDao(database: PtilouDatabase): MasteryDao = database.masteryDao()

    @Provides
    fun provideAttemptDao(database: PtilouDatabase): AttemptDao = database.attemptDao()

    @Provides
    fun provideStreakDao(database: PtilouDatabase): StreakDao = database.streakDao()

    @Provides
    fun provideBadgeDao(database: PtilouDatabase): BadgeDao = database.badgeDao()

    @Provides
    fun provideEconomieDao(database: PtilouDatabase): EconomieDao = database.economieDao()

    @Provides
    fun provideAccessoireDao(database: PtilouDatabase): AccessoireDao = database.accessoireDao()
}
