package com.ptilou.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ptilou.app.data.local.dao.AccessoireDao
import com.ptilou.app.data.local.dao.AttemptDao
import com.ptilou.app.data.local.dao.BadgeDao
import com.ptilou.app.data.local.dao.EconomieDao
import com.ptilou.app.data.local.dao.MasteryDao
import com.ptilou.app.data.local.dao.StreakDao
import com.ptilou.app.data.local.entity.AccessoirePossedeEntity
import com.ptilou.app.data.local.entity.AttemptEntity
import com.ptilou.app.data.local.entity.BadgeEntity
import com.ptilou.app.data.local.entity.EconomieEntity
import com.ptilou.app.data.local.entity.MasteryEntity
import com.ptilou.app.data.local.entity.StreakEntity

@Database(
    entities = [
        MasteryEntity::class, AttemptEntity::class, StreakEntity::class, BadgeEntity::class,
        EconomieEntity::class, AccessoirePossedeEntity::class
    ],
    version = 6,
    exportSchema = true
)
abstract class PtilouDatabase : RoomDatabase() {
    abstract fun masteryDao(): MasteryDao
    abstract fun attemptDao(): AttemptDao
    abstract fun streakDao(): StreakDao
    abstract fun badgeDao(): BadgeDao
    abstract fun economieDao(): EconomieDao
    abstract fun accessoireDao(): AccessoireDao
}
