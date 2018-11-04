package edu.uco.ychong.shareabook.book

class BookStatus {
    companion object {
        val AVAILABLE = "available"
        val CHECKOUT_PENDING = "checkout_pending"
        val RESERVED = "reserved"
        val UNAVAILABLE = "unavailable"
    }
}