package com.eren.hospitalui.repository

data class Announcement(
    val id: Int,
    val title: String,
    val content: String,
    val timestamp: Long
)

object AnnouncementRepository {
    private val announcements = mutableListOf<Announcement>()

    fun addAnnouncement(announcement: Announcement) {
        announcements.add(announcement)
    }

    fun getAnnouncements(): List<Announcement> {
        return announcements.toList()
    }

    fun updateAnnouncement(updatedAnnouncement: Announcement) {
        val index = announcements.indexOfFirst { it.id == updatedAnnouncement.id }
        if (index != -1) {
            announcements[index] = updatedAnnouncement
        }
    }

    fun removeAnnouncement(id: Int) {
        announcements.removeAll { it.id == id }
    }
}