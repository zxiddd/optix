package com.zaddy.optix.sync

data class SyncOutboxItem(
    val eventId: String,
    val eventType: String,
    val aggregateId: String,
    val versionTimestamp: Long,
    val payloadJson: String,
    var isSynced: Boolean = false,
    var retryCount: Int = 0
)

class SyncOutboxRepository {
    private val queue = mutableListOf<SyncOutboxItem>()

    fun enqueueEvent(item: SyncOutboxItem) {
        queue.add(item)
    }

    fun getPendingEvents(): List<SyncOutboxItem> {
        return queue.filter { !it.isSynced }
    }

    fun markEventsSynced(eventIds: List<String>) {
        queue.forEach {
            if (eventIds.contains(it.eventId)) {
                it.isSynced = true
            }
        }
    }
}
