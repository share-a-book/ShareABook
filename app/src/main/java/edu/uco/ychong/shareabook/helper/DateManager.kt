package edu.uco.ychong.shareabook.helper

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class DateManager {
    companion object {
        fun getCurrentDateWithFullFormat(): String {
            return LocalDateTime.now()
                    .format(DateTimeFormatter
                            .ofLocalizedDate(FormatStyle.FULL))
        }
    }
}