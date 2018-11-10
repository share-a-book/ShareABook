package edu.uco.ychong.shareabook.book

class BookStatus {
    companion object {
        val AVAILABLE = "available"
        val REQUEST_PENDING = "request_pending"
        val ACCEPTED = "accepted"
        val REJECTED = "rejected"
        val UNAVAILABLE = "unavailable"
    }
}