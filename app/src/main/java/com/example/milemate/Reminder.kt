package com.example.milemate

import android.icu.util.Calendar
import java.util.*

class Reminder constructor(ReminderDate: Calendar, CheckUpDate: Calendar) {

        var reminderDate: Date = ReminderDate.time
        var checkUpDate: Date = CheckUpDate.time
}